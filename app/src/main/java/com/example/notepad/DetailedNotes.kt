package com.example.notepad

import android.view.View
import com.example.notepad.base.BaseActivity
import com.example.notepad.databinding.ActivityDetailedNotesBinding

class DetailedNotes : BaseActivity() {

    private lateinit var mBinding: ActivityDetailedNotesBinding

    override fun setLayout(): View = mBinding.root

    override fun initView() {
        mBinding = ActivityDetailedNotesBinding.inflate(layoutInflater)

        val mBundle = intent.extras ?: return
        val uri = intent.data
        mBinding.TextTitleDetailNotes.setText(mBundle.getString("title"))
        mBinding.TextViewDateTimeDetailNotes.text = mBundle.getString("time_note")
        mBinding.TextDetailNotes.setText(mBundle.getString("note_content"))
        mBinding.ImageDetailNotes.setImageURI(uri)
    }

}