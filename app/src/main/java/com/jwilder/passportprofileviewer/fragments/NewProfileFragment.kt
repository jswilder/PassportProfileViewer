package com.jwilder.passportprofileviewer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.jwilder.passportprofileviewer.R

class NewProfileFragment : Fragment() {

    companion object {
        fun newInstance() = NewProfileFragment()
    }

//    private lateinit var viewModel: NewProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.new_profile_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProviders.of(this).get(NewProfileViewModel::class.java)
    }

}
