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
	    //set JournalDir
        //db.rawQuery("PRAGMA temp_store_directory='"+context.getDatabasePath("db.db")+"'", null);
        db.rawQuery("PRAGMA journal_mode = OFF", null);
	}

	public String[] getVerses(Integer[] vIdList){

	    String[] result = new String[vIdList.length];

        Cursor verse = null;

        long start = System.nanoTime();

        try {
            db.beginTransaction();

            // Improve performance by compiling select statement
            SQLiteStatement selStmt= db.compileStatement("SELECT verse FROM nkjv WHERE vnum=?");

            for (int i = 0; i < vIdList.length; i++) {

                selStmt.clearBindings();
                selStmt.bindLong(1, vIdList[i]);
                result[i] = selStmt.simpleQueryForString();

                /*
                verse = db.query(
                        "nkjv",
                        new String[]{"verse"},
                        "vnum = ?",
                        new String[]{String.valueOf(vIdList[i])},
                        null, null, null
                );

                if (verse.moveToFirst()) {
                    result[i] = verse.getString(0);
                }

                if (verse != null) {
                    verse.close();
                }
                */

            }
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }

        long totaltime = System.nanoTime() - start;
        Log.i(TAG, "total db access time: "+totaltime/1000000000.0+" sec");

        return result;
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