package com.northis.votingapp.authorization

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.northis.votingapp.app.AppActivity
import com.northis.votingapp.databinding.ActivityAuthorizationBinding
import com.northis.votingapp.di.App
import javax.inject.Inject

class AuthorizationActivity : AppCompatActivity(), IAuthorizationEventHandler {
  private lateinit var _binding: ActivityAuthorizationBinding

  @Inject
  internal lateinit var authorizationViewModelFactory: AuthorizationViewModel.AuthorizationViewModelFactory
  private val authorizationViewModel: AuthorizationViewModel by viewModels(factoryProducer = { authorizationViewModelFactory })
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    _binding = ActivityAuthorizationBinding.inflate(layoutInflater)
    setContentView(_binding.root)
    // DI
    (applicationContext as App).authenticationComponent.inject(this)
    lifecycleScope.launchWhenCreated {
      try {
	authorizationViewModel.checkAuthority(_binding.webView, this@AuthorizationActivity)
      } catch (e: Exception) {
	_binding.progressBar.visibility = View.GONE
	with(
	  Snackbar.make(
	    _binding.scheduleLayout as View,
	    e.message!!,
	    Snackbar.LENGTH_INDEFINITE
	  )
	) {
	  setAction("Повтор")
	  {
	    _binding.progressBar.visibility = View.VISIBLE
	    startActivity(Intent(this@AuthorizationActivity, AuthorizationActivity::class.java))
	  }
	  show()
	}

      }

    }
  }

  override fun onTokenAcquired() {
    _binding.webView.visibility = View.GONE
    _binding.txtWelcome.visibility = View.VISIBLE
    startActivity(Intent(this, AppActivity::class.java))
  }

  override fun onTokenSuccess() {
    startActivity(Intent(this, AppActivity::class.java))
  }


  override fun onAuthorizationBegin() {
    _binding.txtWelcome.visibility = View.GONE
    _binding.progressBar.visibility = View.GONE
    _binding.webView.visibility = View.VISIBLE
  }
}

