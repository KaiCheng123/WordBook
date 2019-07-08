package comt.example.a31372.wordbook.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import comt.example.a31372.wordbook.Data.ChapterData;
import comt.example.a31372.wordbook.Data.DescribeData;
import comt.example.a31372.wordbook.Data.PartData;
import comt.example.a31372.wordbook.Data.WordData;

public class ReadCsv
{
    private String filePath=null; // CSV文件路径
    public static ReadCsv instance;
    public static int dbHelpertVersion_csv=1;

    //数据库操作
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public ReadCsv(Context context)
    {
        instance = this;
        updateDBHelperVersion(context);
    }

    //更新DBHelper的version
    public void updateDBHelperVersion(Context context)
    {
        dbHelper = new DBHelper(context,dbHelpertVersion_csv);
        //dbHelpertVersion_csv++;
    }

    public void setFilePath(String filePathSeleted)
    {
        filePath = filePathSeleted;
    }

    //读取第一个csv文件
    public void readFirstDataCsv(){
        try{
            InputStreamReader isr = new InputStreamReader(new FileInputStream(filePath), "gbk");
                BufferedReader br = new BufferedReader(isr);
                br.readLine();
                String line = "";
            while((line =br.readLine())!=null){
                String[] buffer = line.split(",");
                //part数据设置
                PartData partData = new PartData();
                partData.setPart(Integer.valueOf(buffer[0]));//part
                partData.setPart_name(buffer[1]);//part_name

                //chapter数据设置
                ChapterData chapterData = new ChapterData();
                chapterData.setChapter(Integer.valueOf(buffer[2]));//chapter
                chapterData.setChapter_name(buffer[3]);//chapter_name
                chapterData.setPart(Integer.valueOf(buffer[0]));//part

                //数据录入数据库中
                db = dbHelper.getWritableDatabase();
                csvIntoPartTable(partData);
                csvIntoChapterTable(chapterData);
                db.close();
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.i("csv","csv读取出错1");
        }
    }

    //读取第二个csv文件
    public void readMonDataCsv() {
        int i = 0;// 用于标记打印的条数
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(filePath), "utf-8");
            BufferedReader br = new BufferedReader(isr);

            br.readLine();
            String line = "";
            while ((line = br.readLine()) != null) {
                i++;
                //Log.i("csv","第" + i + "行：" + line);// 输出每一行数据

                String[] buffer = line.split(",");// 以逗号分隔

                //part数据设置
                PartData partData = new PartData();
                partData.setPart(Integer.valueOf(buffer[0]));//part

                //word数据设置
                WordData wordData = new WordData();
                wordData.setWords(buffer[2]);//word
                wordData.setWord_translation(buffer[3]);//word_translation
                wordData.setChapter(Integer.valueOf(buffer[1]));//chapter

                //example数据设置
                DescribeData exampleData = new DescribeData();
                exampleData.setExample(buffer[4]);//example
                exampleData.setExampleTranslation(buffer[5]);//example_translation

                //数据录入数据库中
                db = dbHelper.getWritableDatabase();
                csvIntoWordTable(wordData,partData);
                csvIntoExampleTable(exampleData,partData,wordData);
                db.close();
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("csv","csv读取出错2");
        }
    }

    //csv数据part存到数据中
    private void csvIntoPartTable(PartData partData)
    {
        String part_exit_sql = "select * from table_part where part like "+partData.getPart();
        Cursor cursor = db.rawQuery(part_exit_sql,null);
        if(cursor.getCount()==0)//数据库中没有这个part
        {
            //插入part的数据到数据库中
            ContentValues values = new ContentValues();
            values.put("part",partData.getPart());
            values.put("part_name",partData.getPart_name());
            db.insert("table_part","part",values);  //nullColumnHack表示强行向表中插入null，前提是第三个参数（values）为空或者不包含任何Key-value时生效
        }else{
            Log.i("db_test","数据库已经存在此part："+partData.getPart());
        }
        cursor.close();
    }

    //csv数据chapter存到数据中
    private void csvIntoChapterTable(ChapterData chapterData)
    {
        //插入chapter的数据到数据库中
        ContentValues values = new ContentValues();
        values.put("chapter",chapterData.getChapter());
        values.put("chapter_name",chapterData.getChapter_name());
        values.put("part",chapterData.getPart());
        db.insert("table_chapter","part",values);
    }

    //csv数据word存到数据中
    private void csvIntoWordTable(WordData wordData,PartData partData)
    {
        String find_chapter_sql = "select * from table_chapter c where c.part="+partData.getPart()+" and c.chapter="+wordData.getChapter();
        Cursor find_chapter_cursor = db.rawQuery(find_chapter_sql,null);
        if(find_chapter_cursor.moveToFirst()){//查询章节
            //int chapter_id = find_chapter_cursor.getInt(find_chapter_cursor.getColumnIndex("id"));
            String word_exit_sql = "select * from table_word where word like '"+wordData.getWords()+"'";
            Cursor word_exit_cursor = db.rawQuery(word_exit_sql,null);
            if(word_exit_cursor.getCount()==0){//如果数据库中没有这个单词
                ContentValues values = new ContentValues();
                values.put("word",wordData.getWords());
                values.put("word_translation",wordData.getWord_translation());
                values.put("chapter",wordData.getChapter());
                db.insert("table_word","word",values);
            }else{
                Log.i("db_test","数据库已经存在此word："+wordData.getWords());
            }
            word_exit_cursor.close();
        }
        find_chapter_cursor.close();
    }

    //csv数据example存到数据中
    private void csvIntoExampleTable(DescribeData exampleData,PartData partData,WordData wordData)
    {
        String find_chapter_sql = "select * from table_chapter c where c.part="+partData.getPart()+" and c.chapter="+wordData.getChapter();
        Cursor find_chapter_cursor = db.rawQuery(find_chapter_sql,null);
        if(find_chapter_cursor.moveToFirst()) {//查询章节
            String word_exit_sql = "select * from table_word where word like '"+wordData.getWords()+"'";
            Cursor find_word_cursor = db.rawQuery(word_exit_sql,null);
            if(find_word_cursor.moveToFirst() && find_word_cursor.getCount()==1){
                int word_id = find_word_cursor.getInt(find_word_cursor.getColumnIndex("id"));
                ContentValues values = new ContentValues();
                values.put("example",exampleData.getExample());
                values.put("example_translation",exampleData.getExampleTranslation());
                values.put("word",word_id);
                db.insert("table_example","example",values);
            }else{
                Log.i("db_test","数据库不存在此单词，或者此单词重复，无法插入example"+exampleData.getExample());
            }
            find_word_cursor.close();
        }else {
            Log.i("db_test","数据库不存在此chapter："+wordData.getChapter());
        }
        find_chapter_cursor.close();
    }

    private void clearDatabase(){
        db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + "table_part");
        db.execSQL("DELETE FROM " + "table_chapter");
        db.execSQL("DELETE FROM " + "table_word");
        db.execSQL("DELETE FROM " + "table_example");
        db.close();
    }

}
