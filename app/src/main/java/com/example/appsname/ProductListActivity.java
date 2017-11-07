package com.example.appsname;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by asus on 3/4/2017.
 */
public class ProductListActivity extends AppCompatActivity {
    GridView gridView;
    private Toolbar toolbar;
    private AutoCompleteTextView suggestionProduct;
    private String keyword;
    private ProgressDialog progressDialog;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_grid_product);

       // Get Intent
       Intent intent = getIntent();
       String categoryName = intent.getStringExtra("category_name");
       String categoryID = intent.getStringExtra("category_id");

       // Set Toolbar
       toolbar = (Toolbar) findViewById(R.id.toolbar);
       setSupportActionBar(toolbar);
       toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
       getSupportActionBar().setTitle(categoryName);
       toolbar.setNavigationOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               onBackPressed();
           }
       });

       //Search Product
       suggestionProduct = (AutoCompleteTextView) findViewById(R.id.search);
       getAllProducts(UrlList.PRODUCT_URL);
       suggestionProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
               keyword = (String) parent.getItemAtPosition(position);
           }
       });
       suggestionProduct.setOnEditorActionListener(new TextView.OnEditorActionListener() {
           @Override
           public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
               if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                   performSearch(keyword);
                   suggestionProduct.setText("");
                   return true;
               }
               return false;
           }
       });

       // GridView of Products
       gridView=(GridView)findViewById(R.id.product_grid);
       final String PRODUCT_URL = UrlList.CATEGORY_URL + categoryID;
       getProducts(PRODUCT_URL);
       gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               String product_id = String.valueOf(gridView.getAdapter().getItemId(position));
               Intent intent = new Intent(ProductListActivity.this, ProductActivity.class);
               intent.putExtra("product_id", product_id);
               startActivity(intent);
           }
       });
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem itemCart = menu.findItem(R.id.action_basket);
        final LayerDrawable icon = (LayerDrawable) itemCart.getIcon();
        setBadgeCount(ProductListActivity.this, icon, String.valueOf(ProductActivity.countItem));
        invalidateOptionsMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_basket:
                Intent intent = new Intent(ProductListActivity.this, ShoppingListActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, String count) {
        final BadgeDrawable badge;
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

    public void getProducts(String URL) {
        StringRequest stringRequest = new StringRequest(URL,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("RESPONSE", response);
                    ProductListParser parser = new ProductListParser(response);
                    parser.parseJSON();
                    ProductListAdapter adapterView = new ProductListAdapter(ProductListActivity.this,
                    ProductListParser.ids, ProductListParser.names, ProductListParser.units,
                    ProductListParser.price, ProductListParser.imgs);
                    for (int i = 0; i < ProductListParser.ids.length; i++) {
                        Log.d("PRODUCTPARSER", String.valueOf(ProductListParser.ids[i]));
                    }
                    gridView.setAdapter(adapterView);
                    progressDialog.dismiss(); //Dismiss progress Dialog
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ProductListActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(ProductListActivity.this);
        requestQueue.add(stringRequest);
        //show progress dialog
        progressDialog = new ProgressDialog(ProductListActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }

    private void getAllProducts(String URL) {
        StringRequest stringRequest = new StringRequest(URL,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("RESPONSE", response);
                    ProductListParser parser = new ProductListParser(response);
                    parser.parseJSON();
                    String[] products = ProductListParser.names;
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ProductListActivity.this, android.R.layout.simple_list_item_1, products);
                    suggestionProduct.setAdapter(adapter);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ProductListActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(ProductListActivity.this);
        requestQueue.add(stringRequest);
    }

    private void performSearch(String keyword) {
        Intent intent = new Intent(ProductListActivity.this, SearchActivity.class);
        intent.putExtra("keyword", keyword);
        startActivity(intent);
    }
}
