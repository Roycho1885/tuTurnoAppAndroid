package com.tuTurno.app.Admin.fragmentsConfig;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuTurno.app.ListViewAdaptadorCuotasAdmin;
import com.tuTurno.app.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import models.CuotaConfig;
import models.cliente;
import models.cuotas;

public class AdminCuotas extends Fragment {
    private DatabaseReference databaseReference;
    private EditText buscar, fechapago;
    private Button agregarpago;
    private ScrollView otroscroll;
    private models.cuotas cuotas = new cuotas();
    Context micontexto;
    cliente cli;
    Calendar micalendario, micalendario1;
    String mess, anioo, fechavenc, nombreyapellido, emailcliente, disci1, idcuot, diasporsem, montocuota, dia;
    boolean bandera, bandera1, bandera3, menueli;
    boolean menucuotabool = false;
    Date fechaactual, fechavence, fechaultipago, fechaelegida;
    private ArrayList<String> arraydisci;
    private ArrayList<String> arraycuotas;
    private ArrayList<String> dias;
    private ArrayList<String> montos;
    private ArrayAdapter miadapter;
    private ArrayAdapter miadaptercuotas;
    private TextView registros;

    //CREO UN EVENTLISTENER
    private ValueEventListener milistener;


    //para el listview
    private final ArrayList<cliente> listaclientes = new ArrayList<>();
    private ListView milistacuotasadmin;
    private ListViewAdaptadorCuotasAdmin adaptador;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        micontexto = context;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.admincuotas, container, false);
        final AutoCompleteTextView menudisci = root.findViewById(R.id.botondisciplina);
        final AutoCompleteTextView menucuota = root.findViewById(R.id.botoncuotasdisci);


        //para los diferentes gimnasios
        final TextView gimnasio = requireActivity().findViewById(R.id.textologo);
        otroscroll = root.findViewById(R.id.scroll);
        milistacuotasadmin = root.findViewById(R.id.listacuotasclientes);
        agregarpago = root.findViewById(R.id.botonagregarpago);
        buscar = root.findViewById(R.id.txtbuscar);
        fechapago = root.findViewById(R.id.txtfecha);
        registros = root.findViewById(R.id.registros);

        adaptador = new ListViewAdaptadorCuotasAdmin(micontexto, listaclientes);
        micalendario = Calendar.getInstance();

        iniciarFirebase();

        menudisci.post(() -> menudisci.getText().clear());
        menucuota.post(() -> menucuota.getText().clear());

        //INICIALIZO LOS ARRAYS DISCIPLINA Y CUOTAS
        arraydisci = new ArrayList<>();
        arraycuotas = new ArrayList<>();
        dias = new ArrayList<>();
        montos = new ArrayList<>();

        otroscroll.setOnTouchListener((v, event) -> {
            milistacuotasadmin.getParent()
                    .requestDisallowInterceptTouchEvent(false);
            return false;
        });

        milistacuotasadmin.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });


        //CARGO LAS DISCIPLINAS
        databaseReference.child(gimnasio.getText().toString()).child("Disciplinas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot shot : snapshot.getChildren()) {
                    arraydisci.add(Objects.requireNonNull(shot.getKey()));
                }
                miadapter = new ArrayAdapter<>(micontexto, android.R.layout.simple_list_item_1, arraydisci);
                menudisci.setAdapter(miadapter);
                menudisci.setInputType(InputType.TYPE_NULL);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        //CARGO LAS CUOTAS CUANDO PRESIONE DISCIPLINA
        menudisci.setOnItemClickListener((adapterView, view, i, l) -> {
            menueli = true;
            disci1 = adapterView.getAdapter().getItem(i).toString().trim();
            menucuota.post(() -> menucuota.getText().clear());
            arraycuotas.clear();
            dias.clear();
            montos.clear();
            //REFERENCIA PARA BUSCAR DISCIPLINA E ID DE CUOTA Y DISCIPLINA ELEGIDA
            databaseReference.child(gimnasio.getText().toString()).child("ConfigCuota").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot shot : snapshot.getChildren()) {
                        String diciplina = shot.child("disciplina").getValue(String.class);
                        assert diciplina != null;
                        if (diciplina.equals(disci1)) {
                            idcuot = shot.child("id").getValue(String.class);

                            //CARGO EL MENU DE CUOTAS CON REFERENCIA HACIA LAS CUOTAS
                            databaseReference.child(gimnasio.getText().toString()).child("ConfigCuota").child(idcuot).child("configuracioncuotas").orderByChild("diasporsemana").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot shot : snapshot.getChildren()) {
                                        CuotaConfig cuota = shot.getValue(CuotaConfig.class);
                                        assert cuota != null;
                                        arraycuotas.add(cuota.getDiasporsemana() + " dias " + "$ " + cuota.getMonto());
                                        dias.add(cuota.getDiasporsemana());
                                        montos.add(cuota.getMonto());
                                    }
                                    miadaptercuotas = new ArrayAdapter<>(micontexto, android.R.layout.simple_list_item_1, arraycuotas);
                                    menucuota.setAdapter(miadaptercuotas);
                                    menucuota.setInputType(InputType.TYPE_NULL);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        //BOTON CUOTAS
        menucuota.setOnItemClickListener((parent, view, i, id) -> {
            diasporsem = parent.getAdapter().getItem(i).toString();
            dia = dias.get(i);
            montocuota = montos.get(i);
            menucuotabool = true;
        });

        //CARGO LISTVIEW CON CLIENTES
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy");
        micalendario1 = Calendar.getInstance();
        milistener = new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaclientes.clear();
                int contador = 0;
                for (DataSnapshot shot : snapshot.getChildren()) {
                    if (gimnasio.getText().toString().equals(shot.child("gym").getValue()) && shot.child("admin").getValue().equals("No")) {
                        cli = shot.getValue(cliente.class);
                        contador = contador + 1;
                        String fecha_actual = sdf.format(micalendario1.getTime());
                        String fecha_vence = cli.getFechavencimiento();
                        String fecha_ulti = cli.getUltimopago();


                        if (fecha_vence.equals("Nunca")) {
                            cli.setEstadopago(R.drawable.ic_baseline_cancel_24);
                        } else {
                            try {
                                fechaactual = sdf.parse(fecha_actual);
                                fechavence = sdf.parse(fecha_vence);
                                fechaultipago = sdf.parse(fecha_ulti);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (fechaactual.compareTo(fechavence) < 0) {
                                cli.setEstadopago(R.drawable.ic_baseline_check_circle_24);
                            } else {
                                cli.setEstadopago(R.drawable.ic_baseline_cancel_24);
                                cliente clien = new cliente(cli.getId(), cli.getNombre(), cli.getApellido(), cli.getDni(), cli.getDireccion(),
                                        cli.getEmail(), cli.getGym(), cli.getAdmin(), cli.getToken(), cli.getUltimopago(), cli.getFechavencimiento(), cli.getEstadopago(), "Debe", cli.getDisciplinaelegida(), cli.getDiasporsemana(),cli.getDiasporsemanaresg(), cli.getTelefono());
                                databaseReference.child("Clientes").child(cli.getId()).setValue(clien);
                            }
                        }
                        registros.setText(contador + " clientes listados");
                        listaclientes.add(cli);
                        milistacuotasadmin.setAdapter(adaptador);
                        adaptador.notifyDataSetChanged();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.child("Clientes").orderByChild("apellido").addValueEventListener(milistener);

        final DatePickerDialog.OnDateSetListener date = (view, ano, mes, diames) -> {
            micalendario.set(Calendar.YEAR, ano);
            micalendario.set(Calendar.MONTH, mes);
            micalendario.set(Calendar.DAY_OF_MONTH, diames);
            actualizarformatofecha();
        };


        //BOTON FECHA DE PAGO
        fechapago.setOnClickListener(view -> new DatePickerDialog(micontexto, date, micalendario
                .get(Calendar.YEAR), micalendario.get(Calendar.MONTH),
                micalendario.get(Calendar.DAY_OF_MONTH)).show());

        //BOTON BUSCAR
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

        //CLICK EN LA LISTA DE CLIENTES
        milistacuotasadmin.setOnItemClickListener((adapterView, view, i, l) -> {
            cli = listaclientes.get(i);
            adapterView.setSelected(true);
            bandera = adapterView.isClickable();
            nombreyapellido = cli.getApellido() + " " + cli.getNombre();
            emailcliente = cli.getEmail();
        });


        //CLICK EN BOTON AGREGAR PAGO
        agregarpago.setOnClickListener(view -> {
            bandera3 = false;
            if (!menueli) {
                Snackbar.make(view, "Seleccione una disciplina", Snackbar.LENGTH_SHORT).show();
            } else {
                if (!menucuotabool) {
                    Snackbar.make(view, "Seleccione una cuota", Snackbar.LENGTH_SHORT).show();
                } else {
                    if (!bandera1) {
                        Snackbar.make(view, "Seleccione una fecha", Snackbar.LENGTH_SHORT).show();
                    } else {
                        if (!bandera) {
                            Snackbar.make(view, "Seleccione un cliente", Snackbar.LENGTH_SHORT).show();
                        } else {
                            //CONTROLO QUE EL CLIENTE YA TENGA EL PAGO EFECTUADO
                            databaseReference.child(gimnasio.getText().toString()).child("Cuotas").child(anioo.trim()).child(mess.trim()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot shot : snapshot.getChildren()) {
                                        cuotas cu = shot.getValue(cuotas.class);
                                        assert cu != null;
                                        if (cu.getEmailcliente().equals(emailcliente)) {
                                            if (cu.getMespago().equals(mess)) {
                                                bandera3 = true;
                                            }
                                        }
                                    }
                                    if (bandera3) {
                                        Snackbar.make(view, "Para este mes y cliente ya existe un pago registrado.", Snackbar.LENGTH_SHORT).show();
                                    } else {
                                        databaseReference.child(gimnasio.getText().toString()).child("ConfigCuota").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot shot : snapshot.getChildren()) {
                                                    String diciplina = shot.child("disciplina").getValue(String.class);

                                                    assert diciplina != null;
                                                    if (diciplina.equals(disci1)) {
                                                        if (!cli.getDisciplinaelegida().equals("-")) {
                                                            if (!cli.getDisciplinaelegida().equals(disci1)) {
                                                                androidx.appcompat.app.AlertDialog.Builder mensaje = new AlertDialog.Builder(new ContextThemeWrapper(requireActivity(), R.style.AlertDialogCustom));
                                                                mensaje.setTitle("Atención!");
                                                                mensaje.setIcon(R.drawable.ic_baseline_warning_24);
                                                                mensaje.setMessage("¿Cambiar disciplina del cliente?");
                                                                mensaje.setPositiveButton("Si", (dialogInterface, i) -> {
                                                                    if (cli.getUltimopago().equals("Nunca")) {
                                                                        cliente clien = new cliente(cli.getId(), cli.getNombre(), cli.getApellido(), cli.getDni(), cli.getDireccion(),
                                                                                cli.getEmail(), cli.getGym(), cli.getAdmin(), cli.getToken(), fechapago.getText().toString(), fechavenc, cli.getEstadopago(), "OK", disci1, dia,dia, cli.getTelefono());
                                                                        databaseReference.child("Clientes").child(cli.getId()).setValue(clien);
                                                                    } else {
                                                                        if (!fechaelegida.before(fechaultipago)) {
                                                                            cliente clien = new cliente(cli.getId(), cli.getNombre(), cli.getApellido(), cli.getDni(), cli.getDireccion(),
                                                                                    cli.getEmail(), cli.getGym(), cli.getAdmin(), cli.getToken(), fechapago.getText().toString(), fechavenc, cli.getEstadopago(), "OK", disci1, dia,dia,cli.getTelefono());
                                                                            databaseReference.child("Clientes").child(cli.getId()).setValue(clien);
                                                                        }
                                                                    }
                                                                    cuotas = new cuotas(nombreyapellido, cli.getEmail(), fechapago.getText().toString(), fechavenc, mess, disci1, montocuota,dia);
                                                                    databaseReference.child(gimnasio.getText().toString()).child("Cuotas").child(anioo.trim()).child(mess.trim()).push().setValue(cuotas);
                                                                    Snackbar.make(view, "Pago registrado correctamente", Snackbar.LENGTH_SHORT).show();

                                                                });
                                                                mensaje.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
                                                                AlertDialog dialog = mensaje.create();
                                                                dialog.show();
                                                            } else {
                                                                if (cli.getUltimopago().equals("Nunca")) {
                                                                    cliente clien = new cliente(cli.getId(), cli.getNombre(), cli.getApellido(), cli.getDni(), cli.getDireccion(),
                                                                            cli.getEmail(), cli.getGym(), cli.getAdmin(), cli.getToken(), fechapago.getText().toString(), fechavenc, cli.getEstadopago(), "OK", disci1, dia,dia,cli.getTelefono());
                                                                    databaseReference.child("Clientes").child(cli.getId()).setValue(clien);
                                                                } else {
                                                                    if (!fechaelegida.before(fechaultipago)) {
                                                                        cliente clien = new cliente(cli.getId(), cli.getNombre(), cli.getApellido(), cli.getDni(), cli.getDireccion(),
                                                                                cli.getEmail(), cli.getGym(), cli.getAdmin(), cli.getToken(), fechapago.getText().toString(), fechavenc, cli.getEstadopago(), "OK", disci1, dia,dia,cli.getTelefono());
                                                                        databaseReference.child("Clientes").child(cli.getId()).setValue(clien);
                                                                    }
                                                                }
                                                                cuotas = new cuotas(nombreyapellido, cli.getEmail(), fechapago.getText().toString(), fechavenc, mess, disci1, montocuota, dia);
                                                                databaseReference.child(gimnasio.getText().toString()).child("Cuotas").child(anioo.trim()).child(mess.trim()).push().setValue(cuotas);
                                                                Snackbar.make(view, "Pago registrado correctamente", Snackbar.LENGTH_SHORT).show();
                                                            }
                                                        } else {
                                                            cliente clien = new cliente(cli.getId(), cli.getNombre(), cli.getApellido(), cli.getDni(), cli.getDireccion(),
                                                                    cli.getEmail(), cli.getGym(), cli.getAdmin(), cli.getToken(), fechapago.getText().toString(), fechavenc, cli.getEstadopago(), "OK", disci1, dia,dia,cli.getTelefono());
                                                            databaseReference.child("Clientes").child(cli.getId()).setValue(clien);
                                                            cuotas = new cuotas(nombreyapellido, cli.getEmail(), fechapago.getText().toString(), fechavenc, mess, disci1, montocuota, dia);
                                                            databaseReference.child(gimnasio.getText().toString()).child("Cuotas").child(anioo.trim()).child(mess.trim()).push().setValue(cuotas);
                                                            Snackbar.make(view, "Pago registrado correctamente", Snackbar.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }
                    }
                }
            }
        });

        return root;
    }

    private void iniciarFirebase() {
        FirebaseApp.initializeApp(requireActivity());
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.keepSynced(true);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (milistener != null) {
            databaseReference.child("Clientes").removeEventListener(milistener);
        }
    }

    private void actualizarformatofecha() {
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy");
        fechapago.setText(sdf.format(micalendario.getTime()));
        fechaelegida = micalendario.getTime();
        int index1 = fechapago.getText().toString().indexOf("-", 3);
        mess = fechapago.getText().toString().substring(3, index1);
        anioo = fechapago.getText().toString().substring(index1 + 1);
        bandera1 = true;

        //sumar 30 dias
        micalendario.setTime(micalendario.getTime());
        micalendario.add(Calendar.DAY_OF_YEAR, 30);
        fechavenc = (sdf.format(micalendario.getTime()));

    }
}
