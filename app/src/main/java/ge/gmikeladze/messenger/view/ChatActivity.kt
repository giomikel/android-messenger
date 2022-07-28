package ge.gmikeladze.messenger.view

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ge.gmikeladze.messenger.R
import ge.gmikeladze.messenger.adapter.ConversationAdapter
import ge.gmikeladze.messenger.databinding.ActivityChatBinding
import ge.gmikeladze.messenger.model.Message
import ge.gmikeladze.messenger.view.MainActivity.Companion.MAIL
import ge.gmikeladze.messenger.view.SearchActivity.Companion.CHAT_ID_EXTRA
import ge.gmikeladze.messenger.view.SearchActivity.Companion.SECOND_USER_EXTRA
import ge.gmikeladze.messenger.view_model.ChatViewModel
import ge.gmikeladze.messenger.view_model_factory.ChatViewModelFactory

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    private val viewModel: ChatViewModel by lazy {
        ViewModelProvider(
            this,
            ChatViewModelFactory()
        ).get(ChatViewModel::class.java)
    }

    private lateinit var id: String
    private lateinit var secondUser: String
    private val adapter = ConversationAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.model = viewModel
        binding.lifecycleOwner = this
        getExtras()
        setListeners()
        viewModel.initUser(secondUser)
        viewModel.avatarUrl.observe(this) {
            Glide.with(this).load(it).circleCrop().placeholder(R.drawable.avatar_image_placeholder)
                .into(binding.avatarImage)
        }
        setupRV()
        viewModel.messages.observe(this) {
            adapter.updateMessages(it)
            if (adapter.itemCount > 0) {
                binding.conversationRV.smoothScrollToPosition(adapter.itemCount - 1)
            }
        }
        loadConversation()
    }

    private fun getExtras() {
        secondUser = intent.getStringExtra(SECOND_USER_EXTRA).toString()
        id = intent.getStringExtra(CHAT_ID_EXTRA).toString()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners() {
        binding.backButton.setOnClickListener { onBackPressed() }
        binding.messageBox.setOnTouchListener(object : View.OnTouchListener {
            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                val drawableRight = 2
                if (event?.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >=
                        (binding.messageBox.right
                                - binding.messageBox.compoundDrawables[drawableRight].bounds.width()
                                - binding.messageBox.paddingRight
                                )
                    ) {
                        onSendPressed()
                        return true
                    }
                }
                return false
            }
        })
        binding.conversationRV.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom && adapter.itemCount > 0) {
                binding.conversationRV.smoothScrollToPosition(adapter.itemCount - 1)
            }
        }
    }

    private fun onSendPressed() {
        val message = binding.messageBox.text.toString()
        val currentUser = Firebase.auth.currentUser!!.email!!.substringBefore(MAIL)
        if (message.isNotEmpty()) {
            viewModel.sendMessage(
                id,
                secondUser,
                Message(currentUser, message, System.currentTimeMillis())
            )
            binding.messageBox.text.clear()
        } else {
            Toast.makeText(this, EMPTY_MESSAGE_TOAST, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRV() {
        binding.conversationRV.adapter = adapter
    }

    private fun loadConversation() {
        viewModel.setMessageListener(id)
        viewModel.status.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        viewModel.listenerStatus.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val EMPTY_MESSAGE_TOAST = "Message box is empty"
    }
}