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
import com.northis.votingapp.databinding.DrawerHeaderBinding
import com.northis.votingapp.di.App
import com.northis.votingapp.di.component.ActivityComponent
import com.northis.votingapp.voting.VotingViewModel
import com.northis.votingapp.voting.VotingViewModelFactory
import javax.inject.Inject

class AppActivity : AppCompatActivity() {
  // Binding
  private lateinit var binding: ActivityAppBinding
  private lateinit var bindingDrawerHeaderBinding: DrawerHeaderBinding

  // Navigation
  lateinit var navController: NavController
  private lateinit var navigationView: BottomNavigationView

  // Top AppBar
  private lateinit var topAppBar: MaterialToolbar

  // DI
  private lateinit var activityComponent: ActivityComponent

  // VM
  @Inject
  internal lateinit var votingViewModelFactory: VotingViewModelFactory
  val votingViewModel: VotingViewModel by viewModels(factoryProducer = { votingViewModelFactory })

  @Inject
  internal lateinit var appViewModelFactory: AppViewModelFactory
  val appViewModel: AppViewModel by viewModels(factoryProducer = { appViewModelFactory })


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
    val drawer = binding.drawerLayout
    bindingDrawerHeaderBinding = DrawerHeaderBinding.bind(binding.navView.inflateHeaderView(R.layout.drawer_header))
    appViewModel.loadUser().observe(this, {
      bindingDrawerHeaderBinding.user = it
      bindingDrawerHeaderBinding.executePendingBindings()
    })
    binding.navView.setNavigationItemSelectedListener {
      when (it.itemId) {

        R.id.mySpeeches -> {

          it.isChecked = true

          drawer.closeDrawers()

          true

        }

        R.id.myStatistic -> {

          it.isChecked = true

          drawer.closeDrawers()

          true

        }

        R.id.mySchedule -> {

          it.isChecked = true

          drawer.closeDrawers()

          true

        }

        R.id.nav_sing_out -> {

          onBackPressed()

          it.isChecked = false

          drawer.closeDrawers()

          true

        }

	else -> true

      }
    }
    val toggle = ActionBarDrawerToggle(
      this,
      drawer,
      topAppBar,
      R.string.navigation_drawer_open,
      R.string.navigation_drawer_close
    )
    drawer.addDrawerListener(toggle)
    val appBarConfiguration =
      AppBarConfiguration(
        setOf(R.id.votingFragment, R.id.catalogFragment, R.id.scheduleFragment), drawer
      )

    topAppBar.setupWithNavController(navController, appBarConfiguration)
    navigationView.setupWithNavController(navController)
  }

  override fun onBackPressed() {
    appViewModel.logout
  }
}
