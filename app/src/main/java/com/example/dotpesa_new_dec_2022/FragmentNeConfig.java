package com.example.dotpesa_new_dec_2022;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dotpesa_new_dec_2022.utilities.MisooCenterRequestsTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dotpesa_new_dec_2022.R;

public class FragmentNeConfig extends Fragment {

    public static View v;

    public static SharedPreferences SHAREDPREFERENCES;


    //variables to get values from the network configuration page text views
    String[] lines ={"MPESA","T-KASH","EQUITEL","EQUITY"};
    public static Spinner sleep, workstations, modems, protocol, receiveFrom;
    EditText domain_ip, port_number, licences, appliance_number, url;
    CheckBox deleteOnPush, print, pushReceive, autoSending ;
    Button button_save, button_refresh;
    AutoCompleteTextView autoComplete;
    public FragmentNeConfig() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.settings_network_fragment,container, false);

        SharedPreferences.Editor editor = MainActivity.SHAREDPREFERENCES.edit();

        sleep = (Spinner) v.findViewById(R.id.sleep);
        workstations = (Spinner) v.findViewById(R.id.workstations_selector);
        modems = (Spinner) v.findViewById(R.id.modems_selector);
        protocol = (Spinner) v.findViewById(R.id.protocol_spinner);
        domain_ip = (EditText) v.findViewById(R.id.domain_ip);
        port_number = (EditText) v.findViewById(R.id.port_number);
        licences = (EditText) v.findViewById(R.id.deviceLicense);
        appliance_number = (EditText) v.findViewById(R.id.applianceNumber);
        url = (EditText) v.findViewById(R.id.pagelocation);
        deleteOnPush = (CheckBox) v.findViewById(R.id.deleteOnPushCheckbox);
        print = (CheckBox) v.findViewById(R.id.printincomingsms_checkbox);
        pushReceive = (CheckBox) v.findViewById(R.id.pushreceived_checkbox);
//        autoSending = (CheckBox) v.findViewById(R.id.send_sms);
        button_save = (Button) v.findViewById(R.id.saveconfigurationsbtn);
        button_refresh = (Button) v.findViewById(R.id.refreshconfigurationsbtn);
        autoComplete =  (AutoCompleteTextView) v.findViewById(R.id.specifyprintpushlimit);


        List<String> sleep_list = new ArrayList<String>();
        sleep_list.add(MainActivity.APPLICATION_LOCAL_MISOO_PORTAL_REFRESH_RATE);

        List<String> receive_From_list = new ArrayList<String>();
        receive_From_list.add("receive_From 1");
        receive_From_list.add("receive_From 2");
        receive_From_list.add(MainActivity.APPLICATION_LOCAL_MISOO_LIMIT_PUSH_ONLY_RECEIVED_FROM);

        List<String> protocol_array = new ArrayList<String>();
        protocol_array.add(MainActivity.APPLICATION_LOCAL_MISOO_PORTAL_HTTP_PROTOCOL);


        List<String> modems_array = new ArrayList<String>();
        modems_array.add(MainActivity.APPLICATION_LOCAL_MISOO_PORTAL_MODEM_NAME);


        List<String> workstations_array = new ArrayList<String>();
        workstations_array.add(MainActivity.APPLICATION_LOCAL_MISOO_PORTAL_WORKSTATION);


        //protocol Array
        ArrayAdapter<String> sel_protocol = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, protocol_array);
        sel_protocol.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        protocol.setAdapter(sel_protocol);


        //sel_modem Array
        ArrayAdapter<String> sel_modem = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, modems_array);
        sel_modem.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modems.setAdapter(sel_modem);


        //sel_work Array
        ArrayAdapter<String> sel_work = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, workstations_array);
        sel_work.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        workstations.setAdapter(sel_work);

        // sel_sleep Array
        ArrayAdapter<String> sel_sleep = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, sleep_list);
        sel_sleep.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sleep.setAdapter(sel_sleep);


        ArrayAdapter<String> sel_receive_From = new ArrayAdapter<String>(getActivity() ,android.R.layout.select_dialog_item, lines);
        //Getting the instance of AutoCompleteTextView
        autoComplete.setThreshold(1);//will start working from first character
        autoComplete.setAdapter(sel_receive_From);//setting the adapter data into the AutoCompleteTextView


        //set values
        deleteOnPush.setChecked(Boolean.valueOf(MainActivity.SHAREDPREFERENCES.getString("local_misoo_portal_push_delete", "")));
        print.setChecked(Boolean.valueOf(MainActivity.SHAREDPREFERENCES.getString("local_misoo_portal_print_message_on_push", "")));
        pushReceive.setChecked(Boolean.valueOf(MainActivity.SHAREDPREFERENCES.getString("local_misoo_limit_push_received", "")));
//        autoSending.setChecked(Boolean.valueOf(MainActivity.APPLICATION_LOCAL_MISOO_AUTO_SENDING_SMS));
        domain_ip.setText(MainActivity.SHAREDPREFERENCES.getString("local_misoo_portal_host_IP", "192.168"));
        appliance_number.setText(MainActivity.SHAREDPREFERENCES.getString("local_misoo_appliance_number", "misoo"));
        licences.setText(MainActivity.SHAREDPREFERENCES.getString("local_misoo_licencesKey", "misoo"));
        url.setText(MainActivity.SHAREDPREFERENCES.getString("local_misoo_portal_page", "DotPESA/portal/mpesa/endpoint/safaricom_mpesa_endpoint.jsp"));
        port_number.setText(MainActivity.SHAREDPREFERENCES.getString("local_misoo_portal_port_number", "8080"));
        autoComplete.setText(MainActivity.SHAREDPREFERENCES.getString("local_misoo_only_recieve_from", ""));


        //pressing the save button
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String domainNameOrIP  = domain_ip.getText().toString();
                String port  = port_number.getText().toString();
                String applianceNumber = appliance_number.getText().toString();
                String licenceKey = licences.getText().toString();
                String S_url = url.getText().toString();
                String recieve = autoComplete.getText().toString();
                String protocal = protocol.getSelectedItem().toString();
                String modem = modems.getSelectedItem().toString();
                String work = workstations.getSelectedItem().toString();
                String sleeps = sleep.getSelectedItem().toString();
                //shared preferences will store all checkbox state
                editor.putString("local_misoo_portal_push_delete",String.valueOf(deleteOnPush.isChecked()));
                editor.putString("local_misoo_portal_print_message_on_push",String.valueOf(print.isChecked()));
                editor.putString("local_misoo_limit_push_received",String.valueOf(pushReceive.isChecked()));
                editor.putString("local_misoo_portal_host_IP", domainNameOrIP);
                editor.putString("local_misoo_portal_port_number", port);
                editor.putString("local_misoo_appliance_number", applianceNumber);
                editor.putString("local_misoo_licencesKey", licenceKey);
                editor.putString("local_misoo_portal_page", S_url);
                editor.putString("local_misoo_only_recieve_from", recieve);
                editor.putString("local_misoo_portal_protocol", protocal);
                editor.putString("local_misoo_portal_modem_name", modem);
                editor.putString("local_misoo_portal_workstation", work);
                editor.putString("local_misoo_portal_sleep_time", sleeps);
                editor.commit();


                MainActivity.APPLICATION_LOCAL_MISOO_PORTAL_HTTP_HOST_NAME = MainActivity.SHAREDPREFERENCES.getString("local_misoo_portal_host_IP", "");
                MainActivity.APPLICATION_LOCAL_MISOO_PORTAL_DELETE_ON_PUSH = Boolean.valueOf(MainActivity.SHAREDPREFERENCES.getString("local_misoo_portal_push_delete", ""));
                MainActivity.APPLICATION_LOCAL_MISOO_AUTO_SENDING_SMS = Boolean.valueOf(MainActivity.SHAREDPREFERENCES.getString("local_misoo_auto_send_sms", ""));
                MainActivity.APPLICATION_LOCAL_MISOO_PRINT_MESSAGE_ON_PUSH = Boolean.valueOf(MainActivity.SHAREDPREFERENCES.getString("local_misoo_portal_print_message_on_push", ""));
                MainActivity.APPLICATION_LOCAL_MISOO_LIMIT_PUSH_RECEIVED = Boolean.valueOf(MainActivity.SHAREDPREFERENCES.getString("local_misoo_limit_push_received", ""));
                MainActivity.APPLICATION_LOCAL_MISOO_PORTAL_HTTP_HOST_PORT = MainActivity.SHAREDPREFERENCES.getString("local_misoo_portal_port_number", "");
                MainActivity.DEVICE_APPLIANCE_NUMBER = MainActivity.SHAREDPREFERENCES.getString("local_misoo_appliance_number", "");
                MainActivity.DEVICE_APPLIANCE_API_KEY = MainActivity.SHAREDPREFERENCES.getString("local_misoo_licencesKey", "");
                MainActivity.APPLICATION_LOCAL_MISOO_PORTAL_PAGE = MainActivity.SHAREDPREFERENCES.getString("local_misoo_portal_page", "");
                MainActivity.APPLICATION_LOCAL_MISOO_ONLY_RECIEVE_FROM = MainActivity.SHAREDPREFERENCES.getString("local_misoo_only_recieve_from", "");
                MainActivity.APPLICATION_LOCAL_MISOO_PORTAL_MODEM_NAME = MainActivity.SHAREDPREFERENCES.getString("local_misoo_portal_modem_name", "");
                MainActivity.APPLICATION_LOCAL_MISOO_PORTAL_WORKSTATION = MainActivity.SHAREDPREFERENCES.getString("local_misoo_portal_workstation", "");
                MainActivity.APPLICATION_LOCAL_MISOO_PORTAL_REFRESH_RATE = MainActivity.SHAREDPREFERENCES.getString("local_misoo_portal_sleep_time", "");
                MainActivity.APPLICATION_LOCAL_MISOO_PORTAL_HTTP_PROTOCOL = MainActivity.SHAREDPREFERENCES.getString("local_misoo_portal_protocol", "");

                Toast.makeText(getActivity(),"Network Settings Saved",Toast.LENGTH_SHORT).show();
            }
        });

        button_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> hashmap = new HashMap<String, String>();
                MisooCenterRequestsTool post = new MisooCenterRequestsTool();
                post.onAttach(getActivity());


                //protocol Array
                List<String> protocol_array = new ArrayList<String>();
                protocol_array.add("http");
                protocol_array.add("https");
                //sel_protocol Array
                ArrayAdapter<String> sel_protocol = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, protocol_array);
                sel_protocol.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                protocol.setAdapter(sel_protocol);


                //modems array
                List<String> modems_array = new ArrayList<String>();
                modems_array.add("Refresh From POS");
                //sel_modem Array
                ArrayAdapter<String> sel_modem = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, modems_array);
                sel_modem.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                modems.setAdapter(sel_modem);

                //work stations array
                List<String> workstations_array = new ArrayList<String>();
                workstations_array.add("Refresh From POS");
                //sel_work Array
                ArrayAdapter<String> sel_work = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, workstations_array);
                sel_work.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                workstations.setAdapter(sel_work);

                //sleep Array
                List<String> sleep_list = new ArrayList<String>();
                sleep_list.add("1");
                sleep_list.add("2");
                sleep_list.add("3");
                sleep_list.add("4");
                sleep_list.add("5");
                // sel_sleep Array
                ArrayAdapter<String> sel_sleep = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, sleep_list);
                sel_sleep.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sleep.setAdapter(sel_sleep);

                String workStationsURLPage = "DotPESA/settings/get_active_workstations.jsp?action=list&apiKey="+ MainActivity.DEVICE_APPLIANCE_API_KEY+"&applianceNumber="+ MainActivity.DEVICE_APPLIANCE_NUMBER.replaceAll(" ","+")+"";
                post.getWorkstationAttempt(workStationsURLPage, hashmap, FragmentNeConfig.this);

                String modemsURLPage = "DotPESA/settings/get_enabled_modems.jsp?action=list&apiKey="+ MainActivity.DEVICE_APPLIANCE_API_KEY+"&applianceNumber="+ MainActivity.DEVICE_APPLIANCE_NUMBER.replaceAll(" ","+")+"";
                post.getModemsAttempt(modemsURLPage, hashmap,FragmentNeConfig.this);

            }
        });

        protocol.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selectedText = (String)protocol.getSelectedItem();
                editor.putString("local_misoo_portal_protocol", selectedText);
                editor.commit();

                MainActivity.APPLICATION_LOCAL_MISOO_PORTAL_HTTP_PROTOCOL = MainActivity.SHAREDPREFERENCES.getString("local_misoo_portal_protocol", "");
            }
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        workstations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selectedText = (String)workstations.getSelectedItem();
                editor.putString("local_misoo_portal_workstation", selectedText);
                editor.commit();

                MainActivity.APPLICATION_LOCAL_MISOO_PORTAL_WORKSTATION = MainActivity.SHAREDPREFERENCES.getString("local_misoo_portal_workstation", "");
            }
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        modems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selectedText = (String)modems.getSelectedItem();
                editor.putString("local_misoo_portal_modem_name", selectedText);
                editor.commit();

                MainActivity.APPLICATION_LOCAL_MISOO_PORTAL_MODEM_NAME = MainActivity.SHAREDPREFERENCES.getString("local_misoo_portal_modem_name", "");
            }
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        return v;
    }

    public void populateModems(ArrayAdapter<String> modemsAddapator){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                modemsAddapator.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                modems.setAdapter(modemsAddapator);
            }
        });
    }

    public void populateWorkStations(ArrayAdapter<String> wsAddapator){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                wsAddapator.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                workstations.setAdapter(wsAddapator);
            }
        });
    }
}
