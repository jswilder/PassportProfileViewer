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
import com.jwilder.passportprofileviewer.classes.Field
import com.jwilder.passportprofileviewer.viewmodel.ProfilesViewModel
import kotlinx.android.synthetic.main.profiles_list_fragment.*
import kotlinx.android.synthetic.main.recycler_filter_header.*
import java.lang.Exception

class ProfilesListFragment : Fragment() {

    companion object {
        fun newInstance() = ProfilesListFragment()
    }

    private val TAG = "ProfileListFragment"
    private lateinit var mViewModel: ProfilesViewModel
    private lateinit var mDivider: DividerItemDecoration
    private lateinit var mAdapter: ProfileListAdapter

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

        mViewModel = activity?.run {
            ViewModelProviders.of(this).get(ProfilesViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        mDivider = DividerItemDecoration(context,1)
        mAdapter = ProfileListAdapter(context!!,mViewModel)
        mViewModel.getProfiles().observe( this, Observer { profiles ->
            profiles?.let { mAdapter.setProfiles(profiles) }
        })

        recycler_profiles.adapter = mAdapter
        recycler_profiles.layoutManager = LinearLayoutManager(recycler_profiles.context)
        recycler_profiles.addItemDecoration(mDivider)

        fab_new_profile.setOnClickListener {
            findNavController().navigate(R.id.action_profilesListFragment_to_newProfileFragment)
        }

        text_id.setOnClickListener {
            mViewModel.setSortField(Field.UID)
            mViewModel.applyFilterAndSort()
        }

        text_name.setOnClickListener {
            mViewModel.setSortField(Field.NAME)
            mViewModel.applyFilterAndSort()
        }

        text_gender.setOnClickListener {
            mViewModel.setGenderFilter()
            mViewModel.applyFilterAndSort()
        }

        text_age.setOnClickListener {
            mViewModel.setSortField(Field.AGE)
            mViewModel.applyFilterAndSort()
        }

        image_clear_filter.setOnClickListener {
            mViewModel.setDefaults()
            mViewModel.applyFilterAndSort()
        }
    }
}
