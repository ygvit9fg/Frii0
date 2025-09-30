package com.example.friiomain.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.friiomain.ui.components.ProfileDialog
import androidx.compose.runtime.getValue




@Composable
fun ProfileDialogScreen(onDismiss: () -> Unit) {
    val viewModel: ProfileViewModel = hiltViewModel()

    val name by viewModel.userName.collectAsState()
    val username by viewModel.userUsername.collectAsState()
    val email by viewModel.userEmail.collectAsState()
    val preferences by viewModel.userPreferences.collectAsState()

    // -> ВАЖНО: здесь мы передаём заглушки для avatarBase64 и onAvatarChange,
    // чтобы код компилировался. Реальную логику сохранения/обновления делай в HomeScreen
    ProfileDialog(
        name = name,
        usernameFlow = viewModel.userUsername,
        email = email,
        preferences = preferences,
        avatarBase64 = null,    // заглушка — HomeScreen должен передать реальное значение
        onAvatarChange = {},    // заглушка — HomeScreen должен обработать сохранение
        onDismiss = onDismiss
    )
}



