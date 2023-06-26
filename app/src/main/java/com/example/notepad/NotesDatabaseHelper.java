package com.example.notepad;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.notepad.model.NotesModel;

import java.util.ArrayList;
import java.util.List;

public class NotesDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "NotesDtbHelper";
    private static final String DATABASE_NAME = "notepad.db";

    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NOTE = "note";
    private static final String RECYCLE = "recycle";
    private static final String ARCHIVE = "archive";


    public NotesDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String queryCreateTableNote = "CREATE TABLE " + TABLE_NOTE + " ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title VARCHAR (500) NOT NULL, " +
                "image VARCHAR(1000) NOT NULL," +
                "time VARCHAR (50) NOT NULL," +
                "notes VARCHAR (5000) NOT NULL" +
                ")";
        String queryCreateTableTrashCan = "CREATE TABLE " + RECYCLE + " ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title VARCHAR (500) NOT NULL, " +
                "image VARCHAR(1000) NOT NULL," +
                "time VARCHAR (50) NOT NULL," +
                "notes VARCHAR (5000) NOT NULL" +
                ")";
        String queryCreateTableArchive = "CREATE TABLE " + ARCHIVE + " ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title VARCHAR (500) NOT NULL, " +
                "image VARCHAR(1000) NOT NULL," +
                "time VARCHAR (50) NOT NULL," +
                "notes VARCHAR (5000) NOT NULL" +
                ")";
        sqLiteDatabase.execSQL(queryCreateTableNote);
        sqLiteDatabase.execSQL(queryCreateTableTrashCan);
        sqLiteDatabase.execSQL(queryCreateTableArchive);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RECYCLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ARCHIVE);
        onCreate(sqLiteDatabase);
    }

    public List<NotesModel> getAllNotes(String table) {

        List<NotesModel> mListData = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, title, image, time, notes FROM '" + table + "' ORDER BY id DESC", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int takeNoteID = cursor.getInt(0);
            String titleNote = cursor.getString(1);
            String imageNote = cursor.getString(2);
            String timeNote = cursor.getString(3);
            String noteContent = cursor.getString(4);

            mListData.add(new NotesModel(takeNoteID, titleNote, imageNote, timeNote, noteContent));
            cursor.moveToNext();
        }

        cursor.close();

        return mListData;
    }

    @SuppressLint("Range")
    public List<NotesModel> searchDataNotes(String keyword, String table) {

        List<NotesModel> mListData = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, title, image, time, notes FROM '" + table + "' WHERE title LIKE ?",
                new String[]{"%" + keyword + "%"});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int takeNoteID = cursor.getInt(cursor.getColumnIndex("id"));
            String titleNote = cursor.getString(cursor.getColumnIndex("title"));
            String imageNote = cursor.getString(cursor.getColumnIndex("image"));
            String timeNote = cursor.getString(cursor.getColumnIndex("time"));
            String noteContent = cursor.getString(cursor.getColumnIndex("notes"));

            mListData.add(new NotesModel(takeNoteID, titleNote, imageNote, timeNote, noteContent));
            cursor.moveToNext();
        }

        cursor.close();

        return mListData;
    }

    public void insertNote(NotesModel notesModel, String table) {
        SQLiteDatabase mDatabase = getWritableDatabase();
        mDatabase.execSQL("INSERT INTO '" + table + "' (title, image, time, notes ) VALUES (?,?,?,?)",
                new String[]{notesModel.getTitle(), notesModel.getImage(), notesModel.getTimeNote(), notesModel.getNotes()});
    }

    public void updateNote(NotesModel notesModel, String table) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE '" + table + "' SET title = ?, image = ?, time = ?, notes = ? where id = ?",
                new String[]{notesModel.getTitle(), notesModel.getImage(), notesModel.getTimeNote(), notesModel.getNotes(), notesModel.getTakeNoteID() + ""});
    }

    public void deleteNoteByID(int NoteID, String table) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM '" + table + "' where id = ?", new String[]{String.valueOf(NoteID)});
    }

    public void deleteAllRecycle() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM recycle");
    }
}
