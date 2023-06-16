package com.example.notepad

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.notepad.Model.TakeNoteModel
import com.example.notepad.base.BaseActivity
import com.example.notepad.databinding.ActivityTakeNoteBinding
import java.text.SimpleDateFormat
import java.util.Calendar

@Suppress("DEPRECATION")
class TakeNoteActivity : BaseActivity() {

    private lateinit var mBinding: ActivityTakeNoteBinding
    private var mDatabaseHelper: NotesDatabaseHelper? = null
    private var isCheck = 0
    private var mUri: Uri? = null
    private val TITLE_INTENT_RESULT_LAUNCHER = "Select picture"
    private val MY_REQUEST_CODE = 10
    private val PERMISSION_FAIL = "Please allow the app to access the photo storage"

    private val mActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val intent = result.data
            if (intent == null) {
                return@registerForActivityResult
            } else {
                mUri = intent.data
                val bitmap = MediaStore.Images.Media.getBitmap(
                    contentResolver,
                    mUri
                )
                mBinding.ImageTakeNotes.setImageBitmap(
                    bitmap
                )
                mBinding.ImageTakeNotes.visibility = View.VISIBLE
            }
        }
    }

    override fun setLayout(): View = mBinding.root

    override fun initView() {
        mBinding = ActivityTakeNoteBinding.inflate(layoutInflater)

        setSupportActionBar(mBinding.ToolbarTakeNotes)

        mDatabaseHelper = NotesDatabaseHelper(this@TakeNoteActivity)

        mBinding.ButtonBack.setOnClickListener {
            setDataToBundle()
        }
        mBinding.ButtonPin.setOnClickListener {
            isCheck++
            if (isCheck > 1) {
                mBinding.ButtonPin.setImageResource(R.drawable.pin)
            } else {
                mBinding.ButtonPin.setImageResource(R.drawable.unpin)
            }
        }

        mBinding.TextViewDateTime.text =
            SimpleDateFormat("EEE d MMM yyyy").format(Calendar.getInstance().time).toString().trim()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_take_note, menu)
        return true
    }

    private fun setDataToBundle() {
        val takeNoteModel = TakeNoteModel()
        if (mBinding.EditTextTakeNotes.text!!.trim()
                .isNotEmpty() && mBinding.EditTextTitle.text.trim().isNotEmpty()
        ) {
            takeNoteModel.title = mBinding.EditTextTitle.text.toString().trim()
            takeNoteModel.notes = mBinding.EditTextTakeNotes.text.toString().trim()
        } else if (mBinding.EditTextTakeNotes.text!!.trim()
                .isEmpty() && mBinding.EditTextTitle.text.trim().isEmpty()
        ) {
            takeNoteModel.title = "Nội dung ghi chú trống"
            takeNoteModel.notes = ""
        } else if (mBinding.EditTextTakeNotes.text!!.isNotEmpty() && mBinding.EditTextTitle.text!!.isEmpty()) {
            takeNoteModel.title = ""
            takeNoteModel.notes = mBinding.EditTextTakeNotes.text.toString().trim()
        } else if (mBinding.EditTextTitle.text!!.isNotEmpty() && mBinding.EditTextTakeNotes.text!!.isEmpty()) {
            takeNoteModel.notes = ""
            takeNoteModel.title = mBinding.EditTextTitle.text.toString().trim()
        }
        takeNoteModel.timeNote = mBinding.TextViewDateTime.text.toString().trim()
        if(mUri != null) {
            takeNoteModel.image = mUri.toString().trim()
            Log.d("vxc", mUri.toString().trim() + " |")
        }else {
            takeNoteModel.image = ""
        }
        mDatabaseHelper?.insertProduct(takeNoteModel)

        openActivity(
            MainActivity::
            class.java
        )
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.take_note_labels -> {

            }
            R.id.take_note_add_image -> {
                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                } else {
                    val arrPermission = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(arrPermission, MY_REQUEST_CODE)
                }
            }
            R.id.take_note_delete -> {
                openActivity(MainActivity::class.java)
            }
            R.id.take_note_archive -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Deprecated(
        "Deprecated in Java",
        replaceWith = ReplaceWith(
            "super.onBackPressed()",
            "androidx.appcompat.app.AppCompatActivity"
        )
    )
    override fun onBackPressed() {
        super.onBackPressed()
        setDataToBundle()
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