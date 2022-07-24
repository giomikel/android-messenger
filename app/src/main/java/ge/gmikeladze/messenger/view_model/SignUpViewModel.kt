package ge.gmikeladze.messenger.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ge.gmikeladze.messenger.model.User
import ge.gmikeladze.messenger.view.MainActivity.Companion.DATABASE_USERS
import ge.gmikeladze.messenger.view.MainActivity.Companion.MAIL

class SignUpViewModel : ViewModel() {
    var isSignUpClickable: MutableLiveData<Boolean> = MutableLiveData()
    var isProgressBarVisible: MutableLiveData<Boolean> = MutableLiveData()
    var status: MutableLiveData<String> = MutableLiveData()

    init {
        isSignUpClickable.value = true
        isProgressBarVisible.value = false
        status.value = ""
    }

    fun signUp(nickname: String, password: String, profession: String) {
        changeViewWhileLoading()
        val auth = Firebase.auth
        val database = Firebase.database
        val mail = nickname + MAIL
        auth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener {
            if (it.isSuccessful) {
                database.getReference(DATABASE_USERS).child(nickname)
                    .setValue(User(nickname, profession))
                status.postValue(SIGN_UP_SUCCESSFUL_MESSAGE)
            } else {
                status.postValue(it.exception.toString())
            }
        }
        changeViewAfterLoading()
    }

    private fun changeViewWhileLoading() {
        isSignUpClickable.value = false
        isProgressBarVisible.value = true
    }

    private fun changeViewAfterLoading() {
        isSignUpClickable.value = true
        isProgressBarVisible.value = false
    }

    companion object {
        const val SIGN_UP_SUCCESSFUL_MESSAGE = "SUCCESS"
    }
}