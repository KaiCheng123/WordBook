package comt.example.a31372.wordbook.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import comt.example.a31372.wordbook.Activities.MainActivity;
import comt.example.a31372.wordbook.Activities.TabbActivity;
import comt.example.a31372.wordbook.Data.WordData;
import comt.example.a31372.wordbook.R;

public class WordAdapter extends BaseAdapter{
    private Context context;
    private LayoutInflater layoutInflater;
    private List<WordData> wordData;

    public WordAdapter(Context context, List<WordData> wordData)
    {
        this.context=context;
        this.wordData=wordData;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return wordData.size();
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
        if(convertView==null)
        {
            LinearLayout layout = (LinearLayout) layoutInflater.inflate(R.layout.word_adapter,null);
            convertView = layout;
        }

        TextView word_wordtext = convertView.findViewById(R.id.word_text);
        TextView word_translationtext = convertView.findViewById(R.id.word_translationtext);

        word_wordtext.setText(wordData.get(position).getWords());
        word_translationtext.setText(wordData.get(position).getWord_translation());

        word_wordtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到tabbActivity
                Intent tabbIntent= new Intent(context, TabbActivity.class);
                MainActivity.instane.index=position;
                context.startActivity(tabbIntent);
            }
        });

        word_translationtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到tabbActivity
                Intent tabbIntent= new Intent(context, TabbActivity.class);
                MainActivity.instane.index=position;
                context.startActivity(tabbIntent);
            }
        });

        return convertView;
    }
}
