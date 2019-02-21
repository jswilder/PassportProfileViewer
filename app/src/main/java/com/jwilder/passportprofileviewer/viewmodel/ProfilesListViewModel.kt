package com.jwilder.passportprofileviewer.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.*
import com.jwilder.passportprofileviewer.classes.Profile
import java.lang.Exception

class ProfilesListViewModel : ViewModel() {

    @Suppress("PrivatePropertyName")
    private val TAG : String = "PROFILES_VIEW_MODEL"
    @Suppress("PrivatePropertyName")
    private val COLLECTION : String = "Profiles"
    @Suppress("PrivatePropertyName")
    private val TIME: Long = 1_548_997_200_000 // Feb 1, 2019 in ms

    private val mFireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val mAllProfiles: MutableLiveData<MutableList<Profile>> = MutableLiveData()
    private val mSortedProfiles: MutableLiveData<MutableList<Profile>> = MutableLiveData()
    private val mSelectedProfile = MutableLiveData<Profile>()
    private val mToastMessage : MutableLiveData<String> = MutableLiveData("")
    private val mErrorOccurred : MutableLiveData<Boolean> = MutableLiveData(false)
    private var mAttemptedAction = Actions.NONE

    enum class Actions {
        SAVE,
        DELETE,
        CREATE,
        NONE
    }

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

    fun getSortedProfiles() : MutableLiveData<MutableList<Profile>> {
        val profiles: MutableList<Profile> = mutableListOf()
        mFireStore.collection(COLLECTION)
            .orderBy("uid")
            .get()
            .addOnSuccessListener { result ->
                for(document in result) {
                    profiles.add(Profile(document))
                }
                mSortedProfiles.value = profiles
            }
            .addOnFailureListener { exception ->
                Log.w(TAG,"Sorted Profiles.",exception)
            }
        return mSortedProfiles
    }

    fun getSelectedProfile() = mSelectedProfile
    fun getToastMessage() = mToastMessage
    fun getErrorOccurred() = mErrorOccurred
    fun getAttemptedAction() = mAttemptedAction

    init {
        loadInitialAndListenToChanges()
    }

    private fun loadInitialAndListenToChanges() {
        mFireStore.collection(COLLECTION)
            .addSnapshotListener { snapshot, e ->
                if( e != null ) {
                    Log.w(TAG, "Collection Listen Failed", e)
                }
                val profiles: MutableList<Profile> = mutableListOf()

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
                mAllProfiles.value = profiles
            }
    }

    fun select(profile: Profile) {
        mSelectedProfile.value = profile
    }

    private fun setToastData(message: String, errorOccured: Boolean, action: Actions) {
        mToastMessage.value = message
        mErrorOccurred.value = errorOccured
        mAttemptedAction = action
    }

    fun clearAction() {
        mAttemptedAction = Actions.NONE
    }

    fun submitChangesToDatabase(newHobbies: String) {
        if(mSelectedProfile.value != null) {
            val data = HashMap<String,Any>()
            data["hobbies"] = newHobbies
            mFireStore.collection(COLLECTION)
                .document(mSelectedProfile.value?.queryId!!)
                .set(data, SetOptions.merge())
                .addOnSuccessListener {
                    mSelectedProfile.value?.hobbies = newHobbies
                    setToastData("Changes Saved",false, Actions.SAVE)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG,"Changes failed to save.",e)
                    setToastData("Save Failed",true, Actions.SAVE)
                }
        }
    }

    fun addNewProfileToDatabase(profile: Profile) {
        profile.uid = profile.uid - TIME
        mFireStore.collection(COLLECTION)
            .add(profile.toMap())
            .addOnSuccessListener { documentReference ->
                Log.d(TAG,"Document written with ID: ${documentReference.id}")
                setToastData("Profile Added",false, Actions.CREATE)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error adding document.",exception)
                setToastData("Failed to add profile",true, Actions.CREATE)
            }
    }

    fun deleteProfileFromDatabase(profile: Profile) {
        if(profile.queryId != null) {
            mFireStore.collection(COLLECTION)
                .document(profile.queryId!!)
                .delete()
                .addOnSuccessListener {
                    Log.d(TAG,"${profile.queryId} Deleted!")
                    setToastData("Profile deleted successfully",false, Actions.DELETE)
                    // TODO Disable "save" button when delete succeeds
                }
                .addOnFailureListener { e ->
                    Log.w(TAG,"Error deleting profile ${profile.queryId}",e)
                    setToastData("Failed to delete profile",true, Actions.DELETE)
                }
        }
    }
}
