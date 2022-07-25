package ge.gmikeladze.messenger.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ge.gmikeladze.messenger.view.MainActivity
import ge.gmikeladze.messenger.view.MainActivity.Companion.DATABASE_USERS

class ProfileViewModel : ViewModel() {

    var nickname: MutableLiveData<String> = MutableLiveData()
    var profession: MutableLiveData<String> = MutableLiveData()
    var isUpdateClickable: MutableLiveData<Boolean> = MutableLiveData()
    var isSignOutClickable: MutableLiveData<Boolean> = MutableLiveData()
    var isProgressBarVisible: MutableLiveData<Boolean> = MutableLiveData()

    init {
        isProgressBarVisible.value = false
        isUpdateClickable.value = true
        isSignOutClickable.value = true
        val database = Firebase.database
        val username = Firebase.auth.currentUser!!.email!!.substringBefore(MainActivity.MAIL)
        database.getReference(DATABASE_USERS).child(username).child("nickname").get()
            .addOnSuccessListener {
                this.nickname.value = it.value.toString()
            }
        database.getReference(DATABASE_USERS).child(username).child("profession").get()
            .addOnSuccessListener {
                this.profession.value = it.value.toString()
            }
    }

    fun changeViewWhileLoading() {
        isUpdateClickable.value = false
        isSignOutClickable.value = false
        isProgressBarVisible.value = true
    }

    fun changeViewAfterLoading() {
        isUpdateClickable.value = true
        isSignOutClickable.value = true
        isProgressBarVisible.value = false
    }

    fun updateFields(nickname: String, profession: String) {
        changeViewWhileLoading()
        val database = Firebase.database
        val username = Firebase.auth.currentUser!!.email!!.substringBefore(MainActivity.MAIL)
        database.getReference(DATABASE_USERS).child(username).child("nickname")
            .setValue(nickname)
        database.getReference(DATABASE_USERS).child(username).child("profession")
            .setValue(profession)
        changeViewAfterLoading()
    }
}