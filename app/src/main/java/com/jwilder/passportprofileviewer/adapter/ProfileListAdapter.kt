package com.jwilder.passportprofileviewer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.jwilder.passportprofileviewer.R
import com.jwilder.passportprofileviewer.classes.FirestoreProfile
import com.jwilder.passportprofileviewer.viewmodel.ProfilesViewModel

class ProfileListAdapter internal constructor(private val context: Context, private val model: ProfilesViewModel) : RecyclerView.Adapter<ProfileListAdapter.ProfileViewHolder>() {

    @Suppress("PrivatePropertyName")
    private val TAG = "ProfileListAdapter"
    private val inflater : LayoutInflater = LayoutInflater.from(context)
    private var profiles = emptyList<FirestoreProfile>()

    inner class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileName: TextView = itemView.findViewById(R.id.text_profile_name)
        val profileAge: TextView = itemView.findViewById(R.id.text_profile_age)
        val profileGenderIcon: ImageView = itemView.findViewById(R.id.image_profile_gender_ic)
        val profileId: TextView = itemView.findViewById(R.id.text_profile_id)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val itemView = inflater.inflate(R.layout.recycler_list_item,parent,false)
        return ProfileViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val current = profiles[position]
        holder.profileName.text = current.name
        holder.profileAge.text = current.age.toString()
        holder.profileId.text = current.uid.toString()
        val color = when(current.gender) {
            FirestoreProfile.GENDER.MALE -> ContextCompat.getColor(context,R.color.maleBlue)
            FirestoreProfile.GENDER.FEMALE -> ContextCompat.getColor(context,R.color.femalePink)
        }
        holder.profileGenderIcon.drawable.setTint( color )
        holder.itemView.setOnClickListener {
            model.setSelectedProfile(profiles[position])
            it.findNavController().navigate(R.id.action_profilesListFragment_to_profileViewFragment)
        }
    }

    override fun getItemCount() = profiles.size

    internal fun setProfiles(profiles: List<FirestoreProfile>) {
        this.profiles = profiles
        notifyDataSetChanged()
    }
}