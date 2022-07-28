package ge.gmikeladze.messenger.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import ge.gmikeladze.messenger.R
import ge.gmikeladze.messenger.databinding.ContactItemBinding
import ge.gmikeladze.messenger.model.Chat
import ge.gmikeladze.messenger.model.User
import ge.gmikeladze.messenger.view.MainActivity
import java.text.SimpleDateFormat
import java.util.*

class ContactAdapter : RecyclerView.Adapter<ContactAdapter.ContactItemViewHolder>() {

    var contacts: List<Chat> = listOf()
    var onItemClickListener: ContactItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactItemViewHolder {
        return ContactItemViewHolder(
            ContactItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ContactItemViewHolder, position: Int) {
        holder.bind(contacts[position])
        holder.binding.root.setOnClickListener {
            onItemClickListener!!.onItemClicked(contacts[position])
        }
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateContacts(contacts: List<Chat>) {
        this.contacts = contacts
        notifyDataSetChanged()
    }

    inner class ContactItemViewHolder(val binding: ContactItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: Chat) {
            Firebase.database.getReference(MainActivity.DATABASE_USERS).child(chat.user2!!).get()
                .addOnSuccessListener {
                    val user = it.getValue<User>()
                    binding.nameText.text = user?.nickname
                    Glide.with(binding.root).load(user?.avatarUrl)
                        .placeholder(R.drawable.avatar_image_placeholder).circleCrop()
                        .into(binding.avatarImage)
                }
            var timeDifference =
                System.currentTimeMillis() - (chat.lastMessageTime ?: 0)
            val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
            if (timeDifference < 3600000) {
                timeDifference /= 60000
                val timeText = "$timeDifference min"
                binding.timeText.text = timeText
            } else if (timeDifference < 86400000) {
                timeDifference /= 3600000
                val timeText = "$timeDifference hour"
                binding.timeText.text = timeText
            } else {
                binding.timeText.text =
                    sdf.format(Date(chat.lastMessageTime ?: System.currentTimeMillis()))
            }
            binding.lastMessage.text = chat.lastMessage
        }
    }

    interface ContactItemClickListener {
        fun onItemClicked(chat: Chat)
    }
}