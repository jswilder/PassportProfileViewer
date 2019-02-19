package com.jwilder.passportprofileviewer.classes

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.firestore.QueryDocumentSnapshot

/*
    Profile class
 */

@IgnoreExtraProperties
data class Profile(
    var queryId: String? = "",
    var name: String? = "",
    var hobbies: String? = "",
    var image: String? = "",
    var gender: GENDER = GENDER.MALE,
    var uid: Long = 0,
    var age: Long = 0
) {

    constructor(d: QueryDocumentSnapshot) : this() {
        // TODO Clean this up
        queryId = d.id
        name = d.get("name") as String
        hobbies = d.get("hobbies") as String
        image = d.get("image") as String
        uid = d.get("uid") as Long
        age = d.get("age") as Long
        gender = if(d.get("gender") as String == "male") GENDER.MALE else GENDER.FEMALE
    }

    enum class GENDER(private val gender: String){
        MALE("male"),
        FEMALE("female");

        override fun toString(): String {
            return this.gender
        }
    }

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "hobbies" to hobbies,
            "image" to image,
            "gender" to gender.toString(),
            "uid" to uid,
            "age" to age
        )
    }
}