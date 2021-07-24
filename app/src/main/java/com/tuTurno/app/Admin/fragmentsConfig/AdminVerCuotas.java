package com.tuTurno.app.Admin.fragmentsConfig;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuTurno.app.ListViewAdaptadorVerPagosAdmin;
import com.tuTurno.app.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import models.cuotas;

public class AdminVerCuotas extends Fragment {

    private DatabaseReference databaseReference;
    private EditText buscar;
    private Button verpagos;
    private ScrollView scroll;
    private ArrayAdapter miadaptador;
    String meses,anioo, anoseleccionado;
    Context micontexto;
    boolean banderames, banderaano;
    Calendar micalendario;
    private TextView registros;
    int contador;

    private ArrayList<String> arrayano, arraymeses;
    private ArrayAdapter<String> adapterano, adaptermeses;

    //para el listview
    private final ArrayList<cuotas> listacuotaspagas = new ArrayList<>();
    private ListView milistacuotasadminpagas;
    private ListViewAdaptadorVerPagosAdmin adaptador;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        micontexto = context;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.adminvercuotas, container, false);
        milistacuotasadminpagas = root.findViewById(R.id.listacuotasclientespagas);
        buscar = root.findViewById(R.id.botonbuscar);
        final AutoCompleteTextView botonanodesple = root.findViewById(R.id.despleano);
        final AutoCompleteTextView botonmesdesple = root.findViewById(R.id.desplemes);
        verpagos = root.findViewById(R.id.botonverpagos);
        scroll = root.findViewById(R.id.scrolll);
        registros = root.findViewById(R.id.registros);

        //para los diferentes gimnasios
        final TextView gimnasio = requireActivity().findViewById(R.id.textologo);

        micalendario = Calendar.getInstance();
        adaptador = new ListViewAdaptadorVerPagosAdmin(micontexto,listacuotaspagas);

        //PUBLICIDAD
        MobileAds.initialize(micontexto, initializationStatus -> {
        });
        AdView mAdView = root.findViewById(R.id.adView8);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        iniciarFirebase();

        botonanodesple.post(() -> botonanodesple.getText().clear());

        botonmesdesple.post(() -> botonmesdesple.getText().clear());

        formatearfecha();

        scroll.setOnTouchListener((v, event) -> {
            milistacuotasadminpagas.getParent()
                    .requestDisallowInterceptTouchEvent(false);
            return false;
        });

        milistacuotasadminpagas.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        //CARGO BOTON ANO CORRESPONDIENTE
        arrayano = new ArrayList<>();
        databaseReference.child(gimnasio.getText().toString()).child("Cuotas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot shot : snapshot.getChildren()){
                    arrayano.add(Objects.requireNonNull(shot.getKey()));
                }
                adapterano = new ArrayAdapter<>(micontexto, android.R.layout.simple_list_item_1, arrayano);
                botonanodesple.setAdapter(adapterano);
                botonanodesple.setInputType(InputType.TYPE_NULL);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        botonanodesple.setOnItemClickListener((adapterView, view, i, l) -> {
            banderaano = true;

            //LIMPIO BOTON MES
            botonmesdesple.post(() -> botonmesdesple.getText().clear());
            arraymeses = new ArrayList<>();
            anoseleccionado = adapterView.getAdapter().getItem(i).toString();

            databaseReference.child(gimnasio.getText().toString()).child("Cuotas").child(anoseleccionado).orderByChild("mespago").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot shot: snapshot.getChildren()){
                        arraymeses.add(Objects.requireNonNull(shot.getKey()));
                    }
                    adaptermeses = new ArrayAdapter<>(micontexto, android.R.layout.simple_list_item_1, arraymeses);
                    botonmesdesple.setAdapter(adaptermeses);
                    botonmesdesple.setInputType(InputType.TYPE_NULL);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        //BOTON MES CORRESPONDIENTE
        botonmesdesple.setOnItemClickListener((adapterView, view, i, l) -> {
            banderames = true;
            meses = adapterView.getAdapter().getItem(i).toString();
        });

        //BOTON VER PAGOS
        verpagos.setOnClickListener(view -> {
            contador = 0;
            listacuotaspagas.clear();
            if(!banderames || !banderaano){
                Snackbar.make(view, "Seleccione año y mes", Snackbar.LENGTH_SHORT).show();
            }else {
                databaseReference.child(gimnasio.getText().toString()).child("Cuotas").child(anioo.trim()).child(meses.toLowerCase().trim()).orderByChild("clientenombre").addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot shot : snapshot.getChildren()){
                            cuotas cu = shot.getValue(cuotas.class);
                            assert cu != null;
                            contador = contador + 1;

                            //POSIBLE SOLICION A NUMERO DE MES
                            if(cu.getClientenombre() != null){
                                listacuotaspagas.add(cu);
                                milistacuotasadminpagas.setAdapter(adaptador);
                            }
                        }
                        registros.setText(contador + " pagos listados");
                        if(snapshot.getValue()==null){
                            Snackbar.make(view, "Para este mes no existen pagos efectuados", Snackbar.LENGTH_SHORT).show();
                            milistacuotasadminpagas.setAdapter(null);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

                botonmesdesple.post(() -> botonmesdesple.getText().clear());
                botonanodesple.post(() -> botonanodesple.getText().clear());
            }
        });

        buscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adaptador.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return root;
    }

    private void iniciarFirebase(){
        FirebaseApp.initializeApp(requireActivity());
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.keepSynced(true);

    }

    private void formatearfecha(){
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy");
        String fechaactualmodi = sdf.format(micalendario.getTime());
        int index1 = fechaactualmodi.indexOf("-",3);
        anioo = fechaactualmodi.substring(index1+1);
    }

}
