package com.example.friiomain

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.friiomain.data.DataStoreManager
import com.example.friiomain.ui.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataStoreManager = DataStoreManager(this)

        setContent {
            val navController = rememberNavController()

            // состояние для email и имени
            var userEmail by remember { mutableStateOf<String?>(null) }
            var userName by remember { mutableStateOf<String?>(null) }

            // загружаем данные из DataStore один раз при старте
            LaunchedEffect(Unit) {
                userEmail = dataStoreManager.userEmail.first()
                userName = dataStoreManager.userName.first()
            }

            NavHost(
                navController = navController,
                startDestination = if (userEmail == null) "login" else "home/$userEmail/$userName"
            ) {
                composable("register") {
                    RegisterScreen(navController = navController)
                }

                composable("login") {
                    LoginScreen(
                        navController = navController,
                        onLoginSuccess = { email, name ->
                            lifecycleScope.launch {
                                dataStoreManager.saveUserEmail(email)
                                dataStoreManager.saveUserName(name)
                            }
                            navController.navigate("home/$email/$name") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }

                composable(
                    "home/{email}/{user}",
                    arguments = listOf(
                        navArgument("email") { type = NavType.StringType },
                        navArgument("user") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email") ?: ""
                    val user = backStackEntry.arguments?.getString("user") ?: ""
                    HomeScreen(navController, email, user)
                }

                composable(
                    "friends/{email}",
                    arguments = listOf(navArgument("email") { type = NavType.StringType })
                ) { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email") ?: ""
                    FriendsScreen(navController, currentUserEmail = email)
                }

                composable(
                    "addFriend/{email}",
                    arguments = listOf(navArgument("email") { type = NavType.StringType })
                ) { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email") ?: ""
                    AddFriendScreen(navController, currentUserEmail = email)
                }

                composable("qrScanner") {
                    QrScannerScreen(navController) { result ->
                        // обработка результата QR
                    }
                }
            }
        }
    }
}





