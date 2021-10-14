package com.tuTurno.app.Cliente;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuTurno.app.R;

import java.util.Objects;

import models.MisFunciones;
import models.cliente;
import models.gimnasios;

public class CambiarTemaCliente extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    RadioButton AppTheme, AppTheme1, AppTheme2, AppTheme3;
    RadioGroup group;
    String themeName;
    SharedPreferences sharedPreferences;
    Context micontexto;

    private String nombre;
    private String apellido;
    private String urllogo;
    private String urlfondo;
    private String urldire;
    private CollapsingToolbarLayout tool;
    private TextView cliente;
    private NavigationView navi;
    private cliente c,cli = new cliente();
    MisFunciones cargarNav = new MisFunciones();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        micontexto = context;
        // set theme
        sharedPreferences = micontexto.getSharedPreferences("Theme", Context.MODE_PRIVATE);
        themeName = sharedPreferences.getString("ThemeName", "AppTheme");
        if (themeName.equalsIgnoreCase("AppTheme1")) {
            micontexto.setTheme(R.style.AppTheme1);
        } else if (themeName.equalsIgnoreCase("AppTheme2")) {
            micontexto.setTheme(R.style.AppTheme2);
        } else if (themeName.equalsIgnoreCase("AppTheme3")) {
            micontexto.setTheme(R.style.AppTheme3);
        } else  {
            micontexto.setTheme(R.style.AppTheme);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        micontexto = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.cambiartema, container, false);

        final FloatingActionButton fab = requireActivity().findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_add_black_24dp);
        fab.setEnabled(true);
        fab.setVisibility(View.GONE);

        cliente = requireActivity().findViewById(R.id.versianda);
        tool = requireActivity().findViewById(R.id.CollapsingToolbar);
        navi = requireActivity().findViewById(R.id.nav_view);

        View head = navi.getHeaderView(0);
        final ImageView fondo = head.findViewById(R.id.fondo);
        final ImageView logo = head.findViewById(R.id.imageViewlogo);
        final TextView textologo = head.findViewById(R.id.textologo);
        final TextView txtdirecli = head.findViewById(R.id.textodire);

        iniciarFirebase();

        // init
        group = root.findViewById(R.id.group);
        AppTheme = root.findViewById(R.id.tema1);
        AppTheme1 = root.findViewById(R.id.tema2);
        AppTheme2 = root.findViewById(R.id.tema3);
        AppTheme3 = root.findViewById(R.id.tema4);

        if (themeName.equalsIgnoreCase("AppTheme1")) {
            AppTheme1.setChecked(true);
        } else if (themeName.equalsIgnoreCase("AppTheme2")) {
            AppTheme2.setChecked(true);
        } else if (themeName.equalsIgnoreCase("AppTheme3")) {
            AppTheme3.setChecked(true);
        } else  {
            AppTheme.setChecked(true);
        }

        // Called when the checked radio button has changed.
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.tema1) {
                    setTheme("AppTheme");
                } else if (checkedId == R.id.tema2) {
                    setTheme("AppTheme1");
                } else if (checkedId == R.id.tema3) {
                    setTheme("AppTheme2");
                } else if(checkedId == R.id.tema4) {
                    setTheme("AppTheme3");
                }
            }
        });

        //LECTURA DEL CLIENTE
        databaseReference.child("Clientes").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot shot : snapshot.getChildren()){

                    c = shot.getValue(cliente.class);
                    assert c != null;

                    if(c.getEmail().equals(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail())){

                        cli = shot.getValue(cliente.class);

                        //CARGO NOMBRE, GIMNASIO E IMAGEN AL HEADER
                        nombre = c.getNombre();
                        apellido = c.getApellido();
                        cliente.setText(getString(R.string.cliente)+ " " + c.getNombre());
                        tool.setTitle(c.getGym());

                        textologo.setText(c.getGym());
                    }
                }
                //CARGO LA URL DE IMAGEN PARA COLORCAR EL LOGO EN EL HEADER
                databaseReference.child("Gimnasios").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot shot : snapshot.getChildren()){
                            gimnasios g = shot.getValue(gimnasios.class);
                            assert g != null;
                            if(cli.getGym().equals(g.getNombre())){
                                urldire= cargarNav.cargarDatosNav(micontexto,g.getDireccion(), g.getUrldire(),g.getLogo(),g.getFondonav(),txtdirecli,logo,fondo);
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

        return root;
    }

    public void setTheme(String name) {
        // Create preference to store theme name
        SharedPreferences preferences = micontexto.getSharedPreferences("Theme", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ThemeName", name);
        editor.apply();
        requireActivity().recreate();
    }

    private void iniciarFirebase() {
        FirebaseApp.initializeApp(requireActivity());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference.keepSynced(true);
    }

}
