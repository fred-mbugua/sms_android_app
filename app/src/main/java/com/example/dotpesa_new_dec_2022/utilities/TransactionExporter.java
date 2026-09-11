package com.example.dotpesa_new_dec_2022.utilities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.database.C2BTransactionModel;
import com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.database.NodeSmsModel;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionExporter {

    private static final String TAG = "TransactionExporter";

    // --- C2B EXCEL (.CSV) EXPORT ---
    public static void exportC2BToExcel(Context context, List<C2BTransactionModel> list) {
        if (context == null || list == null || list.isEmpty()) {
            Toast.makeText(context, "No transactions available to export", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                String fileName = "C2B_Transactions_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".csv";
                File exportFile = new File(context.getCacheDir(), fileName);

                FileOutputStream fos = new FileOutputStream(exportFile);
                StringBuilder sb = new StringBuilder();

                sb.append("Trans ID,Account / Ref,Customer Name,Phone Number,Amount (KES),Shortcode,Date & Time,Supplementary Data\n");

                for (C2BTransactionModel tx : list) {
                    sb.append(escapeCsv(tx.transId)).append(",")
                            .append(escapeCsv(tx.billRefNumber)).append(",")
                            .append(escapeCsv(tx.getFullName())).append(",")
                            .append(escapeCsv(tx.msisdn)).append(",")
                            .append(String.format(Locale.US, "%.2f", tx.amount)).append(",")
                            .append(escapeCsv(tx.businessShortcode)).append(",")
                            .append(escapeCsv(tx.transTimeFormatted != null && !tx.transTimeFormatted.isEmpty() ? tx.transTimeFormatted : tx.transTime)).append(",")
                            .append(escapeCsv(tx.extraNote)).append("\n");
                }

                fos.write(sb.toString().getBytes());
                fos.close();

                shareFile(context, exportFile, "text/csv", "Export C2B Excel Report");
            } catch (Exception e) {
                Log.e(TAG, "Error exporting C2B Excel", e);
                showToast(context, "Export failed: " + e.getLocalizedMessage());
            }
        }).start();
    }

    // --- C2B PDF EXPORT ---
    public static void exportC2BToPdf(Context context, List<C2BTransactionModel> list) {
        if (context == null || list == null || list.isEmpty()) {
            Toast.makeText(context, "No transactions available to export", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                String fileName = "C2B_Transactions_Report_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".pdf";
                File pdfFile = new File(context.getCacheDir(), fileName);

                PdfDocument pdfDocument = new PdfDocument();
                int pageWidth = 595;
                int pageHeight = 842;

                Paint paint = new Paint();
                Paint titlePaint = new Paint();
                Paint headerPaint = new Paint();
                Paint textPaint = new Paint();

                titlePaint.setTextSize(16f);
                titlePaint.setFakeBoldText(true);
                titlePaint.setColor(Color.parseColor("#00C853"));

                headerPaint.setTextSize(10f);
                headerPaint.setFakeBoldText(true);
                headerPaint.setColor(Color.WHITE);

                textPaint.setTextSize(9f);
                textPaint.setColor(Color.parseColor("#0F172A"));

                int y = 40;
                int pageNum = 1;

                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create();
                PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                Canvas canvas = page.getCanvas();

                canvas.drawText("DotPesa - Cloud C2B Transactions Report", 20, y, titlePaint);
                y += 20;

                double totalAmount = 0.0;
                for (C2BTransactionModel tx : list) totalAmount += tx.amount;

                paint.setTextSize(9f);
                paint.setColor(Color.parseColor("#64748B"));
                String meta = String.format(Locale.getDefault(), "Generated: %s | Total Records: %d | Total KES: %.2f",
                        new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(new Date()), list.size(), totalAmount);
                canvas.drawText(meta, 20, y, paint);
                y += 25;

                Paint bgPaint = new Paint();
                bgPaint.setColor(Color.parseColor("#00C853"));
                canvas.drawRect(20, y, pageWidth - 20, y + 20, bgPaint);

                canvas.drawText("TRANS ID", 25, y + 14, headerPaint);
                canvas.drawText("ACCOUNT / REF", 110, y + 14, headerPaint);
                canvas.drawText("CUSTOMER NAME", 210, y + 14, headerPaint);
                canvas.drawText("PHONE", 350, y + 14, headerPaint);
                canvas.drawText("AMOUNT (KES)", 430, y + 14, headerPaint);
                canvas.drawText("DATE", 510, y + 14, headerPaint);
                y += 25;

                int rowCount = 0;
                for (C2BTransactionModel tx : list) {
                    if (y > pageHeight - 50) {
                        pdfDocument.finishPage(page);
                        pageNum++;
                        pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create();
                        page = pdfDocument.startPage(pageInfo);
                        canvas = page.getCanvas();

                        canvas.drawRect(20, 30, pageWidth - 20, 50, bgPaint);
                        canvas.drawText("TRANS ID", 25, 44, headerPaint);
                        canvas.drawText("ACCOUNT / REF", 110, 44, headerPaint);
                        canvas.drawText("CUSTOMER NAME", 210, 44, headerPaint);
                        canvas.drawText("PHONE", 350, 44, headerPaint);
                        canvas.drawText("AMOUNT (KES)", 430, 44, headerPaint);
                        canvas.drawText("DATE", 510, 44, headerPaint);
                        y = 55;
                    }

                    if (rowCount % 2 == 1) {
                        Paint rowBg = new Paint();
                        rowBg.setColor(Color.parseColor("#F1F5F9"));
                        canvas.drawRect(20, y - 10, pageWidth - 20, y + 8, rowBg);
                    }

                    canvas.drawText(trunc(tx.transId, 12), 25, y, textPaint);
                    canvas.drawText(trunc(tx.billRefNumber, 14), 110, y, textPaint);
                    canvas.drawText(trunc(tx.getFullName(), 20), 210, y, textPaint);
                    canvas.drawText(trunc(tx.msisdn, 12), 350, y, textPaint);
                    canvas.drawText(String.format(Locale.US, "%.2f", tx.amount), 430, y, textPaint);

                    String dateStr = tx.transTimeFormatted != null && !tx.transTimeFormatted.isEmpty() ? tx.transTimeFormatted : tx.transTime;
                    canvas.drawText(trunc(dateStr, 12), 510, y, textPaint);

                    y += 18;
                    rowCount++;
                }

                pdfDocument.finishPage(page);

                FileOutputStream fos = new FileOutputStream(pdfFile);
                pdfDocument.writeTo(fos);
                pdfDocument.close();
                fos.close();

                shareFile(context, pdfFile, "application/pdf", "Export C2B PDF Report");
            } catch (Exception e) {
                Log.e(TAG, "Error exporting C2B PDF", e);
                showToast(context, "Export PDF failed: " + e.getLocalizedMessage());
            }
        }).start();
    }

    // --- NODE SMS EXCEL (.CSV) EXPORT ---
    public static void exportNodeSmsToExcel(Context context, List<NodeSmsModel> list) {
        if (context == null || list == null || list.isEmpty()) {
            Toast.makeText(context, "No SMS transactions available to export", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                String fileName = "Node_SMS_Transactions_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".csv";
                File exportFile = new File(context.getCacheDir(), fileName);

                FileOutputStream fos = new FileOutputStream(exportFile);
                StringBuilder sb = new StringBuilder();

                sb.append("SMS Serial,M-PESA Code,Sender,Extracted Amount (KES),Message Body,Timestamp\n");

                for (NodeSmsModel sms : list) {
                    sb.append(sms.smsSerial).append(",")
                            .append(escapeCsv(sms.getMpesaTransId())).append(",")
                            .append(escapeCsv(sms.sender)).append(",")
                            .append(String.format(Locale.US, "%.2f", sms.getExtractedAmount())).append(",")
                            .append(escapeCsv(sms.message)).append(",")
                            .append(escapeCsv(sms.timestamp != null ? sms.timestamp : sms.createdAt)).append("\n");
                }

                fos.write(sb.toString().getBytes());
                fos.close();

                shareFile(context, exportFile, "text/csv", "Export Node SMS Excel Report");
            } catch (Exception e) {
                Log.e(TAG, "Error exporting Node SMS Excel", e);
                showToast(context, "Export failed: " + e.getLocalizedMessage());
            }
        }).start();
    }

    // --- NODE SMS PDF EXPORT ---
    public static void exportNodeSmsToPdf(Context context, List<NodeSmsModel> list) {
        if (context == null || list == null || list.isEmpty()) {
            Toast.makeText(context, "No SMS transactions available to export", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                String fileName = "Node_SMS_Report_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".pdf";
                File pdfFile = new File(context.getCacheDir(), fileName);

                PdfDocument pdfDocument = new PdfDocument();
                int pageWidth = 595;
                int pageHeight = 842;

                Paint titlePaint = new Paint();
                titlePaint.setTextSize(16f);
                titlePaint.setFakeBoldText(true);
                titlePaint.setColor(Color.parseColor("#00C853"));

                Paint headerPaint = new Paint();
                headerPaint.setTextSize(10f);
                headerPaint.setFakeBoldText(true);
                headerPaint.setColor(Color.WHITE);

                Paint textPaint = new Paint();
                textPaint.setTextSize(9f);
                textPaint.setColor(Color.parseColor("#0F172A"));

                Paint bgPaint = new Paint();
                bgPaint.setColor(Color.parseColor("#00C853"));

                int y = 40;
                int pageNum = 1;

                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create();
                PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                Canvas canvas = page.getCanvas();

                canvas.drawText("DotPesa - Node.js SMS Transactions Report", 20, y, titlePaint);
                y += 20;

                double totalExtracted = 0.0;
                for (NodeSmsModel sms : list) totalExtracted += sms.getExtractedAmount();

                Paint paint = new Paint();
                paint.setTextSize(9f);
                paint.setColor(Color.parseColor("#64748B"));
                String meta = String.format(Locale.getDefault(), "Generated: %s | Total Messages: %d | Total Extracted KES: %.2f",
                        new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(new Date()), list.size(), totalExtracted);
                canvas.drawText(meta, 20, y, paint);
                y += 25;

                canvas.drawRect(20, y, pageWidth - 20, y + 20, bgPaint);
                canvas.drawText("SERIAL", 25, y + 14, headerPaint);
                canvas.drawText("CODE", 80, y + 14, headerPaint);
                canvas.drawText("SENDER", 170, y + 14, headerPaint);
                canvas.drawText("AMOUNT", 270, y + 14, headerPaint);
                canvas.drawText("MESSAGE BODY", 340, y + 14, headerPaint);
                y += 25;

                int rowCount = 0;
                for (NodeSmsModel sms : list) {
                    if (y > pageHeight - 50) {
                        pdfDocument.finishPage(page);
                        pageNum++;
                        pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create();
                        page = pdfDocument.startPage(pageInfo);
                        canvas = page.getCanvas();

                        canvas.drawRect(20, 30, pageWidth - 20, 50, bgPaint);
                        canvas.drawText("SERIAL", 25, 44, headerPaint);
                        canvas.drawText("CODE", 80, 44, headerPaint);
                        canvas.drawText("SENDER", 170, 44, headerPaint);
                        canvas.drawText("AMOUNT", 270, 44, headerPaint);
                        canvas.drawText("MESSAGE BODY", 340, 44, headerPaint);
                        y = 55;
                    }

                    if (rowCount % 2 == 1) {
                        Paint rowBg = new Paint();
                        rowBg.setColor(Color.parseColor("#F1F5F9"));
                        canvas.drawRect(20, y - 10, pageWidth - 20, y + 8, rowBg);
                    }

                    canvas.drawText(String.valueOf(sms.smsSerial), 25, y, textPaint);
                    canvas.drawText(trunc(sms.getMpesaTransId(), 12), 80, y, textPaint);
                    canvas.drawText(trunc(sms.sender, 14), 170, y, textPaint);
                    canvas.drawText(String.format(Locale.US, "%.2f", sms.getExtractedAmount()), 270, y, textPaint);
                    canvas.drawText(trunc(sms.message, 32), 340, y, textPaint);

                    y += 18;
                    rowCount++;
                }

                pdfDocument.finishPage(page);

                FileOutputStream fos = new FileOutputStream(pdfFile);
                pdfDocument.writeTo(fos);
                pdfDocument.close();
                fos.close();

                shareFile(context, pdfFile, "application/pdf", "Export Node SMS PDF Report");
            } catch (Exception e) {
                Log.e(TAG, "Error exporting Node SMS PDF", e);
                showToast(context, "Export PDF failed: " + e.getLocalizedMessage());
            }
        }).start();
    }

    private static void shareFile(Context context, File file, String mimeType, String chooserTitle) {
        Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_STREAM, contentUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent chooser = Intent.createChooser(intent, chooserTitle);
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(chooser);
    }

    private static String escapeCsv(String str) {
        if (str == null) return "";
        String clean = str.replace("\"", "\"\"");
        if (clean.contains(",") || clean.contains("\n") || clean.contains("\"")) {
            return "\"" + clean + "\"";
        }
        return clean;
    }

    private static String trunc(String str, int maxLen) {
        if (str == null) return "-";
        String clean = str.trim().replaceAll("\\s+", " ");
        if (clean.length() <= maxLen) return clean;
        return clean.substring(0, maxLen - 1) + "…";
    }

    private static void showToast(Context context, String msg) {
        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show());
    }
}
