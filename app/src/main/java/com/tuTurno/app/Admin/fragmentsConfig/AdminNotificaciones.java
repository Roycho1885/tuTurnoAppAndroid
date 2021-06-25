package com.tuTurno.app.Admin.fragmentsConfig;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuTurno.app.Api;
import com.tuTurno.app.R;

import java.util.ArrayList;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminNotificaciones extends Fragment {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private TextView txttitulonoti;
    private TextView txtdetallenoti;
    private ArrayList<String> arraydisci;
    Context micontexto;
    private ArrayAdapter miadapter;
    boolean menueli;
    String disci1;
    ProgressDialog cargando;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        micontexto = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.adminnotifaciones, container, false);
        FloatingActionButton fab= requireActivity().findViewById(R.id.fab_admin);
        fab.setVisibility(View.GONE);

        final AutoCompleteTextView menuelim = root.findViewById(R.id.gruponotifadmin);
        final Button enviarnoti = root.findViewById(R.id.botonenviarnotiadmin);
        txttitulonoti = root.findViewById(R.id.txtTituloNoti);
        txtdetallenoti = root.findViewById(R.id.txtDetalleNoti);
        //para los diferentes gimnasios
        final TextView gimnasio = requireActivity().findViewById(R.id.textologo);
        final String gym = gimnasio.getText().toString().replace(" ","_");

        iniciarFirebase();
        cargando = new ProgressDialog(micontexto);

        arraydisci = new ArrayList<>();

        //TODO LO QUE TENGA QUE VER PARA ELIMINAR DISCIPLINA Y TURNO
        databaseReference.child(gimnasio.getText().toString()).child("Disciplinas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot shot : snapshot.getChildren()){
                    arraydisci.add(Objects.requireNonNull(shot.getKey()));
                }
                miadapter = new ArrayAdapter<>(micontexto, android.R.layout.simple_list_item_1, arraydisci);
                menuelim.setAdapter(miadapter);
                menuelim.setInputType(InputType.TYPE_NULL);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        menuelim.post(new Runnable() {
            @Override
            public void run() {
                menuelim.getText().clear();
            }
        });

        menuelim.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                menueli = true;
                disci1 = adapterView.getAdapter().getItem(i).toString();
            }
        });

        enviarnoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titulo = txttitulonoti.getText().toString();
                String detalle = txtdetallenoti.getText().toString();
                if(!menueli){
                    Snackbar.make(view,"Ingrese grupo",Snackbar.LENGTH_SHORT).show();
                }else {
                    if((titulo.equals("")) || (detalle.equals(""))){
                        Snackbar.make(view,"Ingrese Título y Detalle",Snackbar.LENGTH_SHORT).show();
                    }else{
                        cargando.setTitle("Enviando...");
                        cargando.setMessage("Espere por favor...");
                        cargando.show();
                        enviarnotificacionapi(disci1+gym,txttitulonoti.getText().toString(),txtdetallenoti.getText().toString(),view);
                    }
                }
            }
        });

        return root;
    }

    private void enviarnotificacionapi(String topic, String titulo , String detalle, final View view){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tuturno-91997.web.app/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api api = retrofit.create(Api.class);
        Call<ResponseBody> call = api.enviarnotificacion(topic,titulo,detalle);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                assert response.body() != null;
                cargando.dismiss();
                Snackbar.make(view,"Notificación enviada con exito",Snackbar.LENGTH_SHORT).show();
                txtdetallenoti.setText("");
                txttitulonoti.setText("");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                cargando.dismiss();
                Snackbar.make(view,"No se pudo enviar la notificación, revise su conexión a internet e intente nuevamente",Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void  iniciarFirebase(){
        FirebaseApp.initializeApp(requireActivity());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference.keepSynced(true);
    }
}
