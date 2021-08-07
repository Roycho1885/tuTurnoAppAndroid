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
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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
import com.tuTurno.app.R;

import java.util.UUID;

import models.CuotaConfig;

public class AdminConfigCuota extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private Context micontexto;
    private EditText txtmonto, txtdisciplinacuota, txtcantdias;

    private NavigationView navi;
    String disci1, discimodi;
    private CuotaConfig c = new CuotaConfig();
    boolean diass, controlcuotaexis, bundleexiste;
    String diasele;
    ArrayAdapter<String> miadapter;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        micontexto = context;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.adminconfigcuota, container, false);

        final Button botonaceptar = root.findViewById(R.id.button2);
        txtmonto = root.findViewById(R.id.txtmonto);
        txtdisciplinacuota = root.findViewById(R.id.txtdisciplinacuota);
        final AutoCompleteTextView txtcantdias = root.findViewById(R.id.dropdown_cantdias);
        navi = requireActivity().findViewById(R.id.nav_view_admin);
        View head = navi.getHeaderView(0);
        final TextView textologo = head.findViewById(R.id.textologo);

        String[] vectordias = new String[]{"1", "2", "3", "4", "5"};


        iniciarFirebase();

        miadapter = new ArrayAdapter<>(micontexto, android.R.layout.simple_spinner_dropdown_item, vectordias);
        txtcantdias.setAdapter(miadapter);
        txtcantdias.setInputType(InputType.TYPE_NULL);
        miadapter.notifyDataSetChanged();


        assert getArguments() != null;
        txtdisciplinacuota.setText(getArguments().getString("disciplina"));

        txtcantdias.setOnItemClickListener((parent, v, position, id) -> {
            diass = true;
            diasele = parent.getAdapter().getItem(position).toString();

        });

        String id = getArguments().getString("idcuota");
        if (id != null) {
            txtdisciplinacuota.setText(getArguments().getString("disci"));
            txtmonto.setText(getArguments().getString("monto"));
            txtcantdias.setText(getArguments().getString("dias"), false);
            bundleexiste = true;
        }


        botonaceptar.setOnClickListener(view -> {
            controlcuotaexis = false;

            if (txtmonto.getText().toString().equals("")) {
                Snackbar.make(view, "Ingrese monto", Snackbar.LENGTH_SHORT).show();
            } else {
                //MODIFICACION DE CUOTA
                if (bundleexiste) {
                    databaseReference.child(textologo.getText().toString()).child("ConfigCuota").child(getArguments().getString("idcuota")).child("configuracioncuotas").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot shot : snapshot.getChildren()) {
                                String dias = shot.child("diasporsemana").getValue(String.class);
                                assert dias != null;
                                if (dias.equals(diasele)) {
                                    Snackbar.make(view, "Esta cuota ya existe", Snackbar.LENGTH_SHORT).show();
                                    controlcuotaexis = true;
                                }
                            }

                            if (!controlcuotaexis) {
                                assert getArguments() != null;
                                c.setIdcuotas(getArguments().getString("idcuotamodi"));
                                if (!diass) {
                                    c.setDiasporsemana(getArguments().getString("dias"));
                                } else {
                                    c.setDiasporsemana(diasele);
                                }
                                c.setMonto(txtmonto.getText().toString());

                                assert getArguments() != null;
                                databaseReference.child(textologo.getText().toString()).child("ConfigCuota").child(getArguments().getString("idcuota")).child("configuracioncuotas").child(c.getIdcuotas()).setValue(c);
                                Snackbar.make(view, "Cuota modificada correctamente", Snackbar.LENGTH_SHORT).show();
                                Navigation.findNavController(view).navigate(R.id.fragment_admin);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    //AGREGA NUEVA CUOTA
                    databaseReference.child(textologo.getText().toString()).child("ConfigCuota").child(getArguments().getString("keycuota")).child("configuracioncuotas").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot shot : snapshot.getChildren()) {
                                String dias = shot.child("diasporsemana").getValue(String.class);
                                assert dias != null;
                                if (dias.equals(diasele)) {
                                    Snackbar.make(view, "Esta cuota ya existe", Snackbar.LENGTH_SHORT).show();
                                    controlcuotaexis = true;
                                }
                            }
                            if (!controlcuotaexis) {
                                c.setIdcuotas(UUID.randomUUID().toString());
                                c.setDiasporsemana(diasele);
                                c.setMonto(txtmonto.getText().toString());

                                assert getArguments() != null;
                                databaseReference.child(textologo.getText().toString()).child("ConfigCuota").child(getArguments().getString("keycuota")).child("configuracioncuotas").child(c.getIdcuotas()).setValue(c);
                                Snackbar.make(view, "Cuota cargada correctamente", Snackbar.LENGTH_SHORT).show();
                                txtmonto.setText("");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        return root;
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

}
