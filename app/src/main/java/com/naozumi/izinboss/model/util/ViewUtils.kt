package com.naozumi.izinboss.model.util

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit

object ViewUtils {
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

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

    fun replaceFragment(
        activity: AppCompatActivity,
        containerId: Int,
        fragment: Fragment,
        fragmentTag: String,
    ) {
        val fragmentManager = activity.supportFragmentManager
        fragmentManager.commit {
            replace(containerId, fragment, fragmentTag)
        }
    }
    fun addFragment(
        activity: AppCompatActivity,
        containerId: Int,
        fragment: Fragment,
        fragmentTag: String,
    ) {
        val fragmentManager = activity.supportFragmentManager
        fragmentManager.commit {
            replace(containerId, fragment, fragmentTag)
        }
    }

    fun addFragmentWithParcelable(
        activity: AppCompatActivity,
        containerId: Int,
        fragment: Fragment,
        fragmentTag: String,
        parcelableData: Parcelable? = null // Add this parameter
    ) {
        val fragmentManager = activity.supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val args = Bundle()

        // Put the Parcelable data in the arguments bundle if it's not null
        parcelableData?.let { args.putParcelable("parcelableData", it) }

        fragment.arguments = args
        transaction.replace(containerId, fragment, fragmentTag)
        transaction.commit()
    }


}