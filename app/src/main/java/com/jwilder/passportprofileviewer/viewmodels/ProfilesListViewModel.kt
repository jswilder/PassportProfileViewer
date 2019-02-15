package com.jwilder.passportprofileviewer.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.jwilder.passportprofileviewer.classes.Profile

class ProfilesListViewModel : ViewModel() {

    private val TAG = "PROFILES_VIEW_MODEL"
//    private val mDatabaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("profiles") // Database
    private val mFireStore: FirebaseFirestore = FirebaseFirestore.getInstance() // The one we wanted...
    private var profiles: MutableLiveData<List<Profile>> = MutableLiveData()

    init {
        Log.w(TAG,"Ran init")

        val query = mFireStore.collection("Profiles")
            .get()
            .addOnSuccessListener { result ->
                for(document in result) {
                    Log.d(TAG,"${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener{ exception ->
                Log.w(TAG,"Error getting docs.",exception)
            }
    }
}
