package com.jwilder.passportprofileviewer.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.jwilder.passportprofileviewer.R
import com.jwilder.passportprofileviewer.classes.Profile
import com.jwilder.passportprofileviewer.viewmodels.ProfilesListViewModel
import kotlinx.android.synthetic.main.profile_view_fragment.*
import java.lang.Exception

class ProfileViewFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileViewFragment()
    }

    private lateinit var viewModel: ProfilesListViewModel
    private lateinit var currentProfile: Profile
    private val TAG = "PROFILE_VIEW_FRAG"

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

        viewModel = activity?.run {
            ViewModelProviders.of(this).get(ProfilesListViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        viewModel.mSelectedProfile.observe( this, Observer { profile ->
            profile?.let {
                currentProfile = profile
                updateUI(currentProfile)
            }
        })
    }

    private fun updateUI(profile: Profile) {
        text_name.text = profile.name
        text_age.text = profile.age.toString()
        val color = when(profile.gender) {
            Profile.GENDER.FEMALE -> ContextCompat.getColor(activity!!,R.color.femalePink)
            Profile.GENDER.MALE -> ContextCompat.getColor(activity!!,R.color.maleBlue)
        }
        view_background.setBackgroundColor(color)
        Log.w(TAG,"$color : ${profile.gender}")
    }
}
