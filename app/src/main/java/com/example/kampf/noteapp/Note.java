package com.example.kampf.noteapp;

import com.orm.SugarRecord;

public class Note extends SugarRecord {

    String title;
    String note;

    public Note() {
    }

    public Note(String title, String note) {
        this.title = title;
        this.note = note;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

}
