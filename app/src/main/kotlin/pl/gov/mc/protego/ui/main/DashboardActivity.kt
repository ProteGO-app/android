package pl.gov.mc.protego.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.gov.mc.protego.R
import pl.gov.mc.protego.ui.base.BaseActivity


class DashboardActivity : BaseActivity() {

    private val viewModel: DashboardActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        viewModel.dashboardPage().observe(this, Observer { page -> changePage(page) })
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.dashboard_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_menu) {
            viewModel.homeButtonPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (viewModel.dashboardPage().value is DashboardPage.HistoryPage) {
            viewModel.homeButtonPressed()
        } else {
            super.onBackPressed()
        }
    }

    private fun changePage(page: DashboardPage) {
        val fragmentToAdd: Fragment =
            supportFragmentManager.findFragmentByTag(page.pageFragmentTag) ?: page.createFragment()
        page.showFragment(supportFragmentManager, fragmentToAdd)
    }
}
