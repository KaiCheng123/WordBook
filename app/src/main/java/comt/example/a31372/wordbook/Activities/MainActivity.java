package comt.example.a31372.wordbook.Activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import comt.example.a31372.wordbook.Adapters.ChapterAdapter;
import comt.example.a31372.wordbook.Adapters.PartAdapter;
import comt.example.a31372.wordbook.Adapters.WordAdapter;
import comt.example.a31372.wordbook.Data.ChapterData;
import comt.example.a31372.wordbook.Data.DescribeData;
import comt.example.a31372.wordbook.Data.PartData;
import comt.example.a31372.wordbook.Data.WordData;
import comt.example.a31372.wordbook.R;
import comt.example.a31372.wordbook.util.GetPermisssion;
import comt.example.a31372.wordbook.util.ReadCsv;
import comt.example.a31372.wordbook.util.ReadDB;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static MainActivity instane;
    private Button btn_click;
    private ListView main_listview;

    //文件管理器
    private static final int CHOOSE_FILE_CODE = 0;

    //数据List
    public static List<ChapterData> chapterList = new ArrayList<ChapterData>();
    public static List<PartData> partList = new ArrayList<PartData>();
    public static List<WordData> wordList = new ArrayList<WordData>();
    public static List<DescribeData> exampleList = new ArrayList<DescribeData>();

    //单例模式
    private ReadCsv readCsv;
    private ReadDB readDB;

    //DBHelper的版本
    public static int dbHelperVersion=1;

    //选中单词的下标
    public static int index=0;
    public static MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //主页面部分
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //获取权限
        GetPermisssion.isGrantExternalRW(this, 1);

        //初始化部分
        instane = this;
        instantiate();
        init();
    }

    public void newMediaPlayer()
    {
        mediaPlayer = new MediaPlayer();
    }

    //更新version，提供给所有adpater调用
    public void upAdapter()
    {
        ReadDB.instance.DBHandle(MainActivity.this,dbHelperVersion);
    }

    private void setPartAdapter()
    {
        readDB = new ReadDB(MainActivity.this,dbHelperVersion);
        readDB.readPart();
        if(!partList.isEmpty()) {
            setAdapter("part");
        }else
        {
            Log.i("db_test","partList中没有数据");
        }
    }

    private void instantiate()
    {
        readCsv = new ReadCsv(MainActivity.this);
    }

    private void init()
    {
        btn_click=findViewById(R.id.btn_click);
        btn_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPartAdapter();
            }
        });
        main_listview = findViewById(R.id.main_listview);
    }

    public void setAdapter(String adapterName)
    {
        switch (adapterName){
            case "part":
                PartAdapter partAdapter = new PartAdapter(MainActivity.this,partList);
                main_listview.setAdapter(partAdapter);
                break;
            case "chapter":
                ChapterAdapter chapterAdapter = new ChapterAdapter(MainActivity.this,chapterList);
                main_listview.setAdapter(chapterAdapter);
                break;
            case "word":
                if(!wordList.isEmpty()) {
                    WordAdapter wordAdapter = new WordAdapter(MainActivity.this, wordList);
                    main_listview.setAdapter(wordAdapter);
                }else
                {
                    Log.i("db_test","wordList为空");
                }
                break;
            case "describe":
                Log.i("db_test","exampleAdapter");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_slideshow) {
            //读取csv文件
            chooseFile();
        } else if (id == R.id.nav_share) {
            //搜索单词
            Intent intent = new Intent(MainActivity.this,SearchActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_send) {
            //添加单词
            Intent intent = new Intent(MainActivity.this,AddWordsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

/*
    调用系统文件管理器
*/

    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(".csv/*").addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Choose File"), CHOOSE_FILE_CODE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "打开文件管理器出错！", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    // 文件选择完之后，自动调用此函数
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CHOOSE_FILE_CODE) {
                Uri uri = data.getData();
                String pathSelected = null;
                pathSelected = getPath(this, uri); // Paul Burke写的函数，根据Uri获得文件路径
                ReadCsv.instance.setFilePath(pathSelected);
                Log.i("csv", "onActivityResult: 您选择的文件及其路径为："+pathSelected);
                if(pathSelected.equals("/storage/emulated/0/csv/PartAndChapter.csv"))
                {
                    ReadCsv.instance.readFirstDataCsv();//version=1
                }else{
                    ReadCsv.instance.readMonDataCsv();//version=2
                    ReadCsv.instance.updateDBHelperVersion(MainActivity.this);//version=3
                }
            }
            else
            {
                Log.i("csv","点击无效！");
            }
        } else {
            Log.i("csv", "onActivityResult() error, resultCode: " + resultCode);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
