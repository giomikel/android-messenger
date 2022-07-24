package ge.gmikeladze.messenger.view_model_factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ge.gmikeladze.messenger.view_model.HomepageViewModel

class HomepageViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomepageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomepageViewModel() as T
        }
        throw IllegalStateException("Invalid View Model")
    }
}