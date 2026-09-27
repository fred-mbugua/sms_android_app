package com.payprint.pos;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.payprint.pos.bluetooth.BluetoothPrinterManager;
import com.payprint.pos.R;

import java.util.ArrayList;
import java.util.List;

public class BluetoothPrinterTabFragment extends Fragment implements BluetoothPrinterManager.PrinterStateListener {

    private TextView statusBadge;
    private ImageView statusIcon;
    private TextView deviceNameTxt;
    private TextView statusDetailTxt;
    private Button btnReconnect;

    private SwitchMaterial switchAutoPrint;
    private EditText editHeader;
    private EditText editFooter;
    private CheckBox chkShowSender;
    private CheckBox chkShowDate;
    private CheckBox chkHighlightCode;
    private Button btnSaveSettings;
    private Button btnTestPrint;

    private Button btnScanDevices;
    private RecyclerView rvBtDevices;
    private TextView txtNoDevices;
    private BluetoothDeviceAdapter deviceAdapter;
    private final List<BluetoothDevice> deviceList = new ArrayList<>();

    private SharedPreferences prefs;
    private BluetoothPrinterManager printerManager;

    public BluetoothPrinterTabFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tab_bluetooth_printer, container, false);

        prefs = requireActivity().getSharedPreferences("PAYPRINT_SHARED_PREFS", Context.MODE_PRIVATE);
        printerManager = BluetoothPrinterManager.getInstance(requireActivity());

        statusBadge = v.findViewById(R.id.printer_status_badge);
        statusIcon = v.findViewById(R.id.printer_status_icon);
        deviceNameTxt = v.findViewById(R.id.printer_device_name_txt);
        statusDetailTxt = v.findViewById(R.id.printer_status_detail_txt);
        btnReconnect = v.findViewById(R.id.btn_printer_reconnect);

        switchAutoPrint = v.findViewById(R.id.switch_auto_print);
        editHeader = v.findViewById(R.id.edit_print_header);
        editFooter = v.findViewById(R.id.edit_print_footer);
        chkShowSender = v.findViewById(R.id.chk_show_sender);
        chkShowDate = v.findViewById(R.id.chk_show_date);
        chkHighlightCode = v.findViewById(R.id.chk_highlight_code);
        btnSaveSettings = v.findViewById(R.id.btn_save_print_settings);
        btnTestPrint = v.findViewById(R.id.btn_test_print);

        btnScanDevices = v.findViewById(R.id.btn_scan_bt_devices);
        rvBtDevices = v.findViewById(R.id.rv_bluetooth_devices);
        txtNoDevices = v.findViewById(R.id.txt_no_bt_devices);

        rvBtDevices.setLayoutManager(new LinearLayoutManager(requireContext()));
        deviceAdapter = new BluetoothDeviceAdapter();
        rvBtDevices.setAdapter(deviceAdapter);

        loadSavedSettings();

        btnSaveSettings.setOnClickListener(v1 -> saveSettings());
        btnTestPrint.setOnClickListener(v1 -> runTestPrint());
        btnReconnect.setOnClickListener(v1 -> reconnectSavedPrinter());
        btnScanDevices.setOnClickListener(v1 -> refreshPairedDevices());

        refreshPairedDevices();

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (printerManager != null) {
            printerManager.addStateListener(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (printerManager != null) {
            printerManager.removeStateListener(this);
        }
    }

    private void loadSavedSettings() {
        switchAutoPrint.setChecked(prefs.getBoolean("auto_print_enabled", true));
        editHeader.setText(prefs.getString("print_header_text", "OFFICIAL POS RECEIPT"));
        editFooter.setText(prefs.getString("print_footer_text", "Thank you for transacting!"));
        chkShowSender.setChecked(prefs.getBoolean("print_show_sender", true));
        chkShowDate.setChecked(prefs.getBoolean("print_show_date", true));
        chkHighlightCode.setChecked(prefs.getBoolean("print_highlight_code", true));
    }

    private void saveSettings() {
        prefs.edit()
                .putBoolean("auto_print_enabled", switchAutoPrint.isChecked())
                .putString("print_header_text", editHeader.getText().toString().trim())
                .putString("print_footer_text", editFooter.getText().toString().trim())
                .putBoolean("print_show_sender", chkShowSender.isChecked())
                .putBoolean("print_show_date", chkShowDate.isChecked())
                .putBoolean("print_highlight_code", chkHighlightCode.isChecked())
                .apply();

        Toast.makeText(getActivity(), "Printer settings saved successfully!", Toast.LENGTH_SHORT).show();
    }

    private void runTestPrint() {
        Toast.makeText(getActivity(), "Sending test receipt to thermal printer...", Toast.LENGTH_SHORT).show();
        printerManager.printTestReceipt((success, message) -> {
            if (isAdded() && getActivity() != null) {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void reconnectSavedPrinter() {
        Toast.makeText(getActivity(), "Connecting to saved printer...", Toast.LENGTH_SHORT).show();
        printerManager.connectToSavedPrinter((success, message) -> {
            if (isAdded() && getActivity() != null) {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshPairedDevices() {
        List<BluetoothDevice> paired = printerManager.getPairedDevices();
        deviceList.clear();
        if (paired != null && !paired.isEmpty()) {
            deviceList.addAll(paired);
            rvBtDevices.setVisibility(View.VISIBLE);
            txtNoDevices.setVisibility(View.GONE);
        } else {
            rvBtDevices.setVisibility(View.GONE);
            txtNoDevices.setVisibility(View.VISIBLE);
        }
        deviceAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStateChanged(BluetoothPrinterManager.State state, String deviceName, String message) {
        if (!isAdded() || getActivity() == null) return;

        deviceNameTxt.setText(deviceName);
        statusDetailTxt.setText(message);

        switch (state) {
            case CONNECTED:
                statusBadge.setText("CONNECTED");
                statusBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.appPrimaryGreen));
                statusIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.appPrimaryGreen));
                btnReconnect.setText("Disconnect");
                btnReconnect.setOnClickListener(v -> printerManager.disconnect());
                break;
            case CONNECTING:
                statusBadge.setText("CONNECTING");
                statusBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.appNeutralGrey));
                statusIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.appNeutralGrey));
                btnReconnect.setText("Cancel");
                btnReconnect.setOnClickListener(v -> printerManager.disconnect());
                break;
            case FAILED:
                statusBadge.setText("FAILED / LOST");
                statusBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.appErrorRed));
                statusIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.appErrorRed));
                btnReconnect.setText("Reconnect");
                btnReconnect.setOnClickListener(v -> reconnectSavedPrinter());
                break;
            case DISCONNECTED:
            default:
                statusBadge.setText("DISCONNECTED");
                statusBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.appErrorRed));
                statusIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.appErrorRed));
                btnReconnect.setText("Connect");
                btnReconnect.setOnClickListener(v -> reconnectSavedPrinter());
                break;
        }
    }

    private class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bluetooth_device, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            BluetoothDevice device = deviceList.get(position);
            String name = device.getName();
            holder.nameTxt.setText(name != null && !name.isEmpty() ? name : "Unknown Device");
            holder.addressTxt.setText(device.getAddress());

            holder.btnConnect.setOnClickListener(v -> {
                Toast.makeText(getActivity(), "Connecting to " + holder.nameTxt.getText() + "...", Toast.LENGTH_SHORT).show();
                printerManager.connectToDevice(device, (success, message) -> {
                    if (isAdded() && getActivity() != null) {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }

        @Override
        public int getItemCount() {
            return deviceList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameTxt, addressTxt;
            Button btnConnect;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                nameTxt = itemView.findViewById(R.id.bt_device_name);
                addressTxt = itemView.findViewById(R.id.bt_device_address);
                btnConnect = itemView.findViewById(R.id.btn_connect_bt_device);
            }
        }
    }
}
