package com.jwilder.passportprofileviewer.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.jwilder.passportprofileviewer.R
import com.jwilder.passportprofileviewer.classes.Profile
import com.jwilder.passportprofileviewer.viewmodels.ProfilesListViewModel

class ProfileListAdapter internal constructor(context: Context, private val model: ProfilesListViewModel) : RecyclerView.Adapter<ProfileListAdapter.ProfileViewHolder>() {

    private val inflater : LayoutInflater = LayoutInflater.from(context)
    private var profiles = emptyList<Profile>()

    inner class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileName: TextView = itemView.findViewById(R.id.text_profile_name)
        val profileGenderIcon: ImageView = itemView.findViewById(R.id.image_profile_gender_ic)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val itemView = inflater.inflate(R.layout.profile_recycler_item,parent,false)
        return ProfileViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val current = profiles[position]
        holder.profileName.text = current.name
        // TODO Change icon tint here
        holder.itemView.setOnClickListener {
//            model.select(profiles[position])
            // TODO Write the select method for profile view model
            it.findNavController().navigate(R.id.action_profilesListFragment_to_profileViewFragment)
        }
    }

    override fun getItemCount() = profiles.size

    internal fun setProfiles(profiles: List<Profile>) {
        this.profiles = profiles
        notifyDataSetChanged()
    }
}