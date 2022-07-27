package ge.gmikeladze.messenger.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Tasks.whenAllComplete
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import ge.gmikeladze.messenger.model.Chat
import ge.gmikeladze.messenger.model.User
import ge.gmikeladze.messenger.view.MainActivity
import ge.gmikeladze.messenger.view.MainActivity.Companion.DATABASE_USERS

class SearchViewModel : ViewModel() {

    var searchItems: MutableLiveData<List<User>> = MutableLiveData()
    var isProgressBarVisible: MutableLiveData<Boolean> = MutableLiveData()
    var searchStatus: MutableLiveData<String> = MutableLiveData()
    var clickStatus: MutableLiveData<String> = MutableLiveData()
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
                        searchStatus.postValue(error.message)
                        isLoading = false
                        isProgressBarVisible.postValue(false)
                    }
                })
        }
    }

    fun onSearchUserClicked(user: User) {
        val currentUser = Firebase.auth.currentUser!!.email!!.substringBefore(MainActivity.MAIL)
        val database = Firebase.database
        database.getReference(MainActivity.DATABASE_CONVERSATIONS).push().get()
            .addOnSuccessListener { conversationSnapshot ->
                database.getReference(DATABASE_USERS).orderByChild("nickname")
                    .equalTo(user.nickname)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var secondUser = ""
                            snapshot.children.forEach {
                                secondUser = it.key.toString()
                            }
                            val second =
                                database.getReference(MainActivity.DATABASE_CHATS).child(secondUser)
                                    .child(currentUser).setValue(
                                        Chat(
                                            conversationSnapshot.key,
                                            secondUser,
                                            currentUser
                                        )
                                    )
                            val current = database.getReference(MainActivity.DATABASE_CHATS)
                                .child(currentUser)
                                .child(secondUser).setValue(
                                    Chat(
                                        conversationSnapshot.key,
                                        currentUser,
                                        secondUser
                                    )
                                )
                            whenAllComplete(second, current).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    var key = currentUser + secondUser
                                    if (secondUser < currentUser) {
                                        key = secondUser + currentUser
                                    }
                                    clickStatus.postValue(CHAT_SUCCESS + secondUser + SEPARATOR + key)
                                } else {
                                    clickStatus.postValue(CHAT_FAIL + it.exception.toString())
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            clickStatus.postValue(error.message)
                        }
                    })
            }
    }

    companion object {
        const val CHAT_SUCCESS = "SUCCESS"
        const val CHAT_FAIL = "FAIL"
        const val SEPARATOR = ";"
    }
}