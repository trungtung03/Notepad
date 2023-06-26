package com.example.notepad.fragment

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notepad.activity.DetailedNotesActivity
import com.example.notepad.model.NotesModel
import com.example.notepad.NotesDatabaseHelper
import com.example.notepad.R
import com.example.notepad.activity.TakeNoteActivity
import com.example.notepad.adapter.NotesAdapter
import com.example.notepad.base.BaseFragment
import com.example.notepad.databinding.FragmentNotesBinding

class NotesFragment : BaseFragment<FragmentNotesBinding>() {

    companion object {
        fun newInstance() = NotesFragment()
    }

    private lateinit var mBinding: FragmentNotesBinding
    private lateinit var mNoteAdapter: NotesAdapter
    private var mDatabaseHelper: NotesDatabaseHelper? = null
    private var mListData = arrayListOf<NotesModel>()

    override fun initView(view: View) {
        mBinding = FragmentNotesBinding.bind(view)
        actionView()
    }

    override fun getBinding(): FragmentNotesBinding {
        mBinding = FragmentNotesBinding.inflate(layoutInflater)
        return mBinding
    }

    private fun actionView() {
        mBinding.AddNotes.setOnClickListener {
            openActivity(TakeNoteActivity::class.java)
        }

        mDatabaseHelper = NotesDatabaseHelper(activity)

        addDataToList()
        mNoteAdapter = NotesAdapter(requireActivity(),
            onClickItem = {
                val mIntent = Intent(activity, DetailedNotesActivity::class.java)
                mIntent.putExtra("position", mNoteAdapter.getListItem().indexOf(it))
                startActivity(mIntent)
                activity?.overridePendingTransition(R.anim.fade_in, R.anim.slide_out)
            })

        mBinding.RecyclerViewNotes.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        mNoteAdapter.setData(mListData)
        mBinding.RecyclerViewNotes.adapter = mNoteAdapter
    }

    private fun addDataToList() {
        mDatabaseHelper?.getAllNotes("note")?.let {
            mListData.clear()
            mListData.addAll(it)
        }

        if (mListData.size > 0) {
            mBinding.ImageNotebook.visibility = View.GONE
        } else {
            mBinding.ImageNotebook.visibility = View.VISIBLE
        }
    }
}