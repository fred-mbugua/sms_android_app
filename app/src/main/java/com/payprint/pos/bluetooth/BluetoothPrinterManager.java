package com.payprint.pos.bluetooth;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.payprint.pos.core.App;
import com.payprint.pos.utilities.KenyanTimeFormatter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.payprint.pos.R;

public class BluetoothPrinterManager {

    private static final String TAG = "BluetoothPrinterManager";
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String CHANNEL_ID = "PayPrint_Printer_Alerts";

    public enum State {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        FAILED
    }

    public interface PrinterStateListener {
        void onStateChanged(State state, String deviceName, String message);
    }

    public interface PrintCallback {
        void onResult(boolean success, String message);
    }

    private static BluetoothPrinterManager instance;
    private final Context context;
    private final SharedPreferences prefs;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final List<PrinterStateListener> stateListeners = new ArrayList<>();

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private BluetoothDevice connectedDevice;
    private State currentState = State.DISCONNECTED;

    private BluetoothPrinterManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = this.context.getSharedPreferences("PAYPRINT_SHARED_PREFS", Context.MODE_PRIVATE);
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        createNotificationChannel();
    }

    public static synchronized BluetoothPrinterManager getInstance(Context context) {
        if (instance == null) {
            instance = new BluetoothPrinterManager(context != null ? context : App.getInstance());
        }
        return instance;
    }

    public static synchronized BluetoothPrinterManager getInstance() {
        return getInstance(App.getInstance());
    }

    public void addStateListener(PrinterStateListener listener) {
        if (listener != null && !stateListeners.contains(listener)) {
            stateListeners.add(listener);
            listener.onStateChanged(currentState, getConnectedDeviceName(), getStatusMessage());
        }
    }

    public void removeStateListener(PrinterStateListener listener) {
        stateListeners.remove(listener);
    }

    private void notifyStateChanged(State state, String message) {
        this.currentState = state;
        mainHandler.post(() -> {
            for (PrinterStateListener listener : new ArrayList<>(stateListeners)) {
                listener.onStateChanged(state, getConnectedDeviceName(), message);
            }
        });
    }

    public State getCurrentState() {
        return currentState;
    }

    public boolean isConnected() {
        return currentState == State.CONNECTED && socket != null && socket.isConnected();
    }

    public String getConnectedDeviceName() {
        if (connectedDevice != null) {
            @SuppressLint("MissingPermission")
            String name = connectedDevice.getName();
            return name != null ? name : connectedDevice.getAddress();
        }
        return prefs.getString("printer_name", "No Printer Selected");
    }

    public String getStatusMessage() {
        switch (currentState) {
            case CONNECTED:
                return "Connected to " + getConnectedDeviceName();
            case CONNECTING:
                return "Connecting to printer...";
            case FAILED:
                return "Connection failed / Lost";
            default:
                return "Disconnected";
        }
    }

    @SuppressLint("MissingPermission")
    public List<BluetoothDevice> getPairedDevices() {
        List<BluetoothDevice> list = new ArrayList<>();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            Set<BluetoothDevice> paired = bluetoothAdapter.getBondedDevices();
            if (paired != null) {
                list.addAll(paired);
            }
        }
        return list;
    }

    public void connectToSavedPrinter(PrintCallback callback) {
        String savedMac = prefs.getString("printer_mac", "");
        if (savedMac.isEmpty()) {
            if (callback != null) callback.onResult(false, "No printer MAC saved in settings");
            return;
        }
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            if (callback != null) callback.onResult(false, "Bluetooth is turned off");
            return;
        }

        try {
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(savedMac);
            connectToDevice(device, callback);
        } catch (Exception e) {
            Log.e(TAG, "Error connecting to saved printer", e);
            if (callback != null) callback.onResult(false, "Invalid printer address: " + e.getLocalizedMessage());
        }
    }

    @SuppressLint("MissingPermission")
    public void connectToDevice(BluetoothDevice device, PrintCallback callback) {
        if (device == null) {
            if (callback != null) callback.onResult(false, "Invalid device");
            return;
        }

        disconnect();
        notifyStateChanged(State.CONNECTING, "Connecting to " + (device.getName() != null ? device.getName() : device.getAddress()));

        executor.execute(() -> {
            try {
                if (bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                }

                BluetoothSocket tempSocket = device.createRfcommSocketToServiceRecord(SPP_UUID);
                tempSocket.connect();

                socket = tempSocket;
                outputStream = socket.getOutputStream();
                connectedDevice = device;

                prefs.edit()
                        .putString("printer_mac", device.getAddress())
                        .putString("printer_name", device.getName() != null ? device.getName() : device.getAddress())
                        .apply();

                notifyStateChanged(State.CONNECTED, "Connected successfully");
                if (callback != null) {
                    mainHandler.post(() -> callback.onResult(true, "Connected to " + device.getName()));
                }

            } catch (Exception e) {
                Log.e(TAG, "Error opening Bluetooth socket", e);
                closeSocket();
                notifyStateChanged(State.FAILED, "Connection failed: " + e.getLocalizedMessage());
                showLostConnectionNotification("Failed to connect to " + device.getName());
                if (callback != null) {
                    mainHandler.post(() -> callback.onResult(false, "Could not connect: " + e.getLocalizedMessage()));
                }
            }
        });
    }

    public synchronized void disconnect() {
        closeSocket();
        connectedDevice = null;
        notifyStateChanged(State.DISCONNECTED, "Disconnected");
    }

    private void closeSocket() {
        try {
            if (outputStream != null) outputStream.close();
            if (socket != null) socket.close();
        } catch (IOException ignored) {
        } finally {
            outputStream = null;
            socket = null;
        }
    }

    public void printIncomingSmsIfEnabled(String sender, String message, String timestamp) {
        boolean autoPrint = prefs.getBoolean("auto_print_enabled", true);
        if (!autoPrint) {
            Log.d(TAG, "Auto-print is disabled in settings. Skipping automatic print.");
            return;
        }

        printSmsReceipt(sender, message, timestamp, (success, msg) -> {
            if (!success) {
                Log.w(TAG, "Auto-print failed: " + msg);
                showLostConnectionNotification("Printer disconnected. Could not auto-print SMS from " + sender);
            }
        });
    }

    public void printTestReceipt(PrintCallback callback) {
        String testMessage = "KES 1,500.00 received from JOHN DOE 0712345678 on 2025-02-21 15:30. Ref: QAB1234567.";
        printSmsReceipt("PayPrint Test", testMessage, "2025-02-21 15:30:00", callback);
    }

    public void printSmsReceipt(String sender, String message, String timestamp, PrintCallback callback) {
        executor.execute(() -> {
            if (!isConnected()) {
                // Try auto-reconnect to saved MAC
                String savedMac = prefs.getString("printer_mac", "");
                if (!savedMac.isEmpty() && bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                    try {
                        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(savedMac);
                        @SuppressLint("MissingPermission")
                BluetoothSocket tempSocket = device.createRfcommSocketToServiceRecord(SPP_UUID);
                        tempSocket.connect();
                        socket = tempSocket;
                        outputStream = socket.getOutputStream();
                        connectedDevice = device;
                        notifyStateChanged(State.CONNECTED, "Reconnected to " + getConnectedDeviceName());
                    } catch (Exception e) {
                        closeSocket();
                        notifyStateChanged(State.FAILED, "Printer disconnected");
                        showLostConnectionNotification("Printer connection lost during print request");
                        if (callback != null) {
                            mainHandler.post(() -> callback.onResult(false, "Printer offline. Connection lost."));
                        }
                        return;
                    }
                } else {
                    notifyStateChanged(State.FAILED, "No printer connected");
                    showLostConnectionNotification("No printer connected. Tap to configure printer.");
                    if (callback != null) {
                        mainHandler.post(() -> callback.onResult(false, "No active printer connection"));
                    }
                    return;
                }
            }

            try {
                byte[] printBytes = formatEscPosReceipt(sender, message, timestamp);
                outputStream.write(printBytes);
                outputStream.flush();

                if (callback != null) {
                    mainHandler.post(() -> callback.onResult(true, "Receipt printed successfully!"));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error writing ESC/POS commands to printer", e);
                closeSocket();
                notifyStateChanged(State.FAILED, "Print failed: connection lost");
                showLostConnectionNotification("Bluetooth connection lost while printing receipt");
                if (callback != null) {
                    mainHandler.post(() -> callback.onResult(false, "Print failed: " + e.getLocalizedMessage()));
                }
            }
        });
    }

    private byte[] formatEscPosReceipt(String sender, String message, String timestamp) throws IOException {
        String header = prefs.getString("print_header_text", "OFFICIAL POS RECEIPT").trim();
        String footer = prefs.getString("print_footer_text", "Thank you for transacting!").trim();
        boolean showSender = prefs.getBoolean("print_show_sender", true);
        boolean showDate = prefs.getBoolean("print_show_date", true);
        boolean highlightCode = prefs.getBoolean("print_highlight_code", true);

        List<Byte> bytes = new ArrayList<>();

        // ESC @ -> Initialize Printer
        addBytes(bytes, new byte[]{0x1B, 0x40});

        // Align Center for Header
        addBytes(bytes, new byte[]{0x1B, 0x61, 0x01});
        // Bold & Larger Text
        addBytes(bytes, new byte[]{0x1B, 0x21, 0x20}); // Double height
        addString(bytes, header + "\n");

        // Normal text size, Left Align
        addBytes(bytes, new byte[]{0x1B, 0x21, 0x00});
        addBytes(bytes, new byte[]{0x1B, 0x61, 0x00});

        addString(bytes, "--------------------------------\n");

        if (showDate && timestamp != null && !timestamp.isEmpty()) {
            addString(bytes, "Date: " + KenyanTimeFormatter.formatToKenyanTime(timestamp) + "\n");
        }

        if (showSender && sender != null && !sender.isEmpty()) {
            addString(bytes, "Sender: " + sender + "\n");
        }

        String code = extractTransCode(message);
        if (highlightCode && !code.isEmpty()) {
            addBytes(bytes, new byte[]{0x1B, 0x45, 0x01}); // Bold ON
            addString(bytes, "REF CODE: " + code + "\n");
            addBytes(bytes, new byte[]{0x1B, 0x45, 0x00}); // Bold OFF
        }

        addString(bytes, "--------------------------------\n");

        // Print Message Body
        addString(bytes, message != null ? message : "");
        addString(bytes, "\n--------------------------------\n");

        // Align Center for Footer
        addBytes(bytes, new byte[]{0x1B, 0x61, 0x01});
        addString(bytes, footer + "\n\n\n\n");

        // GS V 66 0 -> Feed & Cut
        addBytes(bytes, new byte[]{0x1D, 0x56, 0x42, 0x00});

        byte[] result = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            result[i] = bytes.get(i);
        }
        return result;
    }

    private String extractTransCode(String text) {
        if (text == null || text.trim().isEmpty()) return "";
        Pattern p = Pattern.compile("([A-Z0-9]{10})");
        Matcher m = p.matcher(text);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    private void addBytes(List<Byte> list, byte[] data) {
        for (byte b : data) {
            list.add(b);
        }
    }

    private void addString(List<Byte> list, String text) {
        if (text == null) return;
        byte[] data = text.getBytes(StandardCharsets.UTF_8);
        for (byte b : data) {
            list.add(b);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "PayPrint Printer Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Alerts for Bluetooth thermal printer disconnects and print errors.");
            NotificationManager nm = context.getSystemService(NotificationManager.class);
            if (nm != null) {
                nm.createNotificationChannel(channel);
            }
        }
    }

    private void showLostConnectionNotification(String message) {
        try {
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm == null) return;

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_printer_24)
                    .setContentTitle("PayPrint Printer Disconnected")
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);

            nm.notify(9001, builder.build());
        } catch (Exception e) {
            Log.e(TAG, "Error posting notification", e);
        }
    }
}
