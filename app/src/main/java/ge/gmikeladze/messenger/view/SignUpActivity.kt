package ge.gmikeladze.messenger.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import ge.gmikeladze.messenger.databinding.ActivitySignUpBinding
import ge.gmikeladze.messenger.view_model.LoginViewModel
import ge.gmikeladze.messenger.view_model.SignUpViewModel
import ge.gmikeladze.messenger.view_model_factory.SignUpViewModelFactory

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private val viewModel: SignUpViewModel by lazy {
        ViewModelProvider(
            this,
            SignUpViewModelFactory()
        ).get(SignUpViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.model = viewModel
        binding.lifecycleOwner = this
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.signUpButton.setOnClickListener {
            onSignUpClicked()
        }
    }

    private fun onSignUpClicked() {
        val nickname = binding.signUpUserIdentification.nicknameText.text.toString()
        val password = binding.signUpUserIdentification.passwordText.text.toString()
        val profession = binding.whatIDoText.text.toString()
        if (validate(nickname, password)) {
            val success = viewModel.signUp(nickname, password, profession)
            if (success) {
                onSuccessfulSignUp()
            } else {
//                onFailedSignUp("fail")
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

    private fun onSuccessfulSignUp() {
        val intent = Intent(this, HomepageActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun onFailedSignUp(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        binding.signUpUserIdentification.passwordText.setText("")
    }
}