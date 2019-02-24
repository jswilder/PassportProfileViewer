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
import kotlinx.android.synthetic.main.recycler_list_filter_bar.*
import java.lang.Exception

/*
    Primary fragment; Shows a list of all profiles matching the current
    filter/sort options
 */
class ProfilesListFragment : Fragment() {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel = activity?.run {
            ViewModelProviders.of(this).get(ProfilesViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        showLoadingCircle()

        mDivider = DividerItemDecoration(context,1)
        mAdapter = ProfileListAdapter(context!!,mViewModel)
        mViewModel.getProfiles().observe( this, Observer { profiles ->
            profiles?.let {
                mAdapter.setProfiles(profiles)
                hideLoadingCircle()
            }
        })

        recycler_profiles.adapter = mAdapter
        recycler_profiles.layoutManager = LinearLayoutManager(recycler_profiles.context)
        recycler_profiles.addItemDecoration(mDivider)

        initLabels()
        initListeners()
    }

    private fun initLabels() {
        clearLabels()
        when(mViewModel.getField()) {
            Field.AGE -> text_age.text = mViewModel.getFieldLabel()
            Field.NAME -> text_name.text = mViewModel.getFieldLabel()
            Field.UID -> text_id.text = mViewModel.getFieldLabel()
        }
        text_gender.text = mViewModel.getFilterLabel()
    }

    private fun clearLabels() {
        text_age.text = getString(R.string.age)
        text_id.text = getString(R.string.id)
        text_name.text = getString(R.string.name)
    }

    private fun initListeners() {
        text_gender.setOnClickListener {
            showLoadingCircle()
            mViewModel.setGenderFilter()
            mViewModel.queryProfiles()
            text_gender.text = mViewModel.getFilterLabel()
        }

        text_id.setOnClickListener {
            showLoadingCircle()
            mViewModel.setSortField(Field.UID)
            clearLabels()
            text_id.text = mViewModel.getFieldLabel()
            mViewModel.queryProfiles()
        }

        text_name.setOnClickListener {
            showLoadingCircle()
            mViewModel.setSortField(Field.NAME)
            clearLabels()
            text_name.text = mViewModel.getFieldLabel()
            mViewModel.queryProfiles()
        }

        text_age.setOnClickListener {
            showLoadingCircle()
            mViewModel.setSortField(Field.AGE)
            clearLabels()
            text_age.text = mViewModel.getFieldLabel()
            mViewModel.queryProfiles()
        }

        image_clear_filter.setOnClickListener {
            showLoadingCircle()
            mViewModel.setDefaultsFilterSort()
            clearLabels()
            text_id.text = mViewModel.getFieldLabel()
            text_gender.text = mViewModel.getFilterLabel()
            mViewModel.queryProfiles()
        }

        fab_new_profile.setOnClickListener {
            findNavController().navigate(R.id.action_profilesListFragment_to_newProfileFragment)
        }
    }

    private fun hideLoadingCircle() {
        loading_circle.visibility = View.GONE
    }

    private fun showLoadingCircle() {
        loading_circle.visibility = View.VISIBLE
    }
}
