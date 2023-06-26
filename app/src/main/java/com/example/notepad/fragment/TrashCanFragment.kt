package com.example.notepad.fragment

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notepad.model.NotesModel
import com.example.notepad.NotesDatabaseHelper
import com.example.notepad.R
import com.example.notepad.activity.MainActivity
import com.example.notepad.activity.NotesRecycleActivity
import com.example.notepad.adapter.NotesAdapter
import com.example.notepad.base.BaseFragment
import com.example.notepad.databinding.FragmentRecycleBinding

class TrashCanFragment: BaseFragment<FragmentRecycleBinding>() {

    companion object {
        fun newInstance() = TrashCanFragment()
        var mListData = arrayListOf<NotesModel>()
    }

    private lateinit var mBinding: FragmentRecycleBinding
    private lateinit var mNoteAdapter: NotesAdapter
    private var mDatabaseHelper: NotesDatabaseHelper? = null

    override fun initView(view: View) {
        mBinding = FragmentRecycleBinding.bind(view)
        actionView()
    }

    override fun getBinding(): FragmentRecycleBinding {
        mBinding = FragmentRecycleBinding.inflate(layoutInflater)
        return mBinding
    }

    private fun actionView() {
        mDatabaseHelper = NotesDatabaseHelper(activity)

        addDataToList()
        mNoteAdapter = NotesAdapter(requireActivity(),
            onClickItem = {
                val mIntent = Intent(activity, NotesRecycleActivity::class.java)
                mIntent.putExtra("position_recycle", mNoteAdapter.getListItem().indexOf(it))
                startActivity(mIntent)
                activity?.overridePendingTransition(R.anim.slide_in, R.anim.fade_out)
            })

        mBinding.RecyclerTrashCan.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        mNoteAdapter.setData(mListData)
        mBinding.RecyclerTrashCan.adapter = mNoteAdapter
    }

    private fun addDataToList() {
        mDatabaseHelper?.getAllNotes("recycle")?.let {
            mListData.clear()
            mListData.addAll(it)
        }

        if (mListData.size > 0) {
            mBinding.ImageRecycle.visibility = View.GONE
        } else {
            mBinding.ImageRecycle.visibility = View.VISIBLE
        }
    }
}