package com.jwilder.passportprofileviewer.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.jwilder.passportprofileviewer.classes.Profile
import java.lang.Exception

class ProfilesListViewModel : ViewModel() {

    private val TAG = "PROFILES_VIEW_MODEL"
    private val mFireStore: FirebaseFirestore = FirebaseFirestore.getInstance() // The one we wanted...
    private var profiles: MutableLiveData<List<Profile>> = MutableLiveData()

    init {
        // Load the initial profiles
        val query = mFireStore.collection("Profiles")
            .get()
            .addOnSuccessListener { result ->
                for(document in result) {
                    try {
                        // TODO Convert to live data
                        val item: Profile = Profile(document)
                        Log.d(TAG,"$item")
                    } catch (e: Exception) {
                        Log.w(TAG,"Failed to convert.",e)
                    }
                }
            }
            .addOnFailureListener{ exception ->
                Log.w(TAG,"Error getting docs.",exception)
            }
    }
}
