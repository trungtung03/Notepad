package com.example.notepad.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notepad.R
import com.example.notepad.activity.DetailedNotesActivity
import com.example.notepad.activity.MainActivity
import com.example.notepad.activity.NotesArchiveActivity
import com.example.notepad.activity.NotesRecycleActivity
import com.example.notepad.adapter.NotesAdapter
import com.example.notepad.base.BaseFragment
import com.example.notepad.databinding.FragmentSearchBinding


class SearchFragment : BaseFragment<FragmentSearchBinding>(), View.OnClickListener {

    companion object {
        fun newInstance() = SearchFragment()

        @SuppressLint("StaticFieldLeak")
        lateinit var mBinding: FragmentSearchBinding

        @SuppressLint("StaticFieldLeak")
        lateinit var mNoteAdapter: NotesAdapter
        var table = "note"
    }

    private lateinit var key: String
    private lateinit var destinationClass: Class<*>

    override fun initView(view: View) {
        mBinding = FragmentSearchBinding.bind(view)
        actionView()
    }

    override fun getBinding(): FragmentSearchBinding {
        mBinding = FragmentSearchBinding.inflate(layoutInflater)
        return mBinding
    }

    @SuppressLint("ResourceAsColor")
    private fun actionView() {
        mBinding.btnNote.isEnabled = true
        mBinding.btnNote.setOnClickListener(this)
        mBinding.btnRecycle.setOnClickListener(this)
        mBinding.btnArchive.setOnClickListener(this)

        mNoteAdapter = NotesAdapter(requireActivity(),
            onClickItem = {
                val mIntent = Intent(activity, destinationClass)
                mIntent.putExtra(key, mNoteAdapter.getListItem().indexOf(it))
                startActivity(mIntent)
                activity?.overridePendingTransition(R.anim.slide_in, R.anim.fade_out)
            })

        mBinding.RecyclerSearch.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        mBinding.RecyclerSearch.adapter = mNoteAdapter

        if (mBinding.btnNote.isEnabled) {
            mBinding.btnNote.setBackgroundResource(R.drawable.bg_btn_fgm)
            table = "note"
            key = "position"
            destinationClass = DetailedNotesActivity::class.java
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_note -> {
                mBinding.btnNote.setBackgroundResource(R.drawable.bg_btn_fgm)
                mBinding.btnRecycle.setBackgroundResource(R.drawable.bg_btn_fgm_default)
                mBinding.btnArchive.setBackgroundResource(R.drawable.bg_btn_fgm_default)
                table = "note"
                key = "position"
                destinationClass = DetailedNotesActivity::class.java
            }
            R.id.btn_recycle -> {
                mBinding.btnRecycle.setBackgroundResource(R.drawable.bg_btn_fgm)
                mBinding.btnNote.setBackgroundResource(R.drawable.bg_btn_fgm_default)
                mBinding.btnArchive.setBackgroundResource(R.drawable.bg_btn_fgm_default)
                table = "recycle"
                key = "position_recycle"
                destinationClass = NotesRecycleActivity::class.java
            }
            R.id.btn_archive -> {
                mBinding.btnArchive.setBackgroundResource(R.drawable.bg_btn_fgm)
                mBinding.btnRecycle.setBackgroundResource(R.drawable.bg_btn_fgm_default)
                mBinding.btnNote.setBackgroundResource(R.drawable.bg_btn_fgm_default)
                table = "archive"
                key = "position_archive"
                destinationClass = NotesArchiveActivity::class.java
            }
        }
    }
}