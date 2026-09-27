package com.payprint.pos;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;

import com.payprint.pos.R;

public class FragmentSettingsAuth extends Fragment {

    private TextView titleTxt;
    private TextView subtitleTxt;
    private MaterialCardView warningNoteCard;

    private TextInputLayout passwordLayout;
    private TextInputLayout confirmPasswordLayout;
    private Button btnAuth;

    private SharedPreferences prefs;

    public FragmentSettingsAuth() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings_auth, container, false);

        prefs = requireActivity().getSharedPreferences(MainActivity.PAYPRINT_SHARED_PREFERENCES, Context.MODE_PRIVATE);

        titleTxt = v.findViewById(R.id.settings_auth_title_txt);
        subtitleTxt = v.findViewById(R.id.settings_auth_subtitle_txt);
        warningNoteCard = v.findViewById(R.id.card_password_warning_note);

        passwordLayout = v.findViewById(R.id.password_settingsauth);
        confirmPasswordLayout = v.findViewById(R.id.confirm_password_settingsauth);
        btnAuth = v.findViewById(R.id.settingsauthbtn);

        String savedPassword = prefs.getString("settings_app_password", "");
        boolean isFirstTimeSetup = savedPassword.isEmpty();

        if (isFirstTimeSetup) {
            titleTxt.setText("Set Settings Password");
            subtitleTxt.setText("Create a password to protect application settings");
            warningNoteCard.setVisibility(View.VISIBLE);
            confirmPasswordLayout.setVisibility(View.VISIBLE);
            btnAuth.setText("Set Password & Continue");
        } else {
            titleTxt.setText("Settings Authentication");
            subtitleTxt.setText("Enter your settings password to continue");
            warningNoteCard.setVisibility(View.GONE);
            confirmPasswordLayout.setVisibility(View.GONE);
            btnAuth.setText("Unlock Settings");
        }

        btnAuth.setOnClickListener(v1 -> {
            passwordLayout.setError(null);
            confirmPasswordLayout.setError(null);

            String pwd = passwordLayout.getEditText() != null ? passwordLayout.getEditText().getText().toString().trim() : "";

            if (isFirstTimeSetup) {
                String confirmPwd = confirmPasswordLayout.getEditText() != null ? confirmPasswordLayout.getEditText().getText().toString().trim() : "";

                if (pwd.isEmpty()) {
                    passwordLayout.setError("Password cannot be empty");
                    return;
                }
                if (pwd.length() < 4) {
                    passwordLayout.setError("Password must be at least 4 characters long");
                    return;
                }
                if (!pwd.equals(confirmPwd)) {
                    confirmPasswordLayout.setError("Passwords do not match");
                    return;
                }

                prefs.edit().putString("settings_app_password", pwd).apply();
                Toast.makeText(getActivity(), "Settings password set successfully!", Toast.LENGTH_SHORT).show();
                openSettingsFragment();

            } else {
                if (pwd.isEmpty()) {
                    passwordLayout.setError("Please enter your password");
                    return;
                }
                if (pwd.equals(savedPassword)) {
                    openSettingsFragment();
                } else {
                    passwordLayout.setError("Incorrect password. Please try again.");
                }
            }
        });

        return v;
    }

    private void openSettingsFragment() {
        if (isAdded() && getParentFragmentManager() != null) {
            SettingsFragment settingsFragment = new SettingsFragment();
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, settingsFragment);
            fragmentTransaction.commit();
        }
    }
}
