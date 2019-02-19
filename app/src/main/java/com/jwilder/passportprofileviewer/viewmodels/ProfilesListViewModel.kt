package com.jwilder.passportprofileviewer.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.*
import com.jwilder.passportprofileviewer.classes.Profile
import java.lang.Exception
import java.util.ArrayList

class ProfilesListViewModel : ViewModel() {

    @Suppress("PrivatePropertyName")
    private val TAG : String = "PROFILES_VIEW_MODEL"
    @Suppress("PrivatePropertyName")
    private val COLLECTION : String = "Profiles"
    private val TIME: Long = 1548997200000 // Feb 1, 2019 in ms

    private val mFireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    var mProfiles: MutableLiveData<MutableList<Profile>> = MutableLiveData()
    var mSelectedProfile = MutableLiveData<Profile>()

    /*
    Needed Indexes/Indices:
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
        loadInitialValues()

        mFireStore.collection(COLLECTION)
            .addSnapshotListener { snapshot, e ->
                if( e != null ) {
                    Log.w(TAG, "Collection Listen Failed", e)
                }

                val profiles: MutableList<Profile> = ArrayList()

                for(document in snapshot!!) {
                    try {
                        val item = Profile(document)
                        profiles.add(item)
                        Log.d(TAG,"$item")

                        mFireStore.collection(COLLECTION).document(document.id)
                            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                                if(firebaseFirestoreException != null) {
                                    Log.w(TAG, "Listed failed.",firebaseFirestoreException)
                                    return@addSnapshotListener
                                }
                                if(documentSnapshot != null && documentSnapshot.exists()) {
                                    Log.d(TAG, "Current data: ${documentSnapshot.data}")
                                } else {
                                    Log.d(TAG, "Current data: NULL")
                                }
                            }
                    } catch (e: Exception) {
                        Log.w(TAG,"Failed to convert.",e)
                    }
                }
                mProfiles.value = profiles
            }
    }

    private fun loadInitialValues() {
        val profiles: MutableList<Profile> = ArrayList()
        mFireStore.collection(COLLECTION)
            .orderBy("uid", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                for(document in result) {
                    try {
                        val item = Profile(document)
                        mProfiles.value?.add(mProfiles.value!!.size,item)
//                        profiles.add(item)
                        Log.d(TAG,"$item")
                        mFireStore.collection(COLLECTION).document(document.id)
                            .addSnapshotListener { snapshot, e ->
                                if(e != null) {
                                    Log.w(TAG, "Listed failed.",e)
                                    return@addSnapshotListener
                                }
                                if(snapshot != null && snapshot.exists()) {
                                    Log.d(TAG, "Current data: ${snapshot.data}")
                                } else {
                                    Log.d(TAG, "Current data: NULL")
                                }
                            }
                    } catch (e: Exception) {
                        Log.w(TAG,"Failed to convert.",e)
                    }
                }
//                mProfiles.value?.addAll(profiles)
            }
            .addOnFailureListener{ exception ->
                Log.w(TAG,"Error getting docs.",exception)
            }
    }

    fun select(profile: Profile) {
        mSelectedProfile.value = profile
    }

    fun submitChangesToDatabase() {
        // TODO The only value that can change is hobbies, not the entire profile
        var profile = mProfiles.value?.get(3)
        profile?.age = profile?.age!!.plus(1)
        val profileMap = mProfiles.value?.get(3)?.toMap()
        mFireStore.collection(COLLECTION).document(profile.queryId!!)
            .set({ profileMap })
            .addOnSuccessListener { Log.d(TAG,"Document written successfully") }
            .addOnFailureListener { e -> Log.w(TAG,"Error.",e) }
    }

    fun addNewProfileToDatabase(profile: Profile) {
        profile.uid = profile.uid - TIME
        mFireStore.collection(COLLECTION)
            .add(profile.toMap())
            .addOnSuccessListener { documentReference ->
                Log.d(TAG,"Document written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error adding document.",exception)
            }
    }

    fun deleteProfileFromDatabase(profile: Profile) {

    }
}
