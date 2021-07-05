package com.tuTurno.app.Admin.fragmentsHomeAdmin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.CollapsingToolbarLayout;
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
import com.tuTurno.app.ListViewAdaptadorLA;
import com.tuTurno.app.R;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import models.DatosTurno;
import models.MisFunciones;
import models.cliente;
import models.gimnasios;
import models.turno;

public class HomeAdmin extends Fragment {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private Context micontexto;
    private TextView cliente_admin, setFecha;
    private CollapsingToolbarLayout tool;
    private NavigationView navi;
    private String user, fechaactual, nombre, apellido, urldire;
    private cliente c,cli = new cliente();
    private DatosTurno t = new DatosTurno();

    private ArrayList<DatosTurno> listturnos = new ArrayList<>();

    private ArrayList<String> arraydisci, arrayturnos;
    private ArrayAdapter<String> miadapter, miadapter2;
    String disci1, horaturno;
    boolean menudisci = false;
    boolean menuturno = false;
    boolean band = false;
    MisFunciones cargarNav = new MisFunciones();


    //para el listview
    private ListView milistaturnoscliente;
    private ListViewAdaptadorLA adaptador;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        micontexto = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home_admin, container, false);
        final FloatingActionButton fab = requireActivity().findViewById(R.id.fab_admin);
        final AutoCompleteTextView menutur = root.findViewById(R.id.dropdown_texto);
        final AutoCompleteTextView menudis = root.findViewById(R.id.dropdown_text);
        setFecha = root.findViewById(R.id.setfecha);
        cliente_admin = requireActivity().findViewById(R.id.versianda);
        fab.setImageResource(R.drawable.lista_admin);




        //ESTO ES PARA EL LISTVIEW
        milistaturnoscliente = root.findViewById(R.id.listadeturnoscliente);


        tool = requireActivity().findViewById(R.id.CollapsingToolbar);
        navi = requireActivity().findViewById(R.id.nav_view_admin);

        View head = navi.getHeaderView(0);
        final ImageView fondo = head.findViewById(R.id.fondo);
        final ImageView logo = head.findViewById(R.id.imageViewlogo);
        final TextView textologo = head.findViewById(R.id.textologo);
        final TextView txtdirecli = head.findViewById(R.id.textodire);

        iniciarFirebase();

        menudis.post(new Runnable() {
            @Override
            public void run() {
                menudis.getText().clear();
            }
        });

        menutur.post(new Runnable() {
            @Override
            public void run() {
                menutur.getText().clear();
            }
        });


        user = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail();


        Calendar cal = Calendar.getInstance();

        //LECTURA DEL CLIENTE
        databaseReference.child("Clientes").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot shot : snapshot.getChildren()){

                    c = shot.getValue(cliente.class);
                    assert c != null;

                    if(c.getEmail().equals(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail())){

                        cli = shot.getValue(cliente.class);


                        //CARGO NOMBRE, GIMNASIO E IMAGEN AL HEADER
                        nombre = c.getNombre();
                        apellido = c.getApellido();
                        cliente_admin.setText(getString(R.string.cliente)+ " " + c.getNombre());
                        tool.setTitle(c.getGym());

                        textologo.setText(c.getGym());
                    }
                }

                //CARGO LA URL DE IMAGEN PARA COLORCAR EL LOGO EN EL HEADER
                databaseReference.child("Gimnasios").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot shot : snapshot.getChildren()){
                            gimnasios g = shot.getValue(gimnasios.class);
                            assert g != null;
                            if(cli.getGym().equals(g.getNombre())){
                                urldire= cargarNav.cargarDatosNav(micontexto,g.getDireccion(), g.getUrldire(),g.getLogo(),g.getFondonav(),txtdirecli,logo,fondo);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                //CARGO DISCIPLINA
                arraydisci = new ArrayList<>();
                databaseReference.child(textologo.getText().toString()).child("Disciplinas").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot shot : snapshot.getChildren()){
                            arraydisci.add(Objects.requireNonNull(shot.getKey()));
                        }
                        miadapter = new ArrayAdapter<>(micontexto, android.R.layout.simple_list_item_1, arraydisci);
                        menudis.setAdapter(miadapter);
                        menudis.setInputType(InputType.TYPE_NULL);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        setearfecha(cal);
        Calendar calendar = Calendar.getInstance();
        DateFormat formato = DateFormat.getDateInstance(DateFormat.FULL);
        fechaactual = formato.format(calendar.getTime());


        menudis.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View v, int position, long id) {

                //LIMPIO EL BOTON TURNO CUANDO PRESIONO BOTON DISCIPLINA
                menutur.post(new Runnable() {
                    @Override
                    public void run() {
                        menutur.getText().clear();
                    }
                });
                arrayturnos = new ArrayList<>();
                menudisci = true;
                disci1 = parent.getAdapter().getItem(position).toString();
                int posi = fechaactual.indexOf(",");
                final String diass = fechaactual.substring(0,posi);

                //CARGO LOS TURNOS
                databaseReference.child(textologo.getText().toString()).child("Disciplinas").child(disci1).orderByChild("horacomienzo").addValueEventListener(new ValueEventListener() {
                    @SuppressLint("Assert")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot shot: dataSnapshot.getChildren()){
                            turno tur = shot.getValue(turno.class);
                            assert tur != null;
                            if(tur.getDias().toLowerCase().contains(diass) || tur.getDias().toLowerCase().contains("todos")) {
                                String hora = Objects.requireNonNull(shot.child("horacomienzo").getValue()).toString();
                                arrayturnos.add(hora);
                            }
                        }
                        miadapter2 = new ArrayAdapter<>(micontexto, android.R.layout.simple_list_item_1, arrayturnos);
                        menutur.setAdapter(miadapter2);
                        menutur.setInputType(InputType.TYPE_NULL);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        menutur.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                horaturno = parent.getAdapter().getItem(i).toString();
                menuturno = true;
            }
        });

        //BOTON DIRECCION DEL NAVHEADER
        txtdirecli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent j = new Intent(Intent.ACTION_VIEW, Uri.parse(urldire));
                startActivity(j);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!menudisci || !menuturno) {
                    Snackbar.make(v, "Seleccione Disciplina y Turno", Snackbar.LENGTH_SHORT).show();
                } else {
                    band = false;
                    databaseReference.child(textologo.getText().toString()).child("Datos Turnos").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            listturnos.clear();
                            for (DataSnapshot shot : snapshot.getChildren()) {
                                t = shot.getValue(DatosTurno.class);
                                assert t != null;
                                if (Objects.equals(shot.child("disciplina").getValue(), disci1) && (Objects.equals(shot.child("turno").getValue(), horaturno)) && (Objects.equals(shot.child("fecha").getValue(),fechaactual))) {
                                    listturnos.add(t);
                                    adaptador = new ListViewAdaptadorLA(micontexto,listturnos);
                                    milistaturnoscliente.setAdapter(adaptador);
                                    band = true;
                                }
                            }

                            if (!band) {
                                milistaturnoscliente.setAdapter(null);
                                Snackbar.make(v, "Por el momento no existen turnos por motrar", Snackbar.LENGTH_SHORT).show();
//                                arrayAdapterTurnos.notifyDataSetChanged();
                            }else{
                                if (adaptador == null) {
                                    Snackbar.make(v, "Por el momento no existen turnos por motrar", Snackbar.LENGTH_SHORT).show();
                                } else {
                                    adaptador.notifyDataSetChanged();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

        });
        return root;
    }

    private void setearfecha(@NotNull Calendar calendar){
        DateFormat formato = DateFormat.getDateInstance(DateFormat.FULL);
        setFecha.setText(formato.format(calendar.getTime()));
    }

    private void iniciarFirebase() {
        FirebaseApp.initializeApp(requireActivity());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference.keepSynced(true);
    }

}
