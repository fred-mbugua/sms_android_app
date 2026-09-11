package com.example.dotpesa_new_dec_2022;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputLayout;

import dotpesa_new_dec_2022.R;

@SuppressWarnings("ALL")
public class FragmentSettingsAuth extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_settings_auth, container, false);
        SettingsFragment settingsFragment = new SettingsFragment();

        Button btnsettingsauthpage = (Button) v.findViewById(R.id.settingsauthbtn);
        final TextInputLayout password_settings_auth = (TextInputLayout) v.findViewById(R.id.password_settingsauth);

        btnsettingsauthpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.container, settingsFragment);
////                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();


                if(password_settings_auth.getEditText().getText().toString().equals("admin123")){


                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, settingsFragment);
                    fragmentTransaction.commit();
                }else{
                    //incorrect
                }

            }
        });
        return v;

    }
}