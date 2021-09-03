package com.tuTurno.app.Admin.fragmentsConfig;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tuTurno.app.R;

import models.MisFunciones;

public class AdminNotificacionPersonal extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private TextView txtenviara, txttitulonotiper, txtdetallenotiper;
    Context micontexto;
    ProgressDialog cargando;
    MisFunciones enviarnoper = new MisFunciones();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        micontexto = context;
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.adminnotificapersonal, container, false);
        FloatingActionButton fab = requireActivity().findViewById(R.id.fab_admin);
        fab.setVisibility(View.GONE);
        final Button enviarnotiper = root.findViewById(R.id.botonenviarnotiadminper);
        txttitulonotiper = root.findViewById(R.id.txtTituloNotiper);
        txtdetallenotiper = root.findViewById(R.id.txtDetalleNotiper);
        txtenviara = root.findViewById(R.id.txtenviara);

        assert getArguments() != null;
        txtenviara.setText("Enviar Notificación a: " + getArguments().getString("nombrecli"));

        iniciarFirebase();
        cargando = new ProgressDialog(micontexto);

        enviarnotiper.setOnClickListener(view -> {
            String titulo = txttitulonotiper.getText().toString();
            String detalle = txtdetallenotiper.getText().toString();
            if ((titulo.equals("")) || (detalle.equals(""))) {
                Snackbar.make(view, "Ingrese Título y Detalle", Snackbar.LENGTH_SHORT).show();
            } else {
                cargando.setTitle("Enviando...");
                cargando.setMessage("Espere por favor...");
                cargando.show();
                enviarnoper.enviarnotificacionapi(getArguments().getString("tokencliente"), txttitulonotiper.getText().toString(), txtdetallenotiper.getText().toString(), view, cargando);
                txtdetallenotiper.setText("");
                txttitulonotiper.setText("");
            }
        });

        return root;
    }

    private void iniciarFirebase() {
        FirebaseApp.initializeApp(requireActivity());
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference.keepSynced(true);
    }
}
