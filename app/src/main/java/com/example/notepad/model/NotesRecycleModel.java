package com.example.notepad.model;

public class NotesRecycleModel {

    private int takeNoteID;
    private String title;
    private String image;
    private String timeNote;
    private String notes;

    public int getTakeNoteID() {
        return takeNoteID;
    }

    public void setTakeNoteID(int takeNoteID) {
        this.takeNoteID = takeNoteID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTimeNote() {
        return timeNote;
    }

    public void setTimeNote(String timeNote) {
        this.timeNote = timeNote;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public NotesRecycleModel(int takeNoteID, String title, String image, String timeNote, String notes) {
        this.takeNoteID = takeNoteID;
        this.title = title;
        this.image = image;
        this.timeNote = timeNote;
        this.notes = notes;
    }

    public NotesRecycleModel() {
    }
}
