package com.example.friiomain.ui

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.friiomain.data.AppDatabase
import com.example.friiomain.utils.QRCodeGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AddFriendScreen(navController: NavController, currentUserEmail: String) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val userDao = db.userDao()
    val coroutineScope = rememberCoroutineScope()

    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // генерируем QR-код на основе email
    LaunchedEffect(currentUserEmail) {
        qrBitmap = QRCodeGenerator.generateQRCode(currentUserEmail)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Мой профиль", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            qrBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "QR-код",
                    modifier = Modifier
                        .size(200.dp)
                        .align(androidx.compose.ui.Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Email: $currentUserEmail")
        }

        Column {
            Button(
                onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        val user = userDao.findByEmail(currentUserEmail)
                        if (user != null) {
                            withContext(Dispatchers.Main) {
                                navController.navigate("login") {
                                    popUpTo("home/{email}/{name}") { inclusive = true }
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Выйти из аккаунта")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        val user = userDao.findByEmail(currentUserEmail)
                        if (user != null) {
                            userDao.delete(user)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Аккаунт удален", Toast.LENGTH_LONG).show()
                                navController.navigate("register") {
                                    popUpTo("home/{email}/{name}") { inclusive = true }
                                }
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Удалить аккаунт")
            }
        }
    }
}


