package comt.example.a31372.wordbook.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import comt.example.a31372.wordbook.Data.DescribeData;
import comt.example.a31372.wordbook.Data.WordData;

public class DescribeAdapter extends BaseAdapter{

    private Context context;
    private LayoutInflater layoutInflater;
    private List<WordData> wordDataList = new ArrayList<>();
    private List<DescribeData> exampleDataList = new ArrayList<>();

    public DescribeAdapter(Context context,List<WordData> wordDataList,List<DescribeData> exampleDataList)
    {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.wordDataList = wordDataList;
        this.exampleDataList = exampleDataList;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
