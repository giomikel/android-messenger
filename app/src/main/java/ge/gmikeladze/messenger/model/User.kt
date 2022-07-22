package ge.gmikeladze.messenger.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    val nickname: String? = null,
    val profession: String? = null,
    val photo: String? = null
)
