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
 * Created by asus on 5/2/2017.
 */
public class SearchProductAdapter extends BaseAdapter {
    private Context mContext;
    private final String[] productText;
    private final String[] massText;
    private final String[] priceText;
    private final String[] productImgId;
    private ImageLoader imageLoader;

    //Constructor
    public SearchProductAdapter(Context c, String[] productText, String[] massText, String[] priceText, String[] productImgId) {
        mContext = c;
        this.productText = productText;
        this.massText = massText;
        this.priceText = priceText;
        this.productImgId = productImgId;
        imageLoader = AppSingleton.getInstance(c).getmImageLoader();
    }

    public int getCount() {
        return productText.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return 0;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if(convertView == null) {
            convertView = inflater.inflate(R.layout.activity_search_product, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.imageView.setImageUrl(productImgId[position], imageLoader);
        holder.name.setText(productText[position]);
        holder.mass.setText(massText[position]);
        holder.price.setText(priceText[position]);

        return convertView;
    }

    private class ViewHolder {
        private TextView name;
        private TextView mass;
        private TextView price;
        private NetworkImageView imageView;

        public ViewHolder(View v) {
            name = (TextView) v.findViewById(R.id.search_product_name);
            mass = (TextView) v.findViewById(R.id.search_product_mass);
            price = (TextView) v.findViewById(R.id.search_product_price);
            imageView = (NetworkImageView) v.findViewById(R.id.search_product_img);
        }

    }
}
