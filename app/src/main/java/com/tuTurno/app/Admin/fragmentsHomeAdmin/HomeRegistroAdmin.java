package com.tuTurno.app.Admin.fragmentsHomeAdmin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.tuTurno.app.R;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;

import models.DatosTurno;

public class HomeRegistroAdmin extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private TextView setFecha;
    private DatosTurno datosTurno = new DatosTurno();
    private NavigationView navi;
    boolean banderaturno;
    String idturno;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.homeregistroadmin, container, false);
        final EditText txtdni = root.findViewById(R.id.txtdoc);
        Calendar cal = Calendar.getInstance();
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
            String dni = txtdni.getText().toString();
            if (dni.equals("")) {
                Snackbar.make(view, "Ingrese DNI", Snackbar.LENGTH_SHORT).show();
            } else {
                databaseReference.child(textologo.getText().toString()).child("Datos Turnos").orderByChild("fecha").equalTo(setFecha.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot shot : snapshot.getChildren()) {
                            datosTurno = shot.getValue(DatosTurno.class);
                            if (dni.equals(datosTurno.getDniturno())) {
                                idturno = datosTurno.getIdTurno();
                                banderaturno = true;
                            }
                        }
                        if (banderaturno) {
                            HashMap hashMap = new HashMap();
                            hashMap.put("asistencia", "Si");
                            databaseReference.child(textologo.getText().toString()).child("Datos Turnos").child(idturno).updateChildren(hashMap).addOnSuccessListener(o -> {
                            });
                            Snackbar.make(view, "Turno registrado", Snackbar.LENGTH_SHORT).show();
                            txtdni.setText("");

                        } else {
                            Snackbar.make(view, "Usted no posee un turno", Snackbar.LENGTH_SHORT).show();
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
}
