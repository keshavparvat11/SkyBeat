package com.example.skybeat.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController){
    LaunchedEffect(Unit) {
        delay(1000)
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val i = 0
        if (currentUser != null) {

                navController.navigate("Home") {
                    popUpTo(0) { inclusive = true }
                }

        } else {

            navController.navigate("SignUp"){
                popUpTo(0) { inclusive = true }
            }
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "SkyBeat",
            fontWeight = FontWeight.SemiBold)
    }
}