package com.example.dotpesa_new_dec_2022;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dotpesa_new_dec_2022.utilities.MisooCenterRequestsTool;

import java.util.HashMap;

import dotpesa_new_dec_2022.R;

public class FragmentLicenseSettings extends Fragment {
    EditText Start_date, end_date ,editText_one, editText_two, editText_three, editText_four, editText_five, applianceNumberTextBox;
    Button licenceBtn,applianceKeyBtn;

    public static View v;

    public FragmentLicenseSettings() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.settings_license_fragment, container, false);

        licenceBtn = v.findViewById(R.id.licence_card_buttom);
        applianceKeyBtn = v.findViewById(R.id.appliance_card_button);
        applianceNumberTextBox = v.findViewById(R.id.appliance_number);

        licenceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String urlPage = "accounts/remote_device_get_licence.jsp?action=get&";
                String macAddress = "";

                try{
                    HashMap<String, String> hashmap = new HashMap<String, String>();
                    hashmap.put("macAddress", macAddress);
                    hashmap.put("isAndroidDevice", "yes");
                    hashmap.put("androidOsRelease", Build.VERSION.RELEASE);
                    hashmap.put("DotPESAVersion", Build.VERSION.RELEASE);
                    hashmap.put("DotPESARelease", Build.VERSION.RELEASE);

                    MisooCenterRequestsTool post = new MisooCenterRequestsTool();
                    post.onAttach(getActivity());
                    post.getLicenceAttempt(urlPage, hashmap);

                }catch(Exception ioe){
                    ioe.printStackTrace();
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        applianceKeyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String urlPage = "accounts/remote_device_get_appliance.jsp?action=get&licenceSerial="+ MainActivity.LICENCE_SERIAL+"&workStationName=DOTPESAndroid&macAddress="+ MainActivity.getMacAddress();
                String macAddress = "";

                try{
                    HashMap<String, String> hashmap = new HashMap<String, String>();
                    hashmap.put("macAddress", macAddress);

                    MisooCenterRequestsTool post = new MisooCenterRequestsTool();
                    post.onAttach(getActivity());
                    post.getApplianceAttempt(urlPage, hashmap);

                }catch(Exception ioe){
                    ioe.printStackTrace();
                }
            }
        });

        loadLicenceKey();
        loadApplianceNumber();
        return v;
    }

    public void loadLicenceKey(){
        String licenceKey =   MainActivity.LICENCE_KEY;
        String[] licencesParts = licenceKey.split("-");

        System.out.println("Licence key = "+licenceKey);
        System.out.println("Licence serial = "+ MainActivity.LICENCE_SERIAL);

        if(licencesParts.length==5) {
            editText_one = v.findViewById(R.id.licence_part_one);
            editText_two = v.findViewById(R.id.licence_part_two);
            editText_three = v.findViewById(R.id.licence_part_three);
            editText_four = v.findViewById(R.id.licence_part_four);
            editText_five = v.findViewById(R.id.licence_part_five);
            Start_date = v.findViewById(R.id.licence_Start_date);
            end_date = v.findViewById(R.id.licence_end_date);

            editText_one.setText(licencesParts[0]);
            editText_two.setText(licencesParts[1]);
            editText_three.setText(licencesParts[2]);
            editText_four.setText(licencesParts[3]);
            editText_five.setText(licencesParts[4]);
            Start_date.setText( MainActivity.LICENCE_START_DATE);
            end_date.setText( MainActivity.LICENCE_END_DATE);
            licenceBtn.setEnabled(Boolean.FALSE);
        }else{
            //display error
            Toast.makeText(getActivity(), "Licence not obtained", Toast.LENGTH_SHORT).show();
        }



    }


    public void loadApplianceNumber(){

        if(!MainActivity.DEVICE_APPLIANCE_NUMBER.isEmpty()){
            applianceNumberTextBox.setText(MainActivity.DEVICE_APPLIANCE_NUMBER);

            applianceKeyBtn.setEnabled(Boolean.FALSE);
        }else{
            //display error
            Toast.makeText(getActivity(), "Appliance not set", Toast.LENGTH_SHORT).show();
        }

    }
}
