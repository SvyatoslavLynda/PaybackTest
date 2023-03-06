package com.svdroid.paybacktest.utils

sealed class Destination(val route: String) {
    object Main : Destination("main?searchQuery={searchQuery}") {
        fun createRoute(searchQuery: String) = "main?searchQuery=$searchQuery"
    }

    object Search : Destination("search?searchQuery={searchQuery}") {
        fun createRoute(searchQuery: String) = "search?searchQuery=$searchQuery"
    }

    object Details : Destination("details?id={id}") {
        fun createRoute(hitId: Int) = "details?id=$hitId"
    }

    object ConfirmNavigationToDetails : Destination("confirm_navigation_to_details?id={id}") {
        fun createRoute(hitId: Int?) = "confirm_navigation_to_details?id=$hitId"
    }
}