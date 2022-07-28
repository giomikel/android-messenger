package ge.gmikeladze.messenger.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import ge.gmikeladze.messenger.R
import ge.gmikeladze.messenger.view_model.MainViewModel
import ge.gmikeladze.messenger.view_model_factory.MainViewModelFactory

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(
            this,
            MainViewModelFactory()
        ).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        openActivity()
    }

    private fun openActivity() {
        val activityClass =
            if (viewModel.isUserLogged()) HomepageActivity::class.java else LoginActivity::class.java
        val intent = Intent(this, activityClass)
        startActivity(intent)
        finish()
    }

    companion object {
        const val MAIL = "@test.com"
        const val DATABASE_USERS = "users"
        const val DATABASE_CHATS = "chats"
        const val DATABASE_CONVERSATIONS = "conversations"
    }
}