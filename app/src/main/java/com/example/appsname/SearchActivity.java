package com.example.appsname;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

/**
 * Created by asus on 5/2/2017.
 */
public class SearchActivity extends AppCompatActivity {
    GridView gridView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        // Get Intent
        Intent intent = getIntent();
        String keyword = intent.getStringExtra("keyword");
        Log.d("keyword", keyword);
        // Set Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        getSupportActionBar().setTitle("Semua Kategori");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // GridView of Products
        gridView=(GridView)findViewById(R.id.search_product_grid);
        //Show Products
        String SEARCH_PRODUCT_URL = UrlList.SEARCH_PRODUCT_URL + keyword;
        SEARCH_PRODUCT_URL = SEARCH_PRODUCT_URL.replaceAll(" ","%20");
        getSearchProduct(SEARCH_PRODUCT_URL);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String product_id = String.valueOf(ProductListParser.ids[+position]);
                Intent intent = new Intent(SearchActivity.this, ProductActivity.class);
                intent.putExtra("product_id", product_id);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem itemCart = menu.findItem(R.id.action_basket);
        LayerDrawable icon = (LayerDrawable) itemCart.getIcon();
        setBadgeCount(this, icon, String.valueOf(ProductActivity.countItem));
        invalidateOptionsMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_basket:
                Intent intent = new Intent(SearchActivity.this, ShoppingListActivity.class);
                startActivity(intent);
                return true;
            case R.id.search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, String count) {
        BadgeDrawable badge;
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }

    public void getSearchProduct(String URL){
        StringRequest stringRequest = new StringRequest(URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("RESPONSE", response);
                        ProductListParser parser = new ProductListParser(response);
                        parser.parseJSON();
                        SearchProductAdapter adapterView = new SearchProductAdapter(SearchActivity.this,
                                ProductListParser.names, ProductListParser.units,
                                ProductListParser.price, ProductListParser.imgs );
                        gridView.setAdapter(adapterView);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SearchActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(SearchActivity.this);
        requestQueue.add(stringRequest);
    }
}
