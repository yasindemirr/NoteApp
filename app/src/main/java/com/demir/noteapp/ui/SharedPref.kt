package com.demir.noteapp.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.Lifecycle.State

class SharedPref(context:Context) {
    internal var sharedPreferences:SharedPreferences
    init {
        sharedPreferences=context.getSharedPreferences("fillname",Context.MODE_PRIVATE)

    }
    fun setNightModeState(state:Boolean?){
        val editor=sharedPreferences.edit()
        editor.putBoolean("Night Mode",state!!)
        editor.commit()

    }
    fun setLayoutModeState(state:Boolean?){
        val editor=sharedPreferences.edit()
        editor.putBoolean("Linear",state!!)
        editor.commit()

    }
    fun loadNightModeState():Boolean?{
        return sharedPreferences.getBoolean("Night Mode",false)

    }
    fun loadLayoutModeState():Boolean?{
        return sharedPreferences.getBoolean("Linear",false)

    }
}