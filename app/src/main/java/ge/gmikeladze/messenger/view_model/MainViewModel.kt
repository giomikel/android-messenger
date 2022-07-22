package ge.gmikeladze.messenger.view_model

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainViewModel : ViewModel() {

    fun isUserLogged(): Boolean {
        val auth = Firebase.auth
        return auth.currentUser != null
    }
}