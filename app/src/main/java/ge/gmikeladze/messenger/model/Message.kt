package ge.gmikeladze.messenger.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Message(
    val sender: String? = null,
    val message: String? = null,
    val time: Long? = null
)