package ge.gmikeladze.messenger.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import ge.gmikeladze.messenger.model.Chat
import ge.gmikeladze.messenger.model.Message
import ge.gmikeladze.messenger.model.User
import ge.gmikeladze.messenger.view.MainActivity.Companion.DATABASE_CHATS
import ge.gmikeladze.messenger.view.MainActivity.Companion.DATABASE_CONVERSATIONS

class ChatViewModel : ViewModel() {

    var nickname: MutableLiveData<String> = MutableLiveData()
    var profession: MutableLiveData<String> = MutableLiveData()
    var avatarUrl: MutableLiveData<String> = MutableLiveData()
    var isProgressBarVisible: MutableLiveData<Boolean> = MutableLiveData()
    var messages: MutableLiveData<List<Message>> = MutableLiveData()
    var status: MutableLiveData<String> = MutableLiveData()
    var listenerStatus: MutableLiveData<String> = MutableLiveData()
    private var messagesList = mutableListOf<Message>()

    init {
        messages.value = listOf()
        isProgressBarVisible.value = false
    }

    fun initUser(username: String) {
        val database = Firebase.database
        database.getReference("users").child(username).get().addOnSuccessListener {
            val user = it.getValue<User>()
            nickname.postValue(user?.nickname)
            profession.postValue(user?.profession)
            avatarUrl.postValue(user?.avatarUrl)
        }
    }

    fun sendMessage(id: String, receiver: String, message: Message) {
        val database = Firebase.database
        database.getReference(DATABASE_CONVERSATIONS).child(id).push().setValue(message)
        database.getReference(DATABASE_CHATS).child(receiver).child(message.sender!!)
            .setValue(Chat(id, receiver, message.sender, message.message, message.time))
        database.getReference(DATABASE_CHATS).child(message.sender).child(receiver)
            .setValue(Chat(id, message.sender, receiver, message.message, message.time))
    }

    fun loadMessages(id: String) {
        val database = Firebase.database
        isProgressBarVisible.value = true
        database.getReference(DATABASE_CONVERSATIONS).orderByKey().equalTo(id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.getValue<Map<String, Map<String, Message>>>()
                    if (data != null) {
                        messagesList =
                            data.iterator().next().value.values.toList() as MutableList<Message>
                        messagesList.sortWith { m1, m2 -> if (m1!!.time!! > m2!!.time!!) 1 else -1 }
                        messages.value = messagesList
                    }
                    isProgressBarVisible.postValue(false)
                }

                override fun onCancelled(error: DatabaseError) {
                    status.postValue(error.message)
                    isProgressBarVisible.postValue(false)
                }

            })
    }

    fun setMessageListener(id: String) {
        val database = Firebase.database
        database.getReference(DATABASE_CONVERSATIONS).child(id)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val data = snapshot.getValue<Message>()
                    if (data != null) {
                        addMessageToList(data)
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    loadMessages(id)
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {}

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onCancelled(error: DatabaseError) {
                    listenerStatus.postValue(error.message)
                }

            })
    }

    private fun addMessageToList(message: Message) {
        messagesList.add(message)
        messages.value = messagesList
    }
}