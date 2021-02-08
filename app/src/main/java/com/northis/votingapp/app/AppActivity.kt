package com.northis.votingapp.app

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.northis.votingapp.R
import com.northis.votingapp.databinding.ActivityAppBinding
import com.northis.votingapp.di.App
import com.northis.votingapp.di.component.ActivityComponent
import com.northis.votingapp.voting.VotingViewModel
import com.northis.votingapp.voting.VotingViewModelFactory
import javax.inject.Inject

class AppActivity : AppCompatActivity() {
  // Binding
  private lateinit var binding: ActivityAppBinding

  // Navigation
  lateinit var navController: NavController
  private lateinit var navigationView: BottomNavigationView

  // Top AppBar
  private lateinit var topAppBar: MaterialToolbar

  // DI
  private lateinit var activityComponent: ActivityComponent

  @Inject
  internal lateinit var votingViewModelFactory: VotingViewModelFactory
  val votingViewModel: VotingViewModel by viewModels(factoryProducer = { votingViewModelFactory })


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityAppBinding.inflate(layoutInflater)
    setContentView(binding.root)
    activityComponent = (applicationContext as App).activityComponent
    activityComponent.inject(this)

    navController = Navigation.findNavController(this, R.id.nav_host)
    navigationView = binding.bottomNavigation
    topAppBar = binding.topAppBar
    setSupportActionBar(topAppBar)
//    val drawer = binding.drawerLayout
//    val toggle = ActionBarDrawerToggle(
//      this,
//      drawer,
//      topAppBar,
//      R.string.navigation_drawer_open,
//      R.string.navigation_drawer_close
//    )
//    drawer.addDrawerListener(toggle)
    val appBarConfiguration =
      AppBarConfiguration(
        setOf(R.id.votingFragment, R.id.catalogFragment, R.id.scheduleFragment)
      )

    topAppBar.setupWithNavController(navController, appBarConfiguration)
    navigationView.setupWithNavController(navController)
  }

}
