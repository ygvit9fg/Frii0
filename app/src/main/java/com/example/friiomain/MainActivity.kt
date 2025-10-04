package com.example.friiomain

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.friiomain.ui.*
import com.example.friiomain.utils.SessionManager
import androidx.compose.runtime.*
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.NavType
import androidx.navigation.navArgument


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(this)

        setContent {
            val navController = rememberNavController()

            // Определяем с чего начинать
            val userEmail = sessionManager.getUserEmail()
            val userName = sessionManager.getUserName()

            val startDestination = if (!userEmail.isNullOrEmpty() && !userName.isNullOrEmpty()) {
                "home/$userEmail/$userName"
            } else {
                "login"
            }



            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                // Экран логина
                composable("login") {
                    LoginScreen(navController) { email, name ->
                        sessionManager.saveUser(email, name)
                        navController.navigate("home/$email/$name") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }

                // Register
                composable("register") {
                    RegisterScreen(navController)
                }

                // Username
                composable("username?name={name}&email={email}&password={password}") { backStackEntry ->
                    val name = backStackEntry.arguments?.getString("name") ?: ""
                    val email = backStackEntry.arguments?.getString("email") ?: ""
                    val password = backStackEntry.arguments?.getString("password") ?: ""
                    UsernameScreen(navController, name, email, password)
                }

                // Preferences (регистрация)
                composable(
                    route = "preferences/{email}/{password}/{name}/{username}",
                ) { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email") ?: ""
                    val password = backStackEntry.arguments?.getString("password") ?: ""
                    val name = backStackEntry.arguments?.getString("name") ?: ""
                    val username = backStackEntry.arguments?.getString("username") ?: ""

                    WeatherPreferencesScreen(
                        navController = navController,
                        name = name,
                        email = email,
                        password = password,
                        username = username,
                        isEditMode = false
                    )
                }

                // Preferences (редактирование из профиля)
                composable(
                    route = "preferences_edit/{email}/{currentPreferences}",
                    arguments = listOf(
                        navArgument("email") { type = NavType.StringType },
                        navArgument("currentPreferences") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email") ?: ""
                    val currentPreferences =
                        backStackEntry.arguments?.getString("currentPreferences") ?: ""

                    WeatherPreferencesScreen(
                        navController = navController,
                        name = "",
                        email = email,
                        password = "",
                        username = "",
                        currentPreferences = currentPreferences,
                        isEditMode = true
                    )
                }


                // Home
                composable("home/{email}/{name}") { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email") ?: ""
                    val name = backStackEntry.arguments?.getString("name") ?: ""
                    HomeScreen(
                        navController = navController,
                        email = email,
                        name = name,
                        onLogout = {
                            sessionManager.clear() // очищаем данные
                            navController.navigate("login") {
                                popUpTo("home/{email}/{name}") { inclusive = true }
                            }
                        }
                    )
                }




                // Add Friend
                composable("addFriend/{email}") { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email") ?: ""
                    AddFriendScreen(navController, email)
                }

                // Friends
                composable("friends/{email}") { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email") ?: ""
                    FriendsScreen(navController, email)
                }

                // QR Scanner
                composable("qrScanner/{email}") { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email") ?: ""
                    QrScannerScreen(navController) { qrResult ->
                        println("QR: $qrResult, Email: $email")
                    }
                }
            }
        }
    }
}







