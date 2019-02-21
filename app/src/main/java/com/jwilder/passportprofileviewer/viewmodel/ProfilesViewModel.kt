package com.jwilder.passportprofileviewer.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.*
import com.jwilder.passportprofileviewer.classes.Field
import com.jwilder.passportprofileviewer.classes.Filter
import com.jwilder.passportprofileviewer.classes.Profile
import java.lang.Exception

class ProfilesViewModel(application: Application) : AndroidViewModel(application) {

    @Suppress("PrivatePropertyName")
    private val TAG : String = "PROFILES_VIEW_MODEL"
    @Suppress("PrivatePropertyName")
    private val COLLECTION : String = "Profiles"
    @Suppress("PrivatePropertyName")
    private val TIME: Long = 1_548_997_200_000 // Feb 1, 2019 in ms

    /*
        Observable Fields (through their get methods
     */
    private val mProfiles: MutableLiveData<MutableList<Profile>> = MutableLiveData()
    private val mSelectedProfile = MutableLiveData<Profile>()

    /*
        FireStore
        mQuery: Holds the currently active query
        mRegstration: Holds the listener for the active query
     */
    private val mFireStore: CollectionReference = FirebaseFirestore.getInstance().collection(COLLECTION)
    private lateinit var mQuery : Query
    private lateinit var mRegistration: ListenerRegistration

    /*
        Sorting / Filtering
     */
    private lateinit var mGender: Filter
    private lateinit var mField: Field
    private lateinit var mDirection: Query.Direction


    fun getSelectedProfile() = mSelectedProfile
    fun getProfiles() = mProfiles

    init {
        setDefaults()
        loadInitialData()
    }

    fun setDefaults() {
        mGender = Filter.DEFAULT
        mField = Field.UID
        mDirection = Query.Direction.ASCENDING
    }

    fun setSortField(field: Field) {
        if(field == mField) {
            mDirection = if(mDirection == Query.Direction.ASCENDING) Query.Direction.DESCENDING
            else Query.Direction.ASCENDING
        } else {
            mField = field
            mDirection = Query.Direction.ASCENDING
        }
    }

    fun setGenderFilter() {
        mGender = when(mGender) {
            Filter.DEFAULT -> Filter.MALE
            Filter.MALE -> Filter.FEMALE
            Filter.FEMALE -> Filter.DEFAULT
        }
    }

    fun getFilterLabel() : String {
        // TODO Get string resource working
        return when(mGender) {
            Filter.DEFAULT -> "MF" // getApplication<Application>().resources.getString(R.string.mf)
            Filter.FEMALE -> "F"
            Filter.MALE -> "M"
        }
    }

    fun getFieldLabel() : String {
        val arrow = if(mDirection == Query.Direction.ASCENDING) "\u2191" else "\u2193"
        return "$arrow ${mField.field}"
    }

    private fun buildQuery() : Query {
        return when(mGender) {
            Filter.DEFAULT -> mFireStore.orderBy(mField.toString(),mDirection)
            Filter.MALE, Filter.FEMALE -> mFireStore.whereEqualTo("gender",mGender.toString()).orderBy(mField.toString(),mDirection)
        }
    }

    fun applyFilterAndSort() {
        mRegistration.remove() // Clear previous listener
        val mQuery = buildQuery()
        mRegistration = mQuery
            .addSnapshotListener { snapshot, e ->
            if( e != null ) {
                Log.w(TAG, "Collection Listen Failed", e)
            }
            val profiles: MutableList<Profile> = mutableListOf()
            for(document in snapshot!!) {
                try {
                    profiles.add(Profile(document))
                    mFireStore.document(document.id)
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

    private fun loadInitialData() {
        mQuery = mFireStore.orderBy(mField.toString())
        mRegistration = mQuery
            .addSnapshotListener { snapshot, e ->
                if( e != null ) {
                    Log.w(TAG, "Collection Listen Failed", e)
                }
                val profiles: MutableList<Profile> = mutableListOf()
                for(document in snapshot!!) {
                    try {
                        profiles.add(Profile(document))
                        mFireStore.document(document.id)
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

    fun select(profile: Profile) {
        mSelectedProfile.value = profile
    }

    fun submitChangesToDatabase(newHobbies: String) {
        if(mSelectedProfile.value != null) {
            val data = HashMap<String,Any>()
            data["hobbies"] = newHobbies
            mFireStore
                .document(mSelectedProfile.value?.queryId!!)
                .set(data, SetOptions.merge())
                .addOnSuccessListener {
                    mSelectedProfile.value?.hobbies = newHobbies
                }
                .addOnFailureListener { e ->
                    Log.w(TAG,"Changes failed to save.",e)
                }
        }
    }

    fun addNewProfileToDatabase(profile: Profile) {
        profile.uid = profile.uid - TIME
        mFireStore
            .add(profile.toMap())
            .addOnSuccessListener { documentReference ->
                Log.d(TAG,"Document written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error adding document.",exception)
            }
    }

    fun deleteProfileFromDatabase(profile: Profile) {
        if(profile.queryId != null) {
            mFireStore
                .document(profile.queryId!!)
                .delete()
                .addOnSuccessListener {
                    Log.d(TAG,"${profile.queryId} Deleted!")
                    // TODO Disable "save" button when delete succeeds
                }
                .addOnFailureListener { e ->
                    Log.w(TAG,"Error deleting profile ${profile.queryId}",e)
                }
        }
    }
}
