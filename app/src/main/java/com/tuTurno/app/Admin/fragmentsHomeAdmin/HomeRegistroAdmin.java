package com.tuTurno.app.Admin.fragmentsHomeAdmin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

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
import com.tuTurno.app.Admin.ActividadAdmin;
import com.tuTurno.app.R;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import models.DatosTurno;
import models.cliente;

public class HomeRegistroAdmin extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private TextView setFecha;
    private DatosTurno datosTurno = new DatosTurno();
    private NavigationView navi;
    public EditText txtdni;
    boolean banderaturno, banderacliente;
    String idturno, horaturn, emailcliente;
    Date horaactualturno, horaactualdia;
    Calendar cal, calendario;
    cliente cli = new cliente();
    public static Bundle bun = new Bundle();
    public static NavController nav;

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

        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        cal = Calendar.getInstance();
        calendario = Calendar.getInstance();
        //HORA ACTUAL
        final String horaactual = sdf.format(cal.getTime());

        final FloatingActionButton fab = requireActivity().findViewById(R.id.fab_admin);
        final FloatingActionButton fabregistrar = root.findViewById(R.id.btnflotante1);
        setFecha = root.findViewById(R.id.setfecha);


        navi = requireActivity().findViewById(R.id.nav_view_admin);
        View head = navi.getHeaderView(0);
        final TextView textologo = head.findViewById(R.id.textologo);

        fab.hide();
        iniciarFirebase();
        setearfecha(cal);

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
                                banderacliente = true;
                            }
                        }

                        if (!banderacliente) {
                            Snackbar.make(view, "Usted no se encuentra registrado", Snackbar.LENGTH_LONG).show();
                            txtdni.setText("");
                        } else {
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

                                                //CONVIERTO LA HORA DEL TURNO PARA CONTROLAR CUANDO SE QUIERE REGISTRAR
                                                calendario.set(Calendar.HOUR_OF_DAY, (Integer.parseInt(datosTurno.getTurno().substring(0, 2))));
                                                calendario.set(Calendar.MINUTE, (Integer.parseInt(datosTurno.getTurno().substring(3, 5)) - 15));
                                                calendario.set(Calendar.SECOND, 0);
                                                horaturn = sdf.format(calendario.getTime());

                                                try {
                                                    horaactualturno = sdf.parse(horaturn);
                                                    horaactualdia = sdf.parse(horaactual);
                                                } catch (
                                                        ParseException e) {
                                                    e.printStackTrace();
                                                }

                                                assert horaactualdia != null;
                                                if (horaactualturno.before(horaactualdia)) {
                                                    HashMap<String, Object> hashMap = new HashMap<String, Object>();
                                                    hashMap.put("asistencia", "Si");
                                                    databaseReference.child(textologo.getText().toString()).child("Datos Turnos").child(idturno).updateChildren(hashMap).addOnSuccessListener(o -> {
                                                    });
                                                    Snackbar.make(view, "Turno registrado", Snackbar.LENGTH_SHORT).show();
                                                } else {
                                                    Snackbar.make(view, "Solo se registra 15 min antes de tu turno", Snackbar.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                if (!banderaturno) {
                                                    Snackbar bar = Snackbar.make(view, "Usted no posee un turno", Snackbar.LENGTH_LONG);
                                                    obtenerdatos(emailcliente, textologo.getText().toString());
                                                    bar.setAction(R.string.pedturno, new pedirturno());
                                                    bar.show();
                                                }
                                            }
                                            txtdni.setText("");
                                        }
                                    } else {
                                        Snackbar bar = Snackbar.make(view, "Usted no posee un turno", Snackbar.LENGTH_LONG);
                                        obtenerdatos(emailcliente, textologo.getText().toString());
                                        bar.setAction(R.string.pedturno, new pedirturno());
                                        bar.show();
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
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference.keepSynced(true);
    }

    public static class pedirturno implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            nav.navigate(R.id.AdminPedirturno, bun);
        }
    }

    private void obtenerdatos(String email, String gimnasio) {
        bun.putString("clienteemail", email);
        bun.putString("gimnasionombre", gimnasio);
    }

}


