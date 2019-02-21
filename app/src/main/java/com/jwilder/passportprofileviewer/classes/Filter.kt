package com.jwilder.passportprofileviewer.classes

enum class Filter(val field: String) {
    DEFAULT(""),
    MALE("male"),
    FEMALE("female");

    override fun toString() = field
}