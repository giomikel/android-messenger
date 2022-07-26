package ge.gmikeladze.messenger.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import ge.gmikeladze.messenger.model.User
import ge.gmikeladze.messenger.view.MainActivity.Companion.DATABASE_USERS

class SearchViewModel : ViewModel() {

    var searchItems: MutableLiveData<List<User>> = MutableLiveData()
    var isProgressBarVisible: MutableLiveData<Boolean> = MutableLiveData()
    var status: MutableLiveData<String> = MutableLiveData()
    private var isLoading: Boolean = false

    init {
        isProgressBarVisible.value = false
    }

    fun searchUsers(text: String) {
        val database = Firebase.database
        isProgressBarVisible.postValue(true)
        if (!isLoading) {
            isLoading = true
            database.getReference(DATABASE_USERS).orderByChild("nickname").startAt(text)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val users = snapshot.getValue<Map<String, User>>()!!.values.toList()
                        val result: MutableList<User> = mutableListOf()
                        for (user in users) {
                            if (user.nickname!!.startsWith(text)) {
                                result.add(user)
                            }
                        }
                        searchItems.postValue(result)
                        isLoading = false
                        isProgressBarVisible.postValue(false)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        status.postValue(error.message)
                        isLoading = false
                        isProgressBarVisible.postValue(false)
                    }
                })
        }
    }
}