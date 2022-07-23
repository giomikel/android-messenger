package ge.gmikeladze.messenger.view_model

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ge.gmikeladze.messenger.view.MainActivity.Companion.MAIL

class LoginViewModel : ViewModel() {

    var isSignInClickable: MutableLiveData<Boolean> = MutableLiveData()
    var isSignUpClickable: MutableLiveData<Boolean> = MutableLiveData()
    var isProgressBarVisible: MutableLiveData<Int> = MutableLiveData()
    var status: MutableLiveData<String> = MutableLiveData()

    init {
        isSignInClickable.value = true
        isSignUpClickable.value = true
        isProgressBarVisible.value = View.INVISIBLE
        status.value = ""
    }

    fun signIn(nickname: String, password: String) {
        changeViewWhileLoading()
        val auth = Firebase.auth
        val mail = nickname + MAIL
        auth.signInWithEmailAndPassword(mail, password).addOnCompleteListener {
            if (it.isSuccessful) {
                status.value = SIGN_IN_SUCCESSFUL_MESSAGE
            } else {
                status.value = it.exception.toString()
            }
        }
        changeViewAfterLoading()
    }

    private fun changeViewWhileLoading() {
        isSignInClickable.value = false
        isSignUpClickable.value = false
        isProgressBarVisible.value = View.VISIBLE
    }

    private fun changeViewAfterLoading() {
        isSignInClickable.value = true
        isSignUpClickable.value = true
        isProgressBarVisible.value = View.INVISIBLE
    }

    companion object {
        const val EMPTY_NICKNAME_ERROR = "Nickname field is empty"
        const val EMPTY_PASSWORD_ERROR = "Password field is empty"
        const val SIGN_IN_SUCCESSFUL_MESSAGE = "SUCCESS"
    }
}