package comt.example.a31372.wordbook.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.List;

import comt.example.a31372.wordbook.Activities.MainActivity;
import comt.example.a31372.wordbook.Data.ChapterData;
import comt.example.a31372.wordbook.R;
import comt.example.a31372.wordbook.util.ReadDB;

public class ChapterAdapter extends BaseAdapter{
    private Context context;
    private List<ChapterData> chapterData;
    private LayoutInflater layoutInflater;

    public ChapterAdapter(Context context, List<ChapterData> chapterData)
    {
        this.context=context;
        this.chapterData=chapterData;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return chapterData.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null) {
            LinearLayout layout = (LinearLayout) layoutInflater.inflate(R.layout.chapter_adapter, null);
            convertView = layout;
        }

        TextView chapter_textview = convertView.findViewById(R.id.chapter_textview);
        chapter_textview.setText("Chapter :"+chapterData.get(position).getChapter()+"   "+chapterData.get(position).getChapter_name());
        chapter_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //读取数据库中选中chapter对应的word和example
                MainActivity.instane.upAdapter();
                ReadDB.instance.readWordAndExample(chapterData.get(position).getChapter());
                //切换adapter
                MainActivity.instane.setAdapter("word");
                Log.i("db_test","wordAdapter");
            }
        });
        return convertView;
    }
}
