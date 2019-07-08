package comt.example.a31372.wordbook.Activities;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import comt.example.a31372.wordbook.R;
import comt.example.a31372.wordbook.util.DBHelper;
import comt.example.a31372.wordbook.util.ReadCsv;

public class AddWordsActivity extends AppCompatActivity {


    private EditText wordEditText;
    private EditText wordtranslationEditText;
    private EditText partEditText;
    private EditText chapterEditText;
    private EditText exampleEditText;
    private EditText exampleTranslationEditText;
    private FloatingActionButton fab;
    private Button addWords_btn;

    private String addWord;
    private String addWordTranslation;
    private String addPart;
    private String addChapter;
    private String addExample;
    private String addExampleTranslation;

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_words);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();

    }

    public void init()
    {
        wordEditText = findViewById(R.id.addwords_edittext_word);
        wordtranslationEditText = findViewById(R.id.addwords_edittext_wordranslation);
        partEditText = findViewById(R.id.addwords_edittext_part);
        chapterEditText = findViewById(R.id.addwords_edittext_chapter);
        exampleEditText = findViewById(R.id.addwords_edittext_example);
        exampleTranslationEditText = findViewById(R.id.addwords_edittext_exampletranslation);
        addWords_btn = findViewById(R.id.addwords_btn);

        addWords_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper = new DBHelper(getApplicationContext(), ReadCsv.instance.dbHelpertVersion_csv);
                db = dbHelper.getWritableDatabase();
                getEditText();
                if(!addWord.equals("") && (!addWordTranslation.equals("")) && (!addPart.equals("")) && (!addChapter.equals(""))
                        && (!addExample.equals("")) && (!addExampleTranslation.equals("")))
                {
                    ContentValues wordValues = new ContentValues();
                    wordValues.put("chapter",addChapter);
                    wordValues.put("word",addWord);
                    wordValues.put("word_translation",addWordTranslation);
                    dBInsertWord(Integer.valueOf(addPart),Integer.valueOf(addChapter),addWord,wordValues);

                    int wordId = getWordId(addWord);
                    if(wordId!=-1) {
                        ContentValues exampleValues = new ContentValues();
                        exampleValues.put("example", addExample);
                        exampleValues.put("example_translation", addExampleTranslation);
                        exampleValues.put("word",wordId);
                        db.insert("table_example",null,exampleValues);
                        Toast toast =Toast.makeText(AddWordsActivity.this,"添加成功!",Toast.LENGTH_SHORT);
                        toast.show();//显示消息
                    }
                    finish();
                }
                else
                {
                    Log.i("test1","没添加单词"+addWord+" "+addWordTranslation+" "+addPart+" "
                            +addChapter+" "+addExample+" "+addExampleTranslation);
                }
                db.close();
            }
        });
    }

    private void getEditText()
    {
        addWord = wordEditText.getText().toString();
        addWordTranslation = wordtranslationEditText.getText().toString();
        addPart = partEditText.getText().toString();
        addChapter = chapterEditText.getText().toString();
        addExample = exampleEditText.getText().toString();
        addExampleTranslation = exampleTranslationEditText.getText().toString();
    }

    //获取插入单词的Id
    private int getWordId(String addWord)
    {
        int wordId =-1;
        String word_exit_sql = "select * from table_word where word like '"+addWord+"'";
        Cursor word_find_cursor = db.rawQuery(word_exit_sql,null);
        if(word_find_cursor.moveToFirst())
        {
            wordId = word_find_cursor.getInt(word_find_cursor.getColumnIndex("id"));
        }
        return wordId;
    }

    //插入单词
    private void dBInsertWord(int addPart,int addChapter,String addWord,ContentValues values)
    {
        String part_exit_sql = "select * from table_part where part="+addPart;
        Cursor partCursor = db.rawQuery(part_exit_sql,null);
        if(partCursor.getCount()==0)//数据库中没有这个part
        {
            Log.i("test1","数据库不存在此part");
        }else{
            String chapter_exit_sql = "select * from table_chapter where chapter="+addChapter;
            Cursor chapterCursor = db.rawQuery(chapter_exit_sql,null);
            if(chapterCursor.getCount()==0)
            {
                Log.i("test1","数据库不存在此chapter");
            }else{
                String word_exit_sql = "select * from table_word where word like '"+addWord+"'";
                Cursor word_exit_cursor = db.rawQuery(word_exit_sql,null);
                if(word_exit_cursor.getCount()==0){//如果数据库中没有这个单词
                    db.insert("table_word",null,values);//插入单词
                    Log.i("test1","添加单词成功");
                }else{
                    Log.i("test1","数据库已经存在此word");
                }
                word_exit_cursor.close();
            }
            chapterCursor.close();
        }
        partCursor.close();
    }
}
