package com.kgeorgiev.notes.presentation.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.kgeorgiev.notes.R

private const val PARAM_POSITIVE_BUTTON_TEXT = "positive_btn_text"
private const val PARAM_NEGATIVE_BUTTON_TEXT = "negative_btn_text"
private const val PARAM_TITLE = "param_title"
private const val PARAM_DESCRIPTION = "param_description"

class MessageDialogFragment(private val clickListener: DialogClickListener) : DialogFragment() {

    companion object {
        fun newInstance(
            titleResId: Int,
            descriptionResId: Int = 0,
            positiveButtonResId: Int = R.string.button_ok,
            negativeButtonResId: Int = R.string.button_cancel,
            dialogClickListener: DialogClickListener
        ): MessageDialogFragment {
            val messageDialogFragment =
                MessageDialogFragment(dialogClickListener)
            val args = Bundle()
            args.putInt(PARAM_TITLE, titleResId)
            args.putInt(PARAM_DESCRIPTION, descriptionResId)
            args.putInt(PARAM_POSITIVE_BUTTON_TEXT, positiveButtonResId)
            args.putInt(PARAM_NEGATIVE_BUTTON_TEXT, negativeButtonResId)
            messageDialogFragment.arguments = args

            return messageDialogFragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val arguments = arguments

        val builder = AlertDialog.Builder(activity)
        builder.setTitle(arguments?.getInt(PARAM_TITLE)!!)

        if (arguments.getInt(PARAM_DESCRIPTION) != 0) {
            builder.setMessage(arguments.getInt(PARAM_DESCRIPTION))
        }

        builder.setPositiveButton(
            arguments.getInt(PARAM_POSITIVE_BUTTON_TEXT)
        ) { dialog, which ->
            clickListener.onPositiveButtonClicked()
            // click
        }

        builder.setNegativeButton(arguments.getInt(PARAM_NEGATIVE_BUTTON_TEXT)) { dialog, which ->
            // click negative button
            clickListener.onNegativeButtonClicked()
        }


        val dialog = builder.create()
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation

        return dialog
    }

    interface DialogClickListener {
        fun onPositiveButtonClicked()
        fun onNegativeButtonClicked()
    }
}
