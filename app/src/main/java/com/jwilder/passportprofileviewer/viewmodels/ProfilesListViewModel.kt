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
    private val TIME: Long = 1_548_997_200_000 // Feb 1, 2019 in ms

    private val mFireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    val mProfiles: MutableLiveData<MutableList<Profile>> = MutableLiveData()
    val mSelectedProfile = MutableLiveData<Profile>()
    val mToastMessage = MutableLiveData<String>()
    val mErrorOccured = MutableLiveData<Boolean>()

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
            }
            .addOnFailureListener{ exception ->
                Log.w(TAG,"Error getting docs.",exception)
            }
    }

    fun select(profile: Profile) {
        mSelectedProfile.value = profile
    }

    private fun setToastData(message: String, errorOccured: Boolean) {
        mToastMessage.value = message
        mErrorOccured.value = errorOccured
    }

    fun submitChangesToDatabase(newHobbies: String) {
        if(mSelectedProfile.value != null) {
            val data = HashMap<String,Any>()
            data["hobbies"] = newHobbies
            mFireStore.collection(COLLECTION)
                .document(mSelectedProfile.value?.queryId!!)
                .set(data, SetOptions.merge())
                .addOnSuccessListener {
                    setToastData("Changes Saved",false)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG,"Changes failed to save.",e)
                    setToastData("Save Failed",true)
                }
        }
    }

    fun addNewProfileToDatabase(profile: Profile) {
        profile.uid = profile.uid - TIME
        mFireStore.collection(COLLECTION)
            .add(profile.toMap())
            .addOnSuccessListener { documentReference ->
                Log.d(TAG,"Document written with ID: ${documentReference.id}")
                setToastData("Profile Added",false)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error adding document.",exception)
                setToastData("Failed to add profile",true)
            }
    }

    fun deleteProfileFromDatabase(profile: Profile) {
        if(profile.queryId != null) {
            mFireStore.collection(COLLECTION)
                .document(profile.queryId!!)
                .delete()
                .addOnSuccessListener {
                    Log.d(TAG,"${profile.queryId} Deleted!")
                    setToastData("Profile deleted successfully",false)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG,"Error deleting profile ${profile.queryId}",e)
                    setToastData("Failed to delete profile",true)
                }
        }
    }
}
