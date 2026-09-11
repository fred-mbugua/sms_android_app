package com.example.dotpesa_new_dec_2022.utilities;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.database.DBHelper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ExcelImporter {

    private static final String TAG = "ExcelImporter";

    public interface ImportCallback {
        void onComplete(boolean success, int matchedCount, String message);
    }

    public static void importExcelFile(Context context, Uri fileUri, ImportCallback callback) {
        if (context == null || fileUri == null) {
            if (callback != null) callback.onComplete(false, 0, "Invalid file or context");
            return;
        }

        new Thread(() -> {
            int updatedCount = 0;
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
                if (inputStream == null) {
                    if (callback != null) callback.onComplete(false, 0, "Cannot open file");
                    return;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                String line;
                int accountCol = -1;
                int noteCol = -1;
                int categoryCol = -1;
                boolean isHeader = true;

                DBHelper db = new DBHelper(context);

                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;

                    String[] tokens = line.split("[,;\\t]");

                    if (isHeader) {
                        isHeader = false;
                        for (int i = 0; i < tokens.length; i++) {
                            String header = tokens[i].trim().toLowerCase().replaceAll("[^a-z0-9]", "");
                            if (header.contains("account") || header.contains("acc") || header.contains("billref") || header.contains("ref")) {
                                accountCol = i;
                            } else if (header.contains("note") || header.contains("description") || header.contains("info") || header.contains("detail") || header.contains("name")) {
                                noteCol = i;
                            } else if (header.contains("category") || header.contains("dept") || header.contains("type")) {
                                categoryCol = i;
                            }
                        }

                        if (accountCol == -1) accountCol = 0;
                        if (noteCol == -1) noteCol = tokens.length > 1 ? 1 : 0;
                        continue;
                    }

                    if (tokens.length > accountCol) {
                        String accountNum = cleanValue(tokens[accountCol]);
                        String note = (noteCol != -1 && tokens.length > noteCol) ? cleanValue(tokens[noteCol]) : "";
                        String category = (categoryCol != -1 && tokens.length > categoryCol) ? cleanValue(tokens[categoryCol]) : "Excel Import";

                        if (!accountNum.isEmpty() && !note.isEmpty()) {
                            int matched = db.updateC2BExtraNoteByAccount(accountNum, note, category);
                            updatedCount += matched;
                        }
                    }
                }
                reader.close();
                inputStream.close();

                final int count = updatedCount;
                if (callback != null) {
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onComplete(true, count, "Excel Import Complete! Linked supplementary data to " + count + " transactions by Account Number.")
                    );
                }
            } catch (Exception e) {
                Log.e(TAG, "Excel Import Error: " + e.getLocalizedMessage(), e);
                if (callback != null) {
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onComplete(false, 0, "Import failed: " + e.getLocalizedMessage())
                    );
                }
            }
        }).start();
    }

    private static String cleanValue(String raw) {
        if (raw == null) return "";
        return raw.replaceAll("^\"|\"$", "").trim();
    }
}
