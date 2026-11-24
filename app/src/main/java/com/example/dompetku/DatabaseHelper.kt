package com.example.dompetku

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class Transaksi(
    val id: Int = 0,
    val nama: String,
    val nominal: Int
)

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "dompetku.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE transaksi (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nama TEXT NOT NULL,
                nominal INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS transaksi")
        onCreate(db)
    }

    fun insertTransaksi(nama: String, nominal: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nama", nama)
            put("nominal", nominal)
        }
        return db.insert("transaksi", null, values)
    }

    fun getAllTransaksi(): List<Transaksi> {
        val list = mutableListOf<Transaksi>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM transaksi ORDER BY id DESC", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val nama = cursor.getString(cursor.getColumnIndexOrThrow("nama"))
                val nominal = cursor.getInt(cursor.getColumnIndexOrThrow("nominal"))
                list.add(Transaksi(id, nama, nominal))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun updateTransaksi(id: Int, nama: String, nominal: Int): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nama", nama)
            put("nominal", nominal)
        }
        return db.update("transaksi", values, "id = ?", arrayOf(id.toString()))
    }

    fun deleteTransaksi(id: Int): Int {
        val db = writableDatabase
        return db.delete("transaksi", "id = ?", arrayOf(id.toString()))
    }
}
