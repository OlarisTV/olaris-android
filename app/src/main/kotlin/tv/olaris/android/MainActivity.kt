package tv.olaris.android

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.navigateUp
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import tv.olaris.android.databases.Server

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val mainViewModel: MainViewModel by viewModel()

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private val serverIdList: MutableList<Int> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.initTimer().collect()
            }
        }

        val navFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navFragment.navController

        drawerLayout = findViewById(R.id.drawer_layout)

        val navView = findViewById<NavigationView>(R.id.navigation_view)

        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        setupActionBarWithNavController(this, navController, appBarConfiguration)

        navView.setNavigationItemSelectedListener(this)

        val menu = navView.menu

        lifecycleScope.launch {
            mainViewModel.allServers().collect {
                Log.d("refreshDebug", "Received a collection of servers")
                // There is a change in the serverList, update all menu items
                for (id in serverIdList) {
                    menu.removeGroup(id)
                }
                serverIdList.clear()

                for (s in it) {
                    Log.d("refreshDebug", "Looping server $s.id")

                    serverIdList.add(s.id)

                    if (mainViewModel.newCheckServer(s)) {
                        menu.removeGroup(s.id)

                        if (it.size > 1) {
                            menu.add(s.id, 1, 0, "Dashboard").setOnMenuItemClickListener {
                                navigateTo(R.id.dashboard, s)
                                true
                            }
                        }

                        menu.add(s.id, 2, 0, "Movies").setOnMenuItemClickListener {
                            navigateTo(R.id.movieLibraryFragment, s)
                            true
                        }

                        menu.add(s.id, 3, 0, "TV Shows").setOnMenuItemClickListener {
                            navigateTo(R.id.fragmentShowLibrary, s)
                            true
                        }
                    } else {
                        menu.removeGroup(s.id)
                    }
                }

                if (it.isEmpty() && !OlarisApplication.applicationContext().initialNavigation) {
                    OlarisApplication.applicationContext().initialNavigation = true
                    navController.navigate(R.id.fragmentServerList)
                }
            }
        }
    }

    private fun navigateTo(fragment: Int, server: Server) {
        Log.e("Navigate ", server.toString())
        mainViewModel.setCurrentServer(server)

        navController.navigate(fragment)

        drawerLayout.closeDrawer(GravityCompat.START)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.d("menu", item.itemId.toString())

        when (item.itemId) {
            R.id.item_manage_servers -> {
                navController.navigate(R.id.fragmentServerList)
                drawerLayout.closeDrawer(GravityCompat.START)
            }
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navFragment.navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}
