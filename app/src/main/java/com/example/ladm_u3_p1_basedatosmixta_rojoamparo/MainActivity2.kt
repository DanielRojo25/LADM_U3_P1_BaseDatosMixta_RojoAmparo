package com.example.ladm_u3_p1_basedatosmixta_rojoamparo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.ladm_u3_p1_basedatosmixta_rojoamparo.databinding.ActivityMain2Binding
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity2 : AppCompatActivity() {
    lateinit var binding: ActivityMain2Binding
    var listaIDs = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        mostrar()
        binding.regresar.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }
    }

    private fun mostrar(){
            FirebaseFirestore.getInstance()
                .collection("ALUMNO")
                .addSnapshotListener { value, error ->
                    if (error!=null){
                        Toast.makeText(this,"No se pudo realizar la consulta",Toast.LENGTH_LONG).show()
                        return@addSnapshotListener
                    }
                    var lista = ArrayList<String>()
                    listaIDs.clear()
                    for (documento in value!!){
                        val cadena = documento.getString("NOMBRE")+"\n"+
                                documento.getString("ESCUELA")+"\n"+
                                documento.getString("TELEFONO")+"\n"+
                                documento.getString("CARRERAUNO")+"\n"+
                                documento.getString("CARRERADOS")+"\n"+
                                documento.getString("CORREO")+"\n"+
                                documento.getString("FECHA")+"\n"
                        lista.add(cadena)
                        listaIDs.add(documento.id)
                    }
                    binding.listaRegistros.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, lista)
                    
                    binding.listaRegistros.setOnItemClickListener { parent, view, position, id ->
                        val idSeleccionado = listaIDs.get(position)

                        AlertDialog.Builder(this)
                            .setTitle("ATENCIÓN")
                            .setMessage("¿Qué desea hacer con ID: ${idSeleccionado}")
                            .setNeutralButton("Eliminar"){d,i->
                                eliminar(idSeleccionado)
                            }
                            .setPositiveButton("Actualizar"){d,i->
                                actualizar(idSeleccionado)
                            }
                            .setNegativeButton("Salir"){d,i->}
                            .show()
                    }
                }
    }

    private fun actualizar(idSeleccionado: String) {
        var otraVentana = Intent(this,MainActivity3::class.java)
        otraVentana.putExtra("idseleccionado",idSeleccionado)
        startActivity(otraVentana)
    }

    private fun eliminar(idSeleccionado: String) {
        val br = FirebaseFirestore.getInstance()
        br.collection("ALUMNO")
            .document(idSeleccionado)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this,"Se elimino el alumno corractamente!", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(this,"No se pudo eliminar!",Toast.LENGTH_LONG).show()
            }
    }
}