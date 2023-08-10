package com.naozumi.izinboss.util

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

object ViewUtils {
    fun setupFullScreen(activity: AppCompatActivity) {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        activity.supportActionBar?.hide()
    }

    fun moveActivity(context: Context, target: Class<*>, data: String? = null, optionsBundle: Bundle? = null) {
        val moveIntent = Intent(context, target)
        data.let { moveIntent.putExtra("data", data) }
        if (optionsBundle != null) {
            context.startActivity(moveIntent, optionsBundle)
        } else {
            context.startActivity(moveIntent)
        }
    }

    fun moveActivityNoHistory(context: Context, target: Class<*>, data: String? = null, optionsBundle: Bundle? = null) {
        val moveIntent = Intent(context, target)
        moveIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        data.let { moveIntent.putExtra("data", data) }
        if (optionsBundle != null) {
            context.startActivity(moveIntent, optionsBundle)
        } else {
            context.startActivity(moveIntent)
        }
    }
}