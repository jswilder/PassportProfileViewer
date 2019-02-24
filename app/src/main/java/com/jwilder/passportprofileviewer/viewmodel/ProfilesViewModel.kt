package com.jwilder.passportprofileviewer.viewmodel

import android.app.Application
import android.view.Gravity
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.*
import com.jwilder.passportprofileviewer.R
import com.jwilder.passportprofileviewer.classes.Field
import com.jwilder.passportprofileviewer.classes.Filter
import com.jwilder.passportprofileviewer.classes.Profile
/*
    Viewmodel used for all application data;
    Holds a reference to the firestore instance and is responsible for all queries/listeners
    **I left those in here due to it being a relatively small amount of code/logic**
 */
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
    private var mQuery : Query                              // Holds the currently active query (for all profiles)
    private var mRegistration: ListenerRegistration         // Holds the listener for the active query (for all profiles)
    private var mRegistrationProfile: ListenerRegistration  // Holds the listener for the selected profile

    /*  Sorting / Filtering */
    private lateinit var mGender: Filter
    private lateinit var mField: Field
    private lateinit var mDirection: Query.Direction

    init {
        setDefaultsFilterSort()
        mQuery = buildAllProfilesQuery()
        mRegistration = ListenerRegistration {}
        mRegistrationProfile = ListenerRegistration {}
        queryProfiles()
    }

    fun getSelectedProfile() = mSelectedProfile
    fun getProfiles() = mProfiles
    fun getField() = mField

    fun getFilterLabel() : String {
        return when(mGender) {
            Filter.DEFAULT -> getApplication<Application>().getString(R.string.mf)
            Filter.FEMALE ->  getApplication<Application>().getString(R.string.f)
            Filter.MALE -> getApplication<Application>().getString(R.string.m)
        }
    }

    fun getFieldLabel() : String {
        val arrow = if(mDirection == Query.Direction.ASCENDING) "\u2191" else "\u2193"
        return when(mField) {
            Field.UID -> "$arrow ${getApplication<Application>().getString(R.string.id)}"
            Field.AGE -> "$arrow ${getApplication<Application>().getString(R.string.age)}"
            Field.NAME -> "$arrow ${getApplication<Application>().getString(R.string.name)}"
        }
    }

    fun setDefaultsFilterSort() {
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

    private fun setToastTextAndShow(message: String) {
        val toast = Toast.makeText(getApplication<Application>(),message,Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER,0,0)
        toast.show()
    }

    fun setSelectedProfile(profile: Profile) {
        @Suppress("UNNECESSARY_SAFE_CALL")
        mRegistrationProfile?.remove()
        mRegistrationProfile = mFireStore.document(profile.queryId.toString())
            .addSnapshotListener(EventListener<DocumentSnapshot> { snapshot, exception ->
                if (exception != null) {
                    return@EventListener
                }
                if (snapshot != null && snapshot.exists()) {
                    mSelectedProfile.value = Profile(snapshot)
                } else {
                    mSelectedProfile.value = null
                }
            })
    }

    private fun buildAllProfilesQuery() : Query {
        return when(mGender) {
            Filter.DEFAULT -> mFireStore.orderBy(mField.toString(),mDirection)
            Filter.MALE, Filter.FEMALE -> mFireStore.whereEqualTo("gender",mGender.toString()).orderBy(mField.toString(),mDirection)
        }
    }

    fun queryProfiles() {
        @Suppress( "UNNECESSARY_SAFE_CALL")
        mRegistration?.remove() // Clear previous listener
        val mQuery = buildAllProfilesQuery()
        mRegistration = mQuery
            .addSnapshotListener(EventListener<QuerySnapshot> { querySnapshot, firestoreException ->
                if (firestoreException != null) {
                    return@EventListener
                }
                val profiles: MutableList<Profile> = mutableListOf()
                for (doc in querySnapshot!!) {
                    profiles.add(Profile(doc))
                }
                mProfiles.value = profiles
            })
    }

    fun submitProfileChangeDB(hobbies: String) {
        if(mSelectedProfile.value != null) {
            mFireStore
                .document(mSelectedProfile.value?.queryId!!)
                .set(mapOf("hobbies" to hobbies), SetOptions.merge())
                .addOnSuccessListener {
                    mSelectedProfile.value?.hobbies = hobbies
                    setToastTextAndShow(getApplication<Application>().getString(R.string.success_save_profile))
                }
                .addOnFailureListener {
                    setToastTextAndShow(getApplication<Application>().getString(R.string.fail_save_profile))
                }
        }
    }

    fun createNewProfileDB(profile: Profile) {
        profile.uid = profile.uid - TIME
        mFireStore
            .add(profile.toMap())
            .addOnSuccessListener {
                setToastTextAndShow(getApplication<Application>().getString(R.string.success_add_profile))
            }
            .addOnFailureListener {
                setToastTextAndShow(getApplication<Application>().getString(R.string.fail_add_profile))
            }
    }

    fun deleteSelectedProfileDB(profile: Profile) {
        if(profile.queryId != null) {
            mFireStore
                .document(profile.queryId!!)
                .delete()
                .addOnSuccessListener {
                    setToastTextAndShow(getApplication<Application>().getString(R.string.success_delete_profile))
                    mSelectedProfile.value = null
                }
                .addOnFailureListener {
                    setToastTextAndShow(getApplication<Application>().getString(R.string.fail_delete_profile))
                }
        }
    }
}