package com.tuTurno.app.LaFuria.ui.ListaTurnos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.tuTurno.app.ListViewAdaptadorLT;
import com.tuTurno.app.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import models.DatosTurno;
import models.turno;

public class ListTurnos extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String fechaactual;
    private Button modi;
    private Button elimi;
    private DatosTurno turnosele;
    private Context micontexto;
    private String user;
    private boolean band1;
    private DatosTurno tur = new DatosTurno();
    private turno claseturno = new turno();


    private Date horadiaactual;
    private Date horaturnolista;
    private Date horaturnoseleccionado;

    private Date horaActual = null;
    private Date horaIngresada = null;



    private ArrayList<DatosTurno> listadatosturnos = new ArrayList<>();
    private final List<String> turnosdialog = new ArrayList<>();
    private final List<String> turnosid = new ArrayList<>();
    private final List<Integer> cupos = new ArrayList<>();
    private final List<Integer> cuposalmacenado = new ArrayList<>();
    private final List<String> diaslista = new ArrayList<>();
    private int cupotursele;
    private String idturnocambiado;
    private String horaturnocambiado;
    private String cupoalmacenado;
    private String dias;

    //para el listview
    private ListView milistadatosturnos;
    private ListViewAdaptadorLT adaptador;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        micontexto = context;
    }

    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_listturnos, container, false);
        modi = root.findViewById(R.id.modifica);
        elimi = root.findViewById(R.id.elimina);
        FloatingActionButton fab = requireActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        //ESTO ES PARA EL LISTVIEW
        milistadatosturnos = root.findViewById(R.id.listadatosturnos);

        //para los diferentes gimnasios
      final TextView gimnasio = requireActivity().findViewById(R.id.textologo);


        //seteo hora para modificar o eliminar turnos
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        String horaactual2;

        String horaactual1 = sdf.format(calendar.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 0);
        horaactual2 = sdf.format(calendar.getTime());

        try {
            horaIngresada = sdf.parse(horaactual2);
            horaActual = sdf.parse(horaactual1);
        } catch (ParseException e) {
            e.printStackTrace();
        }



        iniciarFirebase();

        //CLICK EN LISTVIEW DATOS TURNO
        adaptador = new ListViewAdaptadorLT(micontexto,listadatosturnos);
        milistadatosturnos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Calendar calendar = Calendar.getInstance();
                final String hora_actual = sdf.format(calendar.getTime());
                String horaturn;
                turnosele = listadatosturnos.get(i);
                adapterView.setSelected(true);
                band1 = adapterView.isClickable();

                tur.setIdTurno(turnosele.getIdTurno());
                tur.setNombre(turnosele.getNombre());
                tur.setApellido(turnosele.getApellido());
                tur.setDireccionturno(turnosele.getDireccionturno());
                tur.setDniturno(turnosele.getDniturno());
                tur.setCliente(turnosele.getCliente());
                tur.setFecha(turnosele.getFecha());
                tur.setTurno(turnosele.getTurno());
                tur.setDisciplina(turnosele.getDisciplina());
                tur.setIdturnoseleccionado(turnosele.getIdturnoseleccionado());


                //TOMO LA HORA DEL TURNO SELECCIONADO Y LA CONVIERTO
                calendar.set(Calendar.HOUR_OF_DAY, (Integer.parseInt(turnosele.getTurno().substring(0, 2))));
                calendar.set(Calendar.MINUTE, (Integer.parseInt(turnosele.getTurno().substring(3, 5))));
                calendar.set(Calendar.SECOND, 0);
                horaturn = sdf.format(calendar.getTime());

                try {
                    horadiaactual = sdf.parse(hora_actual);
                    horaturnoseleccionado = sdf.parse(horaturn);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });


        user = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail();
        listarturnos(container, gimnasio);

        DateFormat formato = DateFormat.getDateInstance(DateFormat.FULL);
        fechaactual = formato.format(calendar.getTime());

        if(horaActual.compareTo(horaIngresada)<=0){
            modi.setEnabled(true);
            elimi.setEnabled(true);
        }else{
            modi.setEnabled(false);
            elimi.setEnabled(false);
            Snackbar.make(container,"Ya no se puede modificar o eliminar el turno",Snackbar.LENGTH_SHORT).show();
        }

            //BOTON MODIFICAR - CONTROLO CUPO Y COMPARO HORA ACTUAL Y HORA DE LA LISTA DE TURNOS
            //TAMBIEN SE CONTROLA QUE LA HORA DEL TURNO ACTUAL NO HAYA SUPERADO LA HORA ACTUAL
            //PARA EVITAR MODIFICARLO
            modi.setOnClickListener(new View.OnClickListener() {
                int posi = fechaactual.indexOf(",");
                final String diass = fechaactual.substring(0,posi);
                Calendar calendar = Calendar.getInstance();
                String horaturn1;
                @Override
                public void onClick(final View v) {
                    turnosdialog.clear();
                    cupos.clear();
                    cuposalmacenado.clear();
                    turnosid.clear();
                    diaslista.clear();
                    if (!band1) {
                        Snackbar.make(v,"Seleccione el turno a modificar",Snackbar.LENGTH_SHORT).show();
                    } else {


                        if (horadiaactual.compareTo(horaturnoseleccionado) <= 0) {
                            databaseReference.child(gimnasio.getText().toString()).child("Disciplinas").child(turnosele.getDisciplina()).orderByChild("horacomienzo").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot shot : snapshot.getChildren()) {
                                        turno turhora = shot.getValue(turno.class);

                                        if (tur.getIdturnoseleccionado().equals(shot.child("id").getValue())) {
                                            cupotursele = Integer.parseInt(Objects.requireNonNull(shot.child("cupo").getValue()).toString());
                                            idturnocambiado = Objects.requireNonNull(shot.child("id").getValue()).toString();
                                            horaturnocambiado = Objects.requireNonNull(shot.child("horacomienzo").getValue()).toString();
                                            cupoalmacenado = Objects.requireNonNull(shot.child("cupoalmacenado").getValue()).toString();
                                            dias = Objects.requireNonNull(shot.child("dias").getValue().toString());
                                        }
                                        String hora = Objects.requireNonNull(shot.child("horacomienzo").getValue()).toString();
                                        int cupo = Integer.parseInt(Objects.requireNonNull(shot.child("cupo").getValue()).toString());
                                        int cupoalmacenado = Integer.parseInt(Objects.requireNonNull(shot.child("cupoalmacenado").getValue()).toString());
                                        String listadias = (shot.child("dias").getValue().toString());
                                        String id = Objects.requireNonNull(shot.child("id").getValue()).toString();
                                        calendar.set(Calendar.HOUR_OF_DAY, (Integer.parseInt(hora.substring(0, 2))));
                                        calendar.set(Calendar.MINUTE, (Integer.parseInt(hora.substring(3, 5))));
                                        calendar.set(Calendar.SECOND, 0);
                                        horaturn1 = sdf.format(calendar.getTime());

                                        try {
                                            horaturnolista = sdf.parse(horaturn1);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        if (horadiaactual.compareTo(horaturnolista) <= 0 && (cupo > 0) && (!hora.equals(horaturnocambiado))) {
                                            if (turhora.getDias().toLowerCase().contains(diass) || turhora.getDias().toLowerCase().contains("todos")) {
                                                turnosdialog.add(hora);
                                                turnosid.add(id);
                                                cupos.add(cupo);
                                                cuposalmacenado.add(cupoalmacenado);
                                                diaslista.add(listadias);
                                            }
                                        }
                                    }


                                    AlertDialog.Builder elegirturnos = new AlertDialog.Builder(requireActivity());
                                    elegirturnos.setTitle("Seleccionar Turno");

                                    elegirturnos.setSingleChoiceItems(turnosdialog.toArray(new String[0]), -1, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, final int i) {

                                            tur.setTurno(turnosdialog.get(i));
                                            tur.setIdturnoseleccionado(turnosid.get(i));
                                            if (cupotursele != cuposalmacenado.get(i)) {
                                                cupotursele++;
                                            }
                                            int cupoturclick = Integer.parseInt(cupos.get(i).toString());
                                            cupoturclick--;
                                            dialog.dismiss();
                                            databaseReference.child(gimnasio.getText().toString()).child("Datos Turnos").child(tur.getIdTurno()).setValue(tur);

                                            //CARGO LOS DATOS PARA EL TURNO QUE SE HACE CLICK
                                            claseturno.setCupo(String.valueOf(cupoturclick));
                                            claseturno.setDisciplina(turnosele.getDisciplina());
                                            claseturno.setHoracomienzo(turnosdialog.get(i));
                                            claseturno.setId(turnosid.get(i));
                                            claseturno.setCupoalmacenado(cuposalmacenado.get(i).toString());
                                            claseturno.setDias(diaslista.get(i));
                                            databaseReference.child(gimnasio.getText().toString()).child("Disciplinas").child(turnosele.getDisciplina()).child(turnosid.get(i)).setValue(claseturno);

                                            //CARGO LOS DATOS PARA EL TURNO QUE SE DESECHA
                                            claseturno.setCupo(String.valueOf(cupotursele));
                                            claseturno.setId(idturnocambiado);
                                            claseturno.setHoracomienzo(horaturnocambiado);
                                            claseturno.setCupoalmacenado(cupoalmacenado);
                                            claseturno.setDias(dias);
                                            databaseReference.child(gimnasio.getText().toString()).child("Disciplinas").child(turnosele.getDisciplina()).child(idturnocambiado).setValue(claseturno);
                                        }
                                    });
                                    elegirturnos.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });

                                    AlertDialog dialog = elegirturnos.create();
                                    dialog.show();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            Snackbar.make(v, "Ya no se puede modificar el turno", Snackbar.LENGTH_SHORT).show();
                            modi.setEnabled(false);
                            elimi.setEnabled(false);

                        }
                    }
                }
            });

            //BOTON ELIMINAR TURNO
            elimi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    if (!band1) {
                        Snackbar.make(v,"Seleccione el turno a eliminar",Snackbar.LENGTH_SHORT).show();
                    } else {
                        if (horadiaactual.compareTo(horaturnoseleccionado) <= 0) {
                        final DatosTurno tur = new DatosTurno();
                        tur.setIdTurno(turnosele.getIdTurno());
                        tur.setIdturnoseleccionado(turnosele.getIdturnoseleccionado());

                        databaseReference.child(gimnasio.getText().toString()).child("Disciplinas").child(turnosele.getDisciplina()).orderByChild("horacomienzo").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot shot : snapshot.getChildren()) {

                                    if (tur.getIdturnoseleccionado().equals(shot.child("id").getValue())) {
                                        cupotursele = Integer.parseInt(Objects.requireNonNull(shot.child("cupo").getValue()).toString());
                                        idturnocambiado = Objects.requireNonNull(shot.child("id").getValue()).toString();
                                        horaturnocambiado = Objects.requireNonNull(shot.child("horacomienzo").getValue()).toString();
                                        cupoalmacenado = Objects.requireNonNull(shot.child("cupoalmacenado").getValue()).toString();
                                        dias = Objects.requireNonNull(shot.child("dias").getValue()).toString();
                                    }
                                }

                                //RENUEVO EL CUPO DEL TURNO ELIMINADO
                                claseturno.setId(idturnocambiado);
                                claseturno.setHoracomienzo(horaturnocambiado);
                                cupotursele++;
                                claseturno.setCupo(String.valueOf(cupotursele));
                                claseturno.setDisciplina(turnosele.getDisciplina());
                                claseturno.setCupoalmacenado(cupoalmacenado);
                                claseturno.setDias(dias);
                                databaseReference.child(gimnasio.getText().toString()).child("Disciplinas").child(turnosele.getDisciplina()).child(idturnocambiado).setValue(claseturno);

                                //ELIMINO EL TURNO SELECCIONADO
                                databaseReference.child(gimnasio.getText().toString()).child("Datos Turnos").child(tur.getIdTurno()).removeValue();
                                adaptador.notifyDataSetChanged();
                                milistadatosturnos.setAdapter(null);
                                modi.setEnabled(false);
                                elimi.setEnabled(false);
                                Snackbar.make(v,"Su turno fue eliminado correctamente",Snackbar.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        } else {
                            Snackbar.make(v, "Ya no se puede eliminar el turno", Snackbar.LENGTH_SHORT).show();
                            modi.setEnabled(false);
                            elimi.setEnabled(false);

                        }

                    }
                }
            });

        return root;
    }


    private void listarturnos(final View v, TextView gim) {
        firebaseDatabase.getReference().child(gim.getText().toString()).child("Datos Turnos");
        databaseReference.child(gim.getText().toString()).child("Datos Turnos").addValueEventListener(new ValueEventListener() {
            boolean band = false;
            @SuppressLint("Assert")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listadatosturnos.clear();
                for(DataSnapshot shot: dataSnapshot.getChildren()){
                    assert user != null;
                    if (user.equals(shot.child("cliente").getValue()) && fechaactual.equals(shot.child("fecha").getValue())){
                        band = true;
                        DatosTurno tur = shot.getValue(DatosTurno.class);
                        listadatosturnos.add(tur);
                        milistadatosturnos.setAdapter(adaptador);
                    }
                }
                if(!band){
                    assert false;
                    Snackbar.make(v,"Usted no tiene turnos asignados para este día",Snackbar.LENGTH_SHORT).show();
                    modi.setEnabled(false);
                    elimi.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void  iniciarFirebase(){
        FirebaseApp.initializeApp(requireActivity());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference.keepSynced(true);
    }

}
