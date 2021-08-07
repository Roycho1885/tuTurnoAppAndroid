package com.tuTurno.app.Admin.fragmentsConfig;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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
import com.tuTurno.app.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;

import models.CuotaConfig;
import models.turno;

public class AdminAgregarTur extends Fragment {
    Calendar calendar;
    TimePickerDialog timedialog;
    int horaactual;
    int minactual;
    Context micontexto;
    String disci;
    String disci1;
    boolean band2;
    private final turno infoturno = new turno();
    private final CuotaConfig configcuotasdici = new CuotaConfig();

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    private ArrayList<String> arraydisci;
    private ArrayAdapter miadapter;

    EditText txtdias;
    EditText txtdias1;
    final ArrayList<Integer> list = new ArrayList<>();
    final ArrayList<String> turnoslista = new ArrayList<>();
    String dias = "";


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        micontexto = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.adminagretur, container, false);

        final EditText txthoraturno = root.findViewById(R.id.txthoraturno);
        txtdias = root.findViewById(R.id.txtdias);
        txtdias1 = root.findViewById(R.id.txtdias1);
        final EditText txthoraturno1 = root.findViewById(R.id.txthoraturno1);
        final EditText txtdisciplina = root.findViewById(R.id.txtDisciplina);
        final EditText txtcupo = root.findViewById(R.id.txtcupo);
        final EditText txtcupo1 = root.findViewById(R.id.txtcupo1);
        final Button botonagregarturno = root.findViewById(R.id.botonagregaturno);
        final Button botonagregarturno1 = root.findViewById(R.id.botonagregaturno1);
        //final TextInputLayout botontur = root.findViewById(R.id.botondisciexis);
        final AutoCompleteTextView menutur = root.findViewById(R.id.discidesple);

        //para los diferentes gimnasios
        final TextView gimnasio = requireActivity().findViewById(R.id.textologo);



        iniciarFirebase();
        arraydisci = new ArrayList<>();

        //TODO LO QUE TENGA QUE VER PARA AGREGAR DISCIPLINA Y TURNOS
        txthoraturno.setOnClickListener(view -> {
            calendar = Calendar.getInstance();
            horaactual = calendar.get(Calendar.HOUR_OF_DAY);
            minactual = calendar.get(Calendar.MINUTE);


            timedialog = new TimePickerDialog(micontexto, new TimePickerDialog.OnTimeSetListener() {
                @SuppressLint({"SetTextI18n", "DefaultLocale"})
                @Override
                public void onTimeSet(TimePicker timePicker, int hora, int min) {

                    txthoraturno.setText(String.format("%02d:%02d", hora , min));

                }
            }, horaactual , minactual , true);

            timedialog.show();
        });

        txtdias.setOnClickListener(view -> mostrarAlertDialog());

        txtdias1.setOnClickListener(view -> mostrarAlertDialog1());


        botonagregarturno.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            disci = txtdisciplina.getText().toString().trim();
            if(txthoraturno.getText().toString().equals("") || disci.equals("") || txtcupo.getText().toString().equals("")
                    || txtdias.getText().toString().equals("")){
                Snackbar.make(view,"Complete todo los campos",Snackbar.LENGTH_SHORT).show();
            }else{
                if(arraydisci.contains(disci)){
                    Snackbar.make(view,"Disciplina ya existente",Snackbar.LENGTH_SHORT).show();
                }else{
                    agregarturno(disci, txthoraturno.getText().toString().trim(),txtdias.getText().toString().trim(),txtcupo.getText().toString().trim());
                    agregarmonto(disci);
                    databaseReference.child(gimnasio.getText().toString()).child("Disciplinas").child(disci).child(infoturno.getId()).setValue(infoturno);
                    databaseReference.child(gimnasio.getText().toString()).child("ConfigCuota").child(configcuotasdici.getId()).setValue(configcuotasdici);
                    Snackbar.make(view,"Disciplina y Turno agregado correctamente",Snackbar.LENGTH_SHORT).show();
                }
                txtdisciplina.setText("");
                txtdias.setText("");
                txthoraturno.setText("");
                txtcupo.setText("");
                arraydisci.clear();
                bundle.putString("disciplina",disci);
                bundle.putString("keycuota",configcuotasdici.getId());
                Navigation.findNavController(view).navigate(R.id.AdminConfigCuota,bundle);
            }

        });

        databaseReference.child(gimnasio.getText().toString()).child("Disciplinas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot shot : snapshot.getChildren()){
                    arraydisci.add(Objects.requireNonNull(shot.getKey()));
                }
                miadapter = new ArrayAdapter<>(micontexto, android.R.layout.simple_list_item_1, arraydisci);
                menutur.setAdapter(miadapter);
                menutur.setInputType(InputType.TYPE_NULL);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        menutur.setOnItemClickListener((adapterView, view, i, l) -> {
            disci1 = adapterView.getAdapter().getItem(i).toString();
            if(adapterView.isClickable()){
                band2 = true;
            }

        });

        txthoraturno1.setOnClickListener(view -> {
            calendar = Calendar.getInstance();
            horaactual = calendar.get(Calendar.HOUR_OF_DAY);
            minactual = calendar.get(Calendar.MINUTE);

            timedialog = new TimePickerDialog(micontexto, new TimePickerDialog.OnTimeSetListener() {
                @SuppressLint({"SetTextI18n", "DefaultLocale"})
                @Override
                public void onTimeSet(TimePicker timePicker, int hora, int min) {

                    txthoraturno1.setText(String.format("%02d:%02d", hora , min));

                }
            }, horaactual , minactual , true);

            timedialog.show();
        });

        botonagregarturno1.setOnClickListener(view -> {
            turnoslista.clear();
            arraydisci.clear();
            if(!band2 || txthoraturno1.getText().toString().equals("") || txtcupo1.getText().toString().equals("") || txtdias1.getText().toString().equals("")){
                Snackbar.make(view,"Complete todo los campos",Snackbar.LENGTH_SHORT).show();
            }else{
                databaseReference.child(gimnasio.getText().toString()).child("Disciplinas").child(disci1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot shot : snapshot.getChildren()){
                            turnoslista.add(Objects.requireNonNull(shot.child("horacomienzo").getValue()).toString());
                        }

                            if(turnoslista.contains(txthoraturno1.getText().toString())){
                                Snackbar.make(view,"Turno ya existente",Snackbar.LENGTH_SHORT).show();
                            }else{
                                agregarturno(disci1, txthoraturno1.getText().toString().trim(),txtdias1.getText().toString().trim(),txtcupo1.getText().toString().trim());
                                databaseReference.child(gimnasio.getText().toString()).child("Disciplinas").child(disci1).child(infoturno.getId()).setValue(infoturno);
                                Snackbar.make(view,"Turno agregado correctamente",Snackbar.LENGTH_SHORT).show();

                                menutur.post(() -> menutur.getText().clear());

                            }
                        txthoraturno1.setText("");
                        txtcupo1.setText("");
                        txtdias1.setText("");

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

        });

        return root;
    }

    //ALERTDIALOG PARA NUEVAS DISCIPLINAS Y TURNOS
    private void mostrarAlertDialog() {
        list.clear();
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(requireActivity(),R.style.AlertDialogCustom));
        alertDialog.setTitle("Seleccione días");
        final String[] items = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Todos"};
        final boolean[] checkedItems = {false, false, false, false, false, false};
        alertDialog.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog,  int which, boolean isChecked) {
                if (isChecked) {
                    if (which == 5) {
                        ((AlertDialog) dialog).getListView().setItemChecked(0, true);
                        ((AlertDialog) dialog).getListView().setItemChecked(1, true);
                        ((AlertDialog) dialog).getListView().setItemChecked(2, true);
                        ((AlertDialog) dialog).getListView().setItemChecked(3, true);
                        ((AlertDialog) dialog).getListView().setItemChecked(4, true);
                        list.clear();
                        list.add(5);
                    } else {
                        if (!list.contains(which)) {
                            list.add(which);
                        }
                    }
                }else
                    if(which==5){
                        ((AlertDialog) dialog).getListView().setItemChecked(0, false);
                        ((AlertDialog) dialog).getListView().setItemChecked(1, false);
                        ((AlertDialog) dialog).getListView().setItemChecked(2, false);
                        ((AlertDialog) dialog).getListView().setItemChecked(3, false);
                        ((AlertDialog) dialog).getListView().setItemChecked(4, false);
                        list.clear();
                    }else{
                        if(list.contains(which)){
                            list.remove(Integer.valueOf(which));
                        }
                    }
            }

        });
        alertDialog.setPositiveButton("Aceptar", (dialogInterface, i) -> {
            dias = "";
            for(int t=0; t< list.size();t++){
                dias = dias.concat(items[list.get(t)]);
                if(t!=list.size()-1){
                    dias = dias.concat(", ");
                }
            }
            txtdias.setText(dias);
        });

        alertDialog.setNegativeButton("Cancelar", (dialogInterface, i) -> dialogInterface.cancel());
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    //ALERTDIALOG PARA LAS DISCIPLINAS EXISTENTES
    private void mostrarAlertDialog1() {
        list.clear();
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(requireActivity(),R.style.AlertDialogCustom));
        alertDialog.setTitle("Seleccione días");
        final String[] items = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Todos"};
        final boolean[] checkedItems = {false, false, false, false, false, false};
        alertDialog.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog,  int which, boolean isChecked) {
                if (isChecked) {
                    if (which == 5) {
                        ((AlertDialog) dialog).getListView().setItemChecked(0, true);
                        ((AlertDialog) dialog).getListView().setItemChecked(1, true);
                        ((AlertDialog) dialog).getListView().setItemChecked(2, true);
                        ((AlertDialog) dialog).getListView().setItemChecked(3, true);
                        ((AlertDialog) dialog).getListView().setItemChecked(4, true);
                        list.clear();
                        list.add(5);
                    } else {
                        if (!list.contains(which)) {
                            list.add(which);
                        }
                    }
                }else
                if(which==5){
                    ((AlertDialog) dialog).getListView().setItemChecked(0, false);
                    ((AlertDialog) dialog).getListView().setItemChecked(1, false);
                    ((AlertDialog) dialog).getListView().setItemChecked(2, false);
                    ((AlertDialog) dialog).getListView().setItemChecked(3, false);
                    ((AlertDialog) dialog).getListView().setItemChecked(4, false);
                    list.clear();
                }else{
                    if(list.contains(which)){
                        list.remove(Integer.valueOf(which));
                    }
                }
            }

        });
        alertDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dias = "";
                for(int t=0; t< list.size();t++){
                    dias = dias.concat(items[list.get(t)]);
                    if(t!=list.size()-1){
                        dias = dias.concat(", ");
                    }
                }
                txtdias1.setText(dias);
            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    private void agregarturno(String disci , String hora, String dias , String cupo) {
        infoturno.setId(UUID.randomUUID().toString());
        infoturno.setDisciplina(disci);
        infoturno.setHoracomienzo(hora);
        infoturno.setDias(dias);
        infoturno.setCupo(cupo);
        infoturno.setCupoalmacenado(cupo);
    }

    private void agregarmonto(String disciplina) {
        configcuotasdici.setId(UUID.randomUUID().toString());
        configcuotasdici.setDisciplina(disciplina);
    }

    private void  iniciarFirebase(){
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
