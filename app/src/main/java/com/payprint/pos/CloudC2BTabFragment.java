package com.payprint.pos;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.payprint.pos.call_sms_db_modules.recyclerviewadapter.C2BTransactionAdapter;
import com.payprint.pos.call_sms_db_modules.recyclerviewadapter.database.C2BTransactionModel;
import com.payprint.pos.call_sms_db_modules.recyclerviewadapter.database.DBHelper;
import com.payprint.pos.core.App;
import com.payprint.pos.utilities.NetworkDialogHelper;
import com.payprint.pos.utilities.NodeC2BFetcher;
import com.payprint.pos.utilities.TransactionExporter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.payprint.pos.R;

public class CloudC2BTabFragment extends Fragment {

    public static CloudC2BTabFragment instance;

    private RecyclerView rvC2B;
    private C2BTransactionAdapter adapter;
    private final List<C2BTransactionModel> transactionList = new ArrayList<>();

    private EditText searchEditText;
    private ImageView clearSearchBtn;
    private TextView totalCountTxt;
    private TextView totalAmountTxt;
    private LinearLayout emptyStateView;
    private FloatingActionButton fabRefresh;

    private Button btnFromDate;
    private Button btnToDate;
    private Button btnClearDate;

    private String selectedFromDate = "";
    private String selectedToDate = "";

    public CloudC2BTabFragment() {
    }

    public static CloudC2BTabFragment getInstance() {
        return instance;
    }

    @Override
    public void onStart() {
        super.onStart();
        instance = this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tab_cloud_c2b, container, false);

        rvC2B = v.findViewById(R.id.cloudC2bRecyclerView);
        searchEditText = v.findViewById(R.id.cloud_c2b_search_edit_text);
        clearSearchBtn = v.findViewById(R.id.cloud_c2b_search_clear_btn);
        totalCountTxt = v.findViewById(R.id.cloud_c2b_total_count_txt);
        totalAmountTxt = v.findViewById(R.id.cloud_c2b_total_amount_txt);
        emptyStateView = v.findViewById(R.id.cloudC2bEmptyState);
        fabRefresh = v.findViewById(R.id.cloud_c2b_fab_refresh);

        btnFromDate = v.findViewById(R.id.cloud_c2b_btn_from_date);
        btnToDate = v.findViewById(R.id.cloud_c2b_btn_to_date);
        btnClearDate = v.findViewById(R.id.cloud_c2b_btn_clear_date);

        Button btnExportExcel = v.findViewById(R.id.cloud_c2b_btn_export_excel);
        Button btnExportPdf = v.findViewById(R.id.cloud_c2b_btn_export_pdf);

        btnFromDate.setOnClickListener(v1 -> pickDate(true));
        btnToDate.setOnClickListener(v1 -> pickDate(false));
        btnClearDate.setOnClickListener(v1 -> resetDateFilter());

        btnExportExcel.setOnClickListener(v1 -> TransactionExporter.exportC2BToExcel(getContext(), transactionList));
        btnExportPdf.setOnClickListener(v1 -> TransactionExporter.exportC2BToPdf(getContext(), transactionList));

        Context ctx = getContext() != null ? getContext() : App.getInstance();
        rvC2B.setLayoutManager(new LinearLayoutManager(ctx));
        rvC2B.setItemAnimator(new DefaultItemAnimator());

        adapter = new C2BTransactionAdapter(ctx, transactionList);
        rvC2B.setAdapter(adapter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                clearSearchBtn.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                loadTransactions(query);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        clearSearchBtn.setOnClickListener(v1 -> searchEditText.setText(""));
        fabRefresh.setOnClickListener(v1 -> fetchFromApi());

        loadTransactions("");
        fetchFromApi();

        return v;
    }

    private void pickDate(boolean isFromDate) {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            String formattedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            if (isFromDate) {
                selectedFromDate = formattedDate;
                btnFromDate.setText("From: " + formattedDate);
            } else {
                selectedToDate = formattedDate;
                btnToDate.setText("To: " + formattedDate);
            }
            loadTransactions(searchEditText.getText().toString().trim());
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void resetDateFilter() {
        selectedFromDate = "";
        selectedToDate = "";
        btnFromDate.setText("From: Select");
        btnToDate.setText("To: Select");
        loadTransactions(searchEditText.getText().toString().trim());
    }

    public void loadTransactions(String query) {
        Context context = getContext() != null ? getContext() : App.getInstance();
        if (context == null) return;

        DBHelper db = new DBHelper(context);
        List<C2BTransactionModel> fetched = db.getAllC2BTransactions(query, selectedFromDate, selectedToDate);

        transactionList.clear();
        double sum = 0.0;
        if (fetched != null) {
            transactionList.addAll(fetched);
            for (C2BTransactionModel tx : fetched) {
                sum += tx.amount;
            }
        }

        if (!transactionList.isEmpty()) {
            rvC2B.setVisibility(View.VISIBLE);
            emptyStateView.setVisibility(View.GONE);
        } else {
            rvC2B.setVisibility(View.GONE);
            emptyStateView.setVisibility(View.VISIBLE);
        }
        adapter.updateData(transactionList);

        totalCountTxt.setText(String.format(Locale.getDefault(), "%d C2B Transactions", transactionList.size()));
        totalAmountTxt.setText(String.format(Locale.getDefault(), "Total: KES %.2f", sum));
    }

    public void fetchFromApi() {
        Context context = getContext() != null ? getContext() : App.getInstance();
        if (context == null) return;

        if (!NetworkDialogHelper.isNetworkConnected(context)) {
            if (getActivity() != null) {
                NetworkDialogHelper.showOfflineDialog(getActivity(), this::fetchFromApi);
            }
            return;
        }

        NodeC2BFetcher.fetchC2BTransactions(context, (success, count, message) -> {
            if (isAdded() && getActivity() != null) {
                loadTransactions(searchEditText.getText().toString().trim());
                if (!success) {
                    SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.PAYPRINT_SHARED_PREFERENCES, Context.MODE_PRIVATE);
                    String protocol = prefs.getString("node_api_protocol", "http");
                    String host = prefs.getString("node_api_host", "");
                    String port = prefs.getString("node_api_port", "");
                    String endpoint = prefs.getString("node_api_transactions_endpoint", "transactions");
                    String serverUrl = protocol + "://" + host + (port.isEmpty() ? "" : ":" + port) + "/" + endpoint;

                    NetworkDialogHelper.showNodeServerUnreachableDialog(getActivity(), serverUrl, message, this::fetchFromApi);
                } else if (count > 0) {
                    NetworkDialogHelper.showSuccessDialog(getActivity(), "Payments Synced", "Successfully fetched " + count + " new C2B transactions from server.");
                }
            }
        });
    }
}
