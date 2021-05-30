package com.tuTurno.app.LaFuria.fragmentsConfigCli;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.tuTurno.app.R;

import java.util.ArrayList;
import java.util.Objects;

public class CliNotificaciones extends Fragment {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private ArrayList<String> arraydisci;
    Context micontexto;
    private ArrayAdapter miadapter;
    boolean menueli;
    String disci1;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        micontexto = context;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.clinotificaciones, container, false);
        FloatingActionButton fab= requireActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        final AutoCompleteTextView menuelim = root.findViewById(R.id.gruponotif);
        Button suscribirse = root.findViewById(R.id.botonsuscribir);
        Button dejarsuscri = root.findViewById(R.id.botondejarseguir);
        //para los diferentes gimnasios
        final TextView gimnasio = requireActivity().findViewById(R.id.textologo);
        final String gym = gimnasio.getText().toString().replace(" ","_");

        iniciarFirebase();

        arraydisci = new ArrayList<>();

        //TODO LO QUE TENGA QUE VER PARA ELIMINAR DISCIPLINA Y TURNO
        databaseReference.child(gimnasio.getText().toString()).child("Disciplinas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot shot : snapshot.getChildren()){
                    arraydisci.add(Objects.requireNonNull(shot.getKey()));
                }
                miadapter = new ArrayAdapter<>(micontexto, android.R.layout.simple_list_item_1, arraydisci);
                menuelim.setAdapter(miadapter);
                menuelim.setInputType(InputType.TYPE_NULL);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        menuelim.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                menueli = true;
                disci1 = adapterView.getAdapter().getItem(i).toString().trim();
            }
        });

        suscribirse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(menueli){
                    String topic = disci1+gym;
                    FirebaseMessaging.getInstance().subscribeToTopic(topic);
                    Snackbar.make(view,"Te has suscripto correctamente",Snackbar.LENGTH_SHORT).show();
                }else {
                    Snackbar.make(view,"Ingrese un grupo para suscribirse",Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        dejarsuscri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(menueli){
                    String topic = disci1+gym;
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
                    Snackbar.make(view,"Has cancelado la suscripción correctamente",Snackbar.LENGTH_SHORT).show();
                }else {
                    Snackbar.make(view,"Ingrese un grupo para dejar suscripción",Snackbar.LENGTH_SHORT).show();
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
