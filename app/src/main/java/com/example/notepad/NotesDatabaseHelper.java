package com.example.notepad;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.notepad.Model.TakeNoteModel;

import java.util.ArrayList;
import java.util.List;

public class NotesDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "NotesDtbHelper";
    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NOTE = "note";

    public NotesDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String queryCreateTable = "CREATE TABLE " + TABLE_NOTE + " ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title VARCHAR (500) NOT NULL, " +
                "image VARCHAR(1000) NOT NULL," +
                "time VARCHAR (50) NOT NULL," +
                "notes VARCHAR (5000) NOT NULL" +
                ")";
        sqLiteDatabase.execSQL(queryCreateTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE);
        onCreate(sqLiteDatabase);
    }

    public List<TakeNoteModel> getAllProducts() {

        List<TakeNoteModel> mListData = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, title, image, time, notes from note", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String titleNote = cursor.getString(1);
            String imageNote = cursor.getString(2);
            String timeNote = cursor.getString(3);
            String noteContent = cursor.getString(4);

            mListData.add(new TakeNoteModel(titleNote, imageNote, timeNote, noteContent));
            cursor.moveToNext();
        }

        cursor.close();

        return mListData;
    }

    void insertProduct(TakeNoteModel takeNoteModel) {
        SQLiteDatabase mDatabase = getWritableDatabase();
        mDatabase.execSQL("INSERT INTO note (title, image, time, notes ) VALUES (?,?,?,?)",
                new String[]{takeNoteModel.getTitle(), takeNoteModel.getImage(), takeNoteModel.getTimeNote(), takeNoteModel.getNotes() + ""});
    }
}
