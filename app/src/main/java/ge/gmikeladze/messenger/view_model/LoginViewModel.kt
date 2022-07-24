package ge.gmikeladze.messenger.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ge.gmikeladze.messenger.view.MainActivity.Companion.MAIL

class LoginViewModel : ViewModel() {

    var isSignInClickable: MutableLiveData<Boolean> = MutableLiveData()
    var isSignUpClickable: MutableLiveData<Boolean> = MutableLiveData()
    var isProgressBarVisible: MutableLiveData<Boolean> = MutableLiveData()
    var status: MutableLiveData<String> = MutableLiveData()

    init {
        isSignInClickable.value = true
        isSignUpClickable.value = true
        isProgressBarVisible.value = false
        status.value = ""
    }

    fun signIn(nickname: String, password: String) {
        changeViewWhileLoading()
        val auth = Firebase.auth
        val mail = nickname + MAIL
        auth.signInWithEmailAndPassword(mail, password).addOnCompleteListener {
            if (it.isSuccessful) {
                status.postValue(SIGN_IN_SUCCESSFUL_MESSAGE)
            } else {
                status.postValue(it.exception.toString())
            }
        }
        changeViewAfterLoading()
    }

    private fun changeViewWhileLoading() {
        isSignInClickable.value = false
        isSignUpClickable.value = false
        isProgressBarVisible.value = true
    }

    private fun changeViewAfterLoading() {
        isSignInClickable.value = true
        isSignUpClickable.value = true
        isProgressBarVisible.value = false
    }

    companion object {
        const val EMPTY_NICKNAME_ERROR = "Nickname field is empty"
        const val EMPTY_PASSWORD_ERROR = "Password field is empty"
        const val SIGN_IN_SUCCESSFUL_MESSAGE = "SUCCESS"
    }
}