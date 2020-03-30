package com.phil.myapplication.utils

class Constants {

    companion object{

        const val BASE_URL = "http://192.168.1.165:52477/api/"
        const val PASSWORD_RESET_URL: String = "https://open-api.xyz/password_reset/"

        const val NETWORK_TIMEOUT = 6000L
        const val TESTING_NETWORK_DELAY = 0L // fake network delay for testing
        const val TESTING_CACHE_DELAY = 0L // fake cache delay for testing

        //delay between launchers
        const val MIN_ANIMATION_DELAY = 500
        const val MAX_ANIMATION_DELAY = 1500
        const val MIN_ANIMATION_DURATION = 1000
        const val MAX_ANIMATION_DURATION = 8000
        const val NUMBER_OF_PINS = 5
        const val BALLOONS_PER_LEVEL = 3
    }
}