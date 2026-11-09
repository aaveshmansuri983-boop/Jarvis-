package com.megwil.ai.ui.dashboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class OwnerDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFF0B0F19)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(text = "Owner Dashboard", color = Color(0xFF00E5FF), fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Owner: aaveshmansuri983@gmail.com", color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Status: Verified Owner", color = Color.Green)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = "Groq API Key Configured",
                        onValueChange = {},
                        label = { Text("API Key Management") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
