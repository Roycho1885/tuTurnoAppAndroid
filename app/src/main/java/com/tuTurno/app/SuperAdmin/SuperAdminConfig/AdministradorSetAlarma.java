package com.tuTurno.app.SuperAdmin.SuperAdminConfig;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tuTurno.app.MiAlarma;
import com.tuTurno.app.MiAlarmaDias;
import com.tuTurno.app.R;

import java.util.Calendar;

public class AdministradorSetAlarma extends Fragment {
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private Button botonguardartarea, botonguardartareadias;
    private Button botoneliminartarea, botoneliminartareadias;
    private EditText txthoratarea, txtinputhora;
    Calendar actual = Calendar.getInstance();
    Calendar calendar = Calendar.getInstance();
    Calendar actualdias = Calendar.getInstance();
    Calendar calendardias = Calendar.getInstance();
    private int min,hora;
    private int minutodias, horadias;
    Context micontexto;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        micontexto = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.administradorsetalarma, container, false);
        botonguardartarea = root.findViewById(R.id.botonguardar);
        botoneliminartarea = root.findViewById(R.id.botoneliminar);
        txthoratarea = root.findViewById(R.id.txthoratarea);

        botonguardartareadias = root.findViewById(R.id.botonguardardias);
        botoneliminartareadias = root.findViewById(R.id.botoneliminardias);
        txtinputhora = root.findViewById(R.id.txtinputhora);

        crearCanalNotificacion();
        crearCanalNotificacionDias();


        iniciarFirebase();

        txthoratarea.setOnClickListener(v -> {
            hora = actual.get(Calendar.HOUR_OF_DAY);
            min = actual.get(Calendar.MINUTE);

            @SuppressLint("DefaultLocale") TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(), (view, h, m) -> {
                calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                calendar.set(Calendar.HOUR_OF_DAY,h);
                calendar.set(Calendar.MINUTE,m);
                txthoratarea.setText(String.format("%02d:%02d",h,m));
            }, hora , min, true);
            timePickerDialog.show();
        });

        //HORA PARA ACTUALIZAR LOS DIAS
        txtinputhora.setOnClickListener(v -> {
            horadias = actualdias.get(Calendar.HOUR_OF_DAY);
            minutodias = actualdias.get(Calendar.MINUTE);

            @SuppressLint("DefaultLocale") TimePickerDialog timePickerDialogDias = new TimePickerDialog(v.getContext(), (view, h, m) -> {
                calendardias.set(calendardias.get(Calendar.YEAR),calendardias.get(Calendar.MONTH),calendardias.get(Calendar.DAY_OF_MONTH));
                calendardias.set(Calendar.HOUR_OF_DAY,h);
                calendardias.set(Calendar.MINUTE,m);
                txtinputhora.setText(String.format("%02d:%02d",h,m));
            }, horadias , minutodias, true);
            timePickerDialogDias.show();
        });

        botonguardartarea.setOnClickListener(v -> {
            setAlarma(calendar.getTimeInMillis());
            Toast.makeText(micontexto,"Alarma cupos guardada", Toast.LENGTH_SHORT).show();

        });

        botoneliminartarea.setOnClickListener(v -> {
            elimAlarma();
            Toast.makeText(micontexto,"Alarma cupos cancelada", Toast.LENGTH_SHORT).show();
        });

        botonguardartareadias.setOnClickListener(v -> {
            setAlarmaDias(calendardias.getTimeInMillis());
            Toast.makeText(micontexto,"Alarma dias guardada", Toast.LENGTH_SHORT).show();

        });
        botoneliminartareadias.setOnClickListener(v -> {
            elimAlarmaDias();
            Toast.makeText(micontexto,"Alarma dias cancelada", Toast.LENGTH_SHORT).show();
        });

        return root;
    }

    //SETEO MI ALARMA
    private void setAlarma(long time) {
        AlarmManager am = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(micontexto, MiAlarma.class);
        PendingIntent pi = PendingIntent.getBroadcast(micontexto,0,i,0);
        am.setRepeating(AlarmManager.RTC_WAKEUP,time,AlarmManager.INTERVAL_DAY,pi);
    }
    //ELIMINO ALARMA
    private void elimAlarma(){
        AlarmManager am = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(micontexto,MiAlarma.class);
        PendingIntent pi = PendingIntent.getBroadcast(micontexto,0,i,0);
        am.cancel(pi);

    }

    //ALARMA PARA ACTUALIZAR DIAS
    //SETEO MI ALARMA
    private void setAlarmaDias(long time) {
        AlarmManager am = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(micontexto, MiAlarmaDias.class);
        PendingIntent pi = PendingIntent.getBroadcast(micontexto,1,i,0);
        am.setRepeating(AlarmManager.RTC_WAKEUP,time,AlarmManager.INTERVAL_DAY * 7,pi);
    }
    //ELIMINO ALARMA
    private void elimAlarmaDias(){
        AlarmManager am = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(micontexto,MiAlarmaDias.class);
        PendingIntent pi = PendingIntent.getBroadcast(micontexto,1,i,0);
        am.cancel(pi);

    }

    private void iniciarFirebase(){
        FirebaseApp.initializeApp(requireActivity());
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference.keepSynced(true);

    }
    private void crearCanalNotificacion(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "CanalAlertaTuturno";
            String descripcion = "Canal para tuTurno";
            int importancia = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notituTurno",name,importancia);
            channel.setDescription(descripcion);

            NotificationManager notificationManager = requireActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void crearCanalNotificacionDias(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "CanalAlertaTuturnoDias";
            String descripcion = "Canal para tuTurno dias";
            int importancia = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notituTurnoDias",name,importancia);
            channel.setDescription(descripcion);

            NotificationManager notificationManager = requireActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
