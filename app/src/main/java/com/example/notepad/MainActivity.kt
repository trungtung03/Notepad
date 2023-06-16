package com.example.notepad

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.view.GravityCompat.START
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notepad.Model.TakeNoteModel
import com.example.notepad.adapter.ListNoteAdapter
import com.example.notepad.base.BaseActivity
import com.example.notepad.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mNoteAdapter: ListNoteAdapter
    private var mDatabaseHelper: NotesDatabaseHelper? = null
    private var mListData = arrayListOf<TakeNoteModel>()

    companion object {
        var POSITION_NOTE = -1;
    }

    override fun setLayout(): View = mBinding.root

    override fun initView() {
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setupToolbar()
        setupNavigationMenu()

        mBinding.AddNotes.setOnClickListener {
            openActivity(TakeNoteActivity::class.java)
            finish()
        }

        addDataToList()
        mNoteAdapter = ListNoteAdapter(this@MainActivity,
            onClickItem = {
                val mIntent = Intent(this, DetailedNotes::class.java)
                val mBundle = Bundle()
                mBundle.putString("title", mListData[POSITION_NOTE].title)
                mIntent.data = Uri.parse(mListData[POSITION_NOTE].image)
                mBundle.putString("time_note", mListData[POSITION_NOTE].timeNote)
                mBundle.putString("note_content", mListData[POSITION_NOTE].notes)
                mIntent.putExtras(mBundle)
                startActivity(mIntent)
                overridePendingTransition(R.anim.fade_in, R.anim.slide_out)
                finish()
            })

        mBinding.RecyclerViewNotes.layoutManager =
            LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
        mNoteAdapter.setData(mListData)
        mBinding.RecyclerViewNotes.adapter = mNoteAdapter
    }

    private fun addDataToList() {
        mDatabaseHelper = NotesDatabaseHelper(this@MainActivity)
        mDatabaseHelper?.allProducts?.let { mListData.addAll(it) }

        if (mListData.size > 0) {
            mBinding.ImageNotebook.visibility = GONE
        } else {
            mBinding.ImageNotebook.visibility = VISIBLE
        }
    }

    private fun setupNavigationMenu() {
        mBinding.ButtonMenu.setOnClickListener {
            mBinding.DrawerLayout.openDrawer(START)
        }

        mBinding.NavigationView.inflateMenu(R.menu.menu)
        mBinding.NavigationView.setCheckedItem(R.id.item_notes)
        mBinding.NavigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isCheckable = true
            when (menuItem.itemId) {
                R.id.item_notes -> {
                    mBinding.DrawerLayout.closeDrawer(START)

                    true
                }
                R.id.item_label -> {
                    mBinding.DrawerLayout.closeDrawer(START)

                    true
                }
                R.id.item_deleted -> {
                    mBinding.DrawerLayout.closeDrawer(START)

                    true
                }
                R.id.item_archived -> {
                    mBinding.DrawerLayout.closeDrawer(START)

                    true
                }
                R.id.item_setting -> {
                    mBinding.DrawerLayout.closeDrawer(START)

                    true
                }
                else -> false
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(mBinding.Toolbar)
    }

}