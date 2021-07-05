package com.tuTurno.app.Admin.fragmentsConfig;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import models.cliente;
import models.cuotas;

public class AdminCuotas extends Fragment {
    private DatabaseReference databaseReference;
    private EditText buscar,fechapago;
    private Button agregarpago;
    private ScrollView otroscroll;
    private models.cuotas cuotas = new cuotas();
    Context micontexto;
    cliente cli;
    Calendar micalendario,micalendario1;
    String mess,anioo,fechavenc,nombreyapellido,emailcliente;
    boolean bandera,bandera1,bandera3;
    Date fechaactual,fechavence;



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


        //para los diferentes gimnasios
        final TextView gimnasio = requireActivity().findViewById(R.id.textologo);
        otroscroll = root.findViewById(R.id.scroll);
        milistacuotasadmin = root.findViewById(R.id.listacuotasclientes);
        agregarpago = root.findViewById(R.id.botonagregarpago);
        buscar = root.findViewById(R.id.txtbuscar);
        fechapago = root.findViewById(R.id.txtfecha);

        adaptador = new ListViewAdaptadorCuotasAdmin(micontexto,listaclientes);
        micalendario = Calendar.getInstance();

        iniciarFirebase();


        otroscroll.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {
                milistacuotasadmin.getParent()
                        .requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });

        milistacuotasadmin.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        //CARGO LISTVIEW CON CLIENTES
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy");
        micalendario1 = Calendar.getInstance();
        databaseReference.child("Clientes").orderByChild("apellido").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaclientes.clear();
                for (DataSnapshot shot : snapshot.getChildren()) {
                    if (gimnasio.getText().toString().equals(shot.child("gym").getValue()) && shot.child("admin").getValue().equals("No")) {
                        cli = shot.getValue(cliente.class);
                        String fecha_actual = sdf.format(micalendario1.getTime());
                        String fecha_vence = cli.getFechavencimiento();


                        if(fecha_vence.equals("Nunca")){
                            cli.setEstadopago(R.drawable.ic_baseline_cancel_24);
                            cuotas.setEstadopago(R.drawable.ic_baseline_cancel_24);
                        }else {
                            try {
                                fechaactual = sdf.parse(fecha_actual);
                                fechavence = sdf.parse(fecha_vence);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if(fechaactual.compareTo(fechavence)<0){
                                cli.setEstadopago(R.drawable.ic_baseline_check_circle_24);
                                cuotas.setEstadopago(R.drawable.ic_baseline_check_circle_24);
                            }else {
                                cli.setEstadopago(R.drawable.ic_baseline_cancel_24);
                                cuotas.setEstadopago(R.drawable.ic_baseline_cancel_24);
                            }
                        }
                        listaclientes.add(cli);
                        milistacuotasadmin.setAdapter(adaptador);
                        adaptador.notifyDataSetChanged();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final DatePickerDialog.OnDateSetListener date =  new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int ano, int mes, int diames) {
                micalendario.set(Calendar.YEAR,ano);
                micalendario.set(Calendar.MONTH,mes);
                micalendario.set(Calendar.DAY_OF_MONTH,diames);
                actualizarformatofecha();
            }
        };


        //BOTON FECHA DE PAGO
        fechapago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(micontexto, date, micalendario
                        .get(Calendar.YEAR), micalendario.get(Calendar.MONTH),
                        micalendario.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

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
        milistacuotasadmin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                cli = listaclientes.get(i);
                adapterView.setSelected(true);
                bandera = adapterView.isClickable();
                nombreyapellido = cli.getApellido()+" "+cli.getNombre();
                emailcliente = cli.getEmail();
            }
        });

        //CLICK EN BOTON AGREGAR PAGO

        agregarpago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                bandera3 = false;
                if(!bandera1){
                    Snackbar.make(view, "Seleccione una fecha", Snackbar.LENGTH_SHORT).show();
                }else{
                        if(!bandera){
                            Snackbar.make(view, "Seleccione un cliente", Snackbar.LENGTH_SHORT).show();
                        }else{
                            //CONTROLO QUE EL CLIENTE YA TENGA EL PAGO EFECTUADO
                            databaseReference.child(gimnasio.getText().toString()).child("Cuotas").child(anioo.trim()).child(mess.trim()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot shot : snapshot.getChildren()){
                                        cuotas cu = shot.getValue(cuotas.class);
                                        assert cu != null;
                                        if(cu.getEmailcliente().equals(emailcliente)){
                                            if(cu.getMespago().equals(mess)){
                                                bandera3 = true;
                                            }
                                        }
                                    }
                                    if(bandera3){
                                        Snackbar.make(view, "Para este mes y cliente ya existe un pago registrado.", Snackbar.LENGTH_SHORT).show();
                                    }else {
                                        cliente clien = new cliente(cli.getId(),cli.getNombre(),cli.getApellido(),cli.getDni(),cli.getDireccion(),
                                                cli.getEmail(),cli.getGym(),cli.getAdmin(),cli.getToken(),fechapago.getText().toString(),fechavenc,cli.getEstadopago());
                                        cuotas= new cuotas(nombreyapellido, cli.getEmail(),fechapago.getText().toString(),fechavenc,mess,cuotas.getEstadopago());
                                        databaseReference.child("Clientes").child(cli.getId()).setValue(clien);
                                        databaseReference.child(gimnasio.getText().toString()).child("Cuotas").child(anioo.trim()).child(mess.trim()).push().setValue(cuotas);
                                        Snackbar.make(view, "Pago registrado correctamente", Snackbar.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }
                }
            }
        });

        return root;
    }

    private void iniciarFirebase(){
        FirebaseApp.initializeApp(requireActivity());
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.keepSynced(true);

    }

    private void actualizarformatofecha() {
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy");
        fechapago.setText(sdf.format(micalendario.getTime()));
        int index1 = fechapago.getText().toString().indexOf("-",3);
        mess = fechapago.getText().toString().substring(3,index1);
        anioo = fechapago.getText().toString().substring(index1+1);
        bandera1 = true;

        //sumar 30 dias
        micalendario.setTime(micalendario.getTime());
        micalendario.add(Calendar.DAY_OF_YEAR,30);
        fechavenc = (sdf.format(micalendario.getTime()));

    }

    private int numeroMes(){
        return Calendar.MONTH;
    }
}
