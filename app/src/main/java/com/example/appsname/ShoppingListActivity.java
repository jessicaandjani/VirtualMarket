package com.example.appsname;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
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
 * Created by asus on 3/28/2017.
 */
public class ShoppingListActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView notAvailableCart;
    private Toolbar toolbar;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoppinglist_card);

        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);
        getSupportActionBar().setTitle("Keranjang Belanja");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //shopping cart section
        mRecyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        notAvailableCart = (TextView) findViewById(R.id.not_available_cart);
        //show shopping list
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        if(PreferencesController.getID(ShoppingListActivity.this) != null) {
            setCart(UrlList.CART_URL);
        }
    }

    private void setCart(String URL) {
        final ArrayList<ShoppingList> carts = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ShoppingListParser parser = new ShoppingListParser(response);
                        parser.parseJSON();
                        String[] ids = ShoppingListParser.ids;
                        String[] product_ids = ShoppingListParser.product_ids;
                        String[] product_names = ShoppingListParser.product_names;
                        String[] quantities = ShoppingListParser.quantities;
                        String[] unit_ids = ShoppingListParser.unit_ids;
                        String[] unit_names = ShoppingListParser.unit_names;
                        String[] unit_types = ShoppingListParser.unit_types;
                        String[] prices_min = ShoppingListParser.prices_min;
                        String[] prices_max = ShoppingListParser.prices_max;
                        String[] is_priorities = ShoppingListParser.is_priorities;
                        String[] product_imgs = ShoppingListParser.product_imgs;
                        String subTotal = ShoppingListParser.subTotal;
                        for (int i = 0; i < product_names.length; i++) {
                            ShoppingList shoppingList = new ShoppingList();
                            shoppingList.setID(ids[i]);
                            shoppingList.setName(product_names[i]);
                            shoppingList.setProductId(product_ids[i]);
                            shoppingList.setQuantity(quantities[i]);
                            shoppingList.setUnitId(unit_ids[i]);
                            shoppingList.setUnit(unit_names[i]);
                            shoppingList.setUnitType(unit_types[i]);
                            shoppingList.setPriceMin(prices_min[i]);
                            shoppingList.setPriceMax(prices_max[i]);
                            shoppingList.setPriority(is_priorities[i]);
                            shoppingList.setImage(product_imgs[i]);
                            carts.add(shoppingList);
                        }
                        progressDialog.dismiss(); //Dismiss progress Dialog
                        //show shopping list
                        if(getCommonUnit() != null) {
                            mAdapter = new ShoppingListAdapter(ShoppingListActivity.this, carts, subTotal, getCommonUnit());
                            mRecyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                        }
                        //show the cart or not
                        if(carts.size() != 0) { //cart not empty and cart visible
                            mRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            //status visible
                            mRecyclerView.setVisibility(View.GONE);
                            notAvailableCart.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progressDialog.dismiss(); //Dismiss progress Dialog
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", PreferencesController.getID(ShoppingListActivity.this));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(ShoppingListActivity.this);
        requestQueue.add(stringRequest);
        //show progress dialog
        progressDialog = new ProgressDialog(ShoppingListActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }

    public interface VolleyCallback {
        void onSuccessResponse(String result);
    }

    private void getUnitResponse(String URL, final VolleyCallback callback){
        StringRequest stringRequest = new StringRequest(URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        callback.onSuccessResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ShoppingListActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(ShoppingListActivity.this);
        requestQueue.add(stringRequest);
    }

    public ArrayList<UnitList> getCommonUnit() {
        String URL = UrlList.COMMON_UNITS_URL;
        final ArrayList<UnitList> units = new ArrayList<>();
        getUnitResponse(URL, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                UnitParser parser = new UnitParser(result);
                parser.parseJSON();
                String[] names = UnitParser.names;
                String[] ids = UnitParser.ids;
                String[] types = UnitParser.types;
                for (int i = 0; i < names.length; i++) {
                    UnitList unitList = new UnitList();
                    unitList.setId(ids[i]);
                    unitList.setName(names[i]);
                    unitList.setType(types[i]);
                    units.add(unitList);
                }
            }
        });
        return units;
    }
}
