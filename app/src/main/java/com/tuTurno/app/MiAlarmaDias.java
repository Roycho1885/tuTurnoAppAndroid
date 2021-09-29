package com.tuTurno.app;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import models.cliente;

public class MiAlarmaDias extends BroadcastReceiver {
    private DatabaseReference databaseReference;
    private cliente clientes = new cliente();

    @Override
    public void onReceive(Context context, Intent intent) {
        iniciarFirebase(context);

        //cargo las disciplinas
        databaseReference.child("Clientes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (final DataSnapshot shot : snapshot.getChildren()) {
                    clientes = shot.getValue(cliente.class);
                    assert clientes != null;
                    if (!clientes.getAdmin().equals("Si")) {
                        if (!clientes.getDiasporsemana().equals("5")) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("diasporsemana", clientes.getDiasporsemanaresg());
                            databaseReference.child("Clientes").child(clientes.getId()).updateChildren(hashMap).addOnSuccessListener(o -> {
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        @SuppressLint("ResourceAsColor") NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notituTurnoDias")
                .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
                .setContentTitle("Dias Actualizados")
                .setContentText("Los dias fueron actualizados correctamente")
                .setColor(R.color.secondaryLightColor)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(201, builder.build());

    }

    private void iniciarFirebase(Context context) {
        FirebaseApp.initializeApp(context);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        databaseReference.keepSynced(true);

    }
}
