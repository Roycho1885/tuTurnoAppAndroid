package com.tuTurno.app.Admin.fragmentsConfig;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuTurno.app.ListViewAdaptadorMonto;
import com.tuTurno.app.R;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import models.CuotaConfig;

public class AdminConfigCuota extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private Context micontexto;
    private EditText txtmonto;
    private ScrollView scroll;

    private NavigationView navi;
    private ArrayList<String> arraydisci, arraydimonto;
    private ArrayAdapter<String> miadapter;
    boolean menudisci;
    String disci1, discimodi;
    private CuotaConfig c = new CuotaConfig();
    private ArrayList<CuotaConfig> listmontos = new ArrayList<>();

    //para el listview
    private ListView milistamonto;
    private ListViewAdaptadorMonto adaptador;

    private static final String TAG="lista";
    private static final String TAG1="lista1";


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        micontexto = context;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.adminconfigcuota, container, false);

        scroll = root.findViewById(R.id.scroolmonto);
        final AutoCompleteTextView menudis = root.findViewById(R.id.dropdown_text);
        final Button botonaceptar = root.findViewById(R.id.button2);
        txtmonto = root.findViewById(R.id.txtmonto);
        navi = requireActivity().findViewById(R.id.nav_view_admin);
        View head = navi.getHeaderView(0);
        final TextView textologo = head.findViewById(R.id.textologo);


        //ESTO ES PARA EL LISTVIEW
        milistamonto = root.findViewById(R.id.listacuota);

        iniciarFirebase();
        listarmontos(textologo);

        adaptador = new ListViewAdaptadorMonto(micontexto,listmontos);

        menudis.post(new Runnable() {
            @Override
            public void run() {
                menudis.getText().clear();
            }
        });

        scroll.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                milistamonto.getParent()
                        .requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });

        milistamonto.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        //CARGO DISCIPLINA
        arraydisci = new ArrayList<>();
        arraydimonto = new ArrayList<>();
        databaseReference.child(textologo.getText().toString()).child("Disciplinas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot shot : snapshot.getChildren()){
                    arraydisci.add(Objects.requireNonNull(shot.getKey()));
                }
                databaseReference.child(textologo.getText().toString()).child("ConfigCuota").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot shot: snapshot.getChildren()) {
                            c = shot.getValue(CuotaConfig.class);
                            if (!arraydisci.contains(c.getDisciplina())) {
                                eliminardisciplina(textologo, c.getId());
                                listarmontos(textologo);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                miadapter = new ArrayAdapter<>(micontexto, android.R.layout.simple_list_item_1, arraydisci);
                menudis.setAdapter(miadapter);
                menudis.setInputType(InputType.TYPE_NULL);
                miadapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        menudis.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View v, int position, long id) {
                menudisci = true;
                disci1 = parent.getAdapter().getItem(position).toString();

                //CARGO ID DE LA DISCIPLINA QUE SE VA A MODIFICAR
                databaseReference.child(textologo.getText().toString()).child("ConfigCuota").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot shot: snapshot.getChildren()){
                            CuotaConfig cuota = shot.getValue(CuotaConfig.class);
                            if(disci1.equals(cuota.getDisciplina())){
                                discimodi = cuota.getId();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });


            botonaceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(txtmonto.getText().toString().equals("") || !menudisci){
                    Snackbar.make(view, "Seleccione disciplina e ingrese monto", Snackbar.LENGTH_SHORT).show();
                }else{
                    if(discimodi==null){
                        c.setId(UUID.randomUUID().toString());
                        c.setDisciplina(disci1);
                        c.setMonto(txtmonto.getText().toString().trim());
                        databaseReference.child(textologo.getText().toString()).child("ConfigCuota").child(c.getId()).setValue(c);
                        Snackbar.make(view,"Monto cargado correctamente",Snackbar.LENGTH_SHORT).show();
                    }else {
                        c.setId(discimodi);
                        c.setDisciplina(disci1);
                        c.setMonto(txtmonto.getText().toString().trim());
                        databaseReference.child(textologo.getText().toString()).child("ConfigCuota").child(discimodi).setValue(c);
                        Snackbar.make(view,"Monto Modificado correctamente",Snackbar.LENGTH_SHORT).show();
                    }
                    menudis.post(new Runnable() {
                        @Override
                        public void run() {
                            menudis.getText().clear();
                        }
                    });
                    listarmontos(textologo);
                    txtmonto.setText("");

                }
            }
        });

        return root;
    }

    private void listarmontos(TextView textologo) {
        //CARGO LISTVIEW MONTOS DE LAS DISCIPLINAS
        databaseReference.child(textologo.getText().toString()).child("ConfigCuota").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listmontos.clear();
                for(DataSnapshot shot: snapshot.getChildren()){
                    CuotaConfig cuota = shot.getValue(CuotaConfig.class);
                    listmontos.add(cuota);
                    milistamonto.setAdapter(adaptador);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void eliminardisciplina(TextView textologo, String id){
        databaseReference.child(textologo.getText().toString()).child("ConfigCuota").child(id).removeValue();
    }

    private void iniciarFirebase() {
        FirebaseApp.initializeApp(requireActivity());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference.keepSynced(true);
    }


}
