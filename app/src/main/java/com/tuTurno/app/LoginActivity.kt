package com.tuTurno.app

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.*
import com.tuTurno.app.Admin.ActividadAdmin
import com.tuTurno.app.Cliente.ActividadCliente
import com.tuTurno.app.SuperAdmin.ActividadSuperAdmin
import models.cliente
import java.util.*


class LoginActivity : AppCompatActivity() {

    private lateinit var txtUsuario: EditText
    private lateinit var txtContrasena: EditText
    private lateinit var checkadmin: CheckBox
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var progressDialog: ProgressDialog
    private val keyusuario = "KEYUSUARIO"
    private val keycontra = "KEYCONTRA"
    private var usuario = ""
    private var contrasena = ""
    private lateinit var preferencias: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //OBTENEMOS EL PREFERENCE MANAGER
        preferencias = PreferenceManager.getDefaultSharedPreferences(this)

        txtUsuario = findViewById(R.id.txtUsuario)
        txtContrasena = findViewById(R.id.txtContrase??a)
        checkadmin = findViewById(R.id.checkBox)
        auth = FirebaseAuth.getInstance()

        usuario = preferencias.getString(keyusuario, "No usuario").toString()
        contrasena = preferencias.getString(keycontra, "No contrasena").toString()

        if (usuario != "No usuario" && contrasena != "No contrasena") {
            txtUsuario.setText(preferencias.getString(keyusuario, "No usuario"))
            txtContrasena.setText(preferencias.getString(keycontra, "No contrasena"))
        }

        progressDialog = ProgressDialog(this)

        iniciarFirebase()

    }

    override fun onPause() {
        super.onPause()
        progressDialog.dismiss()
    }

    fun OlvideContrasena(view: View) {
        if (isConnectedToNetwork()) {
            startActivity(Intent(this, RecuperoActivity::class.java))
        } else {
            Toast.makeText(
                this,
                "Compruebe su conexi??n a internet e intente de nuevo",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    fun registrar(view: View) {
        if (isConnectedToNetwork()) {
            startActivity(Intent(this, RegistroActivity::class.java))
        } else {
            Toast.makeText(
                this,
                "Compruebe su conexi??n a internet e intente de nuevo",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    fun login(view: View) {
        if (isConnectedToNetwork()) {
            loginUsuario()
        } else {
            Toast.makeText(
                this,
                "Compruebe su conexi??n a internet e intente de nuevo",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun loginUsuario() {
        val usuario = txtUsuario.text.toString()
        val contrasena = txtContrasena.text.toString()

        if (!TextUtils.isEmpty(usuario) && !TextUtils.isEmpty(contrasena)) {

            if (checkadmin.isChecked) {
                val editor = preferencias.edit()
                editor.putString(keyusuario, usuario)
                editor.putString(keycontra, contrasena)
                editor.apply()
            }

            progressDialog.setTitle("Cargando...")
            progressDialog.setMessage("Espere por favor")
            progressDialog.show()

            auth.signInWithEmailAndPassword(usuario, contrasena)
                .addOnCompleteListener(this) { task ->

                    if (task.isSuccessful) {
                        Accion(usuario)
                    } else {
                        try {
                            throw task.exception!!
                        } catch (e: FirebaseAuthInvalidCredentialsException) {
                            progressDialog.dismiss()
                            Toast.makeText(
                                this,
                                "Email o contrase??a incorrecta, intente nuevamente",
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: FirebaseAuthInvalidUserException) {
                            progressDialog.dismiss()
                            Toast.makeText(
                                this,
                                "Email o contrase??a incorrecta, intente nuevamente",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                }
        } else {
            if (TextUtils.isEmpty(usuario)) {
                txtUsuario.error = getString(R.string.errorusu)
            }
            if (TextUtils.isEmpty(contrasena)) {
                txtContrasena.error = getString(R.string.errorcontra)
            }
        }
    }

    fun Context.isConnectedToNetwork(): Boolean {
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        return connectivityManager?.activeNetworkInfo?.isConnectedOrConnecting() ?: false
    }

    private fun Accion(user: String) {
        databaseReference.child("Clientes")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (shot in dataSnapshot.children) {
                        val c = shot.getValue(cliente::class.java)!!
                        if (c.getEmail() == Objects.requireNonNull(auth.currentUser?.email)) {
                            val admin = c.getAdmin()
                            recibirnombre(user, admin)
                            finish()
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })

    }

    private fun recibirnombre(user: String, admin: String) {
        if (user == "roygenoff@gmail.com") {
            startActivity(Intent(this, ActividadSuperAdmin::class.java))
        } else {
            if (admin == "Si" || admin == "Restringido" || admin == "AdminRestringido") {
                startActivity(Intent(this, ActividadAdmin::class.java))
            } else {
                startActivity(Intent(this, ActividadCliente::class.java))
            }
        }
    }


    private fun iniciarFirebase() {
        FirebaseApp.initializeApp(this)
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference()
        auth = FirebaseAuth.getInstance()
        databaseReference.keepSynced(true)
    }

}
