package com.jwilder.passportprofileviewer.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders

import com.jwilder.passportprofileviewer.R
import com.jwilder.passportprofileviewer.classes.Profile
import com.jwilder.passportprofileviewer.viewmodel.ProfilesViewModel
import kotlinx.android.synthetic.main.new_profile_fragment.*
import java.lang.Exception
import java.util.*

class NewProfileFragment : Fragment() {

    private val TAG = "NewProfileFragment"
    private lateinit var mViewModel: ProfilesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.new_profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel = activity?.run {
            ViewModelProviders.of(this).get(ProfilesViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        button_save.setOnClickListener {
            mViewModel.addNewProfileDB(getProfileFromInputs())
        }
    }

    private fun getProfileFromInputs() : Profile {
        val name = if (text_input_name.text.toString() != "") text_input_name.text.toString() else "None"
        val hobbies = if (text_input_hobbies.text.toString() != "") text_input_hobbies.text.toString() else "None"
        val age = if (text_input_age.text.toString() != "") text_input_age.text.toString().toLong() else 0
        val image = if (text_input_image.text.toString() != "") text_input_image.text.toString() else "None"
        val gender = if ( spinner_gender.selectedItem.toString() == "male" ) Profile.GENDER.MALE else Profile.GENDER.FEMALE
        val uid = Date().time
        return Profile(name = name, hobbies = hobbies, age = age, image = image, gender = gender, uid = uid)
    }
}
