package com.payprint.pos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.payprint.pos.R;

public class SettingsHomeFragment extends Fragment {

    public SettingsHomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings_home, container, false);

        View cardGeneral = v.findViewById(R.id.card_setting_general);
        View cardNode = v.findViewById(R.id.card_setting_node);
        View cardDocs = v.findViewById(R.id.card_setting_developer_docs);

        if (cardGeneral != null) {
            cardGeneral.setOnClickListener(v1 -> navigateToDetail(new FragmentGeneralSettings()));
        }
        if (cardNode != null) {
            cardNode.setOnClickListener(v1 -> navigateToDetail(new FragmentNodeConfig()));
        }
        if (cardDocs != null) {
            cardDocs.setOnClickListener(v1 -> navigateToDetail(new FragmentDeveloperDocs()));
        }

        return v;
    }

    private void navigateToDetail(Fragment fragment) {
        if (getParentFragmentManager() != null) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.settings_container, fragment)
                    .addToBackStack("settings_home")
                    .commit();
        }
    }
}
