package com.example.dotpesa_new_dec_2022;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dotpesa_new_dec_2022.utilities.NodeC2BFetcher;
import com.example.dotpesa_new_dec_2022.utilities.NodeSmsFetcher;
import com.example.dotpesa_new_dec_2022.utilities.NodeSmsSyncQueue;

import java.util.ArrayList;
import java.util.List;

import dotpesa_new_dec_2022.R;

public class FragmentNodeConfig extends Fragment {

    public static View v;
    private Spinner protocolSpinner;
    private EditText hostIpEditText;
    private EditText portNumberEditText;
    private EditText endpointPathEditText;
    private EditText smsFetchEndpointPathEditText;
    private EditText c2bEndpointPathEditText;
    private EditText apiKeyEditText;
    private CheckBox enableSyncCheckBox;
    private CheckBox enableC2BCheckBox;
    private Button buttonSave;
    private Button buttonTestSync;

    public FragmentNodeConfig() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.settings_node_fragment, container, false);

        protocolSpinner = v.findViewById(R.id.node_protocol_spinner);
        hostIpEditText = v.findViewById(R.id.node_host_ip);
        portNumberEditText = v.findViewById(R.id.node_port_number);
        endpointPathEditText = v.findViewById(R.id.node_endpoint_path);
        smsFetchEndpointPathEditText = v.findViewById(R.id.node_sms_fetch_endpoint_path);
        c2bEndpointPathEditText = v.findViewById(R.id.node_c2b_endpoint_path);
        apiKeyEditText = v.findViewById(R.id.node_api_key);
        enableSyncCheckBox = v.findViewById(R.id.node_enable_sync_checkbox);
        enableC2BCheckBox = v.findViewById(R.id.node_enable_c2b_checkbox);
        buttonSave = v.findViewById(R.id.node_save_btn);
        buttonTestSync = v.findViewById(R.id.node_test_sync_btn);

        List<String> protocolList = new ArrayList<>();
        protocolList.add("http");
        protocolList.add("https");

        ArrayAdapter<String> protocolAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, protocolList);
        protocolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        protocolSpinner.setAdapter(protocolAdapter);

        SharedPreferences prefs = requireActivity().getSharedPreferences(MainActivity.DOTPESA_SHARED_PREFERENCES, Context.MODE_PRIVATE);

        String savedProtocol = prefs.getString("node_api_protocol", "http");
        String savedHost = prefs.getString("node_api_host", "192.168.1.100");
        String savedPort = prefs.getString("node_api_port", "3000");
        String savedEndpoint = prefs.getString("node_api_endpoint", "api/v1/sms/receive");
        String savedSmsFetchEndpoint = prefs.getString("node_api_sms_fetch_endpoint", "api/v1/sms/all");
        String savedC2BEndpoint = prefs.getString("node_api_transactions_endpoint", "transactions");
        String savedApiKey = prefs.getString("node_api_key", "");
        boolean isSyncEnabled = prefs.getBoolean("node_api_enabled", false);
        boolean isC2BEnabled = prefs.getBoolean("node_api_c2b_enabled", false);

        if ("https".equalsIgnoreCase(savedProtocol)) {
            protocolSpinner.setSelection(1);
        } else {
            protocolSpinner.setSelection(0);
        }

        hostIpEditText.setText(savedHost);
        portNumberEditText.setText(savedPort);
        endpointPathEditText.setText(savedEndpoint);
        smsFetchEndpointPathEditText.setText(savedSmsFetchEndpoint);
        c2bEndpointPathEditText.setText(savedC2BEndpoint);
        apiKeyEditText.setText(savedApiKey);
        enableSyncCheckBox.setChecked(isSyncEnabled);
        enableC2BCheckBox.setChecked(isC2BEnabled);

        buttonSave.setOnClickListener(v -> saveSettings());
        buttonTestSync.setOnClickListener(v -> testAndSyncNow());

        return v;
    }

    private void saveSettings() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(MainActivity.DOTPESA_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String selectedProtocol = protocolSpinner.getSelectedItem() != null ? protocolSpinner.getSelectedItem().toString() : "http";
        String host = hostIpEditText.getText().toString().trim();
        String port = portNumberEditText.getText().toString().trim();
        String endpoint = endpointPathEditText.getText().toString().trim();
        String smsFetchEndpoint = smsFetchEndpointPathEditText.getText().toString().trim();
        String c2bEndpoint = c2bEndpointPathEditText.getText().toString().trim();
        String apiKey = apiKeyEditText.getText().toString().trim();
        boolean enableSync = enableSyncCheckBox.isChecked();
        boolean enableC2B = enableC2BCheckBox.isChecked();

        editor.putString("node_api_protocol", selectedProtocol);
        editor.putString("node_api_host", host);
        editor.putString("node_api_port", port);
        editor.putString("node_api_endpoint", endpoint);
        editor.putString("node_api_sms_fetch_endpoint", smsFetchEndpoint);
        editor.putString("node_api_transactions_endpoint", c2bEndpoint);
        editor.putString("node_api_key", apiKey);
        editor.putBoolean("node_api_enabled", enableSync);
        editor.putBoolean("node_api_c2b_enabled", enableC2B);
        editor.apply();

        Toast.makeText(getActivity(), "Node.js API Configuration Saved", Toast.LENGTH_SHORT).show();

        if (enableSync) {
            NodeSmsSyncQueue.dispatchUnsyncedMessages(getActivity());
            NodeSmsFetcher.fetchSmsTransactions(getActivity(), null);
        }
        if (enableC2B) {
            NodeC2BFetcher.fetchC2BTransactions(getActivity(), null);
        }
    }

    private void testAndSyncNow() {
        saveSettings();

        String selectedProtocol = protocolSpinner.getSelectedItem() != null ? protocolSpinner.getSelectedItem().toString() : "http";
        String host = hostIpEditText.getText().toString().trim();
        String port = portNumberEditText.getText().toString().trim();
        String endpoint = endpointPathEditText.getText().toString().trim();
        String apiKey = apiKeyEditText.getText().toString().trim();

        if (host.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter Host IP / Domain Name", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getActivity(), "Testing connection & syncing Node.js API...", Toast.LENGTH_SHORT).show();

        NodeSmsSyncQueue.testConnection(getActivity(), selectedProtocol, host, port, endpoint, apiKey, (success, message) -> {
            if (isAdded() && getActivity() != null) {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                if (success) {
                    NodeSmsSyncQueue.dispatchUnsyncedMessages(getActivity());
                    NodeSmsFetcher.fetchSmsTransactions(getActivity(), null);
                }
            }
        });

        if (enableC2BCheckBox.isChecked()) {
            NodeC2BFetcher.fetchC2BTransactions(getActivity(), (success, count, message) -> {
                if (isAdded() && getActivity() != null) {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
