package com.example.friiomain.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.friiomain.data.UserDao
import com.example.friiomain.data.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userDao: UserDao
) : ViewModel() {

    // текущий авторизованный пользователь
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser

    // сообщение об ошибке
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /**
     * Попытка войти в аккаунт
     */
    fun login(email: String, password: String, onSuccess: (UserEntity) -> Unit) {
        viewModelScope.launch {
            val user = userDao.getUserByEmail(email)
            if (user != null && user.password == password) {
                _currentUser.value = user
                onSuccess(user) // ✅ теперь передаём user
            } else {
                _error.value = "Неверный email или пароль"
            }
        }
    }

    /**
     * Выход из аккаунта
     */
    fun logout() {
        _currentUser.value = null
    }

    /**
     * Обновление пароля для текущего пользователя
     */
    fun updatePassword(newPassword: String) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            val updatedUser = user.copy(password = newPassword)
            userDao.update(updatedUser)
            _currentUser.value = updatedUser
        }
    }

    /**
     * Обновление предпочтений
     */
    fun updatePreferences(preferences: List<String>) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            val updatedUser = user.copy(preferences = preferences.joinToString(","))
            userDao.update(updatedUser)
            _currentUser.value = updatedUser
        }
    }
}