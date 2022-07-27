package ge.gmikeladze.messenger.model

data class Chat(
    val id: String? = null,
    val user1: String? = null,
    val user2: String? = null,
    val lastMessage: String? = null,
    val lastMessageTime: Long? = null
)