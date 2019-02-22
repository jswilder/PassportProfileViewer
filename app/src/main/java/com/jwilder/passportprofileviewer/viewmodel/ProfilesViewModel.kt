package com.jwilder.passportprofileviewer.viewmodel

import android.app.Application
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.*
import com.jwilder.passportprofileviewer.classes.Field
import com.jwilder.passportprofileviewer.classes.Filter
import com.jwilder.passportprofileviewer.classes.Profile
import java.lang.Exception

@Suppress("PrivatePropertyName")
class ProfilesViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG : String = "ProfilesViewModel"
    private val COLLECTION : String = "Profiles"
    private val TIME: Long = 1_548_997_200_000         // Feb 1, 2019 in ms; Used to shorten Uid length

    /*  Observable Fields   */
    private val mProfiles: MutableLiveData<MutableList<Profile>> = MutableLiveData()
    private val mSelectedProfile = MutableLiveData<Profile>()

    /*  FireStore   */
    private val mFireStore: CollectionReference = FirebaseFirestore.getInstance().collection(COLLECTION)
    private var mQuery : Query                         // Holds the currently active query
    private var mRegistration: ListenerRegistration    // Holds the listener for the active query

    /*  Sorting / Filtering */
    private lateinit var mGender: Filter
    private lateinit var mField: Field
    private lateinit var mDirection: Query.Direction

    private var toast: Toast

    init {
        setDefaultsFilterSort()
        mQuery = buildQuery()
        mRegistration = ListenerRegistration {  }
        toast = Toast.makeText(getApplication<Application>(),"",Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER,0,0)
        queryProfiles()
    }

    fun getSelectedProfile() = mSelectedProfile
    fun getProfiles() = mProfiles

    fun getFilterLabel() : String {
        // TODO Get string resource working for better internationalization
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

    fun setDefaultsFilterSort() {
        mGender = Filter.DEFAULT
        mField = Field.UID
        mDirection = Query.Direction.ASCENDING
    }

    fun setSelectedProfile(profile: Profile) {
        mSelectedProfile.value = profile
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

    private fun setToastTextAndShow(message: String) {
        toast.setText(message)
        toast.show()
    }

    private fun buildQuery() : Query {
        return when(mGender) {
            Filter.DEFAULT -> mFireStore.orderBy(mField.toString(),mDirection)
            Filter.MALE, Filter.FEMALE -> mFireStore.whereEqualTo("gender",mGender.toString()).orderBy(mField.toString(),mDirection)
        }
    }

    fun queryProfiles() {
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
                                Log.w(TAG, "Listen failed.",firebaseFirestoreException)
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

    fun submitProfileChangeDB(hobbies: String) {
        if(mSelectedProfile.value != null) {
            mFireStore
                .document(mSelectedProfile.value?.queryId!!)
                .set(mapOf("hobbies" to hobbies), SetOptions.merge())
                .addOnSuccessListener {
                    mSelectedProfile.value?.hobbies = hobbies
                    setToastTextAndShow("Changes Saved")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG,"Changes failed to save.",e)
                    setToastTextAndShow("Failed to Save Changes")
                }
        }
    }

    fun addNewProfileDB(profile: Profile) {
        profile.uid = profile.uid - TIME
        mFireStore
            .add(profile.toMap())
            .addOnSuccessListener { documentReference ->
                Log.d(TAG,"Document written with ID: ${documentReference.id}")
                setToastTextAndShow("Profile Added")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error adding document.",exception)
                setToastTextAndShow("Failed to Add Profile")
            }
    }

    fun deleteProfileDB(profile: Profile) {
        if(profile.queryId != null) {
            mFireStore
                .document(profile.queryId!!)
                .delete()
                .addOnSuccessListener {
                    Log.d(TAG,"${profile.queryId} deleted")
                    setToastTextAndShow("Profile Deleted")
                    // TODO Disable "save" button when delete succeeds
                }
                .addOnFailureListener { e ->
                    Log.w(TAG,"Error deleting profile ${profile.queryId}",e)
                    setToastTextAndShow("Failed to Delete Profile")
                }
        }
    }
}