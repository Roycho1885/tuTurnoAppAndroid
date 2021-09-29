package com.tuTurno.app.Admin.fragmentsHomeAdmin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuTurno.app.ListViewAdaptadorHF;
import com.tuTurno.app.R;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import models.DatosTurno;
import models.cliente;
import models.turno;

public class ClienteTurnoAdmin extends Fragment {
    private TextView setFecha;
    private TextView setdisciplina;
    private FloatingActionButton fab;
    private ImageView imagenfindee;

    private ArrayList<turno> listturnos = new ArrayList<>();
    private turno turnoselecc;
    boolean band1, finde, deuda, band;
    private String user;
    Calendar cal, micalendario;
    private cliente c, cli = new cliente();
    private DatosTurno datosturno = new DatosTurno();
    String horaturno, fecha_vence, disci1, textologo, diasporsemana, nombre,apellido,direccion,DNI, idcliente;
    private Date horaactual3, horaactual1, fechaactual, fechavence;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    private ScrollView otroscroll;
    private Context micontexto;
    //CREO UN EVENTLISTENER
    private ValueEventListener milistener;

    //para el listview
    private ListView milistaturnos;
    private ListViewAdaptadorHF adaptador;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        micontexto = context;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.clienteturnoadmin, container, false);
        setdisciplina = root.findViewById(R.id.setdisciplina);
        otroscroll = root.findViewById(R.id.otroscroll);
        CardView car = root.findViewById(R.id.cardview);
        imagenfindee = root.findViewById(R.id.imagenfindee);

        //ESTO ES PARA EL LISTVIEW
        milistaturnos = root.findViewById(R.id.listaturnos);
        milistaturnos.setVisibility(View.VISIBLE);
        imagenfindee.setVisibility(View.GONE);

        fab = requireActivity().findViewById(R.id.fab_admin);
        fab.setImageResource(R.drawable.ic_add_black_24dp);
        fab.show();
        fab.setEnabled(true);
        fab.setVisibility(View.VISIBLE);
        car.setVisibility(View.VISIBLE);

        setFecha = root.findViewById(R.id.setfecha);
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdff = new SimpleDateFormat("dd-MMMM-yyyy");
        final String horaactual2;

        iniciarFirebase();

        //ListView Click
        adaptador = new ListViewAdaptadorHF(micontexto, listturnos);
        milistaturnos.setOnItemClickListener((adapterView, view, i, l) -> {
            turnoselecc = listturnos.get(i);
            adapterView.setSelected(true);
            if (adapterView.isClickable()) {
                band1 = true;
            }
        });
        if (getArguments()!= null){
            user = getArguments().getString("clienteemail");
            textologo = getArguments().getString("gimnasionombre");
        }


        otroscroll.setOnTouchListener((v, event) -> {
            milistaturnos.getParent()
                    .requestDisallowInterceptTouchEvent(false);
            return false;
        });

        milistaturnos.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        cal = Calendar.getInstance();
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            fab.setVisibility(View.GONE);
            car.setVisibility(View.GONE);
            imagenfindee.setVisibility(View.VISIBLE);
            imagenfindee.setBackgroundResource(R.drawable.descanso);
            milistaturnos.setVisibility(View.GONE);
            finde = true;
            assert container != null;
            Snackbar.make(container, "El fin de semana no se asignan turnos", Snackbar.LENGTH_SHORT).show();
        } else {
            setearfecha(cal);
        }

        final String horaactual = sdf.format(cal.getTime());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 0);
        horaactual2 = sdf.format(cal.getTime());

        try {
            horaactual3 = sdf.parse(horaactual2);
            horaactual1 = sdf.parse(horaactual);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //LECTURA DEL CLIENTE
        micalendario = Calendar.getInstance();
        final String fecha_actual = sdff.format(micalendario.getTime());
        databaseReference.child("Clientes").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot shot : snapshot.getChildren()) {

                    c = shot.getValue(cliente.class);
                    assert c != null;

                    if (c.getEmail().equals(user)) {

                        cli = shot.getValue(cliente.class);
                        assert cli != null;
                        fecha_vence = cli.getFechavencimiento();
                        disci1 = cli.getDisciplinaelegida();
                        setdisciplina.setText(cli.getDisciplinaelegida());

                        try {
                            fechaactual = sdff.parse(fecha_actual);
                            fechavence = sdff.parse(fecha_vence);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (fecha_vence.equals("Nunca")) {
                            fab.setVisibility(View.GONE);
                            androidx.appcompat.app.AlertDialog.Builder mensaje = new AlertDialog.Builder(new ContextThemeWrapper(requireActivity(), R.style.AlertDialogCustom));
                            mensaje.setTitle("Atención!");
                            mensaje.setIcon(R.drawable.ic_baseline_cancel_24);
                            mensaje.setMessage("No posee abono mensual, comuníquese con Administración");
                            mensaje.show();
                        } else {
                            if (fechaactual.compareTo(fechavence) > 0) {
                                fab.setVisibility(View.GONE);
                                deuda = true;
                                androidx.appcompat.app.AlertDialog.Builder mensaje = new AlertDialog.Builder(new ContextThemeWrapper(requireActivity(), R.style.AlertDialogCustom));
                                mensaje.setTitle("Atención!");
                                mensaje.setIcon(R.drawable.ic_baseline_cancel_24);
                                mensaje.setMessage("Cuota vencida, comuníquese con Administración");
                                mensaje.show();
                            }
                        }
                        nombre = c.getNombre();
                        apellido = c.getApellido();
                        direccion = c.getDireccion();
                        DNI = c.getDni();
                        diasporsemana = c.getDiasporsemana();
                        idcliente = c.getId();
                    }
                }

                if (!finde) {
                    listarturnos(textologo, disci1, setFecha.getText().toString(), container);
                }


                //REVISO SI EL CLIENTE YA TIENE UN TURNO PARA EL DIA ACTUAL
                band = false;
                databaseReference.child(textologo).child("Datos Turnos").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot shot) {
                        for (DataSnapshot shot1 : shot.getChildren()) {
                            assert user != null;
                            if (user.equals(shot1.child("cliente").getValue()) && (setFecha.getText().toString().equals(shot1.child("fecha").getValue()))) {
                                band = true;
                                assert container != null;
                                Snackbar.make(container, "Usted ya posee un turno para este día, revise en la sección Mi Turno", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (!finde) {
            assert horaactual1 != null;
            if (horaactual1.compareTo(horaactual3) <= 0) {

                //REVISO SI EL CLIENTE ACTUAL TIENE TURNO PARA EL DIA
                fab.setOnClickListener(new View.OnClickListener() {
                    final Calendar calendar = Calendar.getInstance();
                    String horaturn;

                    @Override
                    public void onClick(View view) {
                        if (!band1) {
                            assert container != null;
                            Snackbar.make(container, "Seleccione un turno", Snackbar.LENGTH_SHORT).show();
                        } else {
                            final turno tur = new turno();
                            tur.setId(turnoselecc.getId());
                            int cupo = Integer.parseInt(turnoselecc.getCupo());

                            //TOMO LA HORA DEL TURNO Y LA CONVIERTO
                            calendar.set(Calendar.HOUR_OF_DAY, (Integer.parseInt(turnoselecc.getHoracomienzo().substring(0, 2))));
                            calendar.set(Calendar.MINUTE, (Integer.parseInt(turnoselecc.getHoracomienzo().substring(3, 5))));
                            calendar.set(Calendar.SECOND, 0);
                            horaturn = sdf.format(calendar.getTime());

                            try {
                                horaactual3 = sdf.parse(horaturn);
                                horaactual1 = sdf.parse(horaactual);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            //CARGO TURNO SI ESTA DENTRO DEL HORARIO Y REVISO SI EL CLIENTE YA TIENE UN TURNO
                            assert horaactual1 != null;
                            if (Integer.parseInt(diasporsemana) == 0) {
                                assert container != null;
                                Snackbar.make(container, "Ya ocupo todos sus dias", Snackbar.LENGTH_SHORT).show();
                            } else {
                                if (band) {
                                    assert container != null;
                                    Snackbar.make(container, "Usted ya posee un turno para este día", Snackbar.LENGTH_SHORT).show();
                                } else {
                                    if (cupo <= 0) {
                                        assert container != null;
                                        Snackbar.make(container, "Ya no hay cupo en este turno", Snackbar.LENGTH_SHORT).show();
                                    } else {
                                        if (horaactual1.compareTo(horaactual3) <= 0) {
                                            assert user != null;
                                            agregardatos(user, turnoselecc.getDisciplina(), turnoselecc.getHoracomienzo(), turnoselecc.getId());
                                            databaseReference.child(textologo).child("Datos Turnos").child(datosturno.getIdTurno()).setValue(datosturno);
                                            tur.setDisciplina(turnoselecc.getDisciplina());
                                            tur.setHoracomienzo(turnoselecc.getHoracomienzo());
                                            tur.setDias(turnoselecc.getDias());
                                            cupo--;
                                            //VERIFICO SI TIENE DIAS EN 5
                                            if (!diasporsemana.equals("5")) {
                                                diasporsemana = String.valueOf(Integer.parseInt(diasporsemana) - 1);
                                                actualizardiaporsemana(idcliente, diasporsemana);
                                            }
                                            tur.setCupo(String.valueOf(cupo));
                                            tur.setCupoalmacenado(turnoselecc.getCupoalmacenado());
                                            tur.setCoach(turnoselecc.getCoach());
                                            databaseReference.child(textologo).child("Disciplinas").child(tur.getDisciplina()).child(tur.getId()).setValue(tur);
                                            assert container != null;
                                            Snackbar.make(container, "Turno registrado correctamente", Snackbar.LENGTH_SHORT).show();
                                            band = true;
                                        } else {
                                            assert container != null;
                                            Snackbar.make(container, "Ya no se puede registrar este turno", Snackbar.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                        }
                    }
                });

            } else {
                //botondisci.setEnabled(false);
                assert container != null;
                Snackbar.make(container, "Ya no se registran turnos, espere hasta las 00hs", Snackbar.LENGTH_SHORT).show();
            }
        }

        return root;
    }

    private void listarturnos(String txtlogo, final String dis, final String fecha, final View container) {
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        final Calendar calendar = Calendar.getInstance();
        final String horaactual = sdf.format(calendar.getTime());
        int posi = fecha.indexOf(",");
        final String diass = fecha.substring(0, posi);

        milistener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listturnos.clear();
                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    turno tur = shot.getValue(turno.class);

                    //TOMO LA HORA DEL TURNO Y LA CONVIERTO
                    assert tur != null;
                    calendar.set(Calendar.HOUR_OF_DAY, (Integer.parseInt(tur.getHoracomienzo().substring(0, 2))));
                    calendar.set(Calendar.MINUTE, (Integer.parseInt(tur.getHoracomienzo().substring(3, 5))));
                    calendar.set(Calendar.SECOND, 0);
                    horaturno = sdf.format(calendar.getTime());

                    try {
                        horaactual3 = sdf.parse(horaturno);
                        horaactual1 = sdf.parse(horaactual);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    if (!tur.getCupo().equals("0")) {
                        if ((horaactual1.compareTo(horaactual3) <= 0)) {
                            tur.setFoto(R.drawable.ic_baseline_check_circle_24);
                        } else {
                            tur.setFoto(R.drawable.ic_baseline_cancel_24);
                        }
                    } else {
                        tur.setFoto(R.drawable.ic_baseline_cancel_24);
                    }


                    if (tur.getDias().toLowerCase().contains(diass) || tur.getDias().toLowerCase().contains("todos")) {
                        listturnos.add(tur);
                        milistaturnos.setAdapter(adaptador);
                    }
                }
                if (listturnos.size() == 0) {
                    imagenfindee.setVisibility(View.VISIBLE);
                    imagenfindee.setBackgroundResource(R.drawable.descanso);
                    milistaturnos.setVisibility(View.GONE);
                    fab.setClickable(false);
                    Snackbar.make(container, "Para este día y esta disciplina no existen turnos", Snackbar.LENGTH_SHORT).show();
                    milistaturnos.setAdapter(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.child(txtlogo).child("Disciplinas").child(dis).orderByChild("horacomienzo").addValueEventListener(milistener);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (milistener != null) {
            databaseReference.child(textologo).child("Disciplinas").child(disci1).orderByChild("horacomienzo").removeEventListener(milistener);
        }

    }

    private void iniciarFirebase() {
        FirebaseApp.initializeApp(requireActivity());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference.keepSynced(true);
    }

    private void setearfecha(@NotNull Calendar calendar) {
        DateFormat formato = DateFormat.getDateInstance(DateFormat.FULL);
        setFecha.setText(formato.format(calendar.getTime()));
    }

    private void agregardatos(@NotNull String user, String dis, String tur, String turselecc) {
        datosturno.setIdTurno(UUID.randomUUID().toString());
        datosturno.setNombre(nombre);
        datosturno.setApellido(apellido);
        datosturno.setDireccionturno(direccion);
        datosturno.setDniturno(DNI);
        datosturno.setCliente(user);
        datosturno.setFecha(setFecha.getText().toString());
        datosturno.setTurno(tur);
        datosturno.setDisciplina(dis);
        datosturno.setIdturnoseleccionado(turselecc);
        datosturno.setAsistencia("No");
    }

    private void actualizardiaporsemana(String id, String diaporsemana) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("diasporsemana", diaporsemana);
        databaseReference.child("Clientes").child(id).updateChildren(hashMap).addOnSuccessListener(o -> {
        });
    }
}
