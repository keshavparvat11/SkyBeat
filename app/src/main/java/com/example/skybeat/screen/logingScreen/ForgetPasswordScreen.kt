package com.example.taskdesk.screen.logingScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgetPasswordScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    var loading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TaskDesk") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF00838F))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Heading
            Text(
                text = "Reset Password",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00838F),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Subtitle
            Text(
                text = "Enter your email and we’ll send you a reset link",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 32.dp),
                textAlign = TextAlign.Center
            )

            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Reset Button
            Button(
                onClick = {
                    if (email.isEmpty()) {
                        message = "Please enter your email"
                    } else {
                        loading = true
                        auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener { task ->
                                loading = false
                                message = if (task.isSuccessful) {
                                    "Password reset email sent!"
                                } else {
                                    task.exception?.message ?: "Error sending reset email"
                                }
                            }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Reset Password", fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Show message (success or error)
            message?.let {
                Text(
                    text = it,
                    color = if (it.contains("sent")) Color(0xFF2E7D32) else Color.Red,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Back to login
            Row {
                Text(text = "Remember your password? ")
                Text(
                    text = "Login",
                    color = Color(0xFF00838F),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { navController.navigate("Login") }
                )
            }
        }
    }
}
