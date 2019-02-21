package com.jwilder.passportprofileviewer.fragment

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import com.jwilder.passportprofileviewer.R
import com.jwilder.passportprofileviewer.adapter.ProfileListAdapter
import com.jwilder.passportprofileviewer.viewmodel.ProfilesViewModel
import kotlinx.android.synthetic.main.profiles_list_fragment.*
import kotlinx.android.synthetic.main.recycler_filter_header.*
import java.lang.Exception

class ProfilesListFragment : Fragment() {

    companion object {
        fun newInstance() = ProfilesListFragment()
    }

    private lateinit var mViewModel: ProfilesViewModel
    private lateinit var mDivider: DividerItemDecoration

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
        mViewModel = activity?.run {
            ViewModelProviders.of(this).get(ProfilesViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        val adapter = ProfileListAdapter(context!!,mViewModel)

        mViewModel.getAllProfiles().observe( this, Observer { profiles ->
            profiles?.let { adapter.setProfiles(profiles) }
        })

        mDivider = DividerItemDecoration(context,1)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.addItemDecoration(mDivider)

        fab_new_profile.setOnClickListener {
            findNavController().navigate(R.id.action_profilesListFragment_to_newProfileFragment)
        }

        text_id.setOnClickListener {

        }

        text_name.setOnClickListener {

        }

        text_gender.setOnClickListener {

        }

        text_age.setOnClickListener {

        }

        image_clear_filter.setOnClickListener {

        }
    }
}
