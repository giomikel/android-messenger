package ge.gmikeladze.messenger.view_model

import android.view.View
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
    var isProgressBarVisible: MutableLiveData<Int> = MutableLiveData()

    init {
        isSignUpClickable.value = true
        isProgressBarVisible.value = View.INVISIBLE
    }

    fun signUp(nickname: String, password: String, profession: String): Boolean {
        changeViewWhileLoading()
        val auth = Firebase.auth
        val database = Firebase.database
        val mail = nickname + MAIL
        var success = false
        auth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener {
            if (it.isSuccessful) {
                success = true
                database.getReference(DATABASE_USERS).child(nickname)
                    .setValue(User(nickname, profession))
            }
        }
        changeViewAfterLoading()
        return success
    }

    private fun changeViewWhileLoading() {
        isSignUpClickable.value = false
        isProgressBarVisible.value = View.VISIBLE
    }

    private fun changeViewAfterLoading() {
        isSignUpClickable.value = true
        isProgressBarVisible.value = View.INVISIBLE
    }

    companion object {
        const val SIGN_UP_SUCCESSFUL_MESSAGE = "SUCCESS"
    }
}