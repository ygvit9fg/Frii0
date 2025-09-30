package com.example.friiomain.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.friiomain.ui.components.ProfileDialog
import androidx.compose.runtime.getValue
import androidx.navigation.NavController





@Composable
fun ProfileDialogScreen(
    navController: NavController,
    onDismiss: () -> Unit
) {
    val viewModel: ProfileViewModel = hiltViewModel()

    ProfileDialog(
        viewModel = viewModel,
        avatarBase64 = null,
        onAvatarChange = {},
        onDismiss = onDismiss,
        onEditPreferences = {
            // навигация на экран настроек
            val email = viewModel.userEmail.value
            val prefs = viewModel.userPreferences.value.joinToString(",")
            navController.navigate("preferences_edit/$email/$prefs")
        }
    )
}




