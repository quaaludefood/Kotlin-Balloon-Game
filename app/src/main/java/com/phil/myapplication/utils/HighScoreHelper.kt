package com.phil.myapplication.utils

import android.content.Context
import android.content.SharedPreferences

object HighScoreHelper {
    private const val PREFS_GLOBAL = "prefs_global"
    private const val PREF_TOP_SCORE = "pref_top_score"
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(
            PREFS_GLOBAL, Context.MODE_PRIVATE
        )
    }

    //  Setters and getters for global preferences
    fun isTopScore(context: Context, newScore: Int): Boolean {
        val topScore =
            getPreferences(context).getInt(PREF_TOP_SCORE, 0)
        return newScore > topScore
    }

    fun getTopScore(context: Context): Int {
        return getPreferences(context).getInt(PREF_TOP_SCORE, 0)
    }

    fun setTopScore(context: Context, score: Int) {
        val editor = getPreferences(context).edit()
        editor.putInt(PREF_TOP_SCORE, score)
        editor.apply()
    }
}