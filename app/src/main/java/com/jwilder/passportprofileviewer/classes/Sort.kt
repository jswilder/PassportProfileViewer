package com.jwilder.passportprofileviewer.classes

import com.google.firebase.firestore.Query

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