package com.example.friiomain.ui

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.friiomain.data.AppDatabase
import com.example.friiomain.data.FriendEntity
import com.example.friiomain.utils.QRCodeGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ProfileScreen(navController: NavController, currentUserEmail: String) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val friendDao = db.friendDao()
    val scope = rememberCoroutineScope()

    // Переменная для хранения QR-кода
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Мой профиль", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {

                qrBitmap = QRCodeGenerator.generateQRCode(currentUserEmail)
            }) {
                Text("Показать мой QR")
            }


            qrBitmap?.let { bitmap ->
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "QR Code",
                    modifier = Modifier.size(200.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                scope.launch {
                    val fakeFriendEmail = "friend@example.com" // пока тестовый вариант
                    val friend = FriendEntity(userEmail = currentUserEmail, friendEmail = fakeFriendEmail)
                    friendDao.addFriend(friend)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Друг $fakeFriendEmail добавлен!", Toast.LENGTH_LONG).show()
                    }
                }
            }) {
                Text("Сканировать QR")
            }
        }
    }
}


