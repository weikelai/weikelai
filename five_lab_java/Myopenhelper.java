package com.example.chapter03;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class Myopenhelper extends SQLiteOpenHelper {
    public Myopenhelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Added CHECK constraint for age to ensure it is non-negative
        String sql = "CREATE TABLE IF NOT EXISTS person (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, age INTEGER CHECK(age >= 0))";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // For simplicity in this experiment, we drop and recreate. 
        // In production, use ALTER TABLE.
        db.execSQL("DROP TABLE IF EXISTS person");
        onCreate(db);
    }
}