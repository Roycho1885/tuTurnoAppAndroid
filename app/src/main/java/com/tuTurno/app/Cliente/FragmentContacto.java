package com.tuTurno.app.Cliente;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.tuTurno.app.R;

public class FragmentContacto extends Fragment {

    private Context micontexto;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        micontexto = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_contacto, container, false);

        final EditText etSubject = root.findViewById(R.id.etSubject);
        final EditText etBody = root.findViewById(R.id.etBody);

        FloatingActionButton fab= requireActivity().findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_baseline_send_24);
        fab.setVisibility(View.VISIBLE);
        fab.setEnabled(true);

        MobileAds.initialize(micontexto,new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdView mAdView = root.findViewById(R.id.adView5);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

       fab.setOnClickListener(v -> {
           Intent itSend = new Intent(Intent.ACTION_SEND);

           itSend.setType("text/plain");

           itSend.putExtra(Intent.EXTRA_EMAIL, new String[]{"tuturno.app.contacto@gmail.com"});
           itSend.putExtra(Intent.EXTRA_SUBJECT, etSubject.getText().toString());
           itSend.putExtra(Intent.EXTRA_TEXT, etBody.getText());

           startActivity(itSend);
           Snackbar.make(v, "Enviar", Snackbar.LENGTH_SHORT).show();
       });

        return root;
    }
}
