package com.example.ladm_u3_p1_basedatosmixta_rojoamparo

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class BaseDatos(
    context: Context?,
    name:String?,
    factory:SQLiteDatabase.CursorFactory?,
    version:Int) : SQLiteOpenHelper(context,name,factory,version) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE ALUMNO (ID INTEGER PRIMARY KEY AUTOINCREMENT, NOMBRE VARCHAR(500), ESCUELA VARCHAR(500), TELEFONO INTEGER, CARRERAUNO VARCHAR(200), CARRERADOS VARCHAR(200), CORREO VARCHAR(200), FECHA DATETIME)")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }
}