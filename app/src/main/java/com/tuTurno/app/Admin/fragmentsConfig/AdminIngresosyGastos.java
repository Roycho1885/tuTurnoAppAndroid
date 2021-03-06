package com.tuTurno.app.Admin.fragmentsConfig;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuTurno.app.ListViewAdaptadorIngreyEgre;
import com.tuTurno.app.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import models.cuotas;
import models.ingresosextras;

import static java.lang.Math.round;

public class AdminIngresosyGastos extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    Context micontexto;
    private Button vergastos, reginggas;
    private MaterialButton layout;
    private TextView txtingreso, txtgasto, ingresocuota, ingresoextra, gastoextra;
    Calendar micalendario;
    String anioo, anoseleccionado, meses;
    boolean banderames, banderaano;
    cuotas cuotasclass, cuot = new cuotas();
    ingresosextras ingresoextraa = new ingresosextras();
    ingresosextras ingreext = new ingresosextras();
    double acumuloingreso, acumulogasto, acuingreso, acugasto, ingresoscuotas;

    private ArrayList<String> arrayano, arraymeses;
    private ArrayAdapter<String> adapterano, adaptermeses;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        micontexto = context;
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.adminingreygastos, container, false);

        //para los diferentes gimnasios
        final TextView gimnasio = requireActivity().findViewById(R.id.textologo);
        layout = root.findViewById(R.id.layoutingygas);
        txtingreso = root.findViewById(R.id.importeingreso);
        txtgasto = root.findViewById(R.id.importegasto);
        ingresocuota = root.findViewById(R.id.ingresocuota);
        ingresoextra = root.findViewById(R.id.ingresoextra);
        gastoextra = root.findViewById(R.id.gastoextra);
        final AutoCompleteTextView botonanodesple = root.findViewById(R.id.desplegarano);
        final AutoCompleteTextView botonmesdesple = root.findViewById(R.id.desplegarmes);
        vergastos = root.findViewById(R.id.botonvergastos);
        reginggas = root.findViewById(R.id.btnreging);

        micalendario = Calendar.getInstance();

        iniciarFirebase();

        acumuloingreso = 0;
        acumulogasto = 0;
        ingresocuota.setText(" $0,00");
        ingresoextra.setText(" $0,00");
        gastoextra.setText(" $0,00");

        botonanodesple.post(() -> botonanodesple.getText().clear());
        botonmesdesple.post(() -> botonmesdesple.getText().clear());
        formatearfecha();


        //CARGO BOTON ANO CORRESPONDIENTE
        arrayano = new ArrayList<>();
        databaseReference.child(gimnasio.getText().toString()).child("Cuotas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot shot : snapshot.getChildren()) {
                    arrayano.add(Objects.requireNonNull(shot.getKey()));
                }
                adapterano = new ArrayAdapter<>(micontexto, android.R.layout.simple_list_item_1, arrayano);
                botonanodesple.setAdapter(adapterano);
                botonanodesple.setInputType(InputType.TYPE_NULL);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        botonanodesple.setOnItemClickListener((adapterView, view, i, l) -> {
            banderaano = true;

            //LIMPIO BOTON MES
            botonmesdesple.post(() -> botonmesdesple.getText().clear());
            arraymeses = new ArrayList<>();
            anoseleccionado = adapterView.getAdapter().getItem(i).toString();

            databaseReference.child(gimnasio.getText().toString()).child("Cuotas").child(anoseleccionado).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot shot : snapshot.getChildren()) {
                        arraymeses.add(Objects.requireNonNull(shot.getKey()));
                    }

                    adaptermeses = new ArrayAdapter<>(micontexto, android.R.layout.simple_list_item_1, arraymeses);
                    botonmesdesple.setAdapter(adaptermeses);
                    botonmesdesple.setInputType(InputType.TYPE_NULL);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        //ACA DEBO SUMAR TODAS LAS CUOTAS Y LOS INGRESOS Y GASTOS TOMANDO EL ARRAYMESES
        databaseReference.child(gimnasio.getText().toString()).child("Cuotas").child(anioo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot shot : snapshot.getChildren()) {
                    acumuloingreso = 0;
                    acumulogasto = 0;
                    databaseReference.child(gimnasio.getText().toString()).child("Cuotas").child(anioo).child(Objects.requireNonNull(shot.getKey())).addListenerForSingleValueEvent(new ValueEventListener() {
                        @SuppressLint({"SetTextI18n", "DefaultLocale"})
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot shot : snapshot.getChildren()) {
                                cuot = shot.getValue(cuotas.class);
                                assert cuot != null;
                                acumuloingreso = Double.parseDouble(cuot.getMonto()) + acumuloingreso;
                            }
                            txtingreso.setText(" $" + String.format("%.2f",acumuloingreso));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                databaseReference.child(gimnasio.getText().toString()).child("IngresosyGastos").addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint({"SetTextI18n", "DefaultLocale"})
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot shot : snapshot.getChildren()) {
                            ingresoextraa = shot.getValue(ingresosextras.class);
                            assert ingresoextraa != null;
                            if (ingresoextraa.getAno().equals(anioo.trim())){
                                if (ingresoextraa.getTipo().equals("ingreso")) {
                                    acumuloingreso = Double.parseDouble(ingresoextraa.getMontoingreso()) + acumuloingreso;
                                } else {
                                    acumulogasto = Double.parseDouble(ingresoextraa.getMontoingreso()) + acumulogasto;
                                }
                            }
                        }
                        txtingreso.setText(" $" + String.format("%.2f",acumuloingreso));
                        txtgasto.setText(" $" + String.format("%.2f",acumulogasto));
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

        //BOTON MES CORRESPONDIENTE
        botonmesdesple.setOnItemClickListener((adapterView, view, i, l) -> {
            banderames = true;
            meses = adapterView.getAdapter().getItem(i).toString();
        });

        //BOTON VER INGRESOS Y GASTOS
        vergastos.setOnClickListener(view -> {
            ingresoscuotas = 0;
            acuingreso = 0;
            acugasto = 0;
            if (!banderames || !banderaano) {
                Snackbar.make(view, "Seleccione a??o y mes", Snackbar.LENGTH_SHORT).show();
            } else {

                databaseReference.child(gimnasio.getText().toString()).child("Cuotas").child(anoseleccionado.trim()).child(meses.trim()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot shot : snapshot.getChildren()) {
                            cuotasclass = shot.getValue(cuotas.class);
                            assert cuotasclass != null;
                            ingresoscuotas = Double.parseDouble(cuotasclass.getMonto()) + ingresoscuotas;
                        }
                        databaseReference.child(gimnasio.getText().toString()).child("IngresosyGastos").addListenerForSingleValueEvent(new ValueEventListener() {
                            @SuppressLint({"DefaultLocale", "SetTextI18n"})
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot shot : snapshot.getChildren()) {
                                    ingreext = shot.getValue(ingresosextras.class);
                                    assert ingreext != null;
                                    if (ingreext.getAno().equals(anoseleccionado.trim()) && ingreext.getMes().equals(meses.trim())) {
                                        if (ingreext.getTipo().equals("ingreso")) {
                                            acuingreso = Double.parseDouble(ingreext.getMontoingreso()) + acuingreso;
                                        } else {
                                            acugasto = Double.parseDouble(ingreext.getMontoingreso()) + acugasto;
                                        }
                                    }
                                }

                                ingresocuota.setText(" $" + String.format("%.2f",ingresoscuotas));
                                ingresoextra.setText(" $" + String.format("%.2f",acuingreso));
                                gastoextra.setText(" $" + String.format("%.2f",acugasto));
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


                botonmesdesple.post(() -> botonmesdesple.getText().clear());
                botonanodesple.post(() -> botonanodesple.getText().clear());
            }
        });
        //BOTON REGISTRAR INGRESO Y GASTO
        reginggas.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.AdminRegistroIngyGas);
        });

        //CLICK EN EL LAYOUT PARA DETALLES
        layout.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.AdminDetallesIngyGas);
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

    private void formatearfecha() {
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy");
        String fechaactualmodi = sdf.format(micalendario.getTime());
        int index1 = fechaactualmodi.indexOf("-", 3);
        anioo = fechaactualmodi.substring(index1 + 1);
    }
}
