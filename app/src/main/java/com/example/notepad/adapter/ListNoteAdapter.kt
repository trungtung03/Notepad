package com.example.notepad.adapter

import android.annotation.SuppressLint
import android.content.ClipData.Item
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import com.example.notepad.DetailedNotes
import com.example.notepad.MainActivity.Companion.POSITION_NOTE
import com.example.notepad.Model.TakeNoteModel
import com.example.notepad.R
import com.example.notepad.base.recyclerview.BaseRecyclerViewAdapter
import com.example.notepad.base.recyclerview.BaseViewHolder
import com.example.notepad.databinding.ItemRcvListNoteBinding

class ListNoteAdapter(
    private val context: Context,
    val onClickItem: () -> Unit
) :
    BaseRecyclerViewAdapter<TakeNoteModel, ListNoteAdapter.ViewHolder>() {

    private var mListData: MutableList<TakeNoteModel> = ArrayList()

    class ViewHolder(private val binding: ItemRcvListNoteBinding) :
        BaseViewHolder<TakeNoteModel>(binding) {
        override fun bindViewHolder(context: Context?, data: TakeNoteModel?, position: Int) {
            binding.TextViewTimeRcv.text = data!!.timeNote
            if (data.notes.isNotEmpty() && data.title.isNotEmpty()) {
                binding.TextViewTitleRcv.visibility = VISIBLE
                binding.TextViewTitleRcv.text = data.title
                binding.TextViewNotesRcv.visibility = VISIBLE
                binding.TextViewNotesRcv.text = data.notes
            } else if (data.notes.isNotEmpty() && data.title.isEmpty()) {
                binding.TextViewNotesRcv.visibility = VISIBLE
                binding.TextViewNotesRcv.text = data.notes
            } else if (data.title.isNotEmpty() && data.notes.isEmpty()) {
                binding.TextViewTitleRcv.visibility = VISIBLE
                binding.TextViewTitleRcv.text = data.title
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setData(mList: ArrayList<TakeNoteModel>) {
        mListData = mList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val mBinding = ItemRcvListNoteBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(mBinding)
    }

    override fun getItemCount(): Int {
        return mListData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViewHolder(context, mListData[position], position)
        holder.itemView.setOnClickListener {
            POSITION_NOTE = position
            onClickItem.invoke()
        }
    }
}