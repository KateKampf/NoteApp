package com.example.kampf.noteapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import java.util.List;

public class AddNoteActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton fab;

    private EditText etTitle;
    private EditText etDesc;
    private String title;
    private String note;

    private boolean isEditing;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        setToolbar();
        setFloatingActionButton();

        etTitle = findViewById(R.id.etTitle);
        etDesc = findViewById(R.id.etDesc);

        isEditing = getIntent().getBooleanExtra(MainActivity.IS_EDITING_EXTRA, false);

        if (isEditing) {
            toolbar.setTitle(R.string.edit_note);
            title = getIntent().getStringExtra(MainActivity.NOTE_TITLE_EXTRA);
            note = getIntent().getStringExtra(MainActivity.NOTE_EXTRA);

            etTitle.setText(title);
            etDesc.setText(note);

        }
    }

    @SuppressLint("ResourceAsColor")
    private void setToolbar() {
        toolbar = findViewById(R.id.toolbarAddNote);
        toolbar.setTitle(R.string.add_new_note);
        toolbar.setTitleTextColor(R.color.colorAccent);
        toolbar.setNavigationIcon(R.drawable.ic_clear);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setFloatingActionButton() {
        fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newTitle = etTitle.getText().toString();
                String newDesc = etDesc.getText().toString();

                if (!isEditing) {
                    Note note = new Note(newTitle, newDesc);
                    note.save();
                } else {
                    List<Note> notes = Note.find(Note.class, "title = ?", title);
                    if (notes.size() > 0) {
                        Note note = notes.get(0);
                        note.title = newTitle;
                        note.note = newDesc;
                        note.save();
                    }
                }

                finish();
            }
        });
    }

}
