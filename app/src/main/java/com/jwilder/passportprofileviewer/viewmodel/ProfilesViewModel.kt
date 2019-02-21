package com.jwilder.passportprofileviewer.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.jwilder.passportprofileviewer.classes.Profile
import java.lang.Exception

class ProfilesViewModel : ViewModel() {

    @Suppress("PrivatePropertyName")
    private val TAG : String = "PROFILES_VIEW_MODEL"
    @Suppress("PrivatePropertyName")
    private val COLLECTION : String = "Profiles"
    @Suppress("PrivatePropertyName")
    private val TIME: Long = 1_548_997_200_000 // Feb 1, 2019 in ms

    private val mFireStore: CollectionReference = FirebaseFirestore.getInstance().collection(COLLECTION)

    private val mAllProfiles: MutableLiveData<MutableList<Profile>> = MutableLiveData()
    private val mSelectedProfile = MutableLiveData<Profile>()

    private lateinit var mQuery : Query
    private lateinit var mRegistration: ListenerRegistration

    fun getSelectedProfile() = mSelectedProfile
    fun getAllProfiles() = mAllProfiles

    init {
//        applyFilterAndSort(Filter.DEFAULT,Sort.DEFAULT)
        loadInitialAndListenToChanges()
    }

    enum class Filter(val field: String) {
        DEFAULT(""),
        MALE("male"),
        FEMALE("female");

        override fun toString(): String {
            return field
        }
    }

    enum class Sort(val field: String, private val method: String) {
        AGE_ASC("age","asc"),
        AGE_DESC("age","desc"),
        NAME_ASC("name","asc"),
        NAME_DESC("name","desc"),
        UID_ASC("uid","asc"),
        UID_DESC("uid","desc"),
        DEFAULT("uid","asc");

        fun getMethod() : Query.Direction {
            return if(method == "asc")
                Query.Direction.ASCENDING
            else
                Query.Direction.DESCENDING
        }
    }

    fun applyFilterAndSort(filter: Filter, sort: Sort) {
        mRegistration?.remove() // Clear previous listener
        val mQuery = when(filter) {
            Filter.DEFAULT -> mFireStore.orderBy(sort.field,sort.getMethod())
            Filter.MALE, Filter.FEMALE -> mFireStore.whereEqualTo("gender",filter.toString()).orderBy(sort.field,sort.getMethod())
        }
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
            mAllProfiles.value = profiles
        }
    }

    private fun loadInitialAndListenToChanges() {
        mQuery = mFireStore.orderBy("uid")
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
                mAllProfiles.value = profiles
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
