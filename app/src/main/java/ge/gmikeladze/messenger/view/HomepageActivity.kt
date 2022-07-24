package ge.gmikeladze.messenger.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import ge.gmikeladze.messenger.databinding.ActivityHomepageBinding
import ge.gmikeladze.messenger.view_model.HomepageViewModel
import ge.gmikeladze.messenger.view_model_factory.HomepageViewModelFactory

class HomepageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomepageBinding

    private val viewModel: HomepageViewModel by lazy {
        ViewModelProvider(
            this,
            HomepageViewModelFactory()
        ).get(HomepageViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomepageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.model = viewModel
        binding.lifecycleOwner = this
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.profileButton.setOnClickListener {
            onProfileButtonClicked()
        }
    }

    private fun onProfileButtonClicked() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {}
}