package com.tuTurno.app.Admin.fragmentsHomeAdmin;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

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
import com.tuTurno.app.ListViewAdaptadorSinTur;
import com.tuTurno.app.R;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import models.DatosNotificaciones;
import models.DatosSinTurno;
import models.DatosTurno;
import models.cliente;
import models.turno;

public class HomeAdminSinTur extends Fragment {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private Context micontexto;
    private TextView cliente_admin, setFecha;
    private ImageView imagenvacia;
    private NavigationView navi;
    private String fechaactual;
    private DatosNotificaciones datos = new DatosNotificaciones();
    private DatosSinTurno t, listadeturnos = new DatosSinTurno();
    private ScrollView scroolasis;
    private cliente c = new cliente();

    private ArrayList<DatosSinTurno> listturnos = new ArrayList<>();

    private ArrayList<String> arraydisci;
    private ArrayAdapter<String> miadapter;
    EditText fecha;
    String disci1, user;
    boolean menudisci = false;
    boolean band = false;
    boolean bandera1;
    ProgressDialog cargando;
    Calendar micalendario, micalendario1;
    TextView textologo, registros;

    //CREO UN EVENTLISTENER
    private ValueEventListener milistener;


    //para el listview
    private ListView milistaturnoscliente;
    private ListViewAdaptadorSinTur adaptador;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        micontexto = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.homeadminsintur, container, false);
        final FloatingActionButton fab = requireActivity().findViewById(R.id.fab_admin);
        final AutoCompleteTextView menudis = root.findViewById(R.id.dropdown_text);
        scroolasis = root.findViewById(R.id.scroolasis);
        imagenvacia = root.findViewById(R.id.imagenvacia);
        fecha = root.findViewById(R.id.txtfecha);
        setFecha = root.findViewById(R.id.setfecha);
        cliente_admin = requireActivity().findViewById(R.id.versianda);
        fab.setImageResource(R.drawable.lista_admin);
        imagenvacia.setVisibility(View.GONE);
        navi = requireActivity().findViewById(R.id.nav_view_admin);
        registros = root.findViewById(R.id.registrosintur);

        View head = navi.getHeaderView(0);
        textologo = head.findViewById(R.id.textologo);


        //ESTO ES PARA EL LISTVIEW
        milistaturnoscliente = root.findViewById(R.id.listadeturnoscliente);
        milistaturnoscliente.setVisibility(View.VISIBLE);

        iniciarFirebase();
        cargando = new ProgressDialog(micontexto);

        menudis.post(() -> menudis.getText().clear());

        scroolasis.setOnTouchListener((v, event) -> {
            milistaturnoscliente.getParent()
                    .requestDisallowInterceptTouchEvent(false);
            return false;
        });

        milistaturnoscliente.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        user = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail();

        Calendar cal = Calendar.getInstance();
        micalendario = Calendar.getInstance();
        micalendario1 = Calendar.getInstance();

        setearfecha(cal);
        Calendar calendar = Calendar.getInstance();
        DateFormat formato = DateFormat.getDateInstance(DateFormat.FULL);
        fechaactual = formato.format(calendar.getTime());

        //LECTURA DEL CLIENTE
        databaseReference.child("Clientes").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot shot : snapshot.getChildren()) {

                    c = shot.getValue(cliente.class);
                    assert c != null;

                    if (c.getEmail().equals(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail())) {
                        if (c.getAdmin().equals("Restringido")) {
                            fab.setClickable(false);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        //CARGO DISCIPLINA
        arraydisci = new ArrayList<>();
        databaseReference.child(textologo.getText().toString()).child("Disciplinas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot shot : snapshot.getChildren()) {
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


        //LO QUE TIENE QUE VER PARA EL BOTON FECHA
        final DatePickerDialog.OnDateSetListener date = (view, ano, mes, diames) -> {
            micalendario.set(Calendar.YEAR, ano);
            micalendario.set(Calendar.MONTH, mes);
            micalendario.set(Calendar.DAY_OF_MONTH, diames);
            actualizarformatofecha();
        };

        fecha.setOnClickListener(view -> new DatePickerDialog(micontexto, date, micalendario
                .get(Calendar.YEAR), micalendario.get(Calendar.MONTH),
                micalendario.get(Calendar.DAY_OF_MONTH)).show());


        menudis.setOnItemClickListener((parent, v, position, id) -> {
            menudisci = true;
            disci1 = parent.getAdapter().getItem(position).toString();
                });


        fab.setOnClickListener(v -> {
            if (!menudisci) {
                Snackbar.make(container, "Seleccione Disciplina", Snackbar.LENGTH_SHORT).show();
            } else {
                band = false;
                milistener = new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int contador = 0;
                        listturnos.clear();
                        for (DataSnapshot shot : snapshot.getChildren()) {
                            t = shot.getValue(DatosSinTurno.class);
                            assert t != null;
                            if (!fecha.getText().toString().equals("")) {
                                if (Objects.equals(shot.child("disciplina").getValue(), disci1)
                                        && (Objects.equals(shot.child("fecha").getValue(), fecha.getText().toString()))) {
                                    listturnos.add(t);
                                    contador = contador +1;
                                    adaptador = new ListViewAdaptadorSinTur(micontexto, listturnos);
                                    milistaturnoscliente.setAdapter(adaptador);
                                    band = true;
                                }
                            } else {
                                if (Objects.equals(shot.child("disciplina").getValue(), disci1)
                                        && (Objects.equals(shot.child("fecha").getValue(), fechaactual))) {
                                    listturnos.add(t);
                                    contador = contador +1;
                                    adaptador = new ListViewAdaptadorSinTur(micontexto, listturnos);
                                    milistaturnoscliente.setAdapter(adaptador);
                                    band = true;
                                }
                            }

                        }
                        if(contador == 0){
                            registros.setVisibility(View.GONE);
                        }else {
                            registros.setVisibility(View.VISIBLE);
                            registros.setText(contador + " registros encontrados");
                        }
                        fecha.setText("");
                        menudis.post(() -> menudis.getText().clear());

                        if (!band) {
                            milistaturnoscliente.setAdapter(null);
                            milistaturnoscliente.setVisibility(View.GONE);
                            imagenvacia.setVisibility(View.VISIBLE);
                            imagenvacia.setImageResource(R.drawable.imgvacia);
                            Snackbar.make(container, "Por el momento no existen asistencias", Snackbar.LENGTH_SHORT).show();
                            menudis.post(() -> menudis.getText().clear());
                            fecha.setText("");
//                                arrayAdapterTurnos.notifyDataSetChanged();
                        } else {
                            if (adaptador == null) {
                                milistaturnoscliente.setVisibility(View.GONE);
                                imagenvacia.setVisibility(View.VISIBLE);
                                imagenvacia.setImageResource(R.drawable.imgvacia);
                                Snackbar.make(container, "Por el momento no existen asistencias", Snackbar.LENGTH_SHORT).show();
                                menudis.post(() -> menudis.getText().clear());
                                fecha.setText("");
                            } else {
                                milistaturnoscliente.setVisibility(View.VISIBLE);
                                imagenvacia.setVisibility(View.GONE);
                                adaptador.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                databaseReference.child(textologo.getText().toString()).child("Datos SinTurno").orderByChild("hora").addValueEventListener(milistener);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (milistener != null) {
            databaseReference.child(textologo.getText().toString()).child("Datos SinTurno").removeEventListener(milistener);
        }
    }

    private void setearfecha(@NotNull Calendar calendar) {
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

    private void actualizarformatofecha() {
        DateFormat formato = DateFormat.getDateInstance(DateFormat.FULL);
        fecha.setText(formato.format(micalendario.getTime()));
        bandera1 = true;
    }
}
