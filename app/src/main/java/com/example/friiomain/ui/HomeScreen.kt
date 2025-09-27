package com.example.friiomain.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.friiomain.data.WeatherRepository
import com.example.friiomain.data.WeatherResponse
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.example.friiomain.ui.components.HomeTopBar
import com.example.friiomain.ui.components.ProfileDialog
import com.example.friiomain.utils.loadWeather
import com.example.friiomain.data.AppDatabase
import com.example.friiomain.data.UserEntity
import kotlinx.coroutines.withContext
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import java.net.URLEncoder
import com.example.friiomain.ui.profile.ProfileViewModel
import com.example.friiomain.ui.components.SettingsDialog
import com.example.friiomain.data.DataStoreManager




@Composable
fun HomeScreen(navController: NavController, email: String, name: String) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()




    // Исправление: создаем viewModel
    val viewModel: ProfileViewModel = hiltViewModel()
    val usernameFlow = viewModel.userUsername

    var weather by remember { mutableStateOf<WeatherResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val db = AppDatabase.getDatabase(context)
    val userDao = db.userDao()
    var currentUser by remember { mutableStateOf<UserEntity?>(null) }

    LaunchedEffect(email) {
        currentUser = withContext(Dispatchers.IO) {
            userDao.getUserByEmail(email)
        }

        // fallback если в базе пусто
        if (currentUser == null && name.isNotBlank()) {
            currentUser = UserEntity(
                email = email,
                name = name,
                password = "",
                username = "",
                preferences = ""
            )
        }

    }



    // Состояния для диалогов
    var showProfileDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Лаунчер для разрешений
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            coroutineScope.launch(Dispatchers.IO) {
                val result = loadWeather(context)
                weather = result
                isLoading = false
            }
        } else {
            Toast.makeText(context, "Нет доступа к геолокации", Toast.LENGTH_LONG).show()
            isLoading = false
        }
    }

    // Проверка разрешений
    LaunchedEffect(Unit) {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        when {
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                coroutineScope.launch(Dispatchers.IO) {
                    val result = loadWeather(context)
                    weather = result
                    isLoading = false
                }
            }

            else -> locationPermissionLauncher.launch(permission)
        }
    }

    // Диалог профиля
    if (showProfileDialog && currentUser != null) {
        val preferences = currentUser!!.preferences
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()

        ProfileDialog(
            name = currentUser!!.name,
            usernameFlow = usernameFlow,
            email = currentUser!!.email,
            preferences = preferences,
            avatarBase64 = currentUser!!.avatarBase64,
            onAvatarChange = { newBase64: String? ->
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        val updated = currentUser!!.copy(avatarBase64 = newBase64)
                        userDao.update(updated)
                    }

                    currentUser = withContext(Dispatchers.IO) {
                        userDao.getUserByEmail(currentUser!!.email)
                    }
                }
            },
            onDismiss = { showProfileDialog = false },
            onEditPreferences = {
                val currentPrefs = currentUser!!.preferences ?: ""

                val encoded = URLEncoder.encode(currentPrefs, "UTF-8")
                navController.navigate("preferences_edit/${currentUser!!.email}/$encoded")
            }
        )

    }

    // Диалог уведомлений
    if (showNotificationsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            confirmButton = {},
            title = { Text("Уведомления") },
            text = { Text("Пока пусто 🚀") }
        )
    }

    // Диалог настроек
    val dataStore = remember { DataStoreManager(context) }
    val currentUserEmail by dataStore.userEmail.collectAsState(initial = "")

    if (showSettingsDialog) {
        SettingsDialog(
            navController = navController,
            userDao = userDao,
            currentUserEmail = currentUserEmail?: "",
            onDismiss = { showSettingsDialog = false }
        )
    }



    // Остальная UI часть без изменений


    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        HomeTopBar(
            user = currentUser?.name ?: name,
            onProfileClick = { showProfileDialog = true },
            onNotificationsClick = { showNotificationsDialog = true },
            onSettingsClick = { showSettingsDialog = true } // ✅ используем правильную переменную
        )




        Spacer(modifier = Modifier.height(16.dp))

        // Погода
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Thermostat, contentDescription = "Температура")
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Place,
                                contentDescription = "Локация",
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Город (заглушка)", fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = weather?.weather?.get(0)?.description ?: "Нет данных",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "${weather?.main?.temp ?: "--"}°C",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Weather Matches
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Weather Matches", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    "Friends who also love this weather!",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(12.dp))

                repeat(2) { index ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color.Gray),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "AB",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Имя Друга ${index + 1}")
                            }
                            Button(onClick = { /* TODO: Invite */ }) {
                                Text("Invite")
                            }
                        }
                    }
                }
            }
        }

        // Friends
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = "Friends")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Friends (2)", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { navController.navigate("addFriend/$email") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text("+", color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(2) {
                        Card(
                            modifier = Modifier.size(80.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("AB", fontWeight = FontWeight.Bold)
                                Text("Ник", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }

        // Climate Impact
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🌱", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Your Climate Impact", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("12 km", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                        Text("km walked this week", fontSize = 12.sp, color = Color.Gray)
                    }
                    Column {
                        Text("3.5 kg", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                        Text("kg CO2 saved", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { navController.navigate("qrScanner/$email") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).padding(end = 4.dp)
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = "QR")
                Spacer(modifier = Modifier.width(4.dp))
                Text("QR Code")
            }

            Button(
                onClick = { /* TODO: Find Walks */ },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).padding(start = 4.dp)
            ) {
                Icon(Icons.Default.Place, contentDescription = "Find Walks")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Find Walks")
            }
        }
    }


    suspend fun loadWeather(context: Context): WeatherResponse? {
        return try {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                return null
            }

            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            val location: Location? = fusedLocationClient.lastLocation.await()

            if (location != null) {
                val repo = WeatherRepository("4731afa59235bbee6a194fc02cff4f8b")
                repo.getWeather(location.latitude, location.longitude)
            } else {
                null
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}



















