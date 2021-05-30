package com.tuTurno.app.AdminConfig;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.tuTurno.app.R;

import de.hdodenhof.circleimageview.CircleImageView;
import models.gimnasios;

public class AdministradorConfigClientes extends Fragment {
    private EditText txtnombrecliente,txtcodigoingreso;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    ProgressDialog cargando;
    Bitmap thumb_bitmap = null;
    private Button botoncargarfoto , botoncargartodo;
    private CircleImageView logo;
    Context micontexto;
    private static final int IMAGE_REQUEST =2;
    private Uri imageuri;



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        micontexto = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.administradorconfigclientes, container, false);
        txtnombrecliente = root.findViewById(R.id.txtnombrecliente);
        txtcodigoingreso = root.findViewById(R.id.txtcodigocliente);
        botoncargarfoto = root.findViewById(R.id.botoncargarfoto);
        botoncargartodo = root.findViewById(R.id.botoncargartodo);
        logo = root.findViewById(R.id.imagen);
        cargando = new ProgressDialog(micontexto);

        iniciarFirebase();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Gimnasios");


        botoncargarfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarfoto();
            }
        });

        botoncargartodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subirtodo();
            }
        });

        return root;

    }

    private void cargarfoto(){
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = requireActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        for (Fragment fragment : getChildFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

        if(requestCode== IMAGE_REQUEST && resultCode== Activity.RESULT_OK){
            assert data != null;
            imageuri= data.getData();
            Picasso.with(micontexto).load(imageuri).into(logo);
        }
    }

    private void subirtodo(){
        final String nombre = txtnombrecliente.getText().toString();
        final String codigo = txtcodigoingreso.getText().toString();

        if(nombre.equals("")&& codigo.equals("")){
            Toast.makeText(micontexto,"Ingrese nombre y código",Toast.LENGTH_LONG).show();
        }else{
            
            cargando.setTitle("Subiendo info...");
            cargando.setMessage("Espere por favor...");
            cargando.show();

            if(imageuri != null){
                final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("LogoClientes").child(System.currentTimeMillis()+"."+getFileExtension(imageuri));
                storageReference.putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();

                                gimnasios gim = new gimnasios(nombre,url,codigo);
                                databaseReference.child(nombre).setValue(gim);
                                cargando.dismiss();

                                cargando.dismiss();
                                Toast.makeText(micontexto,"Información cargada con éxito", Toast.LENGTH_LONG).show();
                                txtnombrecliente.setText("");
                                txtcodigoingreso.setText("");
                                logo.setImageResource(android.R.color.transparent);
                            }
                        });
                    }
                });
            }
        }
    }



    private void iniciarFirebase(){
        FirebaseApp.initializeApp(requireActivity());
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference.keepSynced(true);

    }
}
