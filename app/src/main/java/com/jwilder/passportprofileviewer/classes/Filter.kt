package com.jwilder.passportprofileviewer.classes

/*
    Gender filter
 */
enum class Filter(private val field: String) {
    DEFAULT(""),
    MALE("male"),
    FEMALE("female");

    override fun toString() = field
}