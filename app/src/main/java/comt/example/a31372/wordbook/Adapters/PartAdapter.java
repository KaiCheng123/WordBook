package comt.example.a31372.wordbook.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.List;

import comt.example.a31372.wordbook.Activities.MainActivity;
import comt.example.a31372.wordbook.Data.PartData;
import comt.example.a31372.wordbook.R;
import comt.example.a31372.wordbook.util.ReadDB;

public class PartAdapter extends BaseAdapter{

    private Context context;
    private List<PartData> partData;
    private LayoutInflater layoutInflater;

    public PartAdapter(Context context,List<PartData> partData)
    {
        this.context = context;
        this.partData = partData;
        this.layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return partData.size();
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
            LinearLayout layout = (LinearLayout) layoutInflater.inflate(R.layout.part_adapter,null);
            convertView = layout;
        }
        TextView part_textview = convertView.findViewById(R.id.part_textview);
        part_textview.setText("Part: "+partData.get(position).getPart()+"   "+partData.get(position).getPart_name());
        part_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //读取选中part中的chapter数据
                MainActivity.instane.upAdapter();
                ReadDB.instance.readChapter(partData.get(position).getPart());
                //切换adapter
                MainActivity.instane.setAdapter("chapter");
            }
        });
        return convertView;
    }
}
