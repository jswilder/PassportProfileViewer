package com.jwilder.passportprofileviewer.classes

/*
    Fields that can be sorted in firestore
 */
enum class Field(private val field: String) {
    AGE("age"),
    NAME("name"),
    UID("uid");

    override fun toString() = field
}