package ge.gmikeladze.messenger.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ge.gmikeladze.messenger.R
import ge.gmikeladze.messenger.model.Message
import ge.gmikeladze.messenger.view.MainActivity.Companion.MAIL
import java.text.SimpleDateFormat
import java.util.*

class ConversationAdapter : RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    private var messages: List<Message> = listOf()
    private val currentUser = Firebase.auth.currentUser!!.email!!.substringBefore(MAIL)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.outgoing_message, parent, false)
        if (viewType == 1) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.incoming_message, parent, false)
        }
        return ConversationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].sender == currentUser) 0 else 1
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateMessages(messages: List<Message>) {
        this.messages = messages
        notifyDataSetChanged()
    }

    inner class ConversationViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        private val messageText = view.findViewById<TextView>(R.id.messageText)
        private val timeText = view.findViewById<TextView>(R.id.timeText)

        @SuppressLint("SimpleDateFormat")
        fun bind(message: Message) {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            messageText.text = message.message
            timeText.text = sdf.format(Date(message.time!!))
        }
    }
}