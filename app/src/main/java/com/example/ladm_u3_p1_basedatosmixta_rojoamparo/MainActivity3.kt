package com.example.ladm_u3_p1_basedatosmixta_rojoamparo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.ladm_u3_p1_basedatosmixta_rojoamparo.databinding.ActivityMain3Binding
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity3 : AppCompatActivity() {
    lateinit var binding: ActivityMain3Binding
    var idSeleccionado = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        idSeleccionado = intent.extras!!.getString("idseleccionado")!!

        val br = FirebaseFirestore.getInstance()
        br.collection("ALUMNO")
            .document(idSeleccionado)
            .get()
            .addOnSuccessListener {
                binding.nombre.setText(it.getString("NOMBRE"))
                binding.escuela.setText(it.getString("ESCUELA"))
                binding.telefono.setText(it.getString("TELEFONO"))
                binding.c1.setText(it.getString("CARRERAUNO"))
                binding.c2.setText(it.getString("CARRERADOS"))
                binding.correo.setText(it.getString("CORREO"))
            }
            .addOnFailureListener {
                AlertDialog.Builder(this)
                    .setMessage(it.message)
                    .show()
            }

        binding.btnActualizar.setOnClickListener {
            val baseRemota = FirebaseFirestore.getInstance()
            baseRemota.collection("ALUMNO")
                .document(idSeleccionado)
                .update("NOMBRE", binding.nombre.text.toString(),
                    "ESCUELA", binding.escuela.text.toString(),
                    "TELEFONO", binding.telefono.text.toString(),
                    "CARRERAUNO",binding.c1.text.toString(),
                    "CARRERADOS",binding.c2.text.toString(),
                    "CORREO",binding.correo.text.toString())
                .addOnSuccessListener {
                    Toast.makeText(this,"SE ACTULIZO EL ALUMNO CON EXITO!", Toast.LENGTH_LONG)
                        .show()
                    binding.nombre.text.clear()
                    binding.escuela.text.clear()
                    binding.telefono.text.clear()
                    binding.c1.text.clear()
                    binding.c2.text.clear()
                    binding.correo.text.clear()
                }
                .addOnFailureListener {
                    AlertDialog.Builder(this)
                        .setMessage(it.message)
                        .show()
                }
        }

        binding.btnRegresar.setOnClickListener {
            startActivity(Intent(this,MainActivity2::class.java))
        }
    }
}