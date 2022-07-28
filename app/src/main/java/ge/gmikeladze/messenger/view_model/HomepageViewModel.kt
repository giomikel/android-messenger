package ge.gmikeladze.messenger.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import ge.gmikeladze.messenger.model.Chat
import ge.gmikeladze.messenger.model.User
import ge.gmikeladze.messenger.view.MainActivity
import ge.gmikeladze.messenger.view.MainActivity.Companion.DATABASE_CHATS
import ge.gmikeladze.messenger.view.MainActivity.Companion.DATABASE_USERS

class HomepageViewModel : ViewModel() {

    var contacts: MutableLiveData<List<Chat>> = MutableLiveData()
    var contactsList: MutableList<Chat> = mutableListOf()
    var isProgressBarVisible: MutableLiveData<Boolean> = MutableLiveData()
    var status: MutableLiveData<String> = MutableLiveData()

    init {
        isProgressBarVisible.value = false
    }

    fun setChatListener() {
        val database = Firebase.database
        val currentUser = Firebase.auth.currentUser!!.email!!.substringBefore(MainActivity.MAIL)
        isProgressBarVisible.value = true
        database.getReference(DATABASE_CHATS).child(currentUser)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val data = snapshot.getValue<Chat>()
                    if (data != null) {
                        addChatToList(data)
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onChildRemoved(snapshot: DataSnapshot) {}

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onCancelled(error: DatabaseError) {
                    status.postValue(error.message)
                }
            })
        database.getReference(DATABASE_CHATS).child(currentUser)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    isProgressBarVisible.value = false
                }

                override fun onCancelled(error: DatabaseError) {
                    status.postValue(error.message)
                }
            })
    }

    fun loadChats() {
        val database = Firebase.database
        val currentUser = Firebase.auth.currentUser!!.email!!.substringBefore(MainActivity.MAIL)
        isProgressBarVisible.value = true
        database.getReference(DATABASE_CHATS).child(currentUser)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.getValue<Map<String, Chat>>()
                    if (data != null) {
                        contactsList =
                            data.values.toMutableList()
                        sortContacts()
                    } else {
                        contactsList = mutableListOf()
                    }
                    contacts.value = contactsList
                    isProgressBarVisible.postValue(false)
                }

                override fun onCancelled(error: DatabaseError) {
                    status.postValue(error.message)
                    isProgressBarVisible.postValue(false)
                }
            })
    }

    fun loadChatsBy(filter: String) {
        val database = Firebase.database
        val currentUser = Firebase.auth.currentUser!!.email!!.substringBefore(MainActivity.MAIL)
        isProgressBarVisible.postValue(true)
        contactsList.clear()
        database.getReference(DATABASE_USERS).orderByChild("nickname").startAt(filter)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val users = snapshot.getValue<Map<String, User>>()
                    if (users != null) {
                        for ((key, user) in users) {
                            if (user.nickname!!.startsWith(filter)) {
                                database.getReference(DATABASE_CHATS).child(currentUser)
                                    .addListenerForSingleValueEvent(object :
                                        ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            val data = snapshot.getValue<Map<String, Chat>>()
                                            if (data != null) {
                                                val temp =
                                                    data.values.toMutableList()
                                                for (i in temp) {
                                                    if (i.user2 == key && !contactsList.contains(i)) {
                                                        contactsList.add(i)
                                                    }
                                                }
                                                sortContacts()
                                            } else {
                                                contactsList = mutableListOf()
                                            }
                                            contacts.postValue(contactsList)
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            status.postValue(error.message)
                                        }
                                    })
                            }
                        }
                    }
                    isProgressBarVisible.postValue(false)
                }

                override fun onCancelled(error: DatabaseError) {
                    isProgressBarVisible.postValue(false)
                }
            })
    }

    private fun addChatToList(chat: Chat) {
        if (!contactsList.contains(chat)) {
            contactsList.add(chat)
        }
        sortContacts()
        contacts.value = contactsList
    }

    private fun sortContacts() {
        contactsList.sortWith { m1, m2 ->
            if (m1.lastMessageTime == null) {
                1
            } else if (m2.lastMessageTime == null) {
                -1
            } else if (m1.lastMessageTime > m2.lastMessageTime) -1 else 1
        }
    }

}