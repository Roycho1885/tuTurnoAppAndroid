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
    private gimnasios gyms, gym = new gimnasios();



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        micontexto = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.admincodigo, container, false);
        assert getArguments() != null;
        FloatingActionButton fab= requireActivity().findViewById(R.id.fab_admin);
        fab.setVisibility(View.GONE);


        final EditText txtcodigo = root.findViewById(R.id.txtCodigoo);
        final TextView codigoactual = root.findViewById(R.id.codigoctualtext);
        final Button botonaceptar = root.findViewById(R.id.btncodigo);

        //para los diferentes gimnasios
        final TextView gimnasio = requireActivity().findViewById(R.id.textologo);

        iniciarFirebase();



        databaseReference.child("Gimnasios").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot shot : snapshot.getChildren()){
                    gyms = shot.getValue(gimnasios.class);
                    assert gyms != null;
                    if(gimnasio.getText().toString().equals(gyms.getNombre())){
                        gym = shot.getValue(gimnasios.class);
                        assert gym != null;
                        codigoactual.setText(gym.codigoacceso);
                        gym.setNombre(gym.getNombre());
                        gym.setLogo(gym.getLogo());
                        gym.setDireccion(gym.getDireccion());
                        gym.setUrldire(gym.getUrldire());
                        gym.setFondonav(gym.getFondonav());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        botonaceptar.setOnClickListener(view -> {


            if(txtcodigo.getText().toString().equals("")){
                txtcodigo.setError("Ingrese Código");
            }else{
                codigoss = txtcodigo.getText().toString().trim();
                gym.setCodigoacceso(codigoss);
                databaseReference.child("Gimnasios").child(gimnasio.getText().toString()).setValue(gym);
                Snackbar.make(view,"Código modificado correctamente",Snackbar.LENGTH_SHORT).show();
                txtcodigo.setText("");
                codigoactual.setText(codigoss);
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
