package com.example.appsname;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import in.srain.cube.views.GridViewWithHeaderAndFooter;

/**
 * Created by asus on 4/5/2017.
 */
public class HomeFragment extends Fragment {
    private GridViewWithHeaderAndFooter gridView;
    private AutoCompleteTextView suggestionProduct;
    private String keyword;
    private ProgressDialog progressDialog;

    public HomeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //GridView Section
        View v = inflater.inflate(R.layout.activity_home, container, false);
        gridView=(GridViewWithHeaderAndFooter) v.findViewById(R.id.category_grid);
        setGridViewHeaderAndFooter();
        //Show Category
        getCategories(UrlList.CATEGORY_URL);
        // Link To Another Page
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ProductListActivity.class);
                intent.putExtra("category_name", CategoryParser.names[+position]);
                intent.putExtra("category_id", CategoryParser.ids[+position]);
                startActivity(intent);
            }
        });

        return v;
    }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    private void setGridViewHeaderAndFooter() {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View headerView = layoutInflater.inflate(R.layout.activity_header_home, null, false);

        //Locate Views
        ImageView headerImage = (ImageView)headerView.findViewById(R.id.header_home);
        headerImage.setImageResource(R.drawable.header);
        //Search Section
        suggestionProduct = (AutoCompleteTextView)headerView.findViewById(R.id.search);
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
        gridView.addHeaderView(headerView);
    }

    private void getCategories(String URL){
        StringRequest stringRequest = new StringRequest(URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        CategoryParser parser = new CategoryParser(response);
                        parser.parseJSON();
                        CategoryAdapter adapterView = new CategoryAdapter(getActivity(), CategoryParser.names, CategoryParser.imgs);
                        gridView.setAdapter(adapterView);
                        progressDialog.dismiss(); //Dismiss progress Dialog
                    }
                },
                new Response.ErrorListener() {
                     @Override
                        public void onErrorResponse(VolleyError error) {
                         //Toast.makeText(getActivity(), "All Products " + error.getMessage(), Toast.LENGTH_LONG).show();
                         progressDialog.dismiss(); //Dismiss progress Dialog
                     }
                });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
        //Show progress dialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }

    private void getAllProducts(String URL){
        StringRequest stringRequest = new StringRequest(URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("RESPONSE", response);
                        ProductListParser parser = new ProductListParser(response);
                        parser.parseJSON();
                        String[] products = ProductListParser.names;
                        if (getActivity() != null) {
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, products);
                            suggestionProduct.setAdapter(adapter);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), "All Products " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    private void performSearch(String keyword) {
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        intent.putExtra("keyword", keyword);
        startActivity(intent);
    }
}
