package ge.gmikeladze.messenger.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import ge.gmikeladze.messenger.databinding.ActivitySignUpBinding
import ge.gmikeladze.messenger.view_model.LoginViewModel
import ge.gmikeladze.messenger.view_model.LoginViewModel.Companion.SIGN_IN_SUCCESSFUL_MESSAGE
import ge.gmikeladze.messenger.view_model.SignUpViewModel
import ge.gmikeladze.messenger.view_model_factory.SignUpViewModelFactory

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private var toastMessage: Toast? = null

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
        if (validate(nickname, password, profession)) {
            viewModel.signUp(nickname, password, profession)
            viewModel.status.observe(this) {
                if (it == SIGN_IN_SUCCESSFUL_MESSAGE) {
                    onSuccessfulSignUp()
                } else if (it != "") {
                    onFailedSignUp(it)
                }
            }
        }
    }

    private fun validate(nickname: String, password: String, profession: String): Boolean {
        if (nickname.isEmpty()) {
            Toast.makeText(this, LoginViewModel.EMPTY_NICKNAME_ERROR, Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isEmpty()) {
            Toast.makeText(this, LoginViewModel.EMPTY_PASSWORD_ERROR, Toast.LENGTH_SHORT).show()
            return false
        }
        if (profession.isEmpty()) {
            Toast.makeText(this, SignUpViewModel.EMPTY_PROFESSION_ERROR, Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun onSuccessfulSignUp() {
        toastMessage?.cancel()
        val intent = Intent(this, HomepageActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun onFailedSignUp(message: String) {
        toastMessage?.cancel()
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        toastMessage?.show()
        binding.signUpUserIdentification.passwordText.setText("")
    }
}