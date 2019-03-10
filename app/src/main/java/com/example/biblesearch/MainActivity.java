package com.example.biblesearch;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.os.AsyncTask;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.lang.Integer;

public class MainActivity extends AppCompatActivity {

    private RecyclerView sResultRecycler;
    private ResultAdapter sAdapter;
    List<ResultItem> resList = new ArrayList<>();

    private DBMan dbMan = null;
    private SQLiteDatabase db;
    private String[] books = null;
    private String currentSearchPhrase = null;

    AsyncSearch curAsync = null;
    EditText editText = null;

    public static final String GLOBAL_TAG = "BIBLESEARCH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sResultRecycler = (RecyclerView) findViewById(R.id.search_result);
        sResultRecycler.setHasFixedSize(true);
        sResultRecycler.setLayoutManager(new LinearLayoutManager(this));
        sAdapter = new ResultAdapter(resList);
        sResultRecycler.setAdapter(sAdapter);

        dbMan = new DBMan(this);
        db = dbMan.db;

        books = createBooks();

        editText = (EditText)findViewById(R.id.search_phrase);
        addEvents();

        if(savedInstanceState != null){
            String savedSearchPhrase = savedInstanceState.getString("searchPhrase");
            if(savedSearchPhrase != null) {
                editText.setText(savedSearchPhrase);
                createAsyncSearch();
            }
        }

    }

    private class AsyncSearch extends AsyncTask<String, ResultItem, String>{

        private Set<Integer> finalVerses = null;
        private Integer[] vIdList =null;
        private String TAG;

        public AsyncSearch(String TAG){
            this.TAG = TAG;
        }

        ResultItem[] test;

        protected String doInBackground(String... sPhrase){
            Log.i(TAG, "asyncSearch started.");
            String tmpVList;
            String[] tmpVListSplit;
            List<Integer> tmpVListInts = new ArrayList<>();

            for(String phraseTerm : sPhrase){
                tmpVListInts.clear();
                tmpVList = dbMan.getVerseList(phraseTerm);

                // If the word doesn't exist in DB (List is empty) stop
                if(tmpVList == null || isCancelled())
                    return null;

                //Log.i(TAG, tmpVList);
                tmpVListSplit = tmpVList.trim().split(" ");

                //tmpVListInts.clear();
                for(String vId: tmpVListSplit)
                    tmpVListInts.add(Integer.valueOf(vId));

                if(finalVerses == null)
                    finalVerses = new HashSet<Integer>(tmpVListInts);
                else
                    finalVerses.retainAll(new HashSet<Integer>(tmpVListInts));
            }

            Log.i(TAG, "len: "+finalVerses.size());

            // If result Set is empty
            if(finalVerses.isEmpty())
                return null;

            /*
              Parse verseId to get book number, chapter number, verse number and retrieve verse
              Create ResultItem array to pass to recycler view Adaptor
            */
            vIdList = finalVerses.toArray(new Integer[]{});
            Arrays.sort(vIdList);

            //--------------------------------------------------------
            Log.i(TAG, "total db access time check start");
            //long diff = 0, diff_a;
            long start = System.nanoTime();

            try {
                db.beginTransaction();

                //Improve performance by compiling select statement
                SQLiteStatement selStmt = db.compileStatement("SELECT verse FROM nkjv WHERE vnum=?");

                int bookNum, chapNum, verseNum, verseIdCp;
                String verseInfo, verseText;

                for (int i = 0; i < vIdList.length && !isCancelled(); i++) {


                    if(isCancelled()){
                        Log.e(TAG+": --CANCELLED--", "asyncSearch cancelled.");
                        db.setTransactionSuccessful();
                        db.endTransaction();
                        return null;
                    }

                    selStmt.clearBindings();
                    selStmt.bindLong(1, vIdList[i]);
                    verseText = selStmt.simpleQueryForString();

                    verseIdCp = vIdList[i];

                    verseNum = verseIdCp % 1000; verseIdCp /= 1000;
                    chapNum = verseIdCp % 1000; verseIdCp /= 1000;
                    bookNum = verseIdCp;

                    publishProgress(new ResultItem(
                            books[bookNum-1],
                            bookNum,
                            chapNum,
                            verseNum,
                            verseText
                    ));

                }
                db.setTransactionSuccessful();
            }
            finally {
                db.endTransaction();
            }

            long totaltime = System.nanoTime() - start;
            Log.i(TAG, "Total db access time: "+totaltime/1000000000.0+" sec ");

            return "";
        }

        protected void onProgressUpdate(ResultItem... res){
            if(!isCancelled()) {
                resList.add(res[0]);
                sAdapter.updateInsert();
            }
        }

        protected void onPostExecute(String res){
            Log.i(TAG+": --test--", "onPostExecute run "+System.currentTimeMillis());
            curAsync = null;
        }

        protected void onCancelled(){
            Log.i(TAG+": --test--", "onCancelled run");
            // When another search is made when the current search is still going on,
            // wait until the current search is cancelled and start the next search.
            createAsyncSearch();
        }

    }

    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        if(currentSearchPhrase!=null){
            outState.putString("searchPhrase", currentSearchPhrase);
        }
    }

    int async_id=0;

    public void find(View view) {
        async_id++;
        //hideSoftKeyboard();
        if(curAsync!=null) {
            curAsync.cancel(false);
        }
        else {
            createAsyncSearch();
        }
    }

    private void createAsyncSearch(){
        sAdapter.clear();
        Log.i(GLOBAL_TAG + " " + async_id + " ", "resList cleared Size: " + sAdapter.getItemCount());

        String sPhrase = editText.getText().toString();
        currentSearchPhrase = sPhrase;
        String[] sPhraseList = sPhrase.toLowerCase().trim().split("\\s+");
        // Setting so that ResultAdapter can highlight searched words
        sAdapter.setSearchPhraseList(sPhraseList);
        curAsync = new AsyncSearch(GLOBAL_TAG+" "+async_id+" ");

        curAsync.execute(sPhraseList);
    }

    void addEvents(){
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent e){
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    find(editText);
                }
                return true;
            }
        });
    }

    void hideSoftKeyboard(){
        View v = this.getCurrentFocus();
        if(v!=null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            v.clearFocus();
        }
    }

    // Return book list
    private String[] createBooks(){
        return new String[]{
                "Genesis",
                "Exodus",
                "Leviticus",
                "Numbers",
                "Deuteronomy",
                "Joshua",
                "Judges",
                "Ruth",
                "1 Samuel",
                "2 Samuel",
                "1 Kings",
                "2 Kings",
                "1 Chronicles",
                "2 Chronicles",
                "Ezra",
                "Nehemiah",
                "Esther",
                "Job",
                "Psalms",
                "Proverbs",
                "Ecclesiastes",
                "Song of Solomon",
                "Isaiah",
                "Jeremiah",
                "Lamentations",
                "Ezekiel",
                "Daniel",
                "Hosea",
                "Joel",
                "Amos",
                "Obadiah",
                "Jonah",
                "Micah",
                "Nahum",
                "Habakkuk",
                "Zephaniah",
                "Haggai",
                "Zechariah",
                "Malachi",
                "Matthew",
                "Mark",
                "Luke",
                "John",
                "Acts",
                "Romans",
                "1 Corinthians",
                "2 Corinthians",
                "Galatians",
                "Ephesians",
                "Philippians",
                "Colossians",
                "1 Thessalonians",
                "2 Thessalonians",
                "1 Timothy",
                "2 Timothy",
                "Titus",
                "Philemon",
                "Hebrews",
                "James",
                "1 Peter",
                "2 Peter",
                "1 John",
                "2 John",
                "3 John",
                "Jude",
                "Revelation"
        };
    }
}