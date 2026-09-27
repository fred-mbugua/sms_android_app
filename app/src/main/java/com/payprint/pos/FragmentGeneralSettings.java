package com.payprint.pos;

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

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.payprint.pos.utilities.SmsFilterManager;
import com.payprint.pos.utilities.SmsInboxImporter;

import com.payprint.pos.R;

public class FragmentGeneralSettings extends Fragment {

    private CheckBox cbShowSmsInboxNav;

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

    private TextInputLayout layoutCurrentPassword;
    private TextInputLayout layoutNewPassword;
    private TextInputLayout layoutConfirmNewPassword;
    private TextInputEditText editCurrentPassword;
    private TextInputEditText editNewPassword;
    private TextInputEditText editConfirmNewPassword;
    private Button btnChangePassword;

    public FragmentGeneralSettings() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings_general_fragment, container, false);

        View btnBack = v.findViewById(R.id.btn_back_to_settings);
        if (btnBack != null) {
            btnBack.setOnClickListener(v1 -> {
                if (getParentFragmentManager() != null) {
                    getParentFragmentManager().popBackStack();
                }
            });
        }

        cbShowSmsInboxNav = v.findViewById(R.id.cbShowSmsInboxNav);

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

        layoutCurrentPassword = v.findViewById(R.id.edit_current_password_layout);
        layoutNewPassword = v.findViewById(R.id.edit_new_password_layout);
        layoutConfirmNewPassword = v.findViewById(R.id.edit_confirm_new_password_layout);

        editCurrentPassword = v.findViewById(R.id.edit_current_password);
        editNewPassword = v.findViewById(R.id.edit_new_password);
        editConfirmNewPassword = v.findViewById(R.id.edit_confirm_new_password);
        btnChangePassword = v.findViewById(R.id.btn_change_password);

        loadPreferences();

        rgFilterMode.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbFilterSelected) {
                cardProvidersContainer.setVisibility(View.VISIBLE);
            } else {
                cardProvidersContainer.setVisibility(View.GONE);
            }
        });

        btnSave.setOnClickListener(v1 -> savePreferences());

        if (btnChangePassword != null) {
            btnChangePassword.setOnClickListener(v1 -> updatePassword());
        }

        return v;
    }

    private void loadPreferences() {
        if (getActivity() == null) return;
        SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.PAYPRINT_SHARED_PREFERENCES, Context.MODE_PRIVATE);

        if (cbShowSmsInboxNav != null) {
            cbShowSmsInboxNav.setChecked(prefs.getBoolean("show_sms_inbox_tab", true));
        }

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
        SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.PAYPRINT_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        if (cbShowSmsInboxNav != null) {
            editor.putBoolean("show_sms_inbox_tab", cbShowSmsInboxNav.isChecked());
        }

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

        Toast.makeText(getActivity(), "General Settings Saved", Toast.LENGTH_SHORT).show();

        SmsInboxImporter.importDeviceSmsMessagesAsync(getActivity());
    }

    private void updatePassword() {
        if (getActivity() == null) return;
        SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.PAYPRINT_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String savedPassword = prefs.getString("settings_app_password", "");

        String curr = editCurrentPassword != null && editCurrentPassword.getText() != null ? editCurrentPassword.getText().toString().trim() : "";
        String newPwd = editNewPassword != null && editNewPassword.getText() != null ? editNewPassword.getText().toString().trim() : "";
        String confirmPwd = editConfirmNewPassword != null && editConfirmNewPassword.getText() != null ? editConfirmNewPassword.getText().toString().trim() : "";

        if (layoutCurrentPassword != null) layoutCurrentPassword.setError(null);
        if (layoutNewPassword != null) layoutNewPassword.setError(null);
        if (layoutConfirmNewPassword != null) layoutConfirmNewPassword.setError(null);

        if (!savedPassword.isEmpty() && !curr.equals(savedPassword)) {
            if (layoutCurrentPassword != null) layoutCurrentPassword.setError("Current password is incorrect");
            return;
        }

        if (newPwd.isEmpty()) {
            if (layoutNewPassword != null) layoutNewPassword.setError("New password cannot be empty");
            return;
        }

        if (newPwd.length() < 4) {
            if (layoutNewPassword != null) layoutNewPassword.setError("New password must be at least 4 characters long");
            return;
        }

        if (!newPwd.equals(confirmPwd)) {
            if (layoutConfirmNewPassword != null) layoutConfirmNewPassword.setError("Passwords do not match");
            return;
        }

        prefs.edit().putString("settings_app_password", newPwd).apply();

        if (editCurrentPassword != null) editCurrentPassword.setText("");
        if (editNewPassword != null) editNewPassword.setText("");
        if (editConfirmNewPassword != null) editConfirmNewPassword.setText("");

        Toast.makeText(getActivity(), "Settings password updated successfully!", Toast.LENGTH_SHORT).show();
    }
}
