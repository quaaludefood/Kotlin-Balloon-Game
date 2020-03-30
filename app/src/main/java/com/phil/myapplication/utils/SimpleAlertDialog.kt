package com.phil.myapplication.utils

import android.R
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class SimpleAlertDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val args = arguments ?: throw AssertionError()
        val title = args.getString(TITLE_KEY)
        val prompt = args.getString(MESSAGE_KEY)
        val builder =
            AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(prompt)
                .setCancelable(false)
        builder.setPositiveButton(R.string.ok, null)
        return builder.create()
    }

    companion object {
        private const val TITLE_KEY = "title_key"
        private const val MESSAGE_KEY = "message_key"
        fun newInstance(title: String?, message: String?): SimpleAlertDialog {
            val args = Bundle()
            args.putString(TITLE_KEY, title)
            args.putString(MESSAGE_KEY, message)
            val fragment = SimpleAlertDialog()
            fragment.arguments = args
            return fragment
        }
    }
}