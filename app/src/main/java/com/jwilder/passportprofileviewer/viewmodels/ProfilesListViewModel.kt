package com.jwilder.passportprofileviewer.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jwilder.passportprofileviewer.classes.Profile

class ProfilesListViewModel : ViewModel() {

    private val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference("Profiles")
    var profiles: MutableLiveData<List<Profile>> = MutableLiveData()

//    fun getProfiles() : LiveData<List<Profile>> {
//
//    }

    /*
      class ArticleViewModel : ViewModel() {
      var articles: MutableLiveData<List<Article>> = MutableLiveData()

      fun getArticles(): LiveData<List<Article>> {
        if (articles.value == null) {
          FirebaseDatabase.getInstance()
           .getReference("feed/articles")
           .addListenerForSingleValueEvent(object : ValueEventListener {
               override fun onDataChange(dataSnapshot: DataSnapshot) {
                   if (dataSnapshot.exists()) {
                       articles.postValue(toArticles(snapshot))
                   })
        }

        return articles
    }
     */
}
