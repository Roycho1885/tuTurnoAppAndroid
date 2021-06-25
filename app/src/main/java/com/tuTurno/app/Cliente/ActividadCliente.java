package com.tuTurno.app.Cliente;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.tuTurno.app.LoginActivity;
import com.tuTurno.app.R;

public class ActividadCliente extends AppCompatActivity{

    private static final int INTERVALO = 2000;
    private long tiempoPrimerClick;
    private AppBarConfiguration mAppBarConfiguration;

    String themeName;
    SharedPreferences sharedPreferences;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        setContentView(R.layout.activity_navigation);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drawer_navigation,menu);
        return true;
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

    @Override
    public boolean onSupportNavigateUp() {
       NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
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

}

