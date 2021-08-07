package com.tuTurno.app.Admin.fragmentsHomeAdmin;

import android.annotation.SuppressLint;
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
import android.widget.ImageView;
import android.widget.ListView;
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
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
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
    private String user, fechaactual, nombre, apellido, urldire, fecha_venc;
    private cliente c, cli = new cliente();
    private DatosTurno t = new DatosTurno();

    private ArrayList<DatosTurno> listturnos = new ArrayList<>();

    private ArrayList<String> arraydisci, arrayturnos;
    private ArrayAdapter<String> miadapter, miadapter2;
    String disci1, horaturno;
    boolean menudisci = false;
    boolean menuturno = false;
    boolean band = false;
    MisFunciones cargarNav = new MisFunciones();
    MisFunciones enviarno = new MisFunciones();
    ProgressDialog cargando;
    Date fechavencimiento, fechaact;
    Calendar micalendario, micalendario1, micalendario2;
    int numeromes, numeroactmes, noticontador;
    TextView textologo;

    //CREO UN EVENTLISTENER
    private ValueEventListener milistener;

    //LISTAS PARA LAS NOTIFICACIONES
    private ArrayList<String> tokensdeudadebe;


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
        noticontador = 0;

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

        tokensdeudadebe = new ArrayList<>();


        tool = requireActivity().findViewById(R.id.CollapsingToolbar);
        navi = requireActivity().findViewById(R.id.nav_view_admin);

        View head = navi.getHeaderView(0);
        final ImageView fondo = head.findViewById(R.id.fondo);
        final ImageView logo = head.findViewById(R.id.imageViewlogo);
        textologo = head.findViewById(R.id.textologo);
        final TextView txtdirecli = head.findViewById(R.id.textodire);

        iniciarFirebase();
        cargando = new ProgressDialog(micontexto);

        menudis.post(() -> menudis.getText().clear());

        menutur.post(() -> menutur.getText().clear());


        //user = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail();

        Calendar cal = Calendar.getInstance();
        micalendario = Calendar.getInstance();
        micalendario1 = Calendar.getInstance();
        micalendario2 = Calendar.getInstance();

        setearfecha(cal);
        Calendar calendar = Calendar.getInstance();
        DateFormat formato = DateFormat.getDateInstance(DateFormat.FULL);
        fechaactual = formato.format(calendar.getTime());

        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy");
        final String fecha_actual = sdf.format(micalendario1.getTime());
        //LECTURA DEL CLIENTE
        databaseReference.child("Clientes").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot shot : snapshot.getChildren()) {

                    c = shot.getValue(cliente.class);
                    assert c != null;

                    if (c.getEmail().equals(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail())) {
                        cli = shot.getValue(cliente.class);
                        //CARGO NOMBRE, GIMNASIO E IMAGEN AL HEADER
                        nombre = c.getNombre();
                        apellido = c.getApellido();
                        cliente_admin.setText(getString(R.string.cliente) + " " + c.getNombre());
                        tool.setTitle(c.getGym());
                        textologo.setText(c.getGym());
                    }
                }

                //CARGO LA URL DE IMAGEN PARA COLORCAR EL LOGO EN EL HEADER
                databaseReference.child("Gimnasios").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot shot : snapshot.getChildren()) {
                            gimnasios g = shot.getValue(gimnasios.class);
                            assert g != null;
                            if (cli.getGym().equals(g.getNombre())) {
                                urldire = cargarNav.cargarDatosNav(micontexto, g.getDireccion(), g.getUrldire(), g.getLogo(), g.getFondonav(), txtdirecli, logo, fondo);
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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        //ENVIO NOTIFICACIONES LOS LUNES, MIERCOLES Y VIERNES UNICAMENTE
        if (micalendario2.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY || micalendario2.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY
                || micalendario2.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {

            //EL CONTADOR CONTROLA QUE SE HAYA EJECUTADO POR LO MENOS UN ENVIO DE NOTIFICACIONES
            if (noticontador < 1) {

                //LECTURA CLIENTES PARA NOTIFICACIONES
                databaseReference.child("Clientes").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot shot : snapshot.getChildren()) {
                            c = shot.getValue(cliente.class);
                            assert c != null;
                            //COLECTO LOS TOKENS PARA NOTIFICACIONES SEGUN ESTADO DEUDA
                            if (c.getGym().equals(textologo.getText().toString()) && c.getAdmin().equals("No")) {
                                if (c.getEstadodeuda().equals("Debe") || c.getEstadodeuda().equals("0")) {
                                    fecha_venc = c.getUltimopago();

                                    //CALCULAMOS LOS MESES QUE EL CLIENTE ESTA SIN ACTIVIDAD
                                    try {
                                        fechaact = sdf.parse(fecha_actual);
                                        fechavencimiento = sdf.parse(fecha_venc);
                                        assert fechavencimiento != null;
                                        micalendario.setTime(fechavencimiento);
                                        micalendario1.setTime(fechaact);
                                        numeromes = micalendario.get(Calendar.MONTH);
                                        numeroactmes = micalendario1.get(Calendar.MONTH);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    //PREGUNTAMOS SI LA FECHA DE ULTIMO PAGO PASO 3 MESES PARA ELIMINAR CLIENTE
                                    //DESPUES DE BORRAR COLOCA EL EMAIL EN BORRARCLIENTES PARA QUITAR CUENTA
                                    if ((numeroactmes - numeromes) >= 3) {
                                        cliente cli = new cliente(c.getEmail());
                                        databaseReference.child("BorrarClientes").child(textologo.getText().toString()).push().setValue(cli);
                                        databaseReference.child("Clientes").child(c.getId()).removeValue();
                                    } else {
                                        if (!c.getEstadodeuda().equals("0")) {
                                            tokensdeudadebe.add(c.getToken());
                                        }
                                    }
                                }
                            }
                        }
                        //ENVIAMOS NOTIFICACION SI LA LISTA DE TOKEN DEUDORES NO ESTA VACIA
                        if (tokensdeudadebe.size() != 0) {
                            androidx.appcompat.app.AlertDialog.Builder mensaje = new AlertDialog.Builder(new ContextThemeWrapper(requireActivity(), R.style.AlertDialogCustom));
                            mensaje.setTitle("Atención!");
                            mensaje.setIcon(R.drawable.ic_baseline_notification_important_24);
                            mensaje.setMessage("¿Desea enviar notificaciones a los deudores?");
                            mensaje.setPositiveButton("Si", (dialogInterface, i) -> {
                                noticontador = noticontador + 1;
                                cargando.setTitle("Enviando...");
                                cargando.setMessage("Espere por favor...");
                                cargando.show();
                                for (int u = 0; u < tokensdeudadebe.size(); u++) {
                                    enviarno.enviarnotificacionapi(tokensdeudadebe.get(u), "Atención!", "Tu cuota se encuentra vencida, pasa por administración", container, cargando);
                                }
                            });

                            mensaje.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());

                            AlertDialog dialog = mensaje.create();
                            dialog.show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }


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

        menutur.setOnItemClickListener((parent, view, i, id) -> {
            horaturno = parent.getAdapter().getItem(i).toString();
            menuturno = true;
        });

        //BOTON DIRECCION DEL NAVHEADER
        txtdirecli.setOnClickListener(view -> {
            Intent j = new Intent(Intent.ACTION_VIEW, Uri.parse(urldire));
            startActivity(j);
        });

        fab.setOnClickListener(v -> {
            if (!menudisci || !menuturno) {
                Snackbar.make(v, "Seleccione Disciplina y Turno", Snackbar.LENGTH_SHORT).show();
            } else {
                band = false;
                milistener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listturnos.clear();
                        for (DataSnapshot shot : snapshot.getChildren()) {
                            t = shot.getValue(DatosTurno.class);
                            assert t != null;
                            if (Objects.equals(shot.child("disciplina").getValue(), disci1) && (Objects.equals(shot.child("turno").getValue(), horaturno)) && (Objects.equals(shot.child("fecha").getValue(), fechaactual))) {
                                if(t.getAsistencia().equals("Si")){
                                    t.setIcono(R.drawable.ic_baseline_check_circle_24);
                                }else {
                                    t.setIcono(R.drawable.ic_baseline_cancel_24);
                                }
                                listturnos.add(t);
                                adaptador = new ListViewAdaptadorLA(micontexto, listturnos);
                                milistaturnoscliente.setAdapter(adaptador);
                                band = true;
                            }
                        }

                        if (!band) {
                            milistaturnoscliente.setAdapter(null);
                            Snackbar.make(v, "Por el momento no existen turnos por motrar", Snackbar.LENGTH_SHORT).show();
//                                arrayAdapterTurnos.notifyDataSetChanged();
                        } else {
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
                };
                databaseReference.child(textologo.getText().toString()).child("Datos Turnos").addValueEventListener(milistener);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(milistener != null){
            databaseReference.child(textologo.getText().toString()).child("Datos Turnos").removeEventListener(milistener);
        }
    }

    private void setearfecha(@NotNull Calendar calendar) {
        DateFormat formato = DateFormat.getDateInstance(DateFormat.FULL);
        setFecha.setText(formato.format(calendar.getTime()));
    }

    private void iniciarFirebase() {
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
