package ge.gmikeladze.messenger.view.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import ge.gmikeladze.messenger.R
import ge.gmikeladze.messenger.databinding.ActivityLoginBinding
import ge.gmikeladze.messenger.view.sign_up.SignUpActivity
import ge.gmikeladze.messenger.view_model.login.LoginViewModel
import ge.gmikeladze.messenger.view_model_factory.login.LoginViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(
            this,
            LoginViewModelFactory(applicationContext)
        ).get(LoginViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.model = viewModel
        binding.lifecycleOwner = this
        binding.signUpButton.setOnClickListener {
            onSignUpClicked()
        }
    }

    private fun onSignUpClicked() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }
}