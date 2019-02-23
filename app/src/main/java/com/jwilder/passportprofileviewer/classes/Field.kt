package com.jwilder.passportprofileviewer.classes

/*
    Fields that can be sorted in firestore
 */
enum class Field(val field: String, val label: String) {
    AGE("age","Age"),
    NAME("name","Name"),
    UID("uid","Id");

    override fun toString() = field
}