package com.tuTurno.app.SuperAdmin.SuperAdminConfig;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuTurno.app.R;

import java.util.ArrayList;
import java.util.Objects;

import models.cliente;

public class AdministradorSetAdmin extends Fragment {
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private Button botoncargar;
    private Button botonaceptar;
    private Button botoncancelar;
    private ListView lista;
    private AutoCompleteTextView dropdowntxt;
    private TextInputLayout botongym;
    private cliente cliente = new cliente();
    Context micontexto;
    ScrollView miscroll;
    private String gym;
    cliente cli;
    boolean comprueba;
    private ArrayList<String> arraygimnasios;
    private ArrayAdapter myadapter;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        micontexto = context;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.administradorsetadmin, container, false);
        dropdowntxt = root.findViewById(R.id.txtgim);
        botongym = root.findViewById(R.id.botongym);
        botoncargar = root.findViewById(R.id.botoncargar);
        botonaceptar = root.findViewById(R.id.botonaceptar);
        botoncancelar = root.findViewById(R.id.botoncancelar);
        lista = root.findViewById(R.id.lista);
        miscroll = root.findViewById(R.id.misroll);

        arraygimnasios = new ArrayList<>();


        final ArrayList<cliente> listItems = new ArrayList<>();
        final ArrayAdapter<cliente> adapter;

        adapter = new ArrayAdapter<>(micontexto, android.R.layout.simple_list_item_single_choice, listItems);
        lista.setAdapter(adapter);


        iniciarFirebase();

        //CARGO LOS GIMNASIOS EN EL AUTOCOMPLETETEXTVIEW
        databaseReference.child("Gimnasios").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot shot : snapshot.getChildren()){
                    arraygimnasios.add(Objects.requireNonNull(shot.child("nombre").getValue()).toString());
                }
                myadapter = new ArrayAdapter<>(micontexto, android.R.layout.simple_list_item_1, arraygimnasios);
                dropdowntxt.setAdapter(myadapter);
                dropdowntxt.setInputType(InputType.TYPE_NULL);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        //Controlo el scrollView con el ListView
        miscroll.setOnTouchListener((v, event) -> {
            root.findViewById(R.id.lista).getParent()
                    .requestDisallowInterceptTouchEvent(false);
            return false;
        });

        lista.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        dropdowntxt.post(() -> dropdowntxt.getText().clear());

        botoncargar.setOnClickListener(v -> {
            listItems.clear();
            gym = dropdowntxt.getText().toString().trim();
            if(gym.equals("")){
                Toast.makeText(micontexto,"Selecciona un gimnasio", Toast.LENGTH_SHORT).show();
            }else{
                databaseReference.child("Clientes").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot shot : snapshot.getChildren()) {
                            if (gym.equals(shot.child("gym").getValue())) {
                                cli = shot.getValue(cliente.class);
                                listItems.add(cli);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            botonaceptar.setOnClickListener(v12 -> {
                comprueba = false;
                for(int i=0 ; i< lista.getCount();i++){
                    if(lista.isItemChecked(i)){
                        comprueba = true;
                        cli = (cliente) lista.getItemAtPosition(i);
                        cliente.setApellido(cli.getApellido());
                        cliente.setNombre(cli.getNombre());
                        cliente.setEmail(cli.getEmail());
                        cliente.setGym(cli.getGym());
                        cliente.setId(cli.getId());
                        cliente.setDni(cli.getDni());
                        cliente.setDireccion(cli.getDireccion());
                        cliente.setToken(cli.getToken());
                        cliente.setAdmin("Si");
                        cliente.setUltimopago(cli.getUltimopago());
                        cliente.setFechavencimiento(cli.getFechavencimiento());
                        cliente.setEstadopago(cli.getEstadopago());
                        cliente.setEstadodeuda(cli.getEstadodeuda());
                        cliente.setDisciplinaelegida(cli.getDisciplinaelegida());
                        cliente.setDiasporsemana(cli.getDiasporsemana());
                        cliente.setDiasporsemanaresg(cli.getDiasporsemanaresg());

                        databaseReference.child("Clientes").child(cli.getId()).setValue(cliente);
                        Toast.makeText(micontexto,"Los cambios se realizaron correctamente", Toast.LENGTH_SHORT).show();
                    }
                }
                if(!comprueba){
                    Toast.makeText(micontexto,"Selecciona un cliente", Toast.LENGTH_SHORT).show();
                }
            });

            botoncancelar.setOnClickListener(v1 -> {
                comprueba = false;
                for(int i=0 ; i< lista.getCount();i++){
                    if(lista.isItemChecked(i)){
                        comprueba = true;
                        cli = (cliente) lista.getItemAtPosition(i);
                        cliente.setApellido(cli.getApellido());
                        cliente.setNombre(cli.getNombre());
                        cliente.setEmail(cli.getEmail());
                        cliente.setGym(cli.getGym());
                        cliente.setId(cli.getId());
                        cliente.setDni(cli.getDni());
                        cliente.setDireccion(cli.getDireccion());
                        cliente.setToken(cli.getToken());
                        cliente.setAdmin("No");
                        cliente.setUltimopago(cli.getUltimopago());
                        cliente.setFechavencimiento(cli.getFechavencimiento());
                        cliente.setEstadopago(cli.getEstadopago());
                        cliente.setEstadodeuda(cli.getEstadodeuda());
                        cliente.setDisciplinaelegida(cli.getDisciplinaelegida());
                        cliente.setDiasporsemana(cli.getDiasporsemana());
                        cliente.setDiasporsemanaresg(cli.getDiasporsemanaresg());
                        databaseReference.child("Clientes").child(cli.getId()).setValue(cliente);
                        Toast.makeText(micontexto,"Los cambios se realizaron correctamente", Toast.LENGTH_SHORT).show();
                    }
                }
                if(!comprueba){
                    Toast.makeText(micontexto,"Selecciona un cliente", Toast.LENGTH_SHORT).show();
                }
            });

        });
        return root;
    }

    private void iniciarFirebase(){
        FirebaseApp.initializeApp(requireActivity());
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference.keepSynced(true);

    }
}
