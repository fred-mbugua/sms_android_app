package com.payprint.pos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.payprint.pos.R;

public class FragmentDeveloperDocs extends Fragment {

    public FragmentDeveloperDocs() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_developer_docs, container, false);

        View btnBack = v.findViewById(R.id.btn_back_to_settings_docs);
        if (btnBack != null) {
            btnBack.setOnClickListener(v1 -> {
                if (getParentFragmentManager() != null) {
                    getParentFragmentManager().popBackStack();
                }
            });
        }

        return v;
    }
}
