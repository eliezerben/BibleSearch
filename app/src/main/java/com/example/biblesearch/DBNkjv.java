package com.example.biblesearch;

import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.content.Context;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DBNkjv extends SQLiteAssetHelper{
	SQLiteDatabase db;
	Context context;
	public DBNkjv(Context context){
		super(context, "2.db", null, 1);
		db = this.getReadableDatabase();
		this.context=context;		
	}

	public void setJournalDir(){
		db.rawQuery("PRAGMA temp_store_directory='"+context.getDatabasePath("2.db")+"'", null);
	}

	public String getVerse(Integer verseID){
		//SQLiteDatabase db=this.getReadableDatabase();
		Cursor verse = db.query(
		        "nkjv",
                new String[]{"verse"},
                "vnum = ?",
                new String[] {String.valueOf(verseID)},
                null, null, null
        );

		//String result;
        try {
            if (verse.moveToFirst()) {
                return verse.getString(0);
            }
        }
        finally {
            if(verse!=null) {
                verse.close();
            }
        }
		return null;
	}
}