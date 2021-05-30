package com.tuTurno.app.LaFuria.fragmentsConfig;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
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

import models.cuotas;

public class AdminVerCuotas extends Fragment {

    private DatabaseReference databaseReference;
    private AutoCompleteTextView botonmesdesple;
    private EditText buscar;
    private Button verpagos;
    private ScrollView scroll;
    private ArrayAdapter miadaptador;
    String meses,anioo;
    Context micontexto;
    boolean bandera;
    Calendar micalendario;

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
        botonmesdesple = root.findViewById(R.id.desplemes);
        verpagos = root.findViewById(R.id.botonverpagos);
        scroll = root.findViewById(R.id.scrolll);
        //para los diferentes gimnasios
        final TextView gimnasio = requireActivity().findViewById(R.id.textologo);

        String[] mes ={"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};

        miadaptador = new ArrayAdapter<>(micontexto,android.R.layout.simple_list_item_1,mes);
        botonmesdesple.setAdapter(miadaptador);
        micalendario = Calendar.getInstance();
        adaptador = new ListViewAdaptadorVerPagosAdmin(micontexto,listacuotaspagas);

        //PUBLICIDAD
        MobileAds.initialize(micontexto, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdView mAdView = root.findViewById(R.id.adView8);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        iniciarFirebase();
        formatearfecha();

        scroll.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {
                milistacuotasadminpagas.getParent()
                        .requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });

        milistacuotasadminpagas.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        //BOTON MES CORRESPONDIENTE
        botonmesdesple.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                bandera = true;
                meses = adapterView.getAdapter().getItem(i).toString();
            }
        });

        //BOTON VER PAGOS
        verpagos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                listacuotaspagas.clear();
                if(!bandera){
                    Snackbar.make(view, "Seleccione un mes", Snackbar.LENGTH_SHORT).show();
                }else {
                    databaseReference.child(gimnasio.getText().toString()).child("Cuotas").child(anioo.trim()).child(meses.toLowerCase().trim()).orderByChild("clientenombre").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot shot : snapshot.getChildren()){
                                cuotas cu = shot.getValue(cuotas.class);
                                assert cu != null;
                                listacuotaspagas.add(cu);
                                milistacuotasadminpagas.setAdapter(adaptador);
                            }
                            if(snapshot.getValue()==null){
                                Snackbar.make(view, "Para este mes no existen pagos efectuados", Snackbar.LENGTH_SHORT).show();
                                milistacuotasadminpagas.setAdapter(null);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
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
