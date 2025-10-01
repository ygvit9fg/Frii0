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
fun HomeScreen(
    navController: NavController,
    email: String,
    name: String,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var weather by remember { mutableStateOf<WeatherResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Проверка email — если пусто, показываем загрузку
    if (email.isBlank()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        // --- UI вынесен в отдельную функцию ---
        HomeScreenContent(
            navController = navController,
            email = email,
            name = name,
            viewModel = viewModel
        )
    }
}



suspend fun loadWeather(context: Context): WeatherResponse? {
    return try {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) return null

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val location: Location? = fusedLocationClient.lastLocation.await()

        if (location != null) {
            val repo = WeatherRepository("4731afa59235bbee6a194fc02cff4f8b")
            repo.getWeather(location.latitude, location.longitude)
        } else null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}





























