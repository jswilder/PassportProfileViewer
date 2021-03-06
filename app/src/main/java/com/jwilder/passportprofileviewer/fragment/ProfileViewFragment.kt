package com.jwilder.passportprofileviewer.fragment

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController

import com.jwilder.passportprofileviewer.R
import com.jwilder.passportprofileviewer.classes.FirestoreProfile
import com.jwilder.passportprofileviewer.viewmodel.ProfilesViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_profile.*
import kotlinx.android.synthetic.main.profile_view_fragment.*
import java.lang.Exception

/*
    Fragment that shows the currently selected profile
 */
class ProfileViewFragment : Fragment() {

    private val TAG = "ProfileViewFragment"
    private lateinit var mViewModel: ProfilesViewModel
    private lateinit var mDeleteDialog: DeleteProfileDialogFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_view_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel = activity?.run {
            ViewModelProviders.of(this).get(ProfilesViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        mViewModel.getSelectedProfile().observe( this, Observer { profile ->
            if(profile == null) findNavController().navigateUp()
            else {
                updateUI(profile)
            }
        })

        mDeleteDialog = DeleteProfileDialogFragment()

        btn_save_changes.setOnClickListener {
            mViewModel.submitProfileChangeDB( edit_hobbies.text.toString() )
        }

        btn_delete_profile.setOnClickListener {
            mDeleteDialog.show(fragmentManager!!,mDeleteDialog.TAG)
        }
    }

    private fun updateUI(profile: FirestoreProfile) {
        text_name.text = profile.name
        text_age.text = profile.age.toString()
        when(profile.gender) {
            FirestoreProfile.GENDER.MALE -> {
                text_gender.text = getString(R.string.male)
                view_background.setBackgroundColor(getColor(activity!!,R.color.maleBlue))
                btn_save_changes.backgroundTintList = ColorStateList.valueOf(getColor(activity!!,R.color.femalePink))
            }
            FirestoreProfile.GENDER.FEMALE -> {
                text_gender.text = getString(R.string.female)
                view_background.setBackgroundColor(getColor(activity!!,R.color.femalePink))
                btn_save_changes.backgroundTintList = ColorStateList.valueOf(getColor(activity!!,R.color.maleBlue))
            }
        }
        edit_hobbies.setText(profile.hobbies)
        Picasso.get()
            .load(profile.image)
            .placeholder(R.drawable.passport)
            .into(image_profile_picture)
    }
}
