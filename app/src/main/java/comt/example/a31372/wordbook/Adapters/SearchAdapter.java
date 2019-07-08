package comt.example.a31372.wordbook.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.List;

import comt.example.a31372.wordbook.Data.ChapterData;
import comt.example.a31372.wordbook.Data.DescribeData;
import comt.example.a31372.wordbook.Data.PartData;
import comt.example.a31372.wordbook.Data.WordData;
import comt.example.a31372.wordbook.R;

public class SearchAdapter extends BaseAdapter{

    private Context context;
    private LayoutInflater layoutInflater;
    private List<PartData> partDataList;
    private List<ChapterData> chapterDataList;
    private List<WordData> wordDataList;
    private List<DescribeData> exampleDataList;

    public SearchAdapter(Context context, List<PartData> partDataList,List<ChapterData> chapterDataList,
                         List<WordData> wordDataList,List<DescribeData> exampleDataList)
    {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.partDataList = partDataList;
        this.chapterDataList = chapterDataList;
        this.wordDataList = wordDataList;
        this.exampleDataList = exampleDataList;
    }

    @Override
    public int getCount() {
        return wordDataList.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null)
        {
            LinearLayout layout = (LinearLayout) layoutInflater.inflate(R.layout.search_adapter,null);
            convertView = layout;
        }
        //获取组件
        TextView partText = convertView.findViewById(R.id.search_adap_parttext);
        TextView chapterText = convertView.findViewById(R.id.search_adap_chaptertext);
        TextView wordText = convertView.findViewById(R.id.search_adap_wordtext);
        TextView wordTranslationText = convertView.findViewById(R.id.search_adap_wordtranslationtext);
        TextView exampleText = convertView.findViewById(R.id.search_adap_exampletext);
        TextView exampleTranslationText = convertView.findViewById(R.id.search_adap_exampletranslationtext);
        //设置组件的文本
        partText.setText("Part:"+partDataList.get(position).getPart()+" "+partDataList.get(position).getPart_name());
        chapterText.setText(chapterDataList.get(position).getChapter()+" "+chapterDataList.get(position).getChapter_name());
        wordText.setText(wordDataList.get(position).getWords());
        wordTranslationText.setText(wordDataList.get(position).getWord_translation());
        exampleText.setText(exampleDataList.get(position).getExample());
        exampleTranslationText.setText(exampleDataList.get(position).getExampleTranslation());
        return convertView;
    }
}
