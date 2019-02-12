package com.jwilder.passportprofileviewer.fragments

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.jwilder.passportprofileviewer.R
import com.jwilder.passportprofileviewer.viewmodels.ProfilesListViewModel

class ProfilesListFragment : Fragment() {

    companion object {
        fun newInstance() = ProfilesListFragment()
    }

    private lateinit var viewModel: ProfilesListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profiles_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ProfilesListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
