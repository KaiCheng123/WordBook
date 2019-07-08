package comt.example.a31372.wordbook.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import comt.example.a31372.wordbook.Activities.MainActivity;
import comt.example.a31372.wordbook.Data.ChapterData;
import comt.example.a31372.wordbook.Data.DescribeData;
import comt.example.a31372.wordbook.Data.PartData;
import comt.example.a31372.wordbook.Data.WordData;


public class ReadDB {
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    public static ReadDB instance;

    public ReadDB(Context context,int dbHelperVersion){
        instance = this;
        DBHandle(context,dbHelperVersion);
    }

    public void DBHandle(Context context,int dbHelperVersion)
    {
        dbHelper = new DBHelper(context,dbHelperVersion);
        db = dbHelper.getReadableDatabase();
    }

    //读取数据库中的part到partList中
    public void readPart(){
        Log.i("db_test","进入到readPart");
        String sql = "select * from table_part";
        Cursor cursor = db.rawQuery(sql,null);
        MainActivity.instane.partList.clear();//清空partList
        if(cursor.moveToFirst()){//如果数据库part不为空
            do{
                int part = cursor.getInt(cursor.getColumnIndex("part"));
                String partName = cursor.getString(cursor.getColumnIndex("part_name"));
                PartData partData = new PartData();
                partData.setPart(part);
                partData.setPart_name(partName);
                MainActivity.instane.partList.add(partData);//加入partList
            }while(cursor.moveToNext());
            cursor.close();
        }else{
            Log.i("db_test","table_part中还没有数据");
        }
        db.close();
    }

    //读取数据库中的chapter到chapterList中
    public void readChapter(int selectedPart){
        String sql = "select * from table_chapter where part="+ selectedPart;
        Cursor cursor = db.rawQuery(sql,null);
        MainActivity.instane.chapterList.clear();
        if(cursor.moveToFirst()){
            do{
                int chapter = cursor.getInt(cursor.getColumnIndex("chapter"));
                String chapterName = cursor.getString(cursor.getColumnIndex("chapter_name"));
                ChapterData chapterData = new ChapterData();
                chapterData.setChapter(chapter);
                chapterData.setChapter_name(chapterName);
                chapterData.setPart(selectedPart);
                MainActivity.instane.chapterList.add(chapterData);//加入chapterList
            }while(cursor.moveToNext());
            cursor.close();
        }
        db.close();
    }

    //读取数据库中的word到wordList和exampleList中
    public void readWordAndExample(int selectedChapter){
        String find_chapter_sql = "select * from table_chapter where chapter="+selectedChapter;
        Cursor chapter_cursor = db.rawQuery(find_chapter_sql,null);
        //清空list中的数据
        MainActivity.instane.wordList.clear();
        MainActivity.instane.exampleList.clear();
            if (chapter_cursor.moveToFirst()) {
                String find_word_sql = "select * from table_word where chapter="+ selectedChapter;
                Cursor word_cursor = db.rawQuery(find_word_sql,null);
                if(word_cursor.moveToFirst()){
                        do{
                            //获取word
                            int word_id = word_cursor.getInt(word_cursor.getColumnIndex("id"));
                            String word = word_cursor.getString(word_cursor.getColumnIndex("word"));
                            String word_translation = word_cursor.getString(word_cursor.getColumnIndex("word_translation"));
                            //word加入wordList
                            WordData wordData = new WordData();
                            wordData.setWords(word);
                            wordData.setWord_translation(word_translation);
                            wordData.setChapter(selectedChapter);
                            wordData.setWordID(word_id);
                            MainActivity.instane.wordList.add(wordData);
                            //获取example
                            String find_example_sql ="select * from table_example where word="+word_id;
                            Cursor example_cursor = db.rawQuery(find_example_sql,null);
                            if(example_cursor.getCount()!=0) {
                                if (example_cursor.moveToFirst()) {
                                    do{
                                        //获取example
                                        String example = example_cursor.getString(example_cursor.getColumnIndex("example"));
                                        String example_translation = example_cursor.getString(example_cursor.getColumnIndex("example_translation"));
                                        //example加入exampleList
                                        DescribeData exampleData = new DescribeData();
                                        exampleData.setWord(word_id);
                                        exampleData.setExample(example);
                                        exampleData.setExampleTranslation(example_translation);
                                        MainActivity.instane.exampleList.add(exampleData);
                                    }while(example_cursor.moveToNext());
                                }
                            }else{
                                Log.i("db_test","数据库中没有此单词："+word+"的example");
                            }
                        }while(word_cursor.moveToNext());
                    }else{
                    Log.i("db_test","数据库中没有找到该chapter："+selectedChapter+"的单词");
                }
            }else{
            Log.i("db_test","数据库中没有找到该word对应的chapter："+selectedChapter);
        }
        db.close();
    }


}
