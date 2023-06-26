package com.example.notepad.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.VISIBLE
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.notepad.model.NotesModel
import com.example.notepad.NotesDatabaseHelper
import com.example.notepad.R
import com.example.notepad.base.BaseActivity
import com.example.notepad.databinding.ActivityDetailedNotesBinding
import java.text.SimpleDateFormat
import java.util.Calendar

class DetailedNotesActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mBinding: ActivityDetailedNotesBinding
    private var mUri: Uri? = null
    private var mDatabaseHelper: NotesDatabaseHelper? = null
    private var noteID: Int = -1
    private var pathImage: String = ""
    private val MY_REQUEST_CODE = 10
    private val TITLE_INTENT_RESULT_LAUNCHER = "Select picture"
    private val PERMISSION_FAIL = "Please allow the app to access the photo storage"
    val notesModel = NotesModel()

    private val mActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val intent = result.data
            if (intent == null) {
                return@registerForActivityResult
            } else {
                mUri = intent.data
                Glide.with(this).load(getPath(uri = mUri)).into(mBinding.ImageDetailNotes)
                mBinding.ImageDetailNotes.visibility = VISIBLE
            }
        }
    }

    override fun setLayout(): View = mBinding.root

    override fun initView() {
        mBinding = ActivityDetailedNotesBinding.inflate(layoutInflater)
        setSupportActionBar(mBinding.ToolbarDetailNotes)

        val position = intent.getIntExtra("position", -1)

        mDatabaseHelper = NotesDatabaseHelper(this)

        getData(position)

        mBinding.ButtonBackDetailNotes.setOnClickListener(this)

        Log.d("note_id", "$position")

        notesModel.takeNoteID = noteID
        notesModel.title = mBinding.TextTitleDetailNotes.text.toString().trim()
        notesModel.image = pathImage
        notesModel.timeNote = mBinding.TextViewDateTimeDetailNotes.text.toString().trim()
        notesModel.notes = mBinding.TextDetailNotes.text.toString().trim()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail_note, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.detail_note_add_image -> {
                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        MY_REQUEST_CODE
                    )
                }
            }
            R.id.detail_note_delete -> {
                mDatabaseHelper?.insertNote(notesModel, "recycle")
                mDatabaseHelper?.deleteNoteByID(noteID, "note")
                openActivity(MainActivity::class.java)
            }
            R.id.detail_note_archive -> {
                mDatabaseHelper?.insertNote(notesModel, "archive")
                mDatabaseHelper?.deleteNoteByID(noteID, "note")
                openActivity(MainActivity::class.java)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getData(position: Int) {
        mDatabaseHelper?.getAllNotes("note")?.let {
            val takeNoteActivity = it.getOrNull(position)
//            if (takeNoteActivity?.title.equals(getString(R.string.empty_note_content))) {
//                mBinding.TextTitleDetailNotes.setText("")
//            } else {
                mBinding.TextTitleDetailNotes.setText(takeNoteActivity?.title)
//            }
            mBinding.TextViewDateTimeDetailNotes.text = takeNoteActivity?.timeNote
            mBinding.TextDetailNotes.setText(takeNoteActivity?.notes)
            if (takeNoteActivity?.image!!.isNotEmpty()) {
                Glide.with(this).load(takeNoteActivity.image).into(mBinding.ImageDetailNotes)
                pathImage = takeNoteActivity.image
                mBinding.ImageDetailNotes.visibility = VISIBLE
            }
            noteID = takeNoteActivity.takeNoteID
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setDataToBundle() {
        val notesModel = NotesModel()

        notesModel.takeNoteID = (noteID)
        if (mBinding.TextDetailNotes.text!!.trim()
                .isNotEmpty() && mBinding.TextTitleDetailNotes.text.trim().isNotEmpty()
        ) {
            notesModel.title = mBinding.TextTitleDetailNotes.text.toString().trim()
            notesModel.notes = mBinding.TextDetailNotes.text.toString().trim()
        } else if (mBinding.TextDetailNotes.text!!.trim()
                .isEmpty() && mBinding.TextTitleDetailNotes.text.isEmpty()
        ) {
            notesModel.title = getString(R.string.empty_note_content)
            notesModel.notes = ""
        } else if (mBinding.TextDetailNotes.text!!.isNotEmpty() && mBinding.TextTitleDetailNotes.text!!.isEmpty()) {
            notesModel.title = ""
            notesModel.notes = mBinding.TextDetailNotes.text.toString().trim()
        } else if (mBinding.TextTitleDetailNotes.text!!.isNotEmpty() && mBinding.TextDetailNotes.text!!.isEmpty()) {
            notesModel.notes = ""
            notesModel.title = mBinding.TextTitleDetailNotes.text.toString().trim()
        }
        notesModel.timeNote =
            SimpleDateFormat("EEE d MMM yyyy").format(Calendar.getInstance().time).toString().trim()
        notesModel.image = pathImage
        if (mUri != null) {
            notesModel.image = getPath(mUri)
        } else if (pathImage.isEmpty() && mUri == null) {
            notesModel.image = ""
        }
        mDatabaseHelper?.updateNote(notesModel, "note")

        openActivity(
            MainActivity::
            class.java
        )
        finish()
    }

    @SuppressLint("Range")
    fun getPath(uri: Uri?): String? {
        var cursor: Cursor? = contentResolver.query(uri!!, null, null, null, null)
        cursor?.moveToFirst()
        var document_id: String? = cursor?.getString(0)
        document_id = document_id?.substring(document_id.lastIndexOf(":").plus(1))
        cursor?.close()
        cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null, MediaStore.Images.Media._ID + " = ? ", arrayOf(document_id), null
        )
        cursor?.moveToFirst()
        val path: String? = cursor?.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
        cursor?.close()
        return path
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ButtonBackDetailNotes -> {
                setDataToBundle()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                createCustomToast(
                    R.drawable.warning,
                    PERMISSION_FAIL
                )
            }
        }
    }

    private fun openGallery() {
        val intent = Intent()
        with(mActivityResultLauncher) {
            "image/*".also { intent.type = it }
            Intent.ACTION_GET_CONTENT.also { intent.action = it }
            launch(
                Intent.createChooser(
                    intent,
                    TITLE_INTENT_RESULT_LAUNCHER
                )
            )
        }
    }

}