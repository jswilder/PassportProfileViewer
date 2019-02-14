package com.jwilder.passportprofileviewer.classes

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

/*
    Profile class
 */

@IgnoreExtraProperties
data class Profile(
    var name: String? = "",
    var hobbies: String? = "",
    var image: String? = "",
    var gender: GENDER,
    var uid: Int = 0,
    var age: Int = 0
) {

    enum class GENDER(val gender: String){
        MALE("male"),
        FEMALE("female")
    }

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "hobbies" to hobbies,
            "image" to image,
            "gender" to gender,
            "uid" to uid,
            "age" to age
        )
    }
}