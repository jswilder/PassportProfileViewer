package com.jwilder.passportprofileviewer.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.jwilder.passportprofileviewer.classes.Profile
import java.lang.Exception
import java.util.ArrayList

class ProfilesListViewModel : ViewModel() {

    private val TAG = "PROFILES_VIEW_MODEL"
    private val mFireStore: FirebaseFirestore = FirebaseFirestore.getInstance() // The one we wanted...
    private var profiles: MutableLiveData<List<Profile>> = MutableLiveData()

    /*
    Needed Indexes:
        GENDER
            - Age ASC
            - Age DESC
            - Name ASC
            - Name DESC
        All
            - Age ASC
            - Age DESC
            - Name ASC
            - Name DESC
     */

    init {
        // Load the initial profiles
        val _profiles: MutableList<Profile> = ArrayList()

        val query = mFireStore.collection("Profiles")
//            .whereEqualTo("age",26)
            .whereEqualTo("gender","male")
            .orderBy("uid")
            .get()
            .addOnSuccessListener { result ->
                for(document in result) {
                    try {
                        // TODO Convert to live data
                        val item = Profile(document)
                        _profiles.add(item)
                        Log.d(TAG,"$item")
                    } catch (e: Exception) {
                        Log.w(TAG,"Failed to convert.",e)
                    }
                }
                profiles.value = _profiles
            }
            .addOnFailureListener{ exception ->
                Log.w(TAG,"Error getting docs.",exception)
            }
    }
}
