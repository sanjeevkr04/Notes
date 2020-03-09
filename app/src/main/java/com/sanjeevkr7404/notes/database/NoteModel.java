package com.sanjeevkr7404.notes.database;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.sanjeevkr7404.notes.R;

import java.util.List;

public class NoteModel extends ViewModel {

    private NoteDao noteDao;
    private LiveData<List<Note>> allNotes;

    public LiveData<List<Note>> getAllNotes(Context context) {
        NoteDatabase database = NoteDatabase.getInstance(context);
        noteDao = database.noteDao();
        return fullQuery();
    }

    public void insert(Note note) {
        Task task = new Task(note, R.string.INSERT);
        new Thread(task).start();
    }

    public void delete(Note note) {
        Task task = new Task(note, R.string.DELETE);
        new Thread(task).start();
    }

    public void deleteAll() {
        Task task = new Task(R.string.DELETE_ALL);
        new Thread(task).start();
    }

    public void update(Note note) {
        Task task = new Task(note, R.string.UPDATE);
        new Thread(task).start();
    }

    private LiveData<List<Note>> fullQuery() {
        Thread thread = new Thread(new Task(R.string.QUERY));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return allNotes;
    }

    private class Task implements Runnable {
        private Note note;
        private int task;

        Task(int task) {
            this.task = task;
        }

        Task(Note note, int task) {
            this.note = note;
            this.task = task;
        }

        @Override
        public void run() {
            switch (task) {
                case R.string.INSERT:
                    noteDao.insert(note);
                    break;
                case R.string.DELETE:
                    noteDao.delete(note);
                    break;
                case R.string.UPDATE:
                    noteDao.update(note);
                    break;
                case R.string.QUERY:
                    Log.d("ok", "run: ");
                    allNotes = noteDao.fullQuery();
                    break;
                case R.string.DELETE_ALL:
                    noteDao.deleteAll();
                    break;
            }
        }
    }
}
