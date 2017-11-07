package com.example.appsname;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

/**
 * Created by asus on 3/2/2017.
 */
public class CategoryAdapter extends BaseAdapter {
    private Context mContext;
    private final String[] categoryText;
    private final String[] categoryImgId;
    private ImageLoader imageLoader;

    //Constructor
    public CategoryAdapter(Context c,String[] categoryText, String[] categoryImgId) {
        mContext = c;
        this.categoryText = categoryText;
        this.categoryImgId = categoryImgId;
        imageLoader = AppSingleton.getInstance(c).getmImageLoader();
    }

    @Override
    public int getCount() {
        return categoryText.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.activity_category, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.imageView.setImageUrl(categoryImgId[position], imageLoader);
        holder.textView.setText(categoryText[position]);

        return convertView;
    }

    private class ViewHolder {
        private TextView textView;
        private NetworkImageView imageView;

        public ViewHolder(View v) {
            textView = (TextView) v.findViewById(R.id.category_name);
            imageView = (NetworkImageView) v.findViewById(R.id.category_icon);
        }

    }
}
