package ge.gmikeladze.messenger.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.ktx.storage
import ge.gmikeladze.messenger.databinding.ActivityProfileBinding
import ge.gmikeladze.messenger.view_model.LoginViewModel
import ge.gmikeladze.messenger.view_model.ProfileViewModel
import ge.gmikeladze.messenger.view_model.SignUpViewModel
import ge.gmikeladze.messenger.view_model_factory.ProfileViewModelFactory

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private val viewModel: ProfileViewModel by lazy {
        ViewModelProvider(
            this,
            ProfileViewModelFactory()
        ).get(ProfileViewModel::class.java)
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                Glide.with(this).load(imageUri).circleCrop().into(binding.avatarImage)
                if (result.data != null) {
                    Firebase.storage.getReference("avatars/${Firebase.auth.currentUser!!.email}")
                        .putFile(result.data!!.data!!)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.model = viewModel
        binding.lifecycleOwner = this
        setOnClickListeners()
        fetchAvatar()
    }

    private fun setOnClickListeners() {
        binding.homeButton.setOnClickListener {
            onHomeButtonClicked()
        }
        binding.updateButton.setOnClickListener {
            onUpdateClicked()
        }
        binding.signOutButton.setOnClickListener {
            onSignOutClicked()
        }
        binding.avatarImage.setOnClickListener {
            onAvatarClicked()
        }
    }

    private fun fetchAvatar() {
        val cloudStorage = Firebase.storage
        val auth = Firebase.auth
        val image = cloudStorage.reference.child("avatars/${auth.currentUser!!.email}")
        viewModel.changeViewWhileLoading()
        image.getBytes(Long.MAX_VALUE).addOnCompleteListener {
            if (it.isSuccessful) {
                Glide.with(this).load(it.result).circleCrop().into(binding.avatarImage)
            } else if ((it.exception as StorageException).errorCode != StorageException.ERROR_OBJECT_NOT_FOUND) {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
            viewModel.changeViewAfterLoading()
        }
    }

    private fun onHomeButtonClicked() {
        finish()
    }

    private fun onUpdateClicked() {
        val nickname = binding.nameText.text.toString()
        val profession = binding.professionText.text.toString()
        if (validate(nickname, profession)) {
            viewModel.updateFields(nickname, profession)
            Toast.makeText(this, UPDATE_TEXT, Toast.LENGTH_SHORT).show()
        }
    }

    private fun validate(nickname: String, profession: String): Boolean {
        if (nickname.isEmpty()) {
            Toast.makeText(this, LoginViewModel.EMPTY_NICKNAME_ERROR, Toast.LENGTH_SHORT).show()
            return false
        }
        if (profession.isEmpty()) {
            Toast.makeText(this, SignUpViewModel.EMPTY_PROFESSION_ERROR, Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun onSignOutClicked() {
        val auth = Firebase.auth
        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun onAvatarClicked() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        resultLauncher.launch(intent)
    }

    companion object {
        const val UPDATE_TEXT = "Updated"
    }
}