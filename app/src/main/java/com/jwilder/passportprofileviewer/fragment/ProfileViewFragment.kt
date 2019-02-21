package com.jwilder.passportprofileviewer.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.jwilder.passportprofileviewer.R
import com.jwilder.passportprofileviewer.classes.Profile
import com.jwilder.passportprofileviewer.viewmodel.ProfilesViewModel
import kotlinx.android.synthetic.main.profile_view_fragment.*
import java.lang.Exception

class ProfileViewFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileViewFragment()
    }

    private val TAG = "PROFILE_VIEW_FRAG"
    private lateinit var mViewModel: ProfilesViewModel
    private lateinit var mSelectedProfile: Profile

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_view_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel = activity?.run {
            ViewModelProviders.of(this).get(ProfilesViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        mViewModel.getSelectedProfile().observe( this, Observer { profile ->
            profile?.let {
                mSelectedProfile = profile
                updateUI(mSelectedProfile)
            }
        })

        button_save.setOnClickListener { mViewModel.submitChangesToDatabase( edit_hobbies.text.toString() ) }
        button_delete.setOnClickListener { mViewModel.deleteProfileFromDatabase(mSelectedProfile) }
    }

    private fun updateUI(profile: Profile) {
        text_name.text = profile.name
        text_age.text = profile.age.toString()
        val color = when(profile.gender) {
            Profile.GENDER.FEMALE -> ContextCompat.getColor(activity!!,R.color.femalePink)
            Profile.GENDER.MALE -> ContextCompat.getColor(activity!!,R.color.maleBlue)
        }
        edit_hobbies.setText(profile.hobbies)
        view_background.setBackgroundColor(color)
    }
}
