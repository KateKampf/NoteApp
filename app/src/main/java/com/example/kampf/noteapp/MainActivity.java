package com.example.kampf.noteapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;

import com.orm.SugarContext;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String CURRENT_POSITION_KEY = "CURRENT_POSITION_KEY";
    static final String IS_EDITING_EXTRA = "IS_EDITING_EXTRA";
    static final String NOTE_TITLE_EXTRA = "NOTE_TITLE_EXTRA";
    static final String NOTE_EXTRA = "NOTE_EXTRA";

    RecyclerView recyclerView;
    FloatingActionButton fab;
    CollapsingToolbarLayout collapsingToolbarLayout;

    NotesAdapter notesAdapter;
    List<Note> notes = new ArrayList<>();

    long currentCount;
    int currentPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SugarContext.init(this);

        notesAdapter = new NotesAdapter(this, notes);

        setRecyclerView();
        setFloatingActionButton();
        setCollapsingToolbarLayout();
        handlingSwipe();

        currentCount = Note.count(Note.class);

        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt("current");
        }
    }

    private void setRecyclerView() {
        recyclerView = findViewById(R.id.rvNotes);

        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

        recyclerView.setLayoutManager(gridLayoutManager);

        if (currentCount >= 0) {

            notes = Note.listAll(Note.class);

            notesAdapter = new NotesAdapter(MainActivity.this, notes);
            recyclerView.setAdapter(notesAdapter);

            if (notes.isEmpty()) {
                Snackbar.make(recyclerView, R.string.no_notes_added, Snackbar.LENGTH_LONG).show();
            }
        }

        notesAdapter.SetOnItemClickListener(new NotesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Intent intent = new Intent(getApplicationContext(), AddNoteActivity.class);
                intent.putExtra(IS_EDITING_EXTRA, true);
                intent.putExtra(NOTE_TITLE_EXTRA, notes.get(position).title);
                intent.putExtra(NOTE_EXTRA, notes.get(position).note);

                currentPosition = position;

                startActivity(intent);
            }
        });
    }

    private void setFloatingActionButton() {
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddNoteActivity.class));
            }
        });
    }

    public void handlingSwipe() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                final int position = viewHolder.getAdapterPosition();
                final Note note = notes.get(viewHolder.getAdapterPosition());
                notes.remove(viewHolder.getAdapterPosition());
                notesAdapter.notifyItemRemoved(position);

                note.delete();
                currentCount -= 1;

                Snackbar snack = Snackbar.make(recyclerView, R.string.note_deleted, Snackbar.LENGTH_SHORT)
                        .setAction("CANCEL", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                note.save();
                                notes.add(position, note);
                                notesAdapter.notifyItemInserted(position);
                                currentCount += 1;
                            }
                        });

                View view = snack.getView();
                TextView tv = view.findViewById(android.support.design.R.id.snackbar_action);
                tv.setTextColor(Color.GRAY);
                snack.show();

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @SuppressLint("ResourceAsColor")
    private void setCollapsingToolbarLayout() {
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setCollapsedTitleTextColor(R.color.colorAccent);
        collapsingToolbarLayout.setExpandedTitleColor(R.color.colorAccent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_POSITION_KEY, currentPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentPosition = savedInstanceState.getInt(CURRENT_POSITION_KEY);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final long newCount = Note.count(Note.class);

        if (newCount > currentCount) {

            Note note = Note.last(Note.class);

            notes.add(note);
            notesAdapter.notifyItemInserted((int) newCount);

            currentCount = newCount;
        }

        if (currentPosition != -1) {
            notes.set(currentPosition, Note.listAll(Note.class).get(currentPosition));
            notesAdapter.notifyItemChanged(currentPosition);
        }
    }
}


