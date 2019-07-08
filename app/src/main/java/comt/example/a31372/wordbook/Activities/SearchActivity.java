package comt.example.a31372.wordbook.Activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import comt.example.a31372.wordbook.Adapters.SearchAdapter;
import comt.example.a31372.wordbook.Data.ChapterData;
import comt.example.a31372.wordbook.Data.DescribeData;
import comt.example.a31372.wordbook.Data.PartData;
import comt.example.a31372.wordbook.Data.WordData;
import comt.example.a31372.wordbook.R;
import comt.example.a31372.wordbook.util.DBHelper;
import comt.example.a31372.wordbook.util.ReadCsv;

public class SearchActivity extends AppCompatActivity {

    private EditText search_edittext;
    private Button search_btn;
    private ListView search_listView;

    private List<PartData> partDataListSearch = new ArrayList<>();
    private List<ChapterData> chapterDataListSearch = new ArrayList<>();
    private List<WordData> wordDataListSearch = new ArrayList<>();
    private List<DescribeData> exampleDataListSearch =  new ArrayList<>();

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //初始化
        init();
    }


    private void init()
    {
        search_edittext = findViewById(R.id.search_edittext);
        search_btn = findViewById(R.id.search_btn);
        search_listView = findViewById(R.id.search_listview);

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String wordStr = search_edittext.getText().toString();
                if(!wordStr.equals(""))//edittext不为空
                {
                    if(isEnglish(wordStr)){//如果是英文
                        searchWords(wordStr);
                    }else{//如果是中文
                        chineseSearch(wordStr);
                    }
                }
            }
        });
    }

    //判断是否是英文
    public static boolean isEnglish(String charaString){
        return charaString.matches("^[a-zA-Z]*");
    }

    //中文搜索改进版
    public void chineseSearch(String wordStr)
    {
        if (wordDataListSearch != null && wordDataListSearch.size() > 0) {//如果上一次的搜索集合有残留先清空
            //清空搜索结果集合
            clearSearchList();
        }
        dbHelper = new DBHelper(getApplicationContext(),ReadCsv.instance.dbHelpertVersion_csv);
        db = dbHelper.getReadableDatabase();
        //先释义搜索
        Cursor wordCursor = db.rawQuery("select * from table_word where word_translation like '%" + wordStr + "%'", null);
        if(wordCursor.moveToFirst()) {//如果有对应的释义
            do {
                int wordId = wordCursor.getInt(wordCursor.getColumnIndex("id"));
                //example
                Cursor exampleCursor = db.rawQuery("select * from table_example where word=" + wordId, null);
                if (exampleCursor.moveToFirst()) {
                    String example = exampleCursor.getString(exampleCursor.getColumnIndex("example"));
                    String example_translation = exampleCursor.getString(exampleCursor.getColumnIndex("example_translation"));
                    //结果加入到exampleDataListSearch
                    DescribeData exampleData = new DescribeData();
                    exampleData.setWord(wordId);
                    exampleData.setExample(example);
                    exampleData.setExampleTranslation(example_translation);
                    exampleDataListSearch.add(exampleData);
                }
                //收入对应word
                String word = wordCursor.getString(wordCursor.getColumnIndex("word"));
                String word_translation = wordCursor.getString(wordCursor.getColumnIndex("word_translation"));
                int chapter = wordCursor.getInt(wordCursor.getColumnIndex("chapter"));
                WordData wordData = new WordData();
                wordData.setWords(word);
                wordData.setWord_translation(word_translation);
                wordData.setChapter(chapter);
                wordDataListSearch.add(wordData);
                //收入对应chapter
                Cursor chapterCursor = db.rawQuery("select * from table_chapter where id=" + chapter, null);//搜索该part下的所有chapter
                chapterCursor.moveToFirst();
                int thechapter = chapterCursor.getInt(chapterCursor.getColumnIndex("chapter"));
                String chapterName = chapterCursor.getString(chapterCursor.getColumnIndex("chapter_name"));
                int partId = chapterCursor.getInt(chapterCursor.getColumnIndex("part"));
                //结果加入到chapterDataListSearch
                ChapterData chapterData = new ChapterData();
                chapterData.setChapter(thechapter);
                chapterData.setChapter_name(chapterName);
                chapterData.setPart(partId);
                chapterDataListSearch.add(chapterData);
                Cursor partCursor = db.rawQuery("select * from table_part where id=" + partId, null);//搜索该part下的所有chapter
                partCursor.moveToFirst();
                String partName = partCursor.getString(partCursor.getColumnIndex("part_name"));
                //结果加入到partDataListSearch
                PartData partData = new PartData();
                partData.setPart(partId);
                partData.setPart_name(partName);
                partDataListSearch.add(partData);
                partCursor.close();
                chapterCursor.close();
            } while (wordCursor.moveToNext());
            wordCursor.close();
        }
        //场景搜索
        String sql = "select * from table_part where part_name like '%" +wordStr + "%'";
        Cursor cursorTwo = db.rawQuery(sql,null);
        if(cursorTwo.getCount()!=0){//如果为场景搜索
            cursorTwo.moveToFirst();
            do {
                String partName = cursorTwo.getString(cursorTwo.getColumnIndex("part_name"));
                int part = cursorTwo.getInt(cursorTwo.getColumnIndex("part"));
                int partId = cursorTwo.getInt(cursorTwo.getColumnIndex("id"));//获取该part的id
                //结果加入到partDataListSearch
                PartData partData = new PartData();
                partData.setPart(part);
                partData.setPart_name(partName);
                partDataListSearch.add(partData);
                Cursor chapterCursorTwo = db.rawQuery("select * from table_chapter where part="+part, null);//搜索该part下的所有chapter
                if (chapterCursorTwo.moveToFirst()) {
                    do {
                        int chapterId = chapterCursorTwo.getInt(chapterCursorTwo.getColumnIndex("id"));
                        int thechapter = chapterCursorTwo.getInt(chapterCursorTwo.getColumnIndex("chapter"));
                        String chapterName = chapterCursorTwo.getString(chapterCursorTwo.getColumnIndex("chapter_name"));
                        //结果加入到chapterDataListSearch
                        ChapterData chapterData = new ChapterData();
                        chapterData.setChapter(thechapter);
                        chapterData.setChapter_name(chapterName);
                        chapterData.setPart(partId);
                        chapterDataListSearch.add(chapterData);
                        Cursor wordCursorTwo = db.rawQuery("select * from table_word where chapter="+thechapter, null);
                        if (wordCursorTwo.moveToFirst()) {//单词不为空
                            do {
                                Log.i("test3","1");
                                String word = wordCursorTwo.getString(wordCursorTwo.getColumnIndex("word"));
                                String word_translation = wordCursorTwo.getString(wordCursorTwo.getColumnIndex("word_translation"));
                                int wordId = wordCursorTwo.getInt(wordCursorTwo.getColumnIndex("id"));
                                int chapter = wordCursorTwo.getInt(wordCursorTwo.getColumnIndex("chapter"));
                                WordData wordData = new WordData();
                                wordData.setWords(word);
                                wordData.setWord_translation(word_translation);
                                wordData.setChapter(chapter);
                                wordDataListSearch.add(wordData);
                                //example
                                Cursor exampleCursor = db.rawQuery("select * from table_example where word=" + wordId, null);
                                if (exampleCursor.moveToFirst()) {
                                    String example = exampleCursor.getString(exampleCursor.getColumnIndex("example"));
                                    String example_translation = exampleCursor.getString(exampleCursor.getColumnIndex("example_translation"));
                                    //结果加入到exampleDataListSearch
                                    DescribeData exampleData = new DescribeData();
                                    exampleData.setWord(wordId);
                                    exampleData.setExample(example);
                                    exampleData.setExampleTranslation(example_translation);
                                    exampleDataListSearch.add(exampleData);
                                }
                            } while (wordCursorTwo.moveToNext());
                            wordCursorTwo.close();
                        }
                    } while (chapterCursorTwo.moveToNext());
                    chapterCursorTwo.close();
                }
            }while(cursorTwo.moveToNext());
            cursorTwo.close();
        }
        if(wordDataListSearch.size()>0) {//搜索结果不为空
            //设置adapter
            SearchAdapter searchAdapter = new SearchAdapter(SearchActivity.this, partDataListSearch,
                    chapterDataListSearch, wordDataListSearch, exampleDataListSearch);
            search_listView.setAdapter(searchAdapter);
        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(SearchActivity.this)
                    .setTitle("温馨提示")
                    .setMessage("没有该释义，或该场景下没有单词！")
                    .create();
            alertDialog.show();
        }
        db.close();
    }

    //模糊搜索
    public void searchWords(String wordStr)
    {
       // ReadCsv.instance.dbHelpertVersion_csv++;
        dbHelper = new DBHelper(getApplicationContext(), ReadCsv.instance.dbHelpertVersion_csv);
        db = dbHelper.getReadableDatabase();
        Cursor wordCursor = db.rawQuery("select * from table_word where word like '%" + wordStr + "%'", null);
        if (wordDataListSearch != null && wordDataListSearch.size() > 0) {
            //清空模糊搜索结果集合
            partDataListSearch.clear();
            chapterDataListSearch.clear();
            wordDataListSearch.clear();
            exampleDataListSearch.clear();
        }
        if(wordCursor.moveToFirst()) {
            do {
                //word
                String word = wordCursor.getString(wordCursor.getColumnIndex("word"));
                String word_translation = wordCursor.getString(wordCursor.getColumnIndex("word_translation"));
                int wordId = wordCursor.getInt(wordCursor.getColumnIndex("id"));
                int chapter = wordCursor.getInt(wordCursor.getColumnIndex("chapter"));
                //结果加入到wordDataListSearch
                WordData wordData = new WordData();
                wordData.setWords(word);
                wordData.setWord_translation(word_translation);
                wordData.setChapter(chapter);
                wordDataListSearch.add(wordData);
                Log.i("test1",""+wordId+" "+word);
                //chapter
                Cursor chapterCursor = db.rawQuery("select * from table_chapter where chapter="+chapter, null);
                if(chapterCursor.moveToFirst()) {
                    String chapterName = chapterCursor.getString(chapterCursor.getColumnIndex("chapter_name"));
                    //int chapterId = chapterCursor.getInt(chapterCursor.getColumnIndex("chapter"));
                    int partId = chapterCursor.getInt(chapterCursor.getColumnIndex("part"));
                    //结果加入到chapterDataListSearch
                    ChapterData chapterData = new ChapterData();
                    chapterData.setChapter(chapter);
                    chapterData.setChapter_name(chapterName);
                    chapterData.setPart(partId);
                    chapterDataListSearch.add(chapterData);
                    //part
                    Cursor partCursor = db.rawQuery("select * from table_part where part= " + partId, null);
                    if(partCursor.moveToFirst()) {
                        String part_name = partCursor.getString(partCursor.getColumnIndex("part_name"));
                        //结果加入到partDataListSearch
                        PartData partData = new PartData();
                        partData.setPart(partId);
                        partData.setPart_name(part_name);
                        partDataListSearch.add(partData);
                    }
                    partCursor.close();
                }
                chapterCursor.close();

                //example
                Cursor exampleCursor = db.rawQuery("select * from table_example where word=" + wordId , null);
                if(exampleCursor.moveToFirst()) {
                    String example = exampleCursor.getString(exampleCursor.getColumnIndex("example"));
                    String example_translation = exampleCursor.getString(exampleCursor.getColumnIndex("example_translation"));
                    //结果加入到exampleDataListSearch
                    DescribeData exampleData = new DescribeData();
                    exampleData.setWord(wordId);
                    exampleData.setExample(example);
                    exampleData.setExampleTranslation(example_translation);
                    exampleDataListSearch.add(exampleData);
                }
                exampleCursor.close();
            } while (wordCursor.moveToNext());
        }
        wordCursor.close();
        db.close();
        if(wordDataListSearch.size()>0) {
            //设置adapter
            SearchAdapter searchAdapter = new SearchAdapter(SearchActivity.this, partDataListSearch,
                    chapterDataListSearch, wordDataListSearch, exampleDataListSearch);
            search_listView.setAdapter(searchAdapter);
        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(SearchActivity.this)
                    .setTitle("温馨提示")
                    .setMessage("没有该单词！")
                    .create();
                    alertDialog.show();
        }

    }

    //清空搜索结果集合
    private void clearSearchList()
    {
        partDataListSearch.clear();
        chapterDataListSearch.clear();
        wordDataListSearch.clear();
        exampleDataListSearch.clear();
    }

}
