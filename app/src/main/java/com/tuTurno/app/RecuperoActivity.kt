package com.tuTurno.app

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class RecuperoActivity : AppCompatActivity() {

    private lateinit var txtemail: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recupero)

        progressBar= findViewById(R.id.progressBar3)
        auth = FirebaseAuth.getInstance()
        txtemail=findViewById(R.id.txtemail)
    }

    fun enviaremail(view:View){
        val email = txtemail.text.toString()
            if(!TextUtils.isEmpty(email)){
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(this){
                            task ->

                        if (task.isSuccessful){
                            progressBar.visibility = View.VISIBLE
                            Toast.makeText(this, "Email enviado", Toast.LENGTH_SHORT).show()
                            finish()
                            startActivity(Intent(this,LoginActivity::class.java))
                        }else{
                            Toast.makeText(this, "Error al enviar el email", Toast.LENGTH_SHORT).show()
                        }
                    }
            }else{
                txtemail.error = getString(R.string.errorusu)
            }
    }
}
