package com.example.friiomain.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.friiomain.utils.bitmapToBase64
import com.example.friiomain.utils.base64ToBitmap
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.runtime.rememberCoroutineScope
import com.example.friiomain.data.AppDatabase





@Composable
fun AvatarSection(
    name: String,
    email: String,                        // 👈 нужен email, чтобы обновить юзера в БД
    avatarBase64: String?,                // сохранённая аватарка из базы
    onAvatarChange: (String?) -> Unit     // callback для сохранения в DataStore
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val userDao = AppDatabase.getDatabase(context).userDao()

    // Декодируем Base64 → Bitmap (если есть)
    val avatarBitmap = remember(avatarBase64) { base64ToBitmap(avatarBase64 ?: "") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        if (uri != null) {
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            val base64 = bitmapToBase64(bitmap)

            // сохраняем в DataStore
            onAvatarChange(base64)

            // сохраняем в Room
            scope.launch {
                withContext(Dispatchers.IO) {
                    userDao.updateAvatar(email, base64)
                }
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        // аватарка в круге
        Box(
            modifier = Modifier
                .size(85.dp)
                .clip(CircleShape)
                .background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            if (avatarBitmap != null) {
                Image(
                    bitmap = avatarBitmap.asImageBitmap(),
                    contentDescription = "Avatar",
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = name.take(2).uppercase(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { launcher.launch("image/*") },
                shape = RoundedCornerShape(11.dp)
            ) {
                Text("Change Avatar")
            }
            Button(
                onClick = {
                    // очищаем в DataStore
                    onAvatarChange(null)

                    // очищаем в Room
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            userDao.updateAvatar(email, null)
                        }
                    }
                },
                shape = RoundedCornerShape(11.dp),
                enabled = avatarBitmap != null
            ) {
                Text("Remove")
            }
        }
    }
}





