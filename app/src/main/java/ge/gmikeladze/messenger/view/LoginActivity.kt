package ge.gmikeladze.messenger.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import ge.gmikeladze.messenger.databinding.ActivityLoginBinding
import ge.gmikeladze.messenger.view_model.LoginViewModel
import ge.gmikeladze.messenger.view_model.LoginViewModel.Companion.SIGN_IN_SUCCESSFUL_MESSAGE
import ge.gmikeladze.messenger.view_model_factory.LoginViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(
            this,
            LoginViewModelFactory()
        ).get(LoginViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.model = viewModel
        binding.lifecycleOwner = this
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.signInButton.setOnClickListener {
            onSignInClicked()
        }
        binding.signUpButton.setOnClickListener {
            onSignUpClicked()
        }
    }

    private fun onSignUpClicked() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    private fun onSignInClicked() {
        val nickname = binding.loginUserIdentification.nicknameText.text.toString()
        val password = binding.loginUserIdentification.passwordText.text.toString()
        if (validate(nickname, password)) {
            viewModel.signIn(nickname, password)
            viewModel.status.observe(this) {
                if (it == SIGN_IN_SUCCESSFUL_MESSAGE) {
                    onSuccessfulSignIn()
                } else if (it != "") {
                    onFailedSignIn(it)
                }
            }
        }
    }

    private fun validate(nickname: String, password: String): Boolean {
        if (nickname.isEmpty()) {
            Toast.makeText(this, LoginViewModel.EMPTY_NICKNAME_ERROR, Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isEmpty()) {
            Toast.makeText(this, LoginViewModel.EMPTY_PASSWORD_ERROR, Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun onSuccessfulSignIn() {
        val intent = Intent(this, HomepageActivity::class.java)
        startActivity(intent)
    }

    private fun onFailedSignIn(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        binding.loginUserIdentification.passwordText.setText("")
    }
}