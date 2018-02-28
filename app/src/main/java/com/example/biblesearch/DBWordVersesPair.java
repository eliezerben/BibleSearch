package com.example.biblesearch;

import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.content.Context;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DBWordVersesPair extends SQLiteAssetHelper{
	SQLiteDatabase db;
	Context context;
	public DBWordVersesPair(Context context){
		super(context, "1.db", null, 1);
		db = this.getReadableDatabase();
		this.context=context;
	}
	
	public void setJournalDir(){
		db.rawQuery("PRAGMA temp_store_directory='"+context.getDatabasePath("1.db")+"'", null);
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
