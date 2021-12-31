package com.tuTurno.app.Admin.fragmentsHomeAdmin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

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
import com.tuTurno.app.R;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import models.DatosSinTurno;
import models.DatosTurno;
import models.cliente;

public class HomeRegistroSinTur extends Fragment {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private TextView setFecha;
    private DatosTurno datosTurno = new DatosTurno();
    private NavigationView navi;
    private static EditText txtdni;
    boolean banderaturno, banderacliente, deuda, band;
    String idturno, emailcliente, fecha_vence, disci1, nombre, apellido, direccion, diasporsemana, DNI, idcliente, horadeldia;
    Calendar cal;
    cliente cli = new cliente();
    public static NavController nav;
    private Date fechaactual, fechavence;
    private DatosSinTurno datosSinTurno = new DatosSinTurno();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nav = NavHostFragment.findNavController(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.homeregistroadmin, container, false);
        txtdni = root.findViewById(R.id.txtdoc);

        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdff = new SimpleDateFormat("dd-MMMM-yyyy");
        cal = Calendar.getInstance();
        final String fecha_actual = sdff.format(cal.getTime());

        final FloatingActionButton fab = requireActivity().findViewById(R.id.fab_admin);
        final FloatingActionButton fabregistrar = root.findViewById(R.id.btnflotante1);
        setFecha = root.findViewById(R.id.setfecha);


        navi = requireActivity().findViewById(R.id.nav_view_admin);
        View head = navi.getHeaderView(0);
        final TextView textologo = head.findViewById(R.id.textologo);

        fab.hide();
        iniciarFirebase();
        setearfecha(cal);
        horadeldia = sdf.format(cal.getTime());


        fabregistrar.setOnClickListener(view -> {
            banderaturno = false;
            banderacliente = false;
            String dni = txtdni.getText().toString();
            if (dni.equals("")) {
                Snackbar.make(view, "Ingrese DNI", Snackbar.LENGTH_SHORT).show();
            } else {
                //VERIFICO SI EL USUARIO ESTA REGISTRADO
                databaseReference.child("Clientes").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot shot : snapshot.getChildren()) {
                            cli = shot.getValue(cliente.class);
                            assert cli != null;
                            if (cli.getDni().equals(dni) && cli.getGym().equals(textologo.getText().toString()) && cli.getAdmin().equals("No")) {
                                emailcliente = cli.getEmail();
                                fecha_vence = cli.getFechavencimiento();
                                disci1 = cli.getDisciplinaelegida();
                                banderacliente = true;

                                try {
                                    fechaactual = sdff.parse(fecha_actual);
                                    fechavence = sdff.parse(fecha_vence);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                if (fecha_vence.equals("Nunca")) {
                                    fab.setVisibility(View.GONE);
                                    deuda = true;
                                    androidx.appcompat.app.AlertDialog.Builder mensaje = new AlertDialog.Builder(new ContextThemeWrapper(requireActivity(), R.style.AlertDialogCustom));
                                    mensaje.setTitle("Atención!");
                                    mensaje.setIcon(R.drawable.ic_baseline_cancel_24);
                                    mensaje.setMessage("No posee abono mensual, comuníquese con Administración");
                                    mensaje.show();
                                    txtdni.setText("");
                                } else {
                                    if (fechaactual.compareTo(fechavence) > 0) {
                                        fab.setVisibility(View.GONE);
                                        deuda = true;
                                        androidx.appcompat.app.AlertDialog.Builder mensaje = new AlertDialog.Builder(new ContextThemeWrapper(requireActivity(), R.style.AlertDialogCustom));
                                        mensaje.setTitle("Atención!");
                                        mensaje.setIcon(R.drawable.ic_baseline_cancel_24);
                                        mensaje.setMessage("Cuota vencida, comuníquese con Administración");
                                        mensaje.show();
                                        txtdni.setText("");
                                    }
                                }
                                nombre = cli.getNombre();
                                apellido = cli.getApellido();
                                direccion = cli.getDireccion();
                                DNI = cli.getDni();
                                diasporsemana = cli.getDiasporsemana();
                                idcliente = cli.getId();
                            }
                        }

                        if (!banderacliente) {
                            Snackbar.make(view, "Usted no se encuentra registrado", Snackbar.LENGTH_LONG).show();
                            txtdni.setText("");
                        } else {
                            //REVISO SI EL CLIENTE YA SE REGISTRO
                            band = false;
                            databaseReference.child(textologo.getText().toString()).child("Datos SinTurno").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot shot) {
                                    for (DataSnapshot shot1 : shot.getChildren()) {
                                        assert emailcliente != null;
                                        if (emailcliente.equals(shot1.child("clienteemail").getValue()) && (setFecha.getText().toString().equals(shot1.child("fecha").getValue()))) {
                                            band = true;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            //VERIFICO EN TURNOS
                            databaseReference.child(textologo.getText().toString()).child("Datos Turnos").orderByChild("fecha").equalTo(setFecha.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot shot : snapshot.getChildren()) {
                                            datosTurno = shot.getValue(DatosTurno.class);
                                            assert datosTurno != null;
                                            if (dni.equals(datosTurno.getDniturno())) {
                                                banderaturno = true;
                                                idturno = datosTurno.getIdTurno();
                                                emailcliente = datosTurno.getCliente();

                                                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                                                hashMap.put("asistencia", "Si");
                                                databaseReference.child(textologo.getText().toString()).child("Datos Turnos").child(idturno).updateChildren(hashMap).addOnSuccessListener(o -> {
                                                });
                                                Snackbar.make(view, "Turno registrado", Snackbar.LENGTH_SHORT).show();
                                                txtdni.setText("");
                                            }
                                        }
                                    }
                                    if (!band && !banderaturno) {
                                        if (!deuda) {
                                            if (Integer.parseInt(diasporsemana) == 0) {
                                                Snackbar bar = Snackbar.make(view, "Ya ocupo todos sus dias", Snackbar.LENGTH_LONG);
                                                bar.show();
                                            } else {
                                                agregardatos(textologo, view);
                                                //VERIFICO SI TIENE DIAS EN 5
                                                if (!diasporsemana.equals("5")) {
                                                    diasporsemana = String.valueOf(Integer.parseInt(diasporsemana) - 1);
                                                    actualizardiaporsemana(idcliente, diasporsemana);
                                                }

                                            }
                                            txtdni.setText("");
                                        }
                                    } else {
                                        if(banderaturno){
                                            assert container != null;
                                            Snackbar.make(container, "Acceso registrado correctamente", Snackbar.LENGTH_LONG).show();
                                        }else{
                                            Snackbar.make(container, "Usted ya se registro", Snackbar.LENGTH_LONG).show();
                                        }
                                        txtdni.setText("");
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
        });

        return root;
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

    private void agregardatos(TextView textologo, View view) {
        datosSinTurno.setId(UUID.randomUUID().toString() + "NOMBRE" + apellido.trim() + nombre.trim());
        datosSinTurno.setNombre(nombre);
        datosSinTurno.setApellido(apellido);
        datosSinTurno.setDni(DNI);
        datosSinTurno.setDisciplina(disci1);
        datosSinTurno.setFecha(setFecha.getText().toString());
        datosSinTurno.setHora(horadeldia);
        datosSinTurno.setClienteemail(emailcliente);

        databaseReference.child(textologo.getText().toString()).child("Datos SinTurno").child(datosSinTurno.getId()).setValue(datosSinTurno);
        Snackbar bar = Snackbar.make(view, "Acceso registrado correctamente", Snackbar.LENGTH_LONG);
        bar.show();
    }

    private void actualizardiaporsemana(String id, String diaporsemana) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("diasporsemana", diaporsemana);
        databaseReference.child("Clientes").child(id).updateChildren(hashMap).addOnSuccessListener(o -> {
        });
    }
}
