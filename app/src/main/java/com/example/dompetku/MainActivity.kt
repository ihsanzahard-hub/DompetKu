package com.example.dompetku

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var listTransaksi by mutableStateOf(listOf<Transaksi>())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelper(this)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen(
                        transaksiList = listTransaksi,
                        onDelete = { id ->
                            dbHelper.deleteTransaksi(id)
                            refreshData()
                            Toast.makeText(this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun refreshData() {
        listTransaksi = dbHelper.getAllTransaksi()
    }
}

@Composable
fun MainScreen(
    transaksiList: List<Transaksi>,
    onDelete: (Int) -> Unit
) {
    val context = LocalContext.current
    val totalPengeluaran = transaksiList.sumOf { it.nominal }
    val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1976D2), shape = RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_dompet),
                    contentDescription = "Logo DompetKu",
                    modifier = Modifier
                        .size(56.dp)
                        .padding(end = 12.dp)
                )
                Column {
                    Text(
                        text = "DompetKu",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Total: ${formatRupiah.format(totalPengeluaran)}",
                        color = Color(0xFFE3F2FD),
                        fontSize = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val intent = Intent(context, FormActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("+ Tambah Pengeluaran")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Riwayat Pengeluaran",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (transaksiList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Belum ada data pengeluaran.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(transaksiList, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.nama,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = formatRupiah.format(item.nominal),
                                    color = Color(0xFFD32F2F),
                                    fontSize = 14.sp
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        val intent = Intent(context, FormActivity::class.java).apply {
                                            putExtra("EXTRA_ID", item.id)
                                            putExtra("EXTRA_NAMA", item.nama)
                                            putExtra("EXTRA_NOMINAL", item.nominal)
                                        }
                                        context.startActivity(intent)
                                    }
                                ) {
                                    Text("Edit")
                                }

                                Button(
                                    onClick = { onDelete(item.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                                ) {
                                    Text("Hapus")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
