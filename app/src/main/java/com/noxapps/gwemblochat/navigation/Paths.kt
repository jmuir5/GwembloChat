package com.noxapps.gwemblochat.navigation

sealed class Paths(val Path:String) {
    object Home: Paths("Home")
    object Chat: Paths("Chat")
    object Login: Paths("Login")
}