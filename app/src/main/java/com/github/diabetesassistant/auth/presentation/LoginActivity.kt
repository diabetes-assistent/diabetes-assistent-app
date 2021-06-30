package com.github.diabetesassistant.auth.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.github.diabetesassistant.BuildConfig
import com.github.diabetesassistant.R
import com.github.diabetesassistant.auth.data.AuthClient
import com.github.diabetesassistant.auth.domain.AuthService
import com.github.diabetesassistant.auth.domain.Token
import com.github.diabetesassistant.auth.domain.User
import com.github.diabetesassistant.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    private val baseUrl = "https://live-diabetes-assistant-be.herokuapp.com/"
    private val service = AuthService(AuthClient(baseUrl), BuildConfig.ID_TOKEN_SECRET)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.loginEmailAddress.doOnTextChanged(this::setEmailState)
        binding.loginPassword.doOnTextChanged(this::setPasswordState)
        binding.loginSubmitButton.setOnClickListener(this::handleSubmit)
        binding.registerButton.setOnClickListener(this::goToRegisterActivity)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleSubmit(view: View) {
        val errorSnackbar = Snackbar.make(binding.container, R.string.login_failed, Snackbar.LENGTH_LONG)
        if (loginViewModel.isInvalid()) {
            errorSnackbar.show()
        } else {
            lifecycleScope.launch(Dispatchers.IO) {
                val user = User(loginViewModel.email.value.toString(), loginViewModel.password.value.toString())
                val loginResult: Result<Token> = service.login(user)
                loginResult.fold(storeToken(binding.container), handleError(binding.container))
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun goToRegisterActivity(view: View) {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    @Suppress("UNUSED_PARAMETER")
    private fun setEmailState(chars: CharSequence?, a: Int, b: Int, c: Int) {
        this.loginViewModel.email.value = chars.toString()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun setPasswordState(chars: CharSequence?, a: Int, b: Int, c: Int) {
        this.loginViewModel.password.value = chars.toString()
    }

    private fun storeToken(view: View): (Token) -> Unit {
        return { _: Token ->
            Snackbar.make(view, R.string.login_success, Snackbar.LENGTH_LONG).show()
            Log.i("Login", "Successfully logged in")
        }
    }

    private fun handleError(view: View): (Throwable) -> Unit {
        return { error: Throwable ->
            Snackbar.make(view, R.string.login_failed, Snackbar.LENGTH_LONG).show()
            Log.e("Login", error.stackTraceToString())
        }
    }
}
