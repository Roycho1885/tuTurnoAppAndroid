package com.tuTurno.app.Admin.fragmentsConfig;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuTurno.app.R;

import java.util.ArrayList;
import java.util.Objects;

import models.MisFunciones;

public class AdminNotificaciones extends Fragment {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private TextView txttitulonoti;
    private TextView txtdetallenoti;
    private ArrayList<String> arraydisci;
    Context micontexto;
    private ArrayAdapter miadapter;
    boolean menueli;
    String disci1;
    ProgressDialog cargando;
    MisFunciones enviarno = new MisFunciones();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        micontexto = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.adminnotifaciones, container, false);
        FloatingActionButton fab= requireActivity().findViewById(R.id.fab_admin);
        fab.setVisibility(View.GONE);

        final AutoCompleteTextView menuelim = root.findViewById(R.id.gruponotifadmin);
        final Button enviarnoti = root.findViewById(R.id.botonenviarnotiadmin);
        txttitulonoti = root.findViewById(R.id.txtTituloNoti);
        txtdetallenoti = root.findViewById(R.id.txtDetalleNoti);
        //para los diferentes gimnasios
        final TextView gimnasio = requireActivity().findViewById(R.id.textologo);
        final String gym = gimnasio.getText().toString().replace(" ","_");

        iniciarFirebase();
        cargando = new ProgressDialog(micontexto);

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

        menuelim.post(() -> menuelim.getText().clear());

        menuelim.setOnItemClickListener((adapterView, view, i, l) -> {
            menueli = true;
            disci1 = adapterView.getAdapter().getItem(i).toString();
        });

        enviarnoti.setOnClickListener(view -> {
            String titulo = txttitulonoti.getText().toString();
            String detalle = txtdetallenoti.getText().toString();
            if(!menueli){
                Snackbar.make(view,"Ingrese grupo",Snackbar.LENGTH_SHORT).show();
            }else {
                if((titulo.equals("")) || (detalle.equals(""))){
                    Snackbar.make(view,"Ingrese Título y Detalle",Snackbar.LENGTH_SHORT).show();
                }else{
                    cargando.setTitle("Enviando...");
                    cargando.setMessage("Espere por favor...");
                    cargando.show();
                    enviarno.enviarnotificacionapitemas(disci1+gym,txttitulonoti.getText().toString(),txtdetallenoti.getText().toString(),view,cargando);
                    txtdetallenoti.setText("");
                    txttitulonoti.setText("");
                }
            }
        });

        return root;
    }

    private void  iniciarFirebase(){
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
