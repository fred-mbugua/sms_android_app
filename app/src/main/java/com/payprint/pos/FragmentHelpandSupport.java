package com.payprint.pos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.payprint.pos.R;

public class FragmentHelpandSupport extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_helpand_support, container, false);

        Button btnEmail = v.findViewById(R.id.btn_email_support);
        if (btnEmail != null) {
            btnEmail.setOnClickListener(v1 -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:support@freestylecodetechnologies.com"));
                    intent.putExtra(Intent.EXTRA_SUBJECT, "PayPrint POS Support Request");
                    startActivity(Intent.createChooser(intent, "Contact PayPrint Support"));
                } catch (Exception ignored) {}
            });
        }

        return v;
    }
}
