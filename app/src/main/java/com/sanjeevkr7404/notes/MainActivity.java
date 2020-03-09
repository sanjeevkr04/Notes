package com.sanjeevkr7404.notes;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sanjeevkr7404.notes.database.Note;
import com.sanjeevkr7404.notes.database.NoteModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    NoteModel noteModel;
    RecycleViewAdapter adapter;
    Note delNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final LinearLayout undoLayout = findViewById(R.id.undo_layout);
        final TextView undoButton = findViewById(R.id.undo_button);
        final TextView emptyView = findViewById(R.id.empty_view);
        final FloatingActionButton floatingactionButton = findViewById(R.id.floatingActionButton);

        //floatingActionButton Layout Params
        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 60, 60);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        adapter = new RecycleViewAdapter();
        recyclerView.setAdapter(adapter);
        adapter.listener = new RecycleViewAdapter.OnClickListener() {
            @Override
            public void OnClick(int position) {
                Note note = adapter.getNote(position);
                intent(3, note.getId(), "Edit note", note.getTitle(), note.getDescription());
            }
        };

        noteModel = new ViewModelProvider(MainActivity.this).get(NoteModel.class);
        noteModel.getAllNotes(this).observe(MainActivity.this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                if (notes.isEmpty())
                    emptyView.setVisibility(View.VISIBLE);
                else emptyView.setVisibility(View.GONE);
                adapter.submitList(notes);
            }
        });

        floatingactionButton.setLayoutParams(params);
        floatingactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent(2, -1, "Add note", "", "");
            }
        });

        //Animations
        final Animation floatUp = AnimationUtils.loadAnimation(MainActivity.this, R.anim.layout_float_up);
        final Animation floatDown = AnimationUtils.loadAnimation(MainActivity.this, R.anim.layout_float_down);

        //timer
        final CountDownTimer timer = new CountDownTimer(5000, 5000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                undoLayout.setVisibility(View.GONE);
                params.setMargins(0, 0, 60, 60);
                floatingactionButton.startAnimation(floatDown);
            }
        };

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                delNote = adapter.getNote(viewHolder.getAdapterPosition());
                noteModel.delete(delNote);

                //Undo task
                undoLayout.setVisibility(View.VISIBLE);
                params.setMargins(0, 0, 60, 120);

                floatingactionButton.startAnimation(floatUp);
                undoLayout.startAnimation(floatUp);
                undoLayout.bringToFront();
                timer.start();
                undoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        noteModel.insert(delNote);
                        timer.cancel();
                        undoLayout.setVisibility(View.GONE);
                        floatingactionButton.startAnimation(floatDown);
                        params.setMargins(0, 0, 60, 60);
                    }
                });
            }
        }).attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_all_notes) {
            noteModel.deleteAll();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2) {
            int id = data.getIntExtra(NoteActivity.rId, -1);
            String title = data.getStringExtra(NoteActivity.rTitle);
            String description = data.getStringExtra(NoteActivity.rDescription);
            if (title.isEmpty() && description.isEmpty()) {
                if (id != -1) {
                    Note note = new Note(title, description);
                    note.setId(id);
                    noteModel.delete(note);
                }
            } else {
                if (requestCode == 2) {
                    noteModel.insert(new Note(title, description));
                    Toast.makeText(MainActivity.this, "Note added", Toast.LENGTH_SHORT).show();
                } else if (requestCode == 3) {
                    Note note = new Note(title, description);
                    note.setId(id);
                    noteModel.update(note);
                    Toast.makeText(MainActivity.this, "Note updated", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void intent(int requestCode, int id, String menu, String title, String description) {
        Intent intent = new Intent(MainActivity.this, NoteActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("menu", menu);
        intent.putExtra("title", title);
        intent.putExtra("description", description);
        startActivityForResult(intent, requestCode);
    }
}
