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
import kotlinx.android.synthetic.main.layout_new_profile.*
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

        btn_save_new_profile.setOnClickListener {
            if(areSaveCriteriaMet()) {
                mViewModel.addNewProfileDB(getProfileFromInputs())
                clearAllInputs()
            }
        }
    }

    /* Insure a name and age have been given */
    private fun areSaveCriteriaMet() : Boolean {
        val name = if(text_input_name.text.toString() == "") {
            layout_name.isErrorEnabled = true
            layout_name.error = getString(R.string.error_name_required)
            false
        } else {
            layout_name.isErrorEnabled = false
            true
        }
        val age = if(text_input_age.text.toString() == "") {
            layout_age.isErrorEnabled = true
            layout_age.error = getString(R.string.error_age_required)
            false
        } else {
            layout_age.isErrorEnabled = false
            true
        }
        return name && age
    }

    private fun clearAllInputs() {
        text_input_name.setText("")
        text_input_age.setText("")
        text_input_hobbies.setText("")
        text_input_image.setText("")
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
