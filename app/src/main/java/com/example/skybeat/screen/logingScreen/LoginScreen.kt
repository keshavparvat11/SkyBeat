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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.skybeat.model.firebase.AuthRepository
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    val authRepo = remember { AuthRepository(FirebaseAuth.getInstance()) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SkyBeat") },
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
                text = "Welcome Back",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00838F),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Subtitle
            Text(
                text = "Login to continue using SkyBeat",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 32.dp)
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

            Spacer(modifier = Modifier.height(16.dp))

            // Password field
            OutlinedTextField(
                value = pass,
                onValueChange = { pass = it },
                label = { Text("Password") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation()
            )
            Text(
                text = "Forget Password",
                color = Color(0xFF00838F),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().padding(top = 3.dp)
                    .align(alignment = Alignment.Start)
                    .clickable { navController.navigate("ForgetPass") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Login Button
            Button(
                onClick = {
                    if (email.isEmpty()) error = "Enter your email"
                    else if (pass.isEmpty()) error = "Enter your password"
                    else {
                        loading = true
                        authRepo.login(email, pass) { success, msg, userId ->
                            loading = false
                            if(success) {
                                if (userId == "uksZ7nd3xJTm0s4NpwxngY68yZG2") {
                                    navController.navigate("Admin") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                } else {
                                    navController.navigate("Home") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            }
                            else error = msg
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
                    Text("Login", fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Error message
            error?.let {
                Text(text = it, color = Color.Red, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Don’t have an account?
            Row {
                Text(text = "Don't have an account? ")
                Text(
                    text = "Sign Up",
                    color = Color(0xFF00838F),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { navController.navigate("SignUp") }
                )
            }
        }
    }
}
