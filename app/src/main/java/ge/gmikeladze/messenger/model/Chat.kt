package ge.gmikeladze.messenger.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Chat(
    val id: String? = null,
    val user1: String? = null,
    val user2: String? = null,
    val lastMessage: String? = null,
    val lastMessageTime: Long? = null
)