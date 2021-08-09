package com.tuTurno.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import com.santalu.maskara.widget.MaskEditText;
import android.text.Html;
import android.text.InputType;
import android.text.method.LinkMovementMethod;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import models.cliente;
import models.gimnasios;

public class RegistroActivity extends AppCompatActivity {

    private EditText txtNombre;
    private EditText txtApellido;
    private EditText txtdni;
    private EditText txtdireccion;
    private EditText txtEmail;
    private EditText txtContrasena;
    private EditText txtCodigo;
    private MaskEditText txttelefono;
    private AutoCompleteTextView txtgym;
    private CheckBox check;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    String codigoacceso;
    ProgressDialog cargando;

    cliente cli = new cliente();
    gimnasios c = new gimnasios();
    private ArrayList<String> arraygimnasios;
    private ArrayAdapter myadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);



        txtNombre = findViewById(R.id.txtNombre);
        txtApellido = findViewById(R.id.txtApellido);
        txtdni = findViewById(R.id.txtdni);
        txtdireccion = findViewById(R.id.txtdireccion);
        txttelefono = findViewById(R.id.txttelefono);
        txtgym = findViewById(R.id.txtgym);
        txtEmail = findViewById(R.id.txtEmail);
        txtContrasena = findViewById(R.id.txtContrasena);
        txtCodigo = findViewById(R.id.txtCodigo);
        check = findViewById(R.id.checkBox1);
        cargando = new ProgressDialog(this);

        arraygimnasios = new ArrayList<>();


        String checkBoxText = "Estoy de acuerdo con las <a href='https://tuturno.web.app/Pol%C3%ADticas-de-Privacidad.html'> Políticas de Privacidad</a>";

        check.setText(Html.fromHtml(checkBoxText));
        check.setMovementMethod(LinkMovementMethod.getInstance());


        iniciarFirebase();

        //CARGO LOS GIMNASIOS EN EL AUTOCOMPLETETEXTVIEW
        databaseReference.child("Gimnasios").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot shot : snapshot.getChildren()){
                    arraygimnasios.add(shot.child("nombre").getValue().toString());
                }
               myadapter = new ArrayAdapter<>(RegistroActivity.this, android.R.layout.simple_list_item_1, arraygimnasios);
                txtgym.setAdapter(myadapter);
                txtgym.setInputType(InputType.TYPE_NULL);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        

        findViewById(R.id.button).setOnClickListener(v -> {

            if (check.isChecked()){
                CrearNuevaCuenta();
            }else{
                Toast.makeText(getApplicationContext(),"Acepta nuestras Políticas de Privacidad", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void CrearNuevaCuenta(){
    final String nombre = txtNombre.getText().toString().trim();
    final String apellido = txtApellido.getText().toString().trim();
    final String dni = txtdni.getText().toString().trim();
    final String direccion = txtdireccion.getText().toString().trim();
    final String telefono = txttelefono.getText().toString().trim();
    final String gym = txtgym.getText().toString().trim();
    final String email = txtEmail.getText().toString().trim();
    final String contrasena = txtContrasena.getText().toString().trim();
    final String codigo = txtCodigo.getText().toString().trim();


    cli.setId(UUID.randomUUID().toString());
    cli.setNombre(nombre);
    cli.setApellido(apellido);
    cli.setDni(dni);
    cli.setDireccion(direccion);
    cli.setGym(gym);
    cli.setEmail(email);
    cli.setAdmin("No");
    cli.setToken("0");
    cli.setUltimopago("Nunca");
    cli.setFechavencimiento("Nunca");
    cli.setEstadopago(0);
    cli.setEstadodeuda("0");
    cli.setDisciplinaelegida("0");
    cli.setDiasporsemana("0");
    cli.setDiasporsemanaresg("0");
    cli.setTelefono(telefono);

        cargando.setTitle("Registrando...");
        cargando.setMessage("Espere por favor...");
        cargando.show();

        //CARGO EL CODIGO DE ACCESO Y SI ES CORRECTO SE LOGEA A LA APP

       databaseReference.child("Gimnasios").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot shot : snapshot.getChildren()){
                    c = shot.getValue(gimnasios.class);
                    if(c.getNombre().equals(gym.trim())){
                        codigoacceso = c.getCodigoacceso();
                    }
                }

                if(!nombre.equals("") && !apellido.equals("") && !email.equals("") && !dni.equals("") && !direccion.equals("") && !telefono.equals("") && (txttelefono.isDone()) && !contrasena.equals("") && !gym.equals("")&& !codigo.equals("")){
                    if(codigo.equals(codigoacceso)){

                        firebaseAuth.createUserWithEmailAndPassword(email,contrasena).addOnCompleteListener(RegistroActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()){

                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    assert user != null;
                                    ValidarEmail(user);
                                    databaseReference.child("Clientes").child(cli.getId()).setValue(cli);
                                    finish();
                                    Accion();

                                }else{
                                    try {
                                        throw Objects.requireNonNull(task.getException());

                                    }catch (FirebaseAuthException e)
                                    {switch (e.getErrorCode())
                                    { case "ERROR_WEAK_PASSWORD":
                                        cargando.dismiss();
                                        Toast.makeText(getApplicationContext(),"La contraseña debe contener por lo menos seis caracteres", Toast.LENGTH_SHORT).show();
                                        break;

                                        case "ERROR_EMAIL_ALREADY_IN_USE":
                                            cargando.dismiss();
                                            Toast.makeText(getApplicationContext(),"Email utilizado en una cuenta existente", Toast.LENGTH_SHORT).show();
                                            break;
                                    }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        });
                    }else{
                        cargando.dismiss();
                        Toast.makeText(getApplicationContext(),"El código ingresado no corresponde", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    cargando.dismiss();
                    if(nombre.equals("")){
                        txtNombre.setError("Ingrese Nombre");
                    }
                    if(apellido.equals("")){
                        txtApellido.setError("Ingrese Apellido");
                    }
                    if(dni.equals("")){
                        txtdni.setError("Ingrese DNI");
                    }
                    if(direccion.equals("")){
                        txtdireccion.setError("Ingrese Dirección");
                    }
                    if(telefono.equals("")){
                        txttelefono.setError("Ingrese Teléfono");
                    }else{
                        if(!(txttelefono.isDone())){
                            txttelefono.setError("Complete los 11 números");
                        }
                    }
                    if(email.equals("")){
                        txtEmail.setError("Ingrese Email");
                    }
                    if(contrasena.equals("")){
                        txtContrasena.setError("Ingrese Contraseña con mas de 6 caracteres");
                    }
                    if(gym.equals("")){
                        txtgym.setError("Seleccione Gimnasio");
                    }
                    if(codigo.equals("")){
                        txtCodigo.setError("Ingrese Código");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void Accion() {
        cargando.dismiss();
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void ValidarEmail(FirebaseUser user) {
        user.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Email Enviado", Toast.LENGTH_SHORT).show();
                }else{
                    cargando.dismiss();
                    Toast.makeText(getApplicationContext(),"Error al enviar email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void iniciarFirebase(){
        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference.keepSynced(true);

    }
}
