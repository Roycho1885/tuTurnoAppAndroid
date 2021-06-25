package com.tuTurno.app.Admin.fragmentsConfig;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuTurno.app.R;

import models.gimnasios;


public class AdminCodigo extends Fragment {
    static String codigoss = null;
    Context micontexto;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
   // private gimnasios codigoacceso = new gimnasios();
    private gimnasios c = new gimnasios();


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        micontexto = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.admincodigo, container, false);
        FloatingActionButton fab= requireActivity().findViewById(R.id.fab_admin);
        fab.setVisibility(View.GONE);

        final EditText txtcodigo = root.findViewById(R.id.txtCodigoo);
        final TextView codigoactual = root.findViewById(R.id.codigoctualtext);
        final Button botonaceptar = root.findViewById(R.id.button2);

        //para los diferentes gimnasios
        final TextView gimnasio = requireActivity().findViewById(R.id.textologo);

        iniciarFirebase();

        databaseReference.child("Gimnasios").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot shot : snapshot.getChildren()){
                    c = shot.getValue(gimnasios.class);
                    assert c != null;
                    if(gimnasio.getText().toString().equals(c.getNombre())){
                        codigoactual.setText(c.codigoacceso);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        botonaceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(txtcodigo.getText().toString().equals("")){
                    txtcodigo.setError("Ingrese Código");
                }else{
                    codigoss = txtcodigo.getText().toString().trim();
                    c.setNombre(c.getNombre());
                    c.setCodigoacceso(codigoss);
                    c.setLogo(c.getLogo());
                    databaseReference.child("Gimnasios").child(gimnasio.getText().toString()).setValue(c);
                    Snackbar.make(view,"Código modificado correctamente",Snackbar.LENGTH_SHORT).show();
                    txtcodigo.setText("");
                    codigoactual.setText(codigoss);
                }
            }
        });

        return root;
    }

    private void  iniciarFirebase(){
        FirebaseApp.initializeApp(requireActivity());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference.keepSynced(true);
    }
}
