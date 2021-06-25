package com.tuTurno.app.Cliente.fragmentsConfigCli;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuTurno.app.ListViewAdaptadorVerPagosAdmin;
import com.tuTurno.app.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import models.cliente;
import models.cuotas;

public class CliVerPago extends Fragment {

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private AutoCompleteTextView botonmesdesple;
    private Button verpagos;
    private TextView txtpagos;
    private ScrollView scroll;
    private ArrayAdapter miadaptador;
    Context micontexto;
    Calendar micalendario,micalendario1;
    String meses,anioo,emailcliente,fecha_venc;
    boolean bandera,bandera1;
    cliente c,cli;
    Date fechaactual,fechavence;
    cuotas cu;

    //para el listview
    private final ArrayList<cuotas> listacuotaspagas = new ArrayList<>();
    private ListView milistacuotasadminpagas;
    private ListViewAdaptadorVerPagosAdmin adaptador;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        micontexto = context;
    }


    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.cliverpago, container, false);
        milistacuotasadminpagas = root.findViewById(R.id.listacuotasclientespagas);
        botonmesdesple = root.findViewById(R.id.desplemes);
        verpagos = root.findViewById(R.id.botonverpagos);
        scroll = root.findViewById(R.id.scrolll);
        txtpagos = root.findViewById(R.id.txt_estadopagos);
        FloatingActionButton fab= requireActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
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
        AdView mAdView = root.findViewById(R.id.adView7);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        iniciarFirebase();
        formatearfecha();

        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy");
        micalendario1 = Calendar.getInstance();
        final String fecha_actual = sdf.format(micalendario1.getTime());

        databaseReference.child("Clientes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot shot : snapshot.getChildren()){
                        c = shot.getValue(cliente.class);
                        assert c != null;
                        if(c.getEmail().equals(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail())){
                            cli = shot.getValue(cliente.class);
                            assert cli != null;
                            emailcliente = cli.getEmail();
                            fecha_venc = cli.getFechavencimiento();

                            try {
                                fechaactual = sdf.parse(fecha_actual);
                                fechavence = sdf.parse(fecha_venc);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if(fecha_venc.equals("Nunca")){
                                txtpagos.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_cancel_24,0,0,0);
                                txtpagos.setText("Usted no posee pagos efectuados");
                            }else{
                                if(fechaactual.compareTo(fechavence)<0){
                                    txtpagos.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_check_circle_24,0,0,0);
                                    txtpagos.setText("Usted se encuentra al día");
                                }else {
                                    txtpagos.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_cancel_24,0,0,0);
                                    txtpagos.setText("Su cuota mensual se encuentra vencida");
                                }
                            }
                        }

                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


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
                bandera1 = false;
                listacuotaspagas.clear();
                if(!bandera){
                    Snackbar.make(view, "Seleccione un mes", Snackbar.LENGTH_SHORT).show();
                }else {
                    databaseReference.child(gimnasio.getText().toString()).child("Cuotas").child(anioo.trim()).child(meses.toLowerCase().trim()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot shot : snapshot.getChildren()){
                                cu = shot.getValue(cuotas.class);
                                assert cu != null;
                                if(emailcliente.equals(cu.getEmailcliente())){
                                    listacuotaspagas.add(cu);
                                    bandera1=true;
                                    milistacuotasadminpagas.setAdapter(adaptador);
                                }
                            }


                            if(snapshot.getValue()==null){
                                Snackbar.make(view, "Para este mes no existen pagos efectuados", Snackbar.LENGTH_SHORT).show();
                                milistacuotasadminpagas.setAdapter(null);
                            }
                            if(!bandera1){
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


        return root;
    }

    private void iniciarFirebase(){
        FirebaseApp.initializeApp(requireActivity());
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference.keepSynced(true);

    }

    private void formatearfecha(){
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy");
        String fechaactualmodi = sdf.format(micalendario.getTime());
        int index1 = fechaactualmodi.indexOf("-",3);
        anioo = fechaactualmodi.substring(index1+1);
    }
}
