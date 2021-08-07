package com.tuTurno.app.Admin.fragmentsConfig;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.tuTurno.app.ListViewAdaptadorAdEliTur;
import com.tuTurno.app.R;

import java.util.ArrayList;
import java.util.Objects;

import models.turno;

public class AdminElimTur extends Fragment {

    private ScrollView otroscroll;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    boolean menueli;
    private turno turnoselecc;
    boolean band1;
    String disci1;
    Context micontexto;
    TextView gimnasio;

    private ArrayList<String> arraydisci;
    private ArrayList<turno> listturnoseliminar = new ArrayList<>();
    private ArrayAdapter miadapter;

    //para el listview
    private ListView milistaturnoseliminar;
    private ListViewAdaptadorAdEliTur adaptador;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        micontexto = context;
    }


    @Nullable
    @Override
    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.adminelimtur, container, false);

        otroscroll = root.findViewById(R.id.otroscroll);
        //ESTO ES PARA EL LISTVIEW
        milistaturnoseliminar = root.findViewById(R.id.listaturnoseliminar);
        final AutoCompleteTextView menuelim = root.findViewById(R.id.discidespleeli);
        final Button botonelimardisci = root.findViewById(R.id.botonelimidisci);
        //para los diferentes gimnasios
        gimnasio = requireActivity().findViewById(R.id.textologo);

        iniciarFirebase();

        menuelim.post(() -> menuelim.getText().clear());

        //ListView Click
        adaptador = new ListViewAdaptadorAdEliTur(micontexto,listturnoseliminar);
        milistaturnoseliminar.setOnItemClickListener((adapterView, view, i, l) -> {
            turnoselecc = listturnoseliminar.get(i);
            adapterView.setSelected(true);
            if(adapterView.isClickable()) {
                band1 = true;
            }
        });


        arraydisci = new ArrayList<>();

        otroscroll.setOnTouchListener((v, event) -> {
            milistaturnoseliminar.getParent()
                    .requestDisallowInterceptTouchEvent(false);
            return false;
        });

        milistaturnoseliminar.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

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

        menuelim.setOnItemClickListener((adapterView, view, i, l) -> {
            menueli = true;
            disci1 = adapterView.getAdapter().getItem(i).toString();
            listarturnos(view,disci1,gimnasio);
        });

        botonelimardisci.setOnClickListener(view -> {
            if(!menueli){
                assert container != null;
                Snackbar.make(container,"Eliga una disciplina",Snackbar.LENGTH_SHORT).show();
            }else{
                if (!band1){
                    assert container != null;
                    Snackbar.make(container,"Seleccione el turno a eliminar",Snackbar.LENGTH_SHORT).show();
                }else{
                    final turno tur = new turno();
                    tur.setId(turnoselecc.getId());
                    databaseReference.child(gimnasio.getText().toString()).child("Disciplinas").child(disci1).child(tur.getId()).removeValue();
                    milistaturnoseliminar.setAdapter(null);
                    assert container != null;

                    //ESTO ME CONTROLA SI ELIMINA EL ULTIMO TURNO DE LA LISTA ASI QUITO TAMBIEN LA CONFIG DE CUOTAS
                    if(listturnoseliminar.size()==1){
                        databaseReference.child(gimnasio.getText().toString()).child("ConfigCuota").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot shot : snapshot.getChildren()){
                                    String diciplina = shot.child("disciplina").getValue(String.class);
                                    String id = shot.child("id").getValue(String.class);
                                    assert diciplina != null;
                                    if(diciplina.equals(disci1)){
                                        assert id != null;
                                        databaseReference.child(gimnasio.getText().toString()).child("ConfigCuota").child(id).removeValue();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    Snackbar.make(container,"El Turno se elimino correctamente",Snackbar.LENGTH_SHORT).show();

                }
            }

        });

        return root;
    }

    private void listarturnos(final View v, final String dis, TextView gim) {
        databaseReference.child(gim.getText().toString()).child("Disciplinas").child(dis).orderByChild("horacomienzo").addValueEventListener(new ValueEventListener() {
            @SuppressLint("Assert")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listturnoseliminar.clear();
                for(DataSnapshot shot: dataSnapshot.getChildren()){
                    turno tur = shot.getValue(turno.class);
                    listturnoseliminar.add(tur);
                    milistaturnoseliminar.setAdapter(adaptador);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
