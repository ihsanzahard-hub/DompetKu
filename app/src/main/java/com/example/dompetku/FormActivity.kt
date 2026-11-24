package com.example.dompetku

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class FormActivity : ComponentActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelper(this)

        val editId = intent.getIntExtra("EXTRA_ID", -1)
        val editNama = intent.getStringExtra("EXTRA_NAMA") ?: ""
        val editNominal = intent.getIntExtra("EXTRA_NOMINAL", 0)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FormScreen(
                        editId = editId,
                        initialNama = editNama,
                        initialNominal = if (editId != -1) editNominal.toString() else "",
                        onSave = { nama, nominal ->
                            if (editId == -1) {
                                dbHelper.insertTransaksi(nama, nominal)
                                Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                            } else {
                                dbHelper.updateTransaksi(editId, nama, nominal)
                                Toast.makeText(this, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
                            }
                            finish()
                        },
                        onCancel = { finish() }
                    )
                }
            }
        }
    }
}

@Composable
fun FormScreen(
    editId: Int,
    initialNama: String,
    initialNominal: String,
    onSave: (String, Int) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current

    var nama by rememberSaveable { mutableStateOf(initialNama) }
    var nominalText by rememberSaveable { mutableStateOf(initialNominal) }

    var namaError by rememberSaveable { mutableStateOf<String?>(null) }
    var nominalError by rememberSaveable { mutableStateOf<String?>(null) }

    val isEditMode = editId != -1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = if (isEditMode) "Edit Pengeluaran" else "Tambah Pengeluaran",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nama,
            onValueChange = {
                nama = it
                namaError = if (it.isBlank()) "Nama pengeluaran tidak boleh kosong" else null
            },
            label = { Text("Nama Pengeluaran (contoh: Makan Siang)") },
            isError = namaError != null,
            supportingText = {
                if (namaError != null) {
                    Text(text = namaError!!, color = Color.Red)
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = nominalText,
            onValueChange = {
                nominalText = it
                val angka = it.toIntOrNull()
                nominalError = when {
                    it.isBlank() -> "Nominal tidak boleh kosong"
                    angka == null -> "Nominal harus berupa angka valid"
                    angka <= 0 -> "Nominal harus lebih dari 0"
                    else -> null
                }
            },
            label = { Text("Nominal (Rp)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = nominalError != null,
            supportingText = {
                if (nominalError != null) {
                    Text(text = nominalError!!, color = Color.Red)
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Batal")
            }

            Button(
                onClick = {
                    val trimmedNama = nama.trim()
                    val parsedNominal = nominalText.trim().toIntOrNull()

                    var isValid = true
                    if (trimmedNama.isEmpty()) {
                        namaError = "Nama pengeluaran wajib diisi"
                        isValid = false
                    }
                    if (parsedNominal == null || parsedNominal <= 0) {
                        nominalError = "Masukkan nominal angka lebih dari 0"
                        isValid = false
                    }

                    if (isValid && parsedNominal != null) {
                        onSave(trimmedNama, parsedNominal)
                    } else {
                        Toast.makeText(context, "Periksa kembali input Anda", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(if (isEditMode) "Update" else "Simpan")
            }
        }
    }
}
