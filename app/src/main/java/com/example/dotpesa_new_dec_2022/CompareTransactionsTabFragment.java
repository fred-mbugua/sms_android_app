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

import com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.CompareTransactionAdapter;
import com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.database.C2BTransactionModel;
import com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.database.CompareTransactionModel;
import com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.database.DBHelper;
import com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.database.NodeSmsModel;
import com.example.dotpesa_new_dec_2022.core.App;
import com.example.dotpesa_new_dec_2022.utilities.NodeC2BFetcher;
import com.example.dotpesa_new_dec_2022.utilities.NodeSmsFetcher;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import dotpesa_new_dec_2022.R;

public class CompareTransactionsTabFragment extends Fragment {

    public static CompareTransactionsTabFragment instance;

    private RecyclerView rvCompare;
    private CompareTransactionAdapter adapter;
    private final List<CompareTransactionModel> compareList = new ArrayList<>();

    private EditText searchEditText;
    private ImageView clearSearchBtn;
    private TextView matchedCountTxt;
    private TextView mismatchCountTxt;
    private TextView c2bOnlyCountTxt;
    private TextView smsOnlyCountTxt;
    private LinearLayout emptyStateView;
    private FloatingActionButton fabRefresh;

    private Button btnFromDate;
    private Button btnToDate;
    private Button btnClearDate;

    private String selectedFromDate = "";
    private String selectedToDate = "";

    public CompareTransactionsTabFragment() {
    }

    public static CompareTransactionsTabFragment getInstance() {
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
        View v = inflater.inflate(R.layout.fragment_tab_compare_transactions, container, false);

        rvCompare = v.findViewById(R.id.compareRecyclerView);
        searchEditText = v.findViewById(R.id.compare_search_edit_text);
        clearSearchBtn = v.findViewById(R.id.compare_search_clear_btn);

        matchedCountTxt = v.findViewById(R.id.compare_matched_count_txt);
        mismatchCountTxt = v.findViewById(R.id.compare_mismatch_count_txt);
        c2bOnlyCountTxt = v.findViewById(R.id.compare_c2b_only_count_txt);
        smsOnlyCountTxt = v.findViewById(R.id.compare_sms_only_count_txt);

        emptyStateView = v.findViewById(R.id.compareEmptyState);
        fabRefresh = v.findViewById(R.id.compare_fab_refresh);

        btnFromDate = v.findViewById(R.id.compare_btn_from_date);
        btnToDate = v.findViewById(R.id.compare_btn_to_date);
        btnClearDate = v.findViewById(R.id.compare_btn_clear_date);

        btnFromDate.setOnClickListener(v1 -> pickDate(true));
        btnToDate.setOnClickListener(v1 -> pickDate(false));
        btnClearDate.setOnClickListener(v1 -> resetDateFilter());

        Context ctx = getContext() != null ? getContext() : App.getInstance();
        rvCompare.setLayoutManager(new LinearLayoutManager(ctx));
        rvCompare.setItemAnimator(new DefaultItemAnimator());

        adapter = new CompareTransactionAdapter(ctx, compareList);
        rvCompare.setAdapter(adapter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                clearSearchBtn.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                loadComparisonData(query);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        clearSearchBtn.setOnClickListener(v1 -> searchEditText.setText(""));
        fabRefresh.setOnClickListener(v1 -> refreshAllApiData());

        loadComparisonData("");
        refreshAllApiData();

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
            loadComparisonData(searchEditText.getText().toString().trim());
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void resetDateFilter() {
        selectedFromDate = "";
        selectedToDate = "";
        btnFromDate.setText("From: Select");
        btnToDate.setText("To: Select");
        loadComparisonData(searchEditText.getText().toString().trim());
    }

    public void loadComparisonData(String searchQuery) {
        Context context = getContext() != null ? getContext() : App.getInstance();
        if (context == null) return;

        DBHelper db = new DBHelper(context);
        List<C2BTransactionModel> c2bAll = db.getAllC2BTransactions("", selectedFromDate, selectedToDate);
        List<NodeSmsModel> smsAll = db.getAllPushedSmsTransactions("", selectedFromDate, selectedToDate);

        Map<String, C2BTransactionModel> c2bMap = new HashMap<>();
        if (c2bAll != null) {
            for (C2BTransactionModel c2b : c2bAll) {
                if (c2b.transId != null && !c2b.transId.trim().isEmpty()) {
                    c2bMap.put(c2b.transId.trim().toUpperCase(), c2b);
                }
            }
        }

        Map<String, NodeSmsModel> smsMap = new HashMap<>();
        if (smsAll != null) {
            for (NodeSmsModel sms : smsAll) {
                String code = sms.getMpesaTransId();
                if (!code.isEmpty()) {
                    smsMap.put(code.toUpperCase(), sms);
                }
            }
        }

        Set<String> allCodes = new HashSet<>();
        allCodes.addAll(c2bMap.keySet());
        allCodes.addAll(smsMap.keySet());

        List<CompareTransactionModel> fullComparisonList = new ArrayList<>();
        int matchedCount = 0;
        int mismatchCount = 0;
        int c2bOnlyCount = 0;
        int smsOnlyCount = 0;

        for (String code : allCodes) {
            C2BTransactionModel c2b = c2bMap.get(code);
            NodeSmsModel sms = smsMap.get(code);

            CompareTransactionModel.MatchStatus status;
            if (c2b != null && sms != null) {
                double smsAmount = sms.getExtractedAmount();
                if (smsAmount > 0.0 && Math.abs(c2b.amount - smsAmount) > 0.01) {
                    status = CompareTransactionModel.MatchStatus.AMOUNT_MISMATCH;
                    mismatchCount++;
                } else {
                    status = CompareTransactionModel.MatchStatus.MATCHED;
                    matchedCount++;
                }
            } else if (c2b != null) {
                status = CompareTransactionModel.MatchStatus.C2B_ONLY;
                c2bOnlyCount++;
            } else {
                status = CompareTransactionModel.MatchStatus.SMS_ONLY;
                smsOnlyCount++;
            }

            fullComparisonList.add(new CompareTransactionModel(code, status, c2b, sms));
        }

        compareList.clear();
        String query = searchQuery != null ? searchQuery.trim().toLowerCase() : "";

        if (!query.isEmpty()) {
            for (CompareTransactionModel item : fullComparisonList) {
                boolean matchesCode = item.transCode.toLowerCase().contains(query);
                boolean matchesC2bName = item.c2bRecord != null && item.c2bRecord.getFullName().toLowerCase().contains(query);
                boolean matchesC2bPhone = item.c2bRecord != null && item.c2bRecord.msisdn != null && item.c2bRecord.msisdn.toLowerCase().contains(query);
                boolean matchesSmsSender = item.smsRecord != null && item.smsRecord.sender != null && item.smsRecord.sender.toLowerCase().contains(query);
                boolean matchesSmsMsg = item.smsRecord != null && item.smsRecord.message != null && item.smsRecord.message.toLowerCase().contains(query);

                if (matchesCode || matchesC2bName || matchesC2bPhone || matchesSmsSender || matchesSmsMsg) {
                    compareList.add(item);
                }
            }
        } else {
            compareList.addAll(fullComparisonList);
        }

        if (!compareList.isEmpty()) {
            rvCompare.setVisibility(View.VISIBLE);
            emptyStateView.setVisibility(View.GONE);
        } else {
            rvCompare.setVisibility(View.GONE);
            emptyStateView.setVisibility(View.VISIBLE);
        }
        adapter.updateData(compareList);

        matchedCountTxt.setText(String.format(Locale.getDefault(), "Matched: %d", matchedCount));
        mismatchCountTxt.setText(String.format(Locale.getDefault(), "Mismatched: %d", mismatchCount));
        c2bOnlyCountTxt.setText(String.format(Locale.getDefault(), "Cloud C2B Only: %d", c2bOnlyCount));
        smsOnlyCountTxt.setText(String.format(Locale.getDefault(), "Node SMS Only: %d", smsOnlyCount));
    }

    public void refreshAllApiData() {
        Context context = getContext() != null ? getContext() : App.getInstance();
        if (context == null) return;

        NodeC2BFetcher.fetchC2BTransactions(context, null);
        NodeSmsFetcher.fetchSmsTransactions(context, (success, count, message) -> {
            if (isAdded() && getActivity() != null) {
                loadComparisonData(searchEditText.getText().toString().trim());
                if (!success && !message.isEmpty()) {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
