package com.example.notepad.activity

import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.*
import androidx.core.view.GravityCompat.START
import androidx.fragment.app.Fragment
import com.example.notepad.NotesDatabaseHelper
import com.example.notepad.R
import com.example.notepad.R.*
import com.example.notepad.R.id.LayoutFragment
import com.example.notepad.R.menu.*
import com.example.notepad.base.BaseActivity
import com.example.notepad.databinding.ActivityMainBinding
import com.example.notepad.fragment.ArchiveFragment
import com.example.notepad.fragment.NotesFragment
import com.example.notepad.fragment.SearchFragment
import com.example.notepad.fragment.TrashCanFragment
import com.example.notepad.model.NotesModel


class MainActivity : BaseActivity() {

    companion object {
        var mListData = arrayListOf<NotesModel>()
    }

    private lateinit var mBinding: ActivityMainBinding
    private var backPressedCount = 0
    private var mDatabaseHelper: NotesDatabaseHelper? = null
    private var fm: Fragment? = null
    private var searchItem: MenuItem? = null

    override fun setLayout(): View = mBinding.root

    override fun initView() {
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setupToolbar()
        setupNavigationMenu()
        addFragment(LayoutFragment, NotesFragment.newInstance(), "NotesFragment", "NotesFragment")

        mDatabaseHelper = NotesDatabaseHelper(this@MainActivity)

        val mIntent = intent.extras
        if (mIntent != null) {
            if (mIntent.getString("recycle").equals("recycle")) {
                mBinding.NavigationView.menu.findItem(id.item_deleted).isChecked = true
                replaceFragment(
                    LayoutFragment,
                    TrashCanFragment.newInstance(),
                    "TrashCanFragment", "TrashCanFragment"
                )
                mBinding.ButtonDeleteAll.visibility = VISIBLE
                mBinding.TextTitle.text = getString(string.deleted)
            } else if (mIntent.getString("archive").equals("archive")) {
                mBinding.NavigationView.menu.findItem(id.item_archived).isChecked = true
                replaceFragment(
                    LayoutFragment,
                    ArchiveFragment.newInstance(),
                    "ArchiveFragment", "ArchiveFragment"
                )
                mBinding.TextTitle.text = getString(string.archived)
            }
        }
        mBinding.ButtonDeleteAll.setOnClickListener {
            if (TrashCanFragment.mListData.size > 0) {
                mDatabaseHelper!!.deleteAllRecycle()
                replaceFragment(
                    LayoutFragment,
                    TrashCanFragment.newInstance(),
                    "TrashCanFragment", "TrashCanFragment"
                )
                mBinding.NavigationView.menu.findItem(id.item_deleted).isChecked = true
                mBinding.TextTitle.text = getString(string.deleted)
            }
        }
    }

    private fun setupNavigationMenu() {
        mBinding.ButtonMenu.setOnClickListener {
            mBinding.DrawerLayout.openDrawer(START)
        }

        mBinding.NavigationView.inflateMenu(menu)
        mBinding.NavigationView.setCheckedItem(id.item_notes)
        mBinding.NavigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isCheckable = true
            when (menuItem.itemId) {
                id.item_notes -> {
                    mBinding.DrawerLayout.closeDrawer(START)
                    fm = supportFragmentManager.findFragmentByTag("NotesFragment")
                    if (fm != null && !fm!!.isVisible) {
                        mBinding.ButtonDeleteAll.visibility = GONE
                        mBinding.TextTitle.text = getString(R.string.app_name)
                        replaceFragment(
                            LayoutFragment,
                            NotesFragment.newInstance(),
                            "NotesFragment", "NotesFragment"
                        )
                        searchItem?.isVisible = true
                    }
                    true
                }
                id.item_deleted -> {
                    mBinding.DrawerLayout.closeDrawer(START)
                    fm = supportFragmentManager.findFragmentByTag("TrashCanFragment")
                    if (fm == null || (fm != null && !fm!!.isVisible)) {
                        mBinding.TextTitle.text = getString(R.string.deleted)
                        replaceFragment(
                            LayoutFragment,
                            TrashCanFragment.newInstance(),
                            "TrashCanFragment", "TrashCanFragment"
                        )
                        mBinding.ButtonDeleteAll.visibility = VISIBLE
                        searchItem?.isVisible = false
                    }
                    true
                }
                id.item_archived -> {
                    mBinding.DrawerLayout.closeDrawer(START)
                    fm = supportFragmentManager.findFragmentByTag("ArchiveFragment")
                    if (fm == null || (fm != null && !fm!!.isVisible)) {
                        mBinding.TextTitle.text = getString(R.string.archived)
                        replaceFragment(
                            LayoutFragment,
                            ArchiveFragment.newInstance(),
                            "ArchiveFragment", "ArchiveFragment"
                        )
                        searchItem?.isVisible = false
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(mBinding.Toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(menu_search, menu)
        searchItem = menu?.findItem(id.menu_search)
        val searchView: SearchView = searchItem?.actionView as SearchView
        searchView.queryHint = "Search..."
        searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                fm = supportFragmentManager.findFragmentByTag("SearchFragment")
                if (fm != null && fm!!.isVisible) {
                    if (newText!!.isEmpty()) {
                        mListData.clear()
                        SearchFragment.mNoteAdapter.setData(mListData)
                        SearchFragment.mBinding.ImageSearch.visibility = VISIBLE
                    } else {
                        mListData.clear()
                        mListData.addAll(
                            mDatabaseHelper!!.searchDataNotes(
                                newText,
                                SearchFragment.table
                            )
                        )
                        SearchFragment.mNoteAdapter.setData(mListData)
                        SearchFragment.mBinding.ImageSearch.visibility = GONE
                    }
                }
                return false
            }

        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            id.menu_search -> {
                replaceFragment(LayoutFragment, SearchFragment.newInstance(), "SearchFragment")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        supportFragmentManager.popBackStack("NotesFragment", 0)
        fm = supportFragmentManager.findFragmentByTag("NotesFragment")
        if (fm != null && fm!!.isVisible) {
            backPressedCount++
            if (backPressedCount > 1) {
                finish()
                backPressedCount = 0
            } else {
                createCustomToast(drawable.warning, "Go back again to exit the application")
                Handler().postDelayed({
                    backPressedCount = 0
                }, 3000)
            }
        } else {
            mBinding.ButtonDeleteAll.visibility = GONE
            mBinding.NavigationView.menu.findItem(id.item_notes).isChecked = true
            mBinding.TextTitle.text = getString(R.string.app_name)
            replaceFragment(
                LayoutFragment,
                NotesFragment.newInstance(),
                "NotesFragment",
                "NotesFragment"
            )
            searchItem?.isVisible = true
        }
    }
}