package com.jwilder.passportprofileviewer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.jwilder.passportprofileviewer.R

class ProfileViewFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileViewFragment()
    }

//    private lateinit var viewModel: ProfileViewViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_view_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProviders.of(this).get(ProfileViewViewModel::class.java)
    }

}
