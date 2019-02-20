package com.jwilder.passportprofileviewer.fragments

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import com.jwilder.passportprofileviewer.R
import com.jwilder.passportprofileviewer.adapters.ProfileListAdapter
import com.jwilder.passportprofileviewer.classes.Profile
import com.jwilder.passportprofileviewer.viewmodels.ProfilesListViewModel
import kotlinx.android.synthetic.main.profiles_list_fragment.*
import kotlinx.android.synthetic.main.recycler_list_header.*
import java.lang.Exception
import java.util.*

class ProfilesListFragment : Fragment() {

    companion object {
        fun newInstance() = ProfilesListFragment()
    }

    private lateinit var viewModel: ProfilesListViewModel
    private lateinit var divider: DividerItemDecoration

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profiles_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = recycler_profiles
        viewModel = activity?.run {
            ViewModelProviders.of(this).get(ProfilesListViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        val adapter = ProfileListAdapter(context!!,viewModel)

        viewModel.getAllProfiles().observe( this, Observer { profiles ->
            profiles?.let { adapter.setProfiles(profiles) }
        })

        divider = DividerItemDecoration(context,1)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.addItemDecoration(divider)

        fab_new_profile.setOnClickListener {
            findNavController().navigate(R.id.action_profilesListFragment_to_newProfileFragment)
        }

        text_gender.setOnClickListener {
            // TODO
            Toast.makeText(context!!,"Gender",Toast.LENGTH_SHORT).show()
        }

        text_name.setOnClickListener {
            Toast.makeText(context!!,"Name",Toast.LENGTH_SHORT).show()
            // TODO
        }

        text_age.setOnClickListener {
            Toast.makeText(context!!,"Age",Toast.LENGTH_SHORT).show()
            // TODO
        }

        text_id.setOnClickListener {
            Toast.makeText(context!!,"Id",Toast.LENGTH_SHORT).show()
            // TODO
        }
    }
}
