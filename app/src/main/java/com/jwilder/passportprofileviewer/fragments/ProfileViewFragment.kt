package com.jwilder.passportprofileviewer.fragments

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController

import com.jwilder.passportprofileviewer.R
import com.jwilder.passportprofileviewer.classes.Profile
import com.jwilder.passportprofileviewer.viewmodels.ProfilesListViewModel
import kotlinx.android.synthetic.main.profile_view_fragment.*
import java.lang.Exception

class ProfileViewFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileViewFragment()
    }

    private val TAG = "PROFILE_VIEW_FRAG"
    private lateinit var viewModel: ProfilesListViewModel
    private lateinit var currentProfile: Profile

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

        viewModel.getSelectedProfile().observe( this, Observer { profile ->
            profile?.let {
                currentProfile = profile
                updateUI(currentProfile)
            }
        })

        button_save.setOnClickListener { viewModel.submitChangesToDatabase( edit_hobbies.text.toString() ) }
        button_delete.setOnClickListener { viewModel.deleteProfileFromDatabase(currentProfile) }

        /*
        Observe the save/delete buttons
         */
        viewModel.getToastMessage().observe(this, Observer { result ->
            if(result != null && viewModel.getAttemptedAction() != ProfilesListViewModel.Actions.NONE) {
                val toast = Toast.makeText(activity,result,Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER,0,0)
                toast.show()
//                when(viewModel.mActionAttempted) {
//                    ProfilesListViewModel.Actions.NONE -> {}
//                    ProfilesListViewModel.Actions.CREATE -> {}
//                    ProfilesListViewModel.Actions.DELETE -> {
                        // TODO Navigate up on success
//                        viewModel.clearAction()
//                        if(viewModel.mErrorOccured.value!!) {
//                            view.findNavController().navigateUp()
//                        }
//                    }
//                    ProfilesListViewModel.Actions.SAVE -> {}
//                }
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
        edit_hobbies.setText(profile.hobbies)
        view_background.setBackgroundColor(color)
    }
}
