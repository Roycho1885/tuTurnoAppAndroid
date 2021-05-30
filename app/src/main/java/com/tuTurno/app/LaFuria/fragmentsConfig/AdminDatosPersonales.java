package com.tuTurno.app.LaFuria.fragmentsConfig;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuTurno.app.R;

import java.util.Objects;

import models.cliente;

public class AdminDatosPersonales extends Fragment {

    private EditText txtNombre, txtApellido, txtdni,txtdireccion,txtEmail;
    private Button btnactualizar;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private cliente cli,clin = new cliente();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.admindatospersonales, container, false);
        txtNombre = root.findViewById(R.id.txtNombre);
        txtApellido = root.findViewById(R.id.txtApellido);
        txtdni = root.findViewById(R.id.txtdni);
        txtdireccion = root.findViewById(R.id.txtdireccion);
        btnactualizar = root.findViewById(R.id.btnactualizar);
        txtEmail = root.findViewById(R.id.txtEmail);
        txtEmail.setEnabled(false);

        iniciarFirebase();

        databaseReference.child("Clientes").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot shot : snapshot.getChildren()) {
                    cli = shot.getValue(cliente.class);
                    assert cli != null;
                    if (cli.getEmail().equals(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail())) {
                        clin = shot.getValue(cliente.class);
                        assert clin != null;
                        txtNombre.setText(clin.getNombre());
                        txtApellido.setText(clin.getApellido());
                        txtdni.setText(clin.getDni());
                        txtdireccion.setText(clin.getDireccion());
                        txtEmail.setText(clin.getEmail());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnactualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!txtNombre.getText().toString().equals("") && !txtApellido.getText().toString().equals("") && !txtdni.getText().toString().equals("") && !txtdireccion.getText().toString().equals("")){
                    clin.setId(clin.getId());
                    clin.setNombre(txtNombre.getText().toString().trim());
                    clin.setApellido(txtApellido.getText().toString().trim());
                    clin.setDni(txtdni.getText().toString().trim());
                    clin.setDireccion(txtdireccion.getText().toString().trim());
                    clin.setEmail(txtEmail.getText().toString().trim());
                    clin.setGym(clin.getGym());
                    clin.setAdmin(clin.getAdmin());
                    clin.setToken(clin.getToken());
                    cli.setUltimopago(clin.getUltimopago());
                    cli.setFechavencimiento(clin.getFechavencimiento());
                    cli.setEstadopago(clin.getEstadopago());
                    databaseReference.child("Clientes").child(clin.getId()).setValue(clin);
                    Snackbar.make(view,"Datos Actualizados Correctamente",Snackbar.LENGTH_SHORT).show();
                }else {
                    if(txtNombre.getText().toString().equals("")){
                        txtNombre.setError("Ingrese Nombre");
                    }
                    if(txtApellido.getText().toString().equals("")){
                        txtApellido.setError("Ingrese Apellid");
                    }
                    if(txtdni.getText().toString().equals("")){
                        txtdni.setError("Ingrese DNI");
                    }
                    if(txtdireccion.getText().toString().equals("")){
                        txtdireccion.setError("Ingrese Dirección");
                    }
                }
            }
        });



        return root;
    }

    private void iniciarFirebase() {
        FirebaseApp.initializeApp(requireActivity());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference.keepSynced(true);
    }
}
