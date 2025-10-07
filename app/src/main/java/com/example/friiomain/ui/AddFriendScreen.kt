package com.example.friiomain.ui

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonAdd
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
import com.example.friiomain.data.FriendRequestEntity
import com.example.friiomain.ui.components.FriendRequestsDialog
import com.example.friiomain.utils.QRCodeGenerator
import com.example.friiomain.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.material.icons.filled.Person
import com.example.friiomain.data.UserEntity
import androidx.compose.material.icons.filled.Person
import com.example.friiomain.utils.base64ToBitmap
import androidx.compose.material.icons.filled.Delete





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFriendScreen(navController: NavController, currentUserEmail: String) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val friendDao = db.friendDao()
    val coroutineScope = rememberCoroutineScope()
    val sessionManager = remember { SessionManager(context) }

    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var friends by remember { mutableStateOf(listOf<FriendEntity>()) }
    var requests by remember { mutableStateOf(listOf<FriendRequestEntity>()) }
    var showRequests by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var foundUser by remember { mutableStateOf<UserEntity?>(null) }
    val userDao = db.userDao()


    // Загрузка QR + список друзей + входящие заявки
    LaunchedEffect(currentUserEmail) {
        qrBitmap = QRCodeGenerator.generateQRCode(currentUserEmail)
        coroutineScope.launch(Dispatchers.IO) {
            val list = friendDao.getFriendsForUser(currentUserEmail)
            val incoming = friendDao.getIncomingRequests(currentUserEmail)
            withContext(Dispatchers.Main) {
                friends = list
                requests = incoming
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
                },
                actions = {
                    IconButton(onClick = { showRequests = true }) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Заявки в друзья")
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
            verticalArrangement = Arrangement.Top
        ) {
            // QR-код
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
                Text(
                    "Покажите QR-код другу, чтобы добавить вас",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))


            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Введите username") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        val query = searchQuery.trim().removePrefix("@").lowercase()
                        val user: UserEntity? = withContext(Dispatchers.IO) {
                            userDao.getUserByUsername(searchQuery.trim())
                        }

                        if (user != null) {
                            foundUser = user
                        } else {
                            foundUser = null
                            Toast.makeText(context, "Пользователь не найден", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Найти") }


            foundUser?.let { user ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Аватар и имя
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val avatarBitmap = base64ToBitmap(user.avatarBase64 ?: "")
                            if (avatarBitmap != null) {
                                Image(
                                    bitmap = avatarBitmap.asImageBitmap(),
                                    contentDescription = "Avatar",
                                    modifier = Modifier.size(40.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Column {
                                Text(
                                    user.username ?: "Unknown",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(user.email, style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        // Кнопка "Добавить"
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    // Получаем пользователя в IO
                                    val cleanQuery =
                                        searchQuery.trim().removePrefix("@").lowercase()
                                    val user: UserEntity? = withContext(Dispatchers.IO) {
                                        userDao.getUserByUsernameOrEmail("%$cleanQuery%")
                                    }

                                    if (user != null) {
                                        // Вставляем заявку в IO
                                        withContext(Dispatchers.IO) {
                                            val currentUser = withContext(Dispatchers.IO) {
                                                userDao.getUserByEmail(currentUserEmail)
                                            }
                                            val request = FriendRequestEntity(
                                                fromEmail = currentUserEmail,
                                                toEmail = user.email,
                                                status = "pending",
                                                username = currentUser?.username ?: "Unknown",
                                                avatarBase64 = currentUser?.avatarBase64
                                            )
                                            db.friendRequestDao().insert(request)
                                        }

                                        Toast.makeText(
                                            context,
                                            "Заявка отправлена!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        foundUser = null
                                        searchQuery = ""
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Пользователь не найден",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }


                            }
                        ) {
                            Text("Добавить")
                        }

                    }
                }
            }


            // Список друзей
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Мои друзья 👥", style = MaterialTheme.typography.titleSmall)
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
                            .heightIn(max = 300.dp)
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
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Левая часть: аватар + имя + почта
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        var friendData by remember { mutableStateOf<UserEntity?>(null) }

                                        // Загружаем данные друга
                                        LaunchedEffect(friend.friendEmail) {
                                            withContext(Dispatchers.IO) {
                                                val user = userDao.getUserByEmail(friend.friendEmail)
                                                withContext(Dispatchers.Main) {
                                                    friendData = user
                                                }
                                            }
                                        }

                                        // Аватар
                                        val avatarBitmap = friendData?.avatarBase64?.let { base64ToBitmap(it) }
                                        if (avatarBitmap != null) {
                                            Image(
                                                bitmap = avatarBitmap.asImageBitmap(),
                                                contentDescription = "Avatar",
                                                modifier = Modifier.size(40.dp)
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = null,
                                                modifier = Modifier.size(40.dp)
                                            )
                                        }

                                        Spacer(Modifier.width(12.dp))

                                        Column {
                                            Text(friendData?.username ?: friend.friendEmail, style = MaterialTheme.typography.bodyMedium)
                                            Text(friend.friendEmail, style = MaterialTheme.typography.bodySmall)
                                        }
                                    }

                                    // Правая часть: кнопка "Удалить"
                                    IconButton(
                                        onClick = {
                                            coroutineScope.launch(Dispatchers.IO) {
                                                // Удаляем друга с обеих сторон
                                                friendDao.deleteFriend(currentUserEmail, friend.friendEmail)
                                                friendDao.deleteFriend(friend.friendEmail, currentUserEmail)

                                                // Обновляем список
                                                val updatedFriends = friendDao.getFriendsForUser(currentUserEmail)
                                                withContext(Dispatchers.Main) {
                                                    friends = updatedFriends
                                                    Toast.makeText(context, "Друг удалён", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Удалить",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }

        // Диалог заявок
        if (showRequests) {
            FriendRequestsDialog(
                requests = requests,
                onAccept = { req ->
                    coroutineScope.launch(Dispatchers.IO) {
                        try {

                            friendDao.acceptRequest(req)


                            val updatedFriends = friendDao.getFriendsForUser(currentUserEmail)


                            val updatedRequests = friendDao.getIncomingRequests(currentUserEmail)

                            withContext(Dispatchers.Main) {
                                friends = updatedFriends
                                requests = updatedRequests
                                showRequests = true
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Ошибка: ${e.message}", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                    }

                },
                onDecline = { req ->
                    coroutineScope.launch(Dispatchers.IO) {
                        try {
                            friendDao.deleteRequest(req.id)
                            val updatedRequests = friendDao.getIncomingRequests(currentUserEmail)
                            withContext(Dispatchers.Main) {
                                requests = updatedRequests
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Ошибка: ${e.message}", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                    }
                },
                onDismiss = { showRequests = false }
            )
        }
    }
}




