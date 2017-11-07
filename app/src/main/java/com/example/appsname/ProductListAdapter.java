package com.example.appsname;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

/**
 * Created by asus on 4/6/2017.
 */
public class ProductListAdapter extends BaseAdapter {
    private Context mContext;
    private final Integer[] productId;
    private final String[] productText;
    private final String[] massText;
    private final String[] priceText;
    private final String[] productImgId;
    private ImageLoader imageLoader;

    //Constructor
    public ProductListAdapter(Context c, Integer[] productId, String[] productText, String[] massText, String[] priceText, String[] productImgId) {
        mContext = c;
        this.productId = productId;
        this.productText = productText;
        this.massText = massText;
        this.priceText = priceText;
        this.productImgId = productImgId;
        imageLoader = AppSingleton.getInstance(c).getmImageLoader();
    }

    @Override
    public int getCount() {
        return productText.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return productId[position];
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.activity_product_list, parent, false);
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
            name = (TextView) v.findViewById(R.id.product_name);
            mass = (TextView) v.findViewById(R.id.product_mass);
            price = (TextView) v.findViewById(R.id.product_price);
            imageView = (NetworkImageView) v.findViewById(R.id.product_img);
        }
    }
}
