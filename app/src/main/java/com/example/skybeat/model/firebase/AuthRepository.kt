package com.example.skybeat.model.firebase
import com.google.firebase.auth.FirebaseAuth

class AuthRepository(private  var auth : FirebaseAuth) {
    fun signUp(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun login(email: String, password: String, onResult: (Boolean, String?, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null, auth.currentUser?.uid)
                } else {
                    onResult(false, task.exception?.message, null)
                }
            }
    }
}