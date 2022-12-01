package com.example.ladm_u3_p1_basedatosmixta_rojoamparo

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.ladm_u3_p1_basedatosmixta_rojoamparo.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    val baseDatos = BaseDatos(this, "AlumnosITT", null,1)
    var IDs = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mostrarTodos()
        detectarInternet(this)
        importante()


        binding.insertar.setOnClickListener {
            bdLocal()
        }

        binding.registros.setOnClickListener {
            startActivity(Intent(this, MainActivity2::class.java))
        }

    }

    private fun importante() {
        AlertDialog.Builder(this)
            .setTitle("ATENCIÓN")
            .setMessage("Primero cargar datos en la BD local, llenando el formulario y presionando el boton INSERTAR, despues cerrar y volver abrir la app " +
                    "mientras esta conectado a internet para que salga la ventana para cargar los datos a la nube, al presionar SI, los datos" +
                    " se eliminaran de la BD local automaticamente y se subiran a la nube." +
                    " EL CAMPO TELEFONO DEBE SER DE 9 DIGITOS UNICAMENTE, NO SE POR QUE TRUENA CUANDO PONGO 10 DIGITOS O MAS :( ")
            .setPositiveButton("Ok"){d,i->}
            .show()
    }


    private fun bdLocal() {
        try{
            var alumno = baseDatos.writableDatabase
            var datos = ContentValues()

            datos.put("NOMBRE",binding.nombre.text.toString())
            datos.put("ESCUELA",binding.escuela.text.toString())
            datos.put("TELEFONO",binding.telefono.text.toString().toInt())
            datos.put("CARRERAUNO",binding.c1.text.toString())
            datos.put("CARRERADOS",binding.c2.text.toString())
            datos.put("CORREO",binding.correo.text.toString())
            datos.put("FECHA",Date().toString())

            var resultado = alumno.insert("ALUMNO", "ID",datos)

            if (resultado == -1L){
                AlertDialog.Builder(this)
                    .setTitle("ERROR")
                    .setMessage("NO SE PUDO GUARDAR")
                    .show()
            }else{
                Toast.makeText(this, "SE INSERTO CON EXITO!", Toast.LENGTH_LONG).show()
                binding.nombre.setText("")
                binding.escuela.setText("")
                binding.telefono.setText("")
                binding.c1.setText("")
                binding.c2.setText("")
                binding.correo.setText("")
                mostrarTodos()
            }
        }catch (e:java.lang.Exception){
            AlertDialog.Builder(this)
                .setTitle("ERROR")
                .setMessage("ERROR: "+e)
                .show()
        }
    }

    fun mostrarTodos() {
        var alumno = baseDatos.readableDatabase
        val lista = ArrayList<String>()
        IDs.clear()

        var resultado = alumno.query("ALUMNO", arrayOf("*"), null, null, null, null, null)
        if (resultado.moveToFirst()) {
            do {
                val data = resultado.getString(1) + "\n" + resultado.getString(2) + "\n" +
                        resultado.getInt(3) + "\n" + resultado.getString(4) + "\n" + resultado.getString(5) + "\n" + resultado.getString(6) + "\n" + resultado.getString(7)
                lista.add(data)
                IDs.add(resultado.getInt(0))
            } while (resultado.moveToNext())
        } else {
            lista.add("LA TABLA ESTA VACIA")
        }

        binding.listaAlumno.adapter = ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1, lista)

    }

    private fun bdNube() {
        var alumno = baseDatos.readableDatabase
        var resultado = alumno.query("ALUMNO", arrayOf("*"), null, null, null, null, null)
        AlertDialog.Builder(this)
            .setTitle("CONECTADO!")
            .setMessage("Estas conectado a Internet. ¿Désea enviar datos locales a la nube?")
            .setPositiveButton("Si"){d,i->

                if (resultado.moveToFirst()){
                    do{
                        try {
                            var datos = hashMapOf(
                                "NOMBRE" to resultado.getString(1),
                                "ESCUELA" to resultado.getString(2),
                                "TELEFONO" to resultado.getString(3),
                                "CARRERAUNO" to resultado.getString(4),
                                "CARRERADOS" to resultado.getString(5),
                                "CORREO" to resultado.getString(6),
                                "FECHA" to resultado.getString(7)
                            )

                            FirebaseFirestore.getInstance()
                                .collection("ALUMNO")
                                .add(datos)
                                .addOnSuccessListener {
                                    Toast.makeText(this,"SE INSERTO CON EXITO!", Toast.LENGTH_LONG)
                                        .show()
                                    eliminarLocal()
                                    mostrarTodos()
                                }
                                .addOnFailureListener {
                                    AlertDialog.Builder(this)
                                        .setTitle("ERROR")
                                        .setMessage("NO SE PUDO INSERTAR! COMPRUEBE LOS DATOS O SU CONEXIÓN A INTERNET")
                                        .setPositiveButton("OK"){d,i->}
                                        .show()
                                }
                        }catch (e:java.lang.Exception){
                            AlertDialog.Builder(this)
                                .setTitle("ERROR")
                                .setMessage("ERROR: "+e)
                                .show()
                        }
                    } while (resultado.moveToNext())

                } else {

                }

            }
            .show()

    }

    private fun eliminarLocal(){
        val resultado = baseDatos.writableDatabase.delete("ALUMNO",
            null, null
        )

        if(resultado == 0){
            AlertDialog.Builder(this)
                .setMessage("ERROR NO SE BORRO")
                .show()
        }else{
            Toast.makeText(this,"SE BORRO CON EXITO", Toast.LENGTH_LONG).show()
            mostrarTodos()
        }
    }

    private fun eliminar(idSeleccionado: Int) {
        val resultado = baseDatos.writableDatabase.delete("ALUMNO",
            "ID=?", arrayOf(idSeleccionado.toString()))

        if(resultado == 0){
            AlertDialog.Builder(this)
                .setMessage("ERROR NO SE BORRO")
                .show()
        }else{
            Toast.makeText(this,"SE BORRO CON EXITO", Toast.LENGTH_LONG).show()
            mostrarTodos()
        }

    }

    fun detectarInternet(context : Context) {

        Thread(Runnable {

                val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
                val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

                runOnUiThread {
                    if (isConnected) {
                        binding.textoConexion.text = "Conectado a Internet"
                        bdNube()

                    } else {
                        binding.textoConexion.text = "No hay conexión a Internet"
                    }
                }

        }).start()

    }

}


