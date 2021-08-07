package com.tuTurno.app;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

import models.turno;

public class MiAlarma extends BroadcastReceiver {

    private DatabaseReference databaseReference;
    private models.turno turno = new turno();


    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {

        iniciarFirebase(context);

        //cargo las disciplinas
        databaseReference.child("Gimnasios").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (final DataSnapshot shot2 : snapshot.getChildren()) {
                    databaseReference.child(Objects.requireNonNull(shot2.child("nombre").getValue()).toString()).child("Disciplinas").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (final DataSnapshot shot1 : snapshot.getChildren()) {
                                databaseReference.child(Objects.requireNonNull(shot2.child("nombre").getValue()).toString()).child("Disciplinas").child(Objects.requireNonNull(shot1.getKey())).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot shot : snapshot.getChildren()) {
                                            turno = shot.getValue(models.turno.class);

                                            HashMap hashMap = new HashMap();
                                            hashMap.put("cupo", turno.getCupoalmacenado());
                                            databaseReference.child(Objects.requireNonNull(shot2.child("nombre").getValue()).toString()).child("Disciplinas").child(shot1.getKey()).child(turno.getId()).updateChildren(hashMap).addOnSuccessListener(o -> {
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        @SuppressLint("ResourceAsColor") NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notituTurno")
                .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
                .setContentTitle("Cupos Actualizados")
                .setContentText("Los cupos fueron actualizados correctamente")
                .setColor(R.color.secondaryLightColor)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(200, builder.build());

    }

    private void iniciarFirebase(Context context) {
        FirebaseApp.initializeApp(context);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        databaseReference.keepSynced(true);

    }

}
