package com.example.friiomain.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.friiomain.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.Toast
import com.example.friiomain.data.DataStoreManager


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFriendScreen(navController: NavController, currentUserEmail: String) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val userDao = db.userDao()
    val friendDao = db.friendDao()
    val coroutineScope = rememberCoroutineScope()
    val sessionManager = remember { SessionManager(context) }

    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var friends by remember { mutableStateOf(listOf<FriendEntity>()) }


    LaunchedEffect(currentUserEmail) {
        qrBitmap = QRCodeGenerator.generateQRCode(currentUserEmail)
        coroutineScope.launch(Dispatchers.IO) {
            val list = friendDao.getFriendsForUser(currentUserEmail)
            withContext(Dispatchers.Main) {
                friends = list
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Friends", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                qrBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "QR-код",
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("Email: $currentUserEmail", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            //Мои друзья
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Мои друзья", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))

                if (friends.isEmpty()) {
                    Text(
                        "У вас пока нет друзей",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                    ) {
                        items(friends) { friend ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Text(
                                    text = friend.friendEmail,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Выйти / Удалить
            Column {

                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                withContext(Dispatchers.IO) {
                                    val ds = DataStoreManager(context)
                                    ds.clearAll() // удаляем session/email/name
                                }
                                withContext(Dispatchers.Main) {
                                    navController.navigate("login") {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    }
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Ошибка при выходе: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Выйти из аккаунта")
                }


                Spacer(modifier = Modifier.height(12.dp))

                // Кнопка удаления аккаунта
                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                withContext(Dispatchers.IO) {
                                    val user = userDao.getUserByEmail(currentUserEmail) // используем getUserByEmail — тот же метод, что в других местах
                                    if (user != null) {
                                        userDao.delete(user)
                                    }

                                    val ds = DataStoreManager(context)
                                    ds.clearAll() // suspend
                                }

                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Аккаунт удален", Toast.LENGTH_LONG).show()
                                    navController.navigate("register") {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    }
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Ошибка при удалении: ${e.message}", Toast.LENGTH_LONG).show()
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
}



