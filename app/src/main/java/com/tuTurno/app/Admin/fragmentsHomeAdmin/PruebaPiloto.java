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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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
import com.tuTurno.app.R;

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
import models.MisFunciones;
import models.cliente;
import models.gimnasios;

public class PruebaPiloto extends Fragment {
    private ImageButton btnImgconTurno, btnImgsinTurno;
    private Button btnconturno, btnsinturno;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private TextView cliente_admin, textologo;
    private Context micontexto;
    private CollapsingToolbarLayout tool;
    private NavigationView navi;
    private cliente c, cli = new cliente();
    private String urldire, user, fecha_venci, fechaactual, fecha_ulti, fecha;
    Date fechaultimopago, fechaact, fechavencimiento;
    MisFunciones cargarNav = new MisFunciones();
    MisFunciones enviarno = new MisFunciones();
    private DatosNotificaciones datos = new DatosNotificaciones();
    private boolean clienteres;
    public long diferencia;
    Calendar micalendario, micalendario1, micalendario2;
    int numeromes, numeroactmes;
    ProgressDialog cargando;

    //LISTAS PARA LAS NOTIFICACIONES
    private ArrayList<String> tokensdeudadebe;
    private ArrayList<String> token3dias;
    private ArrayList<String> token2dias;
    private ArrayList<String> token1dias;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        micontexto = context;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.pruebapiloto, container, false);
        final FloatingActionButton fab = requireActivity().findViewById(R.id.fab_admin);
        btnImgconTurno = root.findViewById(R.id.btnImgconturno);
        btnImgsinTurno = root.findViewById(R.id.btnImgsinturno);
        btnconturno = root.findViewById(R.id.btnconturno);
        btnsinturno = root.findViewById(R.id.btnsinturno);
        cliente_admin = requireActivity().findViewById(R.id.versianda);
        tool = requireActivity().findViewById(R.id.CollapsingToolbar);
        navi = requireActivity().findViewById(R.id.nav_view_admin);

        View head = navi.getHeaderView(0);
        final ImageView fondo = head.findViewById(R.id.fondo);
        final ImageView logo = head.findViewById(R.id.imageViewlogo);
        textologo = head.findViewById(R.id.textologo);
        final TextView txtdirecli = head.findViewById(R.id.textodire);
        fab.setVisibility(View.GONE);
        clienteres = false;

        iniciarFirebase();
        cargando = new ProgressDialog(micontexto);
        user = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail();

        micalendario = Calendar.getInstance();
        micalendario1 = Calendar.getInstance();
        micalendario2 = Calendar.getInstance();

        Calendar calendar = Calendar.getInstance();
        DateFormat formato = DateFormat.getDateInstance(DateFormat.FULL);
        fechaactual = formato.format(calendar.getTime());

        tokensdeudadebe = new ArrayList<>();
        token3dias = new ArrayList<>();
        token2dias = new ArrayList<>();
        token1dias = new ArrayList<>();

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
                        assert cli != null;
                        //CARGO NOMBRE, GIMNASIO E IMAGEN AL HEADER
                        cliente_admin.setText(getString(R.string.cliente) + " " + c.getNombre());
                        tool.setTitle(c.getGym());
                        textologo.setText(c.getGym());

                        if (cli.getAdmin().equals("Restringido")) {
                            fab.setClickable(false);
                        }
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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        //BOTON DIRECCION DEL NAVHEADER
        txtdirecli.setOnClickListener(view -> {
            Intent j = new Intent(Intent.ACTION_VIEW, Uri.parse(urldire));
            startActivity(j);
        });

        btnImgconTurno.setOnClickListener(view -> {
            androidx.appcompat.app.AlertDialog.Builder mensaje = new AlertDialog.Builder(new ContextThemeWrapper(requireActivity(), R.style.AlertDialogCustom));
            mensaje.setTitle("Información");
            mensaje.setIcon(R.drawable.ic_baseline_info);
            mensaje.setMessage("Se registrará al cliente con uno de los turnos que se hayan creado, previa verificación de su estado.");
            mensaje.setPositiveButton("Ok", (dialogInterface, t) -> dialogInterface.dismiss());
            AlertDialog dialog = mensaje.create();
            dialog.show();
        });

        btnImgsinTurno.setOnClickListener(view -> {
            androidx.appcompat.app.AlertDialog.Builder mensaje = new AlertDialog.Builder(new ContextThemeWrapper(requireActivity(), R.style.AlertDialogCustom));
            mensaje.setTitle("Información");
            mensaje.setIcon(R.drawable.ic_baseline_info);
            mensaje.setMessage("Se registrará al cliente con sus datos, hora y fecha de ingreso, previa verificación de su estado.");
            mensaje.setPositiveButton("Ok", (dialogInterface, t) -> dialogInterface.dismiss());
            AlertDialog dialog = mensaje.create();
            dialog.show();
        });

        btnconturno.setOnClickListener(view -> Navigation.findNavController(view).navigate(R.id.fragment_admin));

        btnsinturno.setOnClickListener(view -> Navigation.findNavController(view).navigate(R.id.fragment_admin_sintur));

        //LECTURA CLIENTES PARA NOTIFICACIONES
        databaseReference.child("Clientes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot shot : snapshot.getChildren()) {
                    c = shot.getValue(cliente.class);
                    assert c != null;
                    if (c.getEmail().equals(user)) {
                        if (c.getAdmin().equals("Restringido"))
                            clienteres = true;
                    }

                    if (c.getGym().equals(textologo.getText().toString()) && c.getAdmin().equals("No")
                            && c.getEstadodeuda().equals("OK")) {
                        fecha_venci = c.getFechavencimiento();

                        try {
                            fechaact = sdf.parse(fecha_actual);
                            fechavencimiento = sdf.parse(fecha_venci);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        //SACO DIFERENCIA DE DIAS PARA ENVIAR NOTIFICACIONES A LAS CUOTAS PRÓXIMAS A VENCER
                        assert fechavencimiento != null;
                        long diff = fechavencimiento.getTime() - fechaact.getTime();
                        TimeUnit time = TimeUnit.DAYS;
                        diferencia = time.convert(diff, TimeUnit.MILLISECONDS);

                        switch ((int) diferencia) {
                            case 1:
                                token1dias.add(c.getToken());
                                break;
                            case 2:
                                token2dias.add(c.getToken());
                                break;
                            case 3:
                                token3dias.add(c.getToken());
                                break;
                        }
                    }

                    //COLECTO LOS TOKENS PARA NOTIFICACIONES SEGUN ESTADO DEUDA
                    if (c.getGym().equals(textologo.getText().toString()) && c.getAdmin().equals("No")) {
                        if (c.getEstadodeuda().equals("Debe") || c.getEstadodeuda().equals("0")) {
                            fecha_ulti = c.getUltimopago();

                            //CALCULAMOS LOS MESES QUE EL CLIENTE ESTA SIN ACTIVIDAD
                            try {
                                fechaact = sdf.parse(fecha_actual);
                                fechaultimopago = sdf.parse(fecha_ulti);
                                assert fechaultimopago != null;
                                micalendario.setTime(fechaultimopago);
                                micalendario1.setTime(fechaact);
                                //SACAMOS LOS MESES DE FECHA ACTUAL Y FECHA VENCIMIENTO
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

                //ENVIO NOTIFICACIONES UNA SOLA VEZ AL DIA
                databaseReference.child(textologo.getText().toString()).child("DatosNotificaciones").child("FechaEnvio").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("fecha")) {
                            fecha = Objects.requireNonNull(snapshot.child("fecha").getValue()).toString();

                            if (!fecha.equals(fechaactual)) {
                                //ENVIO NOTIFICACIONES PARA LOS CLIENTES CON DEUDA A VENCER 3-2-1 DIA
                                if (token1dias.size() != 0 || token2dias.size() != 0 || token3dias.size() != 0) {
                                    if (!clienteres) {
                                        notificacionsegunddias((int) diferencia, container);
                                    }
                                }
                                datos.setFecha(fechaactual);
                                databaseReference.child(textologo.getText().toString()).child("DatosNotificaciones").child("FechaEnvio").setValue(datos);

                                //ENVIO NOTIFICACIONES PARA DEUDORES LOS DIAS LUNES-MIERCOLES-VIERNES
                                if (micalendario2.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY || micalendario2.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY
                                        || micalendario2.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
                                    //ENVIAMOS NOTIFICACION SI LA LISTA DE TOKEN DEUDORES NO ESTA VACIA
                                    if (tokensdeudadebe.size() != 0 && !clienteres) {
                                        androidx.appcompat.app.AlertDialog.Builder mensaje = new AlertDialog.Builder(new ContextThemeWrapper(requireActivity(), R.style.AlertDialogCustom));
                                        mensaje.setTitle("Atención!");
                                        mensaje.setIcon(R.drawable.ic_baseline_notification_important_24);
                                        mensaje.setMessage("¿Desea enviar notificaciones a los deudores?");
                                        mensaje.setPositiveButton("Si", (dialogInterface, i) -> {
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
                            }
                        } else {
                            datos.setFecha(fechaactual);
                            databaseReference.child(textologo.getText().toString()).child("DatosNotificaciones").child("FechaEnvio").setValue(datos);
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

        return root;
    }

    private void iniciarFirebase() {
        FirebaseApp.initializeApp(requireActivity());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference.keepSynced(true);
    }

    private void notificacionsegunddias(int dias, View view) {
        androidx.appcompat.app.AlertDialog.Builder mensaje = new AlertDialog.Builder(new ContextThemeWrapper(requireActivity(), R.style.AlertDialogCustom));
        mensaje.setTitle("Atención!");
        mensaje.setIcon(R.drawable.ic_baseline_notification_important_24);
        mensaje.setMessage("¿Desea enviar notificaciones a los clientes con cuotas por vencer?");
        mensaje.setPositiveButton("Si", (dialogInterface, i) -> {
            cargando.setTitle("Enviando...");
            cargando.setMessage("Espere por favor...");
            cargando.show();

            switch (dias) {
                case 1:
                    for (int u = 0; u < token1dias.size(); u++) {
                        enviarno.enviarnotificacionapi(token1dias.get(u), "Atención!", "Tu cuota vencerá en un día. Renuévala para evitar inconvenientes", view, cargando);
                    }
                case 2:
                    for (int u = 0; u < token2dias.size(); u++) {
                        enviarno.enviarnotificacionapi(token2dias.get(u), "Atención!", "Tu cuota vencerá en dos días. Renuévala para evitar inconvenientes", view, cargando);
                    }
                case 3:
                    for (int u = 0; u < token3dias.size(); u++) {
                        enviarno.enviarnotificacionapi(token3dias.get(u), "Atención!", "Tu cuota vencerá en tres días. Renuévala para evitar inconvenientes", view, cargando);
                    }
            }

        });

        mensaje.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());

        AlertDialog dialog = mensaje.create();
        dialog.show();
    }
}
