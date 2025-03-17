package com.noxapps.gwemblochat.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController

@Composable
fun NavMain(navHostController: NavHostController){
    NavHost(navController = navHostController, startDestination = Paths.Home.Path) {
        composable(Paths.Home.Path) {
            HomePage(
                auth = auth,
                currentUser = currentUser.value,
                db = db,
                navController = navController
            )
        }
    }
}