package com.example.biblesearch;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.database.Cursor;
import android.content.Context;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import android.util.Log;

public class DBMan extends SQLiteAssetHelper{

	SQLiteDatabase db;
	Context context;
    public static final String TAG = "BIBLESEARCH";

	public DBMan(Context context){
		super(context, "db.db", null, 1);
		db = this.getReadableDatabase();
        setPragma();
		this.context = context;
	}

	public void setPragma(){
        //db.rawQuery("PRAGMA temp_store_directory='"+context.getDatabasePath("db.db")+"'", null);
        db.rawQuery("PRAGMA journal_mode = OFF", null);
	}

    public String getVerseList(String word){
        Cursor verseList = db.query(
                "wordVerses",
                new String[] {"verses"},
                "word= ?",
                new String[] {word},
                null, null, null
        );

        try {
            if (verseList.moveToFirst()) {
                return verseList.getString(0);
            }
        }
        finally {
            if(verseList!=null){
                verseList.close();
            }
        }
        return null;
    }
}