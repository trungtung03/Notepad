package com.example.notepad.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.notepad.NotesDatabaseHelper
import com.example.notepad.R
import com.example.notepad.base.BaseActivity
import com.example.notepad.databinding.ActivityNotesRecycleBinding
import com.example.notepad.model.NotesModel
import java.text.SimpleDateFormat
import java.util.*

class NotesRecycleActivity : BaseActivity() {

    private lateinit var mBinding: ActivityNotesRecycleBinding
    private var mUri: Uri? = null
    private var pathImage: String = ""
    private var mDatabaseHelper: NotesDatabaseHelper? = null
    private val MY_REQUEST_CODE = 10
    private var noteID: Int = -1
    private val TITLE_INTENT_RESULT_LAUNCHER = "Select picture"
    private val PERMISSION_FAIL = "Please allow the app to access the photo storage"


    private val mActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val intent = result.data
            if (intent == null) {
                return@registerForActivityResult
            } else {
                mUri = intent.data
                Glide.with(this).load(getPath(uri = mUri)).into(mBinding.ImageRecycleNotes)
                mBinding.ImageRecycleNotes.visibility = View.VISIBLE
            }
        }
    }

    override fun setLayout(): View = mBinding.root

    override fun initView() {
        mBinding = ActivityNotesRecycleBinding.inflate(layoutInflater)
        setSupportActionBar(mBinding.ToolbarRecycleNotes)
        actionView()
    }

    private fun actionView() {
        val position = intent.getIntExtra("position_recycle", -1)

        mDatabaseHelper = NotesDatabaseHelper(this)
        getData(position)

        mBinding.ButtonBackRecycleNotes.setOnClickListener { setDataToBundle() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_recycle_note, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.recycle_note_add_image -> {
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
            R.id.recycle_note_restore -> {
                mDatabaseHelper?.deleteNoteByID(noteID, "recycle")
                val notesModel = NotesModel()
                notesModel.takeNoteID = noteID
                notesModel.title = mBinding.TextTitleRecycleNotes.text.toString().trim()
                notesModel.image = pathImage
                notesModel.timeNote = mBinding.TextViewDateTimeRecycleNotes.text.toString().trim()
                notesModel.notes = mBinding.TextRecycleNotes.text.toString().trim()
                mDatabaseHelper?.insertNote(notesModel, "note")
                backToMain()
            }
            R.id.recycle_note_forever -> {
                mDatabaseHelper?.deleteNoteByID(noteID, "recycle")
                backToMain()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getData(position: Int) {
        mDatabaseHelper?.getAllNotes("recycle")?.let {
            val takeNoteActivity = it.getOrNull(position)
//            if (takeNoteActivity?.title.equals(getString(R.string.empty_note_content))) {
//                mBinding.TextTitleRecycleNotes.transformationMethod = null
//            } else {
                mBinding.TextTitleRecycleNotes.setText(takeNoteActivity?.title)
//            }
            mBinding.TextViewDateTimeRecycleNotes.text = takeNoteActivity?.timeNote
            mBinding.TextRecycleNotes.setText(takeNoteActivity?.notes)
            if (takeNoteActivity?.image!!.isNotEmpty()) {
                Glide.with(this).load(takeNoteActivity.image).into(mBinding.ImageRecycleNotes)
                pathImage = takeNoteActivity.image
                mBinding.ImageRecycleNotes.visibility = View.VISIBLE
            }
            noteID = takeNoteActivity.takeNoteID
        }
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

    @SuppressLint("SimpleDateFormat")
    private fun setDataToBundle() {
        val notesModel = NotesModel()

        notesModel.takeNoteID = (noteID)
        if (mBinding.TextRecycleNotes.text!!.trim()
                .isNotEmpty() && mBinding.TextTitleRecycleNotes.text.trim().isNotEmpty()
        ) {
            notesModel.title = mBinding.TextTitleRecycleNotes.text.toString().trim()
            notesModel.notes = mBinding.TextRecycleNotes.text.toString().trim()
        } else if (mBinding.TextRecycleNotes.text!!.trim()
                .isEmpty() && mBinding.TextTitleRecycleNotes.text.isEmpty()
        ) {
            notesModel.title = getString(R.string.empty_note_content)
            notesModel.notes = ""
        } else if (mBinding.TextRecycleNotes.text!!.isNotEmpty() && mBinding.TextTitleRecycleNotes.text!!.isEmpty()) {
            notesModel.title = ""
            notesModel.notes = mBinding.TextRecycleNotes.text.toString().trim()
        } else if (mBinding.TextTitleRecycleNotes.text!!.isNotEmpty() && mBinding.TextRecycleNotes.text!!.isEmpty()) {
            notesModel.notes = ""
            notesModel.title = mBinding.TextTitleRecycleNotes.text.toString().trim()
        }
        notesModel.timeNote =
            SimpleDateFormat("EEE d MMM yyyy").format(Calendar.getInstance().time).toString().trim()
        notesModel.image = pathImage
        if (mUri != null) {
            notesModel.image = getPath(mUri)
        } else if (pathImage.isEmpty() && mUri == null) {
            notesModel.image = ""
        }
        mDatabaseHelper?.updateNote(notesModel, "recycle")
        backToMain()
    }

    private fun backToMain() {
        val mIntent = Intent(this@NotesRecycleActivity, MainActivity::class.java)
        mIntent.putExtra("recycle", "recycle")
        startActivity(mIntent)
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out)
        finish()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        setDataToBundle()
    }
}