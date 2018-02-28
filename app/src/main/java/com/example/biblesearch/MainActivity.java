package com.example.biblesearch;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.lang.Integer;

public class MainActivity extends Activity {

	private RecyclerView sResultRecycler;
	private RecyclerView.Adapter sAdapter;

	private DBNkjv dbNkjv = null;
	private DBWordVersesPair dbWordVerse = null;
	private String[] books = null;

	public static final String TAG = "BIBLESEARCH";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sResultRecycler = (RecyclerView) findViewById(R.id.search_result);
        sResultRecycler.setHasFixedSize(true);
        sResultRecycler.setLayoutManager(new LinearLayoutManager(this));
        
		dbNkjv=new DBNkjv(this);
        dbNkjv.setJournalDir();

		dbWordVerse=new DBWordVersesPair(this);
		dbWordVerse.setJournalDir();
		
		books = createBooks();
		
		/*if(savedInstanceState!=null){
			tv=savedInstanceState.getStringArrayList("content");
			if(tv!=null){
				for(int i=0; i<tv.size(); i++){
					TextView tv1=new TextView(this);
					tv1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
					tv1.setTextSize(18);
					tv1.setFreezesText(true);
					tv1.setText(tv.get(i));
					layout.addView(tv1);
				}
			}
		}*/
	}
	
	private class BSearch extends AsyncTask<String, Void, String>{

        Set<Integer> finalVerses = null;
        ResultItem[] resList = null;

		protected String doInBackground(String... sPhrase){

			String tmpVList;
			String[] tmpVListSplit;
			List<Integer> tmpVListInts = new ArrayList<>();

			String[] phrase = sPhrase[0].trim().split("\\s+");

			for(String phraseTerm : phrase){
                tmpVList=dbWordVerse.getVerseList(phraseTerm);

                // If the word does'nt exist in DB (List is empty) stop
				if(tmpVList == null)
					return null;

				tmpVListSplit = tmpVList.trim().split(" ");

				tmpVListInts.clear();
				for(String vId: tmpVListSplit)
					tmpVListInts.add(Integer.valueOf(vId));

				if(finalVerses == null)
					finalVerses = new HashSet<Integer>(tmpVListInts);
				else
					finalVerses.retainAll(new HashSet<Integer>(tmpVListInts));
			}

			//Integer[] i = finalVerses.toArray(new Integer[0]);
			Log.i(TAG, "len: "+finalVerses.size()); // For problem when searching for "gather gold", "gods grace"

            // If result Set is empty
            if(finalVerses.isEmpty())
				return null;

            /*
              Parse verseId to get book number, chapter number, verse number and retrieve verse
              Create ResultItem array to pass to recycler view Adapor
            */
            int i=0;
            resList = new ResultItem[finalVerses.size()];
            int bookNum, chapNum, verseNum, verseIdCp;
            String verseInfo, verse;

            long totaltime=0;

            for(Integer verseId: finalVerses){

                verseIdCp = verseId;

                verseNum = verseIdCp % 1000; verseIdCp /= 1000;
                chapNum = verseIdCp % 1000; verseIdCp /= 1000;
                bookNum = verseIdCp;

                long start = System.nanoTime();

                verseInfo = books[bookNum-1]+" "+chapNum+" "+verseNum;
                verse = dbNkjv.getVerse(verseId);

                totaltime += System.nanoTime() - start;

                resList[i++] = new ResultItem(verseInfo, verse);

            }
            Log.i(TAG, "total db access time: "+totaltime/1000000000+" sec");
            return "";
		}

		protected void onPostExecute(String res){
			if(res == null) return;
            sAdapter = new ResultAdapter(resList);
            sResultRecycler.setAdapter(sAdapter);
		}
	}

	public void onSaveInstanceState(Bundle outState){
		/*if(tv!=null)
			outState.putStringArrayList("content", tv);*/
	}

	public void find(View view) throws IOException{
        sResultRecycler.removeAllViewsInLayout();
		String sPhrase=((EditText)findViewById(R.id.search_phrase)).getText().toString();
		(new BSearch()).execute(sPhrase);
	}

	// Return book list
	public String[] createBooks(){
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

/* Error when 'gather gold' is searched
 * 
 */