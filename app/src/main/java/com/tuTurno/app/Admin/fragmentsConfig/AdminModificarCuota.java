package com.tuTurno.app.Admin.fragmentsConfig;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.tuTurno.app.SwipeListViewTouchListener;

import java.util.ArrayList;
import java.util.Objects;

import models.CuotaConfig;

public class AdminModificarCuota extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private Context micontexto;
    private ScrollView scroll;
    private NavigationView navi;
    private String disci1, idcuot;
    private ImageView imgcuotavacia;
    boolean menudisci, prueba;
    private CuotaConfig objcuota = new CuotaConfig();
    CuotaConfig idcuota;
    FloatingActionButton botonflotante;

    private final ArrayList<CuotaConfig> listmontos = new ArrayList<>();
    private ArrayList<String> arraydisci, arraydimonto;
    private ArrayAdapter<String> miadapter;

    //para el listview
    private ListView milistamonto;
    private ListViewAdaptadorMonto adaptador;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        micontexto = context;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.adminmodificarcuota, container, false);
        scroll = root.findViewById(R.id.scroolmonto);
        final AutoCompleteTextView menudis = root.findViewById(R.id.dropdown_text);
        botonflotante = root.findViewById(R.id.btnflotante);
        imgcuotavacia = root.findViewById(R.id.imgcuotavacia);
        navi = requireActivity().findViewById(R.id.nav_view_admin);
        View head = navi.getHeaderView(0);
        final TextView textologo = head.findViewById(R.id.textologo);
        imgcuotavacia.setVisibility(View.GONE);

        //ESTO ES PARA EL LISTVIEW
        milistamonto = root.findViewById(R.id.listacuota);

        iniciarFirebase();
        botonflotante.setVisibility(View.GONE);

        adaptador = new ListViewAdaptadorMonto(micontexto, listmontos);

        menudis.post(() -> menudis.getText().clear());


        //CARGO DISCIPLINA
        arraydisci = new ArrayList<>();
        arraydimonto = new ArrayList<>();
        databaseReference.child(textologo.getText().toString()).child("Disciplinas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot shot : snapshot.getChildren()) {
                    arraydisci.add(Objects.requireNonNull(shot.getKey()));

                }
                miadapter = new ArrayAdapter<>(micontexto, android.R.layout.simple_list_item_1, arraydisci);
                menudis.setAdapter(miadapter);
                menudis.setInputType(InputType.TYPE_NULL);
                miadapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        menudis.setOnItemClickListener((parent, v, position, id) -> {
            menudisci = true;
            disci1 = parent.getAdapter().getItem(position).toString();

            databaseReference.child(textologo.getText().toString()).child("ConfigCuota").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot shot : snapshot.getChildren()) {
                        String diciplina = shot.child("disciplina").getValue(String.class);
                        assert diciplina != null;
                        if (diciplina.equals(disci1)) {
                            idcuot = shot.child("id").getValue(String.class);
                            listarmontos(textologo, idcuot, diciplina);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        });
        botonflotante.setOnClickListener(view -> {
            Bundle otrobundle = new Bundle();
            otrobundle.putString("keycuota",idcuot);
            otrobundle.putString("disciplina",disci1);
            Navigation.findNavController(view).navigate(R.id.AdminConfigCuota,otrobundle);
        });
        return root;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void listarmontos(TextView textologo, String id, String diciplina) {
        milistamonto.setVisibility(View.VISIBLE);
        botonflotante.setVisibility(View.VISIBLE);
        prueba = false;
        //CARGO LISTVIEW MONTOS DE LAS DISCIPLINAS
        databaseReference.child(textologo.getText().toString()).child("ConfigCuota").child(id).child("configuracioncuotas").orderByChild("diasporsemana").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listmontos.clear();
                for (DataSnapshot shot : snapshot.getChildren()) {
                    CuotaConfig cuota = shot.getValue(CuotaConfig.class);
                    listmontos.add(cuota);
                    milistamonto.setAdapter(adaptador);
                    prueba = true;
                }
                //POR SI LA LISTA DE ESA DISCIPLINA ESTA VACIA
                if (!prueba) {
                    milistamonto.setAdapter(null);
                    milistamonto.setVisibility(View.GONE);
                    imgcuotavacia.setVisibility(View.VISIBLE);
                    imgcuotavacia.setImageResource(R.drawable.imgvacia);
                    Snackbar.make(botonflotante, "No existen cuotas para esta disciplina", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Deslizar item para borrarlo
        SwipeListViewTouchListener touchListener = new SwipeListViewTouchListener(milistamonto, new SwipeListViewTouchListener.OnSwipeCallback() {
            @Override
            public void onSwipeLeft(ListView listView, int[] reverseSortedPositions) {
                //Aqui ponemos lo que hara el programa cuando deslizamos un item ha la izquierda
                objcuota = listmontos.get(reverseSortedPositions[0]);

                //ALERT DIALOG SI DESEA BORRAR
                androidx.appcompat.app.AlertDialog.Builder mensaje = new AlertDialog.Builder(new ContextThemeWrapper(requireActivity(), R.style.AlertDialogCustom));
                mensaje.setTitle("Atención!");
                mensaje.setIcon(R.drawable.ic_baseline_warning_24);
                mensaje.setMessage("¿Desea borrar este registro?");
                mensaje.setPositiveButton("Si", (dialogInterface, i) -> {
                    listmontos.remove(reverseSortedPositions[0]);
                    databaseReference.child(textologo.getText().toString()).child("ConfigCuota").child(id).child("configuracioncuotas").child(objcuota.getIdcuotas()).removeValue();
                    adaptador.notifyDataSetChanged();
                });
                mensaje.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
                AlertDialog dialog = mensaje.create();
                dialog.show();
            }
        }, true, false);

        //Escuchadores del listView
        milistamonto.setOnTouchListener(touchListener);
        milistamonto.setOnScrollListener(touchListener.makeScrollListener());

        //CLICK LARGO EN LA LISTA DE CLIENTES Y PASO DATOS CON BUNDLE A ADMINCONFIGCUOTA
        milistamonto.setOnItemLongClickListener((adapterView, view, i, l) -> {
            Bundle bundle = new Bundle();
            PopupMenu popupMenu = new PopupMenu(micontexto, milistamonto);
            popupMenu.getMenuInflater().inflate(R.menu.menucontextualconfigcuota, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.accionconfig) {
                    idcuota = listmontos.get(i);
                    bundle.putString("idcuota", id);
                    bundle.putString("disci", diciplina);
                    bundle.putString("idcuotamodi", idcuota.getIdcuotas());
                    bundle.putString("dias", idcuota.getDiasporsemana());
                    bundle.putString("monto", idcuota.getMonto());
                    Navigation.findNavController(view).navigate(R.id.AdminConfigCuota, bundle);
                }
                return true;
            });
            popupMenu.show();
            return true;
        });

    }

    private void iniciarFirebase() {
        FirebaseApp.initializeApp(requireActivity());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference.keepSynced(true);
    }
}
