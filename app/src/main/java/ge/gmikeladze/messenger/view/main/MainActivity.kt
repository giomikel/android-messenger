package ge.gmikeladze.messenger.view.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ge.gmikeladze.messenger.R
import ge.gmikeladze.messenger.view.login.LoginActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        openActivity()
    }

    private fun openActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    companion object {
        const val PREFERENCE_NAME = "messengerAppPreferences"
    }
}