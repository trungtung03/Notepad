package com.example.notepad.fragment

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notepad.NotesDatabaseHelper
import com.example.notepad.R
import com.example.notepad.activity.NotesArchiveActivity
import com.example.notepad.activity.NotesRecycleActivity
import com.example.notepad.adapter.NotesAdapter
import com.example.notepad.base.BaseFragment
import com.example.notepad.databinding.FragmentArchiveBinding
import com.example.notepad.model.NotesModel

class ArchiveFragment : BaseFragment<FragmentArchiveBinding>() {

    companion object {
        fun newInstance() = ArchiveFragment()
    }

    private lateinit var mBinding: FragmentArchiveBinding
    private lateinit var mNoteAdapter: NotesAdapter
    private var mDatabaseHelper: NotesDatabaseHelper? = null
    private val mListData = arrayListOf<NotesModel>()

    override fun initView(view: View) {
        mBinding = FragmentArchiveBinding.bind(view)
        actionView()
    }

    override fun getBinding(): FragmentArchiveBinding {
        mBinding = FragmentArchiveBinding.inflate(layoutInflater)
        return mBinding
    }

    private fun actionView() {
        mDatabaseHelper = NotesDatabaseHelper(activity)

        addDataToList()
        mNoteAdapter = NotesAdapter(requireActivity(),
            onClickItem = {
                val mIntent = Intent(activity, NotesArchiveActivity::class.java)
                mIntent.putExtra("position_archive", mNoteAdapter.getListItem().indexOf(it))
                startActivity(mIntent)
                activity?.overridePendingTransition(R.anim.slide_in, R.anim.fade_out)
            })

        mBinding.RecyclerArchive.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        mNoteAdapter.setData(mListData)
        mBinding.RecyclerArchive.adapter = mNoteAdapter
    }

    private fun addDataToList() {
        mDatabaseHelper?.getAllNotes("archive")?.let {
            mListData.clear()
            mListData.addAll(it)
        }

        if (mListData.size > 0) {
            mBinding.ImageArchive.visibility = View.GONE
        } else {
            mBinding.ImageArchive.visibility = View.VISIBLE
        }
    }
}