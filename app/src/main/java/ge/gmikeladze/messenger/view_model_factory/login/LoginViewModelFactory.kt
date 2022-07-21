package ge.gmikeladze.messenger.view_model_factory.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ge.gmikeladze.messenger.view.main.MainActivity.Companion.PREFERENCE_NAME
import ge.gmikeladze.messenger.view_model.login.LoginViewModel

class LoginViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            val preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(
                preferences
            ) as T
        }
        throw IllegalStateException("Invalid View Model")
    }
}