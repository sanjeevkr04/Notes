package com.sanjeevkr7404.notes.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = Note.class, version = 2)
abstract class NoteDatabase extends RoomDatabase {
    private static NoteDatabase instance;

    static synchronized NoteDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), NoteDatabase.class, "Notes")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    abstract NoteDao noteDao();
}
