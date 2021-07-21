package com.tuTurno.app.Admin.fragmentsConfig;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuTurno.app.ListViewAdaptadorDatosClientes;
import com.tuTurno.app.R;

import java.util.ArrayList;

import models.cliente;

public class AdminDatosClientes extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    Context micontexto;
    private EditText buscar;
    cliente cli, idcliente;

    public AdminDatosClientes(){

    }

    //para el listview
    private final ArrayList<cliente> listadatoscliente = new ArrayList<>();
    private ListView milistadatosclientes;
    private ListViewAdaptadorDatosClientes adaptador;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        micontexto = context;
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.admindatosclientes, container, false);
        milistadatosclientes = root.findViewById(R.id.listaclientes);
        buscar = root.findViewById(R.id.botonbuscar);


        //para los diferentes gimnasios
        final TextView gimnasio = requireActivity().findViewById(R.id.textologo);
        adaptador = new ListViewAdaptadorDatosClientes(micontexto,listadatoscliente);

        milistadatosclientes.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        iniciarFirebase();


        databaseReference.child("Clientes").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listadatoscliente.clear();
                for (DataSnapshot shot : snapshot.getChildren()) {
                    cli = shot.getValue(cliente.class);
                    assert cli != null;
                    if (cli.getGym().equals(gimnasio.getText().toString()) && cli.getAdmin().equals("No")) {
                        listadatoscliente.add(cli);
                        milistadatosclientes.setAdapter(adaptador);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        buscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adaptador.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //CLICK LARGO EN LA LISTA DE CLIENTES Y PASO EN UN BUNDLE EL ID DE CLIENTE
        milistadatosclientes.setOnItemLongClickListener((adapterView, view, i, l) -> {
            Bundle bundle = new Bundle();
            PopupMenu popupMenu = new PopupMenu(micontexto,milistadatosclientes);
            popupMenu.getMenuInflater().inflate(R.menu.menucontextualdatosclientes,popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.accionconfig) {
                    idcliente = listadatoscliente.get(i);
                    bundle.putString("idcliente",idcliente.getId());
                    Navigation.findNavController(view).navigate(R.id.AdminDatosPersonales,bundle);
                }
                return true;
            });

            popupMenu.show();
            return true;
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
