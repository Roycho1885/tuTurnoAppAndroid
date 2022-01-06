package com.tuTurno.app.Admin.fragmentsHomeAdmin;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import models.DatosNotificaciones;
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
    private TextView setFecha;
    private ImageView imagenvacia;
    private NavigationView navi;
    private String fechaactual;
    private DatosTurno t, listadeturnos = new DatosTurno();
    private ScrollView scroolasis;
    private cliente c = new cliente();

    private ArrayList<DatosTurno> listturnos = new ArrayList<>();

    private ArrayList<String> arraydisci, arrayturnos;
    private ArrayAdapter<String> miadapter, miadapter2;
    EditText fechapago;
    String disci1, horaturno, user;
    boolean menudisci = false;
    boolean menuturno = false;
    boolean band = false;
    boolean bandera1;
    ProgressDialog cargando;
    Calendar micalendario, micalendario1;
    TextView textologo, registros;

    //CREO UN EVENTLISTENER
    private ValueEventListener milistener;


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


    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home_admin, container, false);
        final FloatingActionButton fab = requireActivity().findViewById(R.id.fab_admin);
        final AutoCompleteTextView menutur = root.findViewById(R.id.dropdown_texto);
        final AutoCompleteTextView menudis = root.findViewById(R.id.dropdown_text);
        scroolasis = root.findViewById(R.id.scroolasis);
        imagenvacia = root.findViewById(R.id.imagenvacia);
        fechapago = root.findViewById(R.id.txtfecha);
        setFecha = root.findViewById(R.id.setfecha);
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

        menutur.post(() -> menutur.getText().clear());

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


        menudis.setOnItemClickListener((parent, v, position, id) -> {

            //LIMPIO EL BOTON TURNO CUANDO PRESIONO BOTON DISCIPLINA
            menutur.post(() -> menutur.getText().clear());
            arrayturnos = new ArrayList<>();
            menudisci = true;
            disci1 = parent.getAdapter().getItem(position).toString();
            int posi = fechaactual.indexOf(",");
            final String diass = fechaactual.substring(0, posi);

            //CARGO LOS TURNOS
            databaseReference.child(textologo.getText().toString()).child("Disciplinas").child(disci1).orderByChild("horacomienzo").addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("Assert")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot shot : dataSnapshot.getChildren()) {
                        turno tur = shot.getValue(turno.class);
                        assert tur != null;
                        if (tur.getDias().toLowerCase().contains(diass) || tur.getDias().toLowerCase().contains("todos")) {
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

        });

        //LO QUE TIENE QUE VER PARA EL BOTON FECHA
        final DatePickerDialog.OnDateSetListener date = (view, ano, mes, diames) -> {
            micalendario.set(Calendar.YEAR, ano);
            micalendario.set(Calendar.MONTH, mes);
            micalendario.set(Calendar.DAY_OF_MONTH, diames);
            actualizarformatofecha();
        };

        fechapago.setOnClickListener(view -> new DatePickerDialog(micontexto, date, micalendario
                .get(Calendar.YEAR), micalendario.get(Calendar.MONTH),
                micalendario.get(Calendar.DAY_OF_MONTH)).show());


        menutur.setOnItemClickListener((parent, view, i, id) -> {
            horaturno = parent.getAdapter().getItem(i).toString();
            menuturno = true;
        });


        fab.setOnClickListener(v -> {
            if (!menudisci || !menuturno) {
                Snackbar.make(container, "Seleccione Disciplina y Turno", Snackbar.LENGTH_SHORT).show();
            } else {
                band = false;
                milistener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int contador = 0;
                        listturnos.clear();
                        for (DataSnapshot shot : snapshot.getChildren()) {
                            t = shot.getValue(DatosTurno.class);
                            assert t != null;
                            if (!fechapago.getText().toString().equals("")) {
                                if (Objects.equals(shot.child("disciplina").getValue(), disci1) && (Objects.equals(shot.child("turno").getValue(), horaturno))
                                        && (Objects.equals(shot.child("fecha").getValue(), fechapago.getText().toString()))) {
                                    if (t.getAsistencia().equals("Si")) {
                                        t.setIcono(R.drawable.ic_baseline_check_circle_24);
                                    } else {
                                        t.setIcono(R.drawable.ic_baseline_cancel_24);
                                    }
                                    listturnos.add(t);
                                    contador = contador +1;
                                    adaptador = new ListViewAdaptadorLA(micontexto, listturnos);
                                    milistaturnoscliente.setAdapter(adaptador);
                                    band = true;
                                }
                            } else {
                                if (Objects.equals(shot.child("disciplina").getValue(), disci1) && (Objects.equals(shot.child("turno").getValue(), horaturno))
                                        && (Objects.equals(shot.child("fecha").getValue(), fechaactual))) {
                                    if (t.getAsistencia().equals("Si")) {
                                        t.setIcono(R.drawable.ic_baseline_check_circle_24);
                                    } else {
                                        t.setIcono(R.drawable.ic_baseline_cancel_24);
                                    }
                                    listturnos.add(t);
                                    contador = contador +1;
                                    adaptador = new ListViewAdaptadorLA(micontexto, listturnos);
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
                        fechapago.setText("");
                        menudis.post(() -> menudis.getText().clear());
                        menutur.post(() -> menutur.getText().clear());

                        if (!band) {
                            milistaturnoscliente.setAdapter(null);
                            milistaturnoscliente.setVisibility(View.GONE);
                            imagenvacia.setVisibility(View.VISIBLE);
                            imagenvacia.setImageResource(R.drawable.imgvacia);
                            Snackbar.make(container, "Por el momento no existen turnos por motrar", Snackbar.LENGTH_SHORT).show();
                            menudis.post(() -> menudis.getText().clear());
                            menutur.post(() -> menutur.getText().clear());
                            fechapago.setText("");
//                                arrayAdapterTurnos.notifyDataSetChanged();
                        } else {
                            if (adaptador == null) {
                                milistaturnoscliente.setVisibility(View.GONE);
                                imagenvacia.setVisibility(View.VISIBLE);
                                imagenvacia.setImageResource(R.drawable.imgvacia);
                                Snackbar.make(container, "Por el momento no existen turnos por motrar", Snackbar.LENGTH_SHORT).show();
                                menudis.post(() -> menudis.getText().clear());
                                menutur.post(() -> menutur.getText().clear());
                                fechapago.setText("");
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
                databaseReference.child(textologo.getText().toString()).child("Datos Turnos").addValueEventListener(milistener);
            }
        });

        //CLICK LARGO EN LA LISTA PARA REGISTRAR
        milistaturnoscliente.setOnItemLongClickListener((adapterView, view, i, l) -> {
            listadeturnos = listturnos.get(i);

            androidx.appcompat.app.AlertDialog.Builder mensaje = new AlertDialog.Builder(new ContextThemeWrapper(requireActivity(), R.style.AlertDialogCustom));
            mensaje.setTitle("Atención!");
            mensaje.setIcon(R.drawable.ic_baseline_warning_24);
            mensaje.setMessage("¿Registrar turno de cliente?");
            mensaje.setPositiveButton("Si", (dialogInterface, t) -> {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("asistencia", "Si");
                databaseReference.child(textologo.getText().toString()).child("Datos Turnos").child(listadeturnos.getIdTurno()).updateChildren(hashMap).addOnSuccessListener(o -> {
                });
                Snackbar.make(view, "Turno registrado", Snackbar.LENGTH_SHORT).show();
                adaptador.notifyDataSetChanged();
            });
            mensaje.setNegativeButton("No", (dialogInterface, t) -> dialogInterface.dismiss());
            AlertDialog dialog = mensaje.create();
            dialog.show();

            return false;
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (milistener != null) {
            databaseReference.child(textologo.getText().toString()).child("Datos Turnos").removeEventListener(milistener);
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
        fechapago.setText(formato.format(micalendario.getTime()));
        bandera1 = true;
    }

}
