package comt.example.a31372.wordbook.Activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

import comt.example.a31372.wordbook.R;


public class TabbActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private boolean isUp=true;
    private float lastX;
    private static final int LEFT_TO_RIGHT=1;
    private static final  int RIGHT_TO_LEFT=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);

        setContentView(R.layout.activity_tabb);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        int direction=0;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                isUp=false;
                lastX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                if(event.getX()-lastX<0)//左滑
                {
                    direction=RIGHT_TO_LEFT;
                }else{//右滑
                    direction=LEFT_TO_RIGHT;
                }
                break;
            case MotionEvent.ACTION_UP:
                isUp=true;
                break;
        }
        if(isUp){
            return super.onTouchEvent(event);
        }else if(direction==RIGHT_TO_LEFT ){//左滑
            Log.i("test3","左");
            return super.onTouchEvent(event);
        }
        else{//右滑
            Log.i("test3","右边");
            if(MainActivity.instane.index>0) {
                MainActivity.instane.index--;
            }
            return super.onTouchEvent(event);
        }
    }

    @Override
    public void onBackPressed() {
        if(MainActivity.instane.mediaPlayer!=null && MainActivity.instane.mediaPlayer.isPlaying())
        {
            MainActivity.instane.mediaPlayer.stop();
            MainActivity.instane.mediaPlayer.release();
        }
        super.onBackPressed();
    }

    @Override
    public void finish(){
        MainActivity.instane.index=0;
        super.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabb, menu);
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
            Intent intent = new Intent(TabbActivity.this,MainActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment{
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";



        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tabb, container, false);
            final TextView word = (TextView) rootView.findViewById(R.id.section_label);
            TextView example = (TextView) rootView.findViewById(R.id.section_labe2);
            TextView exampleTransation = (TextView) rootView.findViewById(R.id.section_labe3);
            TextView wordTransation = (TextView) rootView.findViewById(R.id.section_labe4);
            Button music = rootView.findViewById(R.id.music);
            int TabIndex = MainActivity.instane.index;
            if(TabIndex>=MainActivity.instane.wordList.size()){
                TabIndex=1;
            }
            word.setText(MainActivity.instane.wordList.get(TabIndex).getWords());
            wordTransation.setText("单词："+MainActivity.instane.wordList.get(TabIndex).getWord_translation());

            int wordId = MainActivity.instane.wordList.get(TabIndex).getWordID();
            example.setText("例子：\n");
            exampleTransation.setText("例子翻译：\n");
            int num=0;
            for(int i=TabIndex;i<MainActivity.instane.exampleList.size();i++)
            {
                if(wordId==MainActivity.instane.exampleList.get(i).getWord()) {
                    num++;
                    String exampleText = example.getText().toString();
                    String exampleTransText = exampleTransation.getText().toString();
                    example.setText(exampleText+(num)+"."+MainActivity.instane.exampleList.get(i).getExample()+"\n");
                    exampleTransation.setText(exampleTransText+(num)+"."+MainActivity.instane.exampleList.get(i).getExampleTranslation()+"\n");
                }
            }

            MainActivity.instane.newMediaPlayer();
            music.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //找word
                    String theWord = word.getText().toString();
                    //找chapter
                    int part = 0;
                    int chapter = MainActivity.instane.wordList.get(MainActivity.instane.index).getChapter();
                    //找part
                    for (int i = 0; i < MainActivity.instane.chapterList.size(); i++) {
                        if (MainActivity.instane.chapterList.get(i).getChapter() == chapter) {
                            part = MainActivity.instane.chapterList.get(i).getPart();
                        }
                    }
                    try {
                        File file = new File(Environment.getExternalStorageDirectory(),"/csv/p"+part+"ch"+chapter+"_"+theWord+".mp3");
                        MainActivity.instane.mediaPlayer.reset();
                        Log.i("test3",file.getPath());
                        MainActivity.instane.mediaPlayer.setDataSource(file.getPath());
                        MainActivity.instane.mediaPlayer.prepare();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i("test3","播放出错了");
                    }
                    if(MainActivity.instane.mediaPlayer!=null)
                    {
                        if(!MainActivity.instane.mediaPlayer.isPlaying()) {
                            MainActivity.instane.mediaPlayer.start();
                        }
                        else{
                            MainActivity.instane.mediaPlayer.pause();
                        }
                    }
                }
            });
            MainActivity.instane.index++;

            return rootView;
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return MainActivity.instane.wordList.size();//允许滑动次数=单词个数
        }
    }
}
