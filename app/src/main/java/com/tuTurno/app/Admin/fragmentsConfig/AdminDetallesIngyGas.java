package com.tuTurno.app.Admin.fragmentsConfig;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuTurno.app.ListViewAdaptadorDetallesIngyGas;
import com.tuTurno.app.R;
import com.tuTurno.app.SwipeListViewTouchListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import models.cliente;
import models.ingresosextras;

public class AdminDetallesIngyGas extends Fragment {
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private Button botondetalles;
    private ImageView imgvacia;
    private String perfil, user;
    Calendar micalendario;
    boolean bandera1;
    Context micontexto;
    private ScrollView scr;
    private ingresosextras objing = new ingresosextras();
    private ingresosextras objlista = new ingresosextras();

    //para el listview
    private final ArrayList<ingresosextras> listadetallesingygas = new ArrayList<>();
    private ListView milistadetallesingygas;
    private ListViewAdaptadorDetallesIngyGas adaptador;

    //CREO UN EVENTLISTENER
    private ValueEventListener milistenercliente;
    private cliente cli, c = new cliente();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        micontexto = context;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.admindetallesingygas, container, false);
        scr = root.findViewById(R.id.scr);
        milistadetallesingygas = root.findViewById(R.id.listadetallesingresosyegresos);
        botondetalles = root.findViewById(R.id.botonvdetalles);
        imgvacia = root.findViewById(R.id.imgvacia);
        micalendario = Calendar.getInstance();
        imgvacia.setVisibility(View.GONE);
        milistadetallesingygas.setVisibility(View.VISIBLE);

        //para los diferentes gimnasios
        final TextView gimnasio = requireActivity().findViewById(R.id.textologo);
        adaptador = new ListViewAdaptadorDetallesIngyGas(micontexto, listadetallesingygas);

        iniciarFirebase();
        user = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail();

        //LECTURA DE CLIENTE PARA RESTRINGIR ACCESO
        milistenercliente = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot shot : snapshot.getChildren()){
                    cli = shot.getValue(cliente.class);
                    if(cli.getEmail().equals(user)){
                        c = shot.getValue(cliente.class);
                        perfil = c.getAdmin();
                    }
                }
                if(perfil.equals("AdminRestringido")){
                    milistadetallesingygas.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.child("Clientes").addValueEventListener(milistenercliente);


        botondetalles.setOnClickListener(view -> {
            bandera1 = false;
            databaseReference.child(gimnasio.getText().toString()).child("IngresosyGastos").orderByChild("tipo").addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    listadetallesingygas.clear();
                    for (DataSnapshot shot : snapshot.getChildren()) {
                        objing = shot.getValue(ingresosextras.class);
                        assert objing != null;
                        listadetallesingygas.add(objing);
                        milistadetallesingygas.setAdapter(adaptador);
                        bandera1 = true;
                    }
                    if (!bandera1) {
                        imgvacia.setVisibility(View.VISIBLE);
                        imgvacia.setImageResource(R.drawable.imgvacia);
                        Snackbar.make(view, "Por el momento no existen registros", Snackbar.LENGTH_SHORT).show();
                        milistadetallesingygas.setAdapter(null);
                        milistadetallesingygas.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            //Deslizar item para borrarlo
            SwipeListViewTouchListener touchListener = new SwipeListViewTouchListener(milistadetallesingygas, (listView, reverseSortedPositions) -> {
                //Aqui ponemos lo que hara el programa cuando deslizamos un item ha la izquierda
                objlista = listadetallesingygas.get(reverseSortedPositions[0]);

                //ALERT DIALOG SI DESEA BORRAR
                AlertDialog.Builder mensaje = new AlertDialog.Builder(new ContextThemeWrapper(requireActivity(), R.style.AlertDialogCustom));
                mensaje.setTitle("Atención!");
                mensaje.setIcon(R.drawable.ic_baseline_warning_24);
                mensaje.setMessage("¿Desea borrar este registro?");
                mensaje.setPositiveButton("Si", (dialogInterface, i) -> {
                    listadetallesingygas.remove(reverseSortedPositions[0]);
                    databaseReference.child(gimnasio.getText().toString()).child("IngresosyGastos").child(objlista.getId()).removeValue();
                    adaptador.notifyDataSetChanged();
                });
                mensaje.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
                AlertDialog dialog = mensaje.create();
                dialog.show();
            }, true, false);

            //Escuchadores del listView
            milistadetallesingygas.setOnTouchListener(touchListener);
            milistadetallesingygas.setOnScrollListener(touchListener.makeScrollListener());

        });

        return root;
    }

    private void iniciarFirebase() {
        FirebaseApp.initializeApp(requireActivity());
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference.keepSynced(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (milistenercliente != null) {
            databaseReference.child("Clientes").removeEventListener(milistenercliente);
        }

    }

}
