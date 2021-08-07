package com.tuTurno.app.Admin.fragmentsConfig;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuTurno.app.ListViewAdaptadorDetallesIngyGas;
import com.tuTurno.app.R;
import com.tuTurno.app.SwipeListViewTouchListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import models.ingresosextras;

public class AdminDetallesIngyGas extends Fragment {
    private DatabaseReference databaseReference;
    private Button botondetalles;
    Calendar micalendario;
    boolean banderafecha, bandera1;
    EditText txtfechadetalle;
    Context micontexto;
    String mess, anioo;
    private ScrollView scr;
    private ingresosextras objing = new ingresosextras();
    private ingresosextras objlista = new ingresosextras();

    //para el listview
    private final ArrayList<ingresosextras> listadetallesingygas = new ArrayList<>();
    private ListView milistadetallesingygas;
    private ListViewAdaptadorDetallesIngyGas adaptador;

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
        micalendario = Calendar.getInstance();

        //para los diferentes gimnasios
        final TextView gimnasio = requireActivity().findViewById(R.id.textologo);
        adaptador = new ListViewAdaptadorDetallesIngyGas(micontexto, listadetallesingygas);

        iniciarFirebase();


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
                        Snackbar.make(view, "Por el momento no existen registros", Snackbar.LENGTH_SHORT).show();
                        milistadetallesingygas.setAdapter(null);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            //Deslizar item para borrarlo
            SwipeListViewTouchListener touchListener = new SwipeListViewTouchListener(milistadetallesingygas, new SwipeListViewTouchListener.OnSwipeCallback() {
                @Override
                public void onSwipeLeft(ListView listView, int[] reverseSortedPositions) {
                    //Aqui ponemos lo que hara el programa cuando deslizamos un item ha la izquierda
                    objlista = listadetallesingygas.get(reverseSortedPositions[0]);

                    //ALERT DIALOG SI DESEA BORRAR
                    androidx.appcompat.app.AlertDialog.Builder mensaje = new AlertDialog.Builder(new ContextThemeWrapper(requireActivity(), R.style.AlertDialogCustom));
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
                }
            }, true, false);

            //Escuchadores del listView
            milistadetallesingygas.setOnTouchListener(touchListener);
            milistadetallesingygas.setOnScrollListener(touchListener.makeScrollListener());

        });

        return root;
    }

    private void iniciarFirebase() {
        FirebaseApp.initializeApp(requireActivity());
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.keepSynced(true);
    }

    private void actualizarformatofecha() {
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy");
        txtfechadetalle.setText(sdf.format(micalendario.getTime()));
        int index1 = txtfechadetalle.getText().toString().indexOf("-", 3);
        mess = txtfechadetalle.getText().toString().substring(3, index1);
        anioo = txtfechadetalle.getText().toString().substring(index1 + 1);
        banderafecha = true;
    }
}
