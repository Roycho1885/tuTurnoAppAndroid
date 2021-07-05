package com.tuTurno.app.Admin;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tuTurno.app.R;

public class AcercadeAdmin extends Fragment {
    private Context micontexto;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        micontexto = context;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.acercadeadmin, container, false);
        ImageView firebase = root.findViewById(R.id.imageView1);
        ImageView web = root.findViewById(R.id.imagenweb);
        ImageView facebook = root.findViewById(R.id.imagenfacebook);
        ImageView instagram = root.findViewById(R.id.imageninstagram);
        Button calificar = root.findViewById(R.id.btncalificar);

        FloatingActionButton fab= requireActivity().findViewById(R.id.fab_admin);
        fab.setVisibility(View.GONE);


        firebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(Intent.ACTION_VIEW, Uri.parse("https://firebase.google.com/"));
                startActivity(a);
            }
        });

        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent b = new Intent(Intent.ACTION_VIEW, Uri.parse("https://tuturno.web.app/"));
                startActivity(b);
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent b = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/tuTurnoApp"));
                startActivity(b);
            }
        });

        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent b = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/tuturnoapp/"));
                startActivity(b);
            }
        });

        calificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent b = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.tuTurno.app&hl=es_AR&gl=US"));
                startActivity(b);
            }
        });
        return root;
    }
}
