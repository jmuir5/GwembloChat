package com.noxapps.gwemblochat.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.noxapps.gwemblochat.navigation.Paths
import com.noxapps.gwemblochat.data.User
import com.noxapps.gwemblochat.data.AppDatabase
import com.noxapps.gwemblochat.data.FirebaseDBInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class LoginViewModel(
    val auth: FirebaseAuth,
    val db:AppDatabase,
    val navController: NavController
):ViewModel() {
    fun login(
        email:String,
        password:String,
        enableState: MutableState<Boolean>,
        loggedUser: MutableState<User>,
        context: Context,
        coroutineScope: CoroutineScope
    ){
        enableState.value = false
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(context as Activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Login", "signInWithEmail:success")
                    val user = auth.currentUser

                    Log.d("login debug", "loading data")

                    coroutineScope.launch {
                        try {
                            user?.let {
                                val loadedUser = db.userDao().getOneById(user.uid)
                                Log.d("login debug", "loaded user $loadedUser")
                                Log.d("login debug", "loaded user ${loadedUser.userId}")
                                if ( loadedUser.userId != "") {
                                    MainScope().launch {
                                        loggedUser.value = loadedUser
                                        Log.d("login debug", "load successfull, going to home")

                                        navController.navigate(Paths.Home.Path) {
                                            popUpTo(Paths.Home.Path) {
                                                inclusive = true
                                            }
                                        }
                                    }
                                } else {
                                    throw Exception("load failed")
                                }

                            } ?: throw Exception("load failed")
                        } catch (e:Exception){
                            Log.d("login debug", "load failed, pulling data")
                            FirebaseDBInteractor
                                .getUserByEmail(
                                    email = email,
                                    onFail = {
                                        Toast.makeText(context, "Failed to pull user from database", Toast.LENGTH_SHORT)
                                            .show()
                                        auth.signOut()
                                        enableState.value = true

                                    }
                                ){ _, pulledUser->
                                    Log.d("login debug", "downloadedUser: $pulledUser, inserting")
                                    coroutineScope.launch {
                                        db.userDao().insert(pulledUser)
                                        MainScope().launch {
                                            loggedUser.value = pulledUser
                                            Log.d("login debug", "inserted, going home")

                                            navController.navigate(Paths.Home.Path){
                                                popUpTo(Paths.Home.Path) {
                                                    inclusive=true
                                                }
                                            }
                                        }
                                    }
                                }

                        }
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    when (task.exception) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            Log.e("Login", "Invalid credentials: ${task.exception?.message}")
                            Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT)
                                .show()
                        }
                        is FirebaseAuthInvalidUserException -> {
                            Log.e("Login", "Invalid user: ${task.exception?.message}")
                            Toast.makeText(context, "User account not found", Toast.LENGTH_SHORT)
                                .show()
                        }
                        else ->{
                            Log.e("Login", "Login failed: ${task.exception?.message}", task.exception)
                            Toast.makeText(context, "Login failed. Please try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    enableState.value = true // Ensure button is re-enabled



                    /*Log.w("Login", "signInWithEmail:failure", task.exception)

                    Toast.makeText(
                        context,
                        "Authentication failed: ${task.exception}",
                        Toast.LENGTH_SHORT,
                    ).show()
                    enableState.value = true
                    //updateUI(null)

                     */
                }
            }
    }

    fun register(
        userName:String,
        email:String,
        password:String,
        enableState: MutableState<Boolean>,
        loggedUser: MutableState<User>,
        context: Context,
        coroutineScope: CoroutineScope
    ){
        enableState.value = false
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(context as Activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("register", "createUserWithEmail:success")
                    val user = auth.currentUser
                    user?.let {
                        val newUser =  User(it.uid, email, userName, "")
                        coroutineScope.launch{
                            db.userDao().insert(newUser)
                            FirebaseDBInteractor.upsertUser(newUser)

                            MainScope().launch {
                                loggedUser.value = newUser
                                navController.navigate(Paths.Home.Path) {
                                    popUpTo(Paths.Home.Path) {
                                        inclusive = true
                                    }
                                }
                            }
                        }

                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("register", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        context,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    enableState.value = true
                }
            }

    }
}