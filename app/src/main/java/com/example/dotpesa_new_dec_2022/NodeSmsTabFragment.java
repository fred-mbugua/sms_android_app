package com.example.dotpesa_new_dec_2022;

import android.app.DatePickerDialog;
import android.content.Context;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.NodeSmsAdapter;
import com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.database.DBHelper;
import com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.database.NodeSmsModel;
import com.example.dotpesa_new_dec_2022.core.App;
import com.example.dotpesa_new_dec_2022.utilities.NodeSmsFetcher;
import com.example.dotpesa_new_dec_2022.utilities.TransactionExporter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import dotpesa_new_dec_2022.R;

public class NodeSmsTabFragment extends Fragment {

    public static NodeSmsTabFragment instance;

    private RecyclerView rvNodeSms;
    private NodeSmsAdapter adapter;
    private final List<NodeSmsModel> smsList = new ArrayList<>();

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

    public NodeSmsTabFragment() {
    }

    public static NodeSmsTabFragment getInstance() {
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
        View v = inflater.inflate(R.layout.fragment_tab_node_sms, container, false);

        rvNodeSms = v.findViewById(R.id.nodeSmsRecyclerView);
        searchEditText = v.findViewById(R.id.node_sms_search_edit_text);
        clearSearchBtn = v.findViewById(R.id.node_sms_search_clear_btn);
        totalCountTxt = v.findViewById(R.id.node_sms_total_count_txt);
        totalAmountTxt = v.findViewById(R.id.node_sms_total_extracted_amount_txt);
        emptyStateView = v.findViewById(R.id.nodeSmsEmptyState);
        fabRefresh = v.findViewById(R.id.node_sms_fab_refresh);

        btnFromDate = v.findViewById(R.id.node_sms_btn_from_date);
        btnToDate = v.findViewById(R.id.node_sms_btn_to_date);
        btnClearDate = v.findViewById(R.id.node_sms_btn_clear_date);

        Button btnExportExcel = v.findViewById(R.id.node_sms_btn_export_excel);
        Button btnExportPdf = v.findViewById(R.id.node_sms_btn_export_pdf);

        btnFromDate.setOnClickListener(v1 -> pickDate(true));
        btnToDate.setOnClickListener(v1 -> pickDate(false));
        btnClearDate.setOnClickListener(v1 -> resetDateFilter());

        btnExportExcel.setOnClickListener(v1 -> TransactionExporter.exportNodeSmsToExcel(getContext(), smsList));
        btnExportPdf.setOnClickListener(v1 -> TransactionExporter.exportNodeSmsToPdf(getContext(), smsList));

        Context ctx = getContext() != null ? getContext() : App.getInstance();
        rvNodeSms.setLayoutManager(new LinearLayoutManager(ctx));
        rvNodeSms.setItemAnimator(new DefaultItemAnimator());

        adapter = new NodeSmsAdapter(ctx, smsList);
        rvNodeSms.setAdapter(adapter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                clearSearchBtn.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                loadSmsMessages(query);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        clearSearchBtn.setOnClickListener(v1 -> searchEditText.setText(""));
        fabRefresh.setOnClickListener(v1 -> fetchFromApi());

        loadSmsMessages("");
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
            loadSmsMessages(searchEditText.getText().toString().trim());
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void resetDateFilter() {
        selectedFromDate = "";
        selectedToDate = "";
        btnFromDate.setText("From: Select");
        btnToDate.setText("To: Select");
        loadSmsMessages(searchEditText.getText().toString().trim());
    }

    public void loadSmsMessages(String query) {
        Context context = getContext() != null ? getContext() : App.getInstance();
        if (context == null) return;

        DBHelper db = new DBHelper(context);
        List<NodeSmsModel> fetched = db.getAllPushedSmsTransactions(query, selectedFromDate, selectedToDate);

        smsList.clear();
        double sum = 0.0;
        if (fetched != null) {
            smsList.addAll(fetched);
            for (NodeSmsModel sms : fetched) {
                sum += sms.getExtractedAmount();
            }
        }

        if (!smsList.isEmpty()) {
            rvNodeSms.setVisibility(View.VISIBLE);
            emptyStateView.setVisibility(View.GONE);
        } else {
            rvNodeSms.setVisibility(View.GONE);
            emptyStateView.setVisibility(View.VISIBLE);
        }
        adapter.updateData(smsList);

        totalCountTxt.setText(String.format(Locale.getDefault(), "%d SMS Messages", smsList.size()));
        totalAmountTxt.setText(String.format(Locale.getDefault(), "Total: KES %.2f", sum));
    }

    public void fetchFromApi() {
        Context context = getContext() != null ? getContext() : App.getInstance();
        if (context == null) return;

        NodeSmsFetcher.fetchSmsTransactions(context, (success, count, message) -> {
            if (isAdded() && getActivity() != null) {
                loadSmsMessages(searchEditText.getText().toString().trim());
                if (!success && !message.isEmpty()) {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
