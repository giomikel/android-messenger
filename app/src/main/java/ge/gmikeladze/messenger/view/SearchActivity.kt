package ge.gmikeladze.messenger.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import ge.gmikeladze.messenger.adapter.SearchItemAdapter
import ge.gmikeladze.messenger.adapter.SearchItemClickListener
import ge.gmikeladze.messenger.databinding.ActivitySearchBinding
import ge.gmikeladze.messenger.model.User
import ge.gmikeladze.messenger.view_model.SearchViewModel
import ge.gmikeladze.messenger.view_model_factory.SearchViewModelFactory
import kotlinx.coroutines.delay
import java.util.*

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    private val viewModel: SearchViewModel by lazy {
        ViewModelProvider(
            this,
            SearchViewModelFactory()
        ).get(SearchViewModel::class.java)
    }

    private val searchItemClickListener = object : SearchItemClickListener {
        override fun onItemClicked(user: User) {
            TODO("Not yet implemented")
        }

    }

    private lateinit var adapter: SearchItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.model = viewModel
        binding.lifecycleOwner = this
        setupRV()
        setOnClickListeners()
        setTextListener()
    }

    private fun setupRV() {
        adapter = SearchItemAdapter()
        adapter.onItemClickListener = searchItemClickListener
        binding.searchRV.adapter = adapter
    }

    private fun setOnClickListeners() {
        binding.backButton.setOnClickListener {
            onBackPressed()
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
                if (p0?.length!! > 2) {
                    timer = Timer()
                    timer.schedule(object : TimerTask() {
                        override fun run() {
                            viewModel.searchUsers(p0.toString())
                        }
                    }, delay)
                    viewModel.searchItems.observe(this@SearchActivity) {
                        adapter.updateSearchItems(it)
                    }
                    viewModel.status.observe(this@SearchActivity) {
                        Toast.makeText(this@SearchActivity, it.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

        })
    }
}