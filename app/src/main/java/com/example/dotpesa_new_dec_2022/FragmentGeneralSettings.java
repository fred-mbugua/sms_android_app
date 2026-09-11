package com.example.dotpesa_new_dec_2022;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dotpesa_new_dec_2022.utilities.SmsFilterManager;
import com.example.dotpesa_new_dec_2022.utilities.SmsInboxImporter;
import com.google.android.material.card.MaterialCardView;

import dotpesa_new_dec_2022.R;

public class FragmentGeneralSettings extends Fragment {

    private RadioGroup rgFilterMode;
    private RadioButton rbAllowAll;
    private RadioButton rbFilterSelected;

    private MaterialCardView cardProvidersContainer;

    private CheckBox cbMpesa;
    private CheckBox cbAirtel;
    private CheckBox cbTkash;
    private CheckBox cbEquity;
    private CheckBox cbKcb;
    private CheckBox cbCoop;
    private CheckBox cbNcba;
    private CheckBox cbBanks;

    private Button btnSave;

    public FragmentGeneralSettings() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings_general_fragment, container, false);

        rgFilterMode = v.findViewById(R.id.rgSmsFilterMode);
        rbAllowAll = v.findViewById(R.id.rbAllowAll);
        rbFilterSelected = v.findViewById(R.id.rbFilterSelected);

        cardProvidersContainer = v.findViewById(R.id.cardProvidersContainer);

        cbMpesa = v.findViewById(R.id.cbFilterMpesa);
        cbAirtel = v.findViewById(R.id.cbFilterAirtel);
        cbTkash = v.findViewById(R.id.cbFilterTkash);
        cbEquity = v.findViewById(R.id.cbFilterEquity);
        cbKcb = v.findViewById(R.id.cbFilterKcb);
        cbCoop = v.findViewById(R.id.cbFilterCoop);
        cbNcba = v.findViewById(R.id.cbFilterNcba);
        cbBanks = v.findViewById(R.id.cbFilterBanks);

        btnSave = v.findViewById(R.id.btnSaveGeneralSettings);

        loadPreferences();

        rgFilterMode.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbFilterSelected) {
                cardProvidersContainer.setVisibility(View.VISIBLE);
            } else {
                cardProvidersContainer.setVisibility(View.GONE);
            }
        });

        btnSave.setOnClickListener(v1 -> savePreferences());

        return v;
    }

    private void loadPreferences() {
        if (getActivity() == null) return;
        SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.DOTPESA_SHARED_PREFERENCES, Context.MODE_PRIVATE);

        String mode = prefs.getString("sms_filter_mode", SmsFilterManager.MODE_ALL);
        if (SmsFilterManager.MODE_SELECTED.equalsIgnoreCase(mode)) {
            rbFilterSelected.setChecked(true);
            cardProvidersContainer.setVisibility(View.VISIBLE);
        } else {
            rbAllowAll.setChecked(true);
            cardProvidersContainer.setVisibility(View.GONE);
        }

        cbMpesa.setChecked(prefs.getBoolean("sms_filter_mpesa", true));
        cbAirtel.setChecked(prefs.getBoolean("sms_filter_airtel", false));
        cbTkash.setChecked(prefs.getBoolean("sms_filter_tkash", false));
        cbEquity.setChecked(prefs.getBoolean("sms_filter_equity", false));
        cbKcb.setChecked(prefs.getBoolean("sms_filter_kcb", false));
        cbCoop.setChecked(prefs.getBoolean("sms_filter_coop", false));
        cbNcba.setChecked(prefs.getBoolean("sms_filter_ncba", false));
        cbBanks.setChecked(prefs.getBoolean("sms_filter_absa_banks", false));
    }

    private void savePreferences() {
        if (getActivity() == null) return;
        SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.DOTPESA_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String selectedMode = rbFilterSelected.isChecked() ? SmsFilterManager.MODE_SELECTED : SmsFilterManager.MODE_ALL;
        editor.putString("sms_filter_mode", selectedMode);

        editor.putBoolean("sms_filter_mpesa", cbMpesa.isChecked());
        editor.putBoolean("sms_filter_airtel", cbAirtel.isChecked());
        editor.putBoolean("sms_filter_tkash", cbTkash.isChecked());
        editor.putBoolean("sms_filter_equity", cbEquity.isChecked());
        editor.putBoolean("sms_filter_kcb", cbKcb.isChecked());
        editor.putBoolean("sms_filter_coop", cbCoop.isChecked());
        editor.putBoolean("sms_filter_ncba", cbNcba.isChecked());
        editor.putBoolean("sms_filter_absa_banks", cbBanks.isChecked());

        editor.apply();

        Toast.makeText(getActivity(), "General SMS Filtering Settings Saved", Toast.LENGTH_SHORT).show();

        SmsInboxImporter.importDeviceSmsMessagesAsync(getActivity());
    }
}
