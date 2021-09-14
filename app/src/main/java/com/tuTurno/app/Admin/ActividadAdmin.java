package com.tuTurno.app.Admin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuTurno.app.LoginActivity;
import com.tuTurno.app.R;
import com.tuTurno.app.SlidePagerAdapter;

import java.util.Objects;

import models.cliente;

public class ActividadAdmin  extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private cliente c, cli = new cliente();


    private static final int INTERVALO = 2000;
    private long tiempoPrimerClick;

    private AppBarConfiguration mAppBarConfiguration;

    String themeName;
    SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // SET TEMA
        sharedPreferences = getSharedPreferences("Theme", Context.MODE_PRIVATE);
        themeName = sharedPreferences.getString("ThemeName", "AppTheme");
        if (themeName.equalsIgnoreCase("AppTheme1")) {
            setTheme(R.style.AppTheme1);
        } else if (themeName.equalsIgnoreCase("AppTheme2")) {
            setTheme(R.style.AppTheme2);
        } else if (themeName.equalsIgnoreCase("AppTheme3")) {
            setTheme(R.style.AppTheme3);
        } else  {
            setTheme(R.style.AppTheme);
        }

        setContentView(R.layout.activity_navigation_admin);
        new SlidePagerAdapter(getSupportFragmentManager());

        Toolbar toolbar = findViewById(R.id.toolbar_admin);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout_admin);
        NavigationView navigationView = findViewById(R.id.nav_view_admin);



        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.fragment_admin)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_admin);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);



        iniciarFirebase();

        //LECTURA DEL CLIENTE
        databaseReference.child("Clientes").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Menu menuView = navigationView.getMenu();
                for (DataSnapshot shot : snapshot.getChildren()) {

                    c = shot.getValue(cliente.class);
                    assert c != null;

                    if (c.getEmail().equals(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail())) {
                        cli = shot.getValue(cliente.class);
                        assert cli != null;
                        menuView.getItem(1).setEnabled(!cli.getAdmin().equals("Restringido"));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drawer_navigation, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_admin);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed(){
        if (tiempoPrimerClick + INTERVALO > System.currentTimeMillis()){
            super.onBackPressed();
            return;
        }else {
            Toast.makeText(this, "Vuelve a presionar para salir", Toast.LENGTH_SHORT).show();
        }
        tiempoPrimerClick = System.currentTimeMillis();
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.compartir:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String cuerpo = "https://play.google.com/store/apps/details?id=com.tuTurno.app&hl=es_AR&gl=US";
                String subcuerpo = "App tuTurno";
                intent.putExtra(Intent.EXTRA_TEXT, cuerpo);
                intent.putExtra(Intent.EXTRA_SUBJECT, subcuerpo);
                startActivity(Intent.createChooser(intent, "Compartir via"));
                return true;

            case R.id.cerrarses:
                FirebaseAuth.getInstance().signOut();
                Intent intent1 = new Intent(this, LoginActivity.class);
                startActivity(intent1);
                finish();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    private void iniciarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference.keepSynced(true);
    }
}
