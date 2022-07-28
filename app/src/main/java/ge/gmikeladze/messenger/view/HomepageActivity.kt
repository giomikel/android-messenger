package ge.gmikeladze.messenger.view

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ge.gmikeladze.messenger.adapter.ContactAdapter
import ge.gmikeladze.messenger.databinding.ActivityHomepageBinding
import ge.gmikeladze.messenger.model.Chat
import ge.gmikeladze.messenger.view_model.HomepageViewModel
import ge.gmikeladze.messenger.view_model_factory.HomepageViewModelFactory
import java.util.*

class HomepageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomepageBinding

    private val viewModel: HomepageViewModel by lazy {
        ViewModelProvider(
            this,
            HomepageViewModelFactory()
        ).get(HomepageViewModel::class.java)
    }

    private val contactItemClickListener = object : ContactAdapter.ContactItemClickListener {
        override fun onItemClicked(chat: Chat) {
            val intent = Intent(this@HomepageActivity, ChatActivity::class.java).apply {
                putExtra(SearchActivity.SECOND_USER_EXTRA, chat.user2)
                putExtra(SearchActivity.CHAT_ID_EXTRA, chat.id)
            }
            startActivity(intent)
        }

    }

    private val adapter = ContactAdapter()
    private var afterFirstAccess = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomepageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.model = viewModel
        binding.lifecycleOwner = this
        setOnClickListeners()
        setupRV()
        viewModel.contacts.observe(this) {
            adapter.updateContacts(it)
            if (adapter.itemCount > 0) {
                binding.contactRV.smoothScrollToPosition(adapter.itemCount - 1)
            } else {
                Toast.makeText(this@HomepageActivity, CHAT_LIST_EMPTY_MESSAGE, Toast.LENGTH_SHORT)
                    .show()
            }
        }
        loadChats()
        setTextListener()
    }

    private fun setOnClickListeners() {
        binding.profileButton.setOnClickListener {
            onProfileButtonClicked()
        }
        binding.fab.setOnClickListener {
            onFabClicked()
        }
    }

    private fun onProfileButtonClicked() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun onFabClicked() {
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        val auth = Firebase.auth
        if (auth.currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.searchBar.setText("")
        if (afterFirstAccess) {
            viewModel.loadChats()
        } else afterFirstAccess = true
    }

    private fun setupRV() {
        adapter.onItemClickListener = contactItemClickListener
        binding.contactRV.adapter = adapter
    }

    private fun loadChats() {
        viewModel.setChatListener()
        viewModel.status.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setTextListener() {
        var timer = Timer()
        val delay: Long = 600
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                timer.cancel()
                timer.purge()
            }

            override fun afterTextChanged(p0: Editable?) {
                timer = Timer()
                timer.schedule(object : TimerTask() {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun run() {
                        viewModel.loadChatsBy(p0.toString())
                        runOnUiThread { adapter.notifyDataSetChanged() }
                    }
                }, delay)
            }

        })
    }

    companion object {
        const val CHAT_LIST_EMPTY_MESSAGE = "Chat list empty"
    }
}