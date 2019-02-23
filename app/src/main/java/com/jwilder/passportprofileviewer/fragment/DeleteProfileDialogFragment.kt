package com.jwilder.passportprofileviewer.fragment

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders

import com.jwilder.passportprofileviewer.R
import com.jwilder.passportprofileviewer.viewmodel.ProfilesViewModel
import java.lang.Exception

class DeleteProfileDialogFragment : DialogFragment() {

    @Suppress("PropertyName")
    val TAG: String = "DeleteProfileDialogFragment"
    private lateinit var mViewModel: ProfilesViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mViewModel = activity?.run {
            ViewModelProviders.of(this).get(ProfilesViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.dialog_confirm_delete)
                .setPositiveButton(R.string.delete
                ) { dialog, id ->
                    if(mViewModel.getSelectedProfile().value != null)
                        mViewModel.deleteProfileDB(mViewModel.getSelectedProfile().value!!)
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.cancel
                ) { dialog, id ->
                    dialog.dismiss()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
