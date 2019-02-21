package com.jwilder.passportprofileviewer.classes

enum class Field(val field: String) {
    AGE("age"),
    NAME("name"),
    UID("uid");

    override fun toString() = field
}