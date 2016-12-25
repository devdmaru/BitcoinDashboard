package edu.temple.bitcoindashboard;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by Dev on 12/6/16.
 */

public class CustomAdapter extends BaseAdapter {

   private Context context;
    private String[] items;

    public CustomAdapter (Context context, String[] items){
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

            TextView l = new TextView(context);

            l.setText(items[position]);
            l.setTextSize(20);
            l.setPadding(16, 25, 16, 25);




        return l;
    }
}
