package ge.gmikeladze.messenger.view_model_factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ge.gmikeladze.messenger.view_model.SignUpViewModel

class SignUpViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SignUpViewModel() as T
        }
        throw IllegalStateException("Invalid View Model")
    }
}