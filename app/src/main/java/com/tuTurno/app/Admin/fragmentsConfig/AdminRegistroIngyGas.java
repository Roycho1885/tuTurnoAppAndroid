package com.tuTurno.app.Admin.fragmentsConfig;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tuTurno.app.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import models.ingresosextras;

public class AdminRegistroIngyGas extends Fragment {

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    Calendar micalendario;

    private TextView gimnasio;
    private EditText txtdescripcion;
    private EditText txtmontoingygas;
    private EditText txtfechadescrip;
    private Button btnregingygas;
    private AutoCompleteTextView txttipo;

    private ArrayList<String> arraytipo = new ArrayList<>();
    private ArrayAdapter miadapter;
    Context micontexto;
    boolean menutipo, bolfecha;
    String textotipo, mess,anioo;
    ingresosextras ing = new ingresosextras();


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        micontexto = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.adminregistroingygas, container, false);
        //para los diferentes gimnasios
         gimnasio = requireActivity().findViewById(R.id.textologo);

        txtdescripcion = root.findViewById(R.id.txtdescrip);
        txtfechadescrip = root.findViewById(R.id.txtfechaingygas);
        txtmontoingygas = root.findViewById(R.id.txtmontoingygas);
        txttipo = root.findViewById(R.id.tipodesple);
        btnregingygas = root.findViewById(R.id.botonagregaingygas);
        micalendario = Calendar.getInstance();

        txttipo.post(() -> txttipo.getText().clear());

        arraytipo.add("ingreso");
        arraytipo.add("gasto");

        miadapter = new ArrayAdapter<>(micontexto, android.R.layout.simple_list_item_1, arraytipo);
        txttipo.setAdapter(miadapter);
        txttipo.setInputType(InputType.TYPE_NULL);

        iniciarFirebase();

        txttipo.setOnItemClickListener((adapterView, view, i, l) -> {
            menutipo = true;
            textotipo = adapterView.getAdapter().getItem(i).toString().trim();
        });

        final DatePickerDialog.OnDateSetListener date = (view, ano, mes, diames) -> {
            micalendario.set(Calendar.YEAR, ano);
            micalendario.set(Calendar.MONTH, mes);
            micalendario.set(Calendar.DAY_OF_MONTH, diames);
            actualizarformatofecha();
        };

        //BOTON FECHA
        txtfechadescrip.setOnClickListener(view -> new DatePickerDialog(micontexto, date, micalendario
                .get(Calendar.YEAR), micalendario.get(Calendar.MONTH),
                micalendario.get(Calendar.DAY_OF_MONTH)).show());

        btnregingygas.setOnClickListener(this::RegistrarIngyGas);

        return root;
    }

    private void RegistrarIngyGas(View view){
        final String descrip = txtdescripcion.getText().toString().trim();
        final String monto = txtmontoingygas.getText().toString().trim();

        ing.setId(UUID.randomUUID().toString());
        ing.setDescripcion(descrip);
        ing.setMontoingreso(monto);
        ing.setAno(anioo);
        ing.setMes(mess);
        ing.setTipo(textotipo);

        if(!descrip.equals("") && !monto.equals("") && bolfecha && menutipo){
            databaseReference.child(gimnasio.getText().toString()).child("IngresosyGastos").child(ing.getId()).setValue(ing);
            Snackbar.make(view, "Datos cargado correctamente", Snackbar.LENGTH_SHORT).show();
            txtdescripcion.setText("");
            txtmontoingygas.setText("");
            txttipo.post(() -> txttipo.getText().clear());
            txtfechadescrip.setText("");

        }else {
            Snackbar.make(view, "Complete todo los campos", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void iniciarFirebase(){
        FirebaseApp.initializeApp(requireActivity());
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference.keepSynced(true);

    }

    private void actualizarformatofecha() {
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy");
        txtfechadescrip.setText(sdf.format(micalendario.getTime()));
        //fechaelegida = micalendario.getTime();
        int index1 = txtfechadescrip.getText().toString().indexOf("-", 3);
        mess = txtfechadescrip.getText().toString().substring(3, index1);
        anioo = txtfechadescrip.getText().toString().substring(index1 + 1);
        bolfecha = true;

    }
}
