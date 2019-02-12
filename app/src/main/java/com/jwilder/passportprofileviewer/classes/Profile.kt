package com.jwilder.passportprofileviewer.classes

/*
    Profile class
 */

class Profile(var name: String, var age: Int, var hobbies: String, val gender: GENDER) {

    // TODO Unique Id, should be generated outside of class, in firebase; Profile image

    enum class GENDER{
        MALE,
        FEMALE
    }
}