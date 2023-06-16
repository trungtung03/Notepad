package com.example.notepad.Model;

public class TakeNoteModel {

    private String title;
    private String image;
    private String timeNote;
    private String notes;

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

    public TakeNoteModel(String title, String image, String timeNote, String notes) {
        this.title = title;
        this.image = image;
        this.timeNote = timeNote;
        this.notes = notes;
    }

    public TakeNoteModel() {
    }
}
