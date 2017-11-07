package com.example.appsname;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by asus on 3/9/2017.
 */
public class ProductActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Spinner mass;
    private EditText orderQuantity;
    private NetworkImageView image;
    private TextView name, price;
    private Button addCart;
    private String unit, quantity, unit_id, unit_type, selected_unit_id;
    private ProgressDialog progressDialog;
    public static int countItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        // Get Intent
        Intent intent = getIntent();
        String productID = intent.getStringExtra("product_id");
        Log.d("PRODUCTID", productID);

        // Set Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final Drawable backArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back);
        backArrow.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(backArrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Show Product Detail
        final String PRODUCT_DETAIL_URL = UrlList.PRODUCT_URL + productID;
        getProduct(PRODUCT_DETAIL_URL);

        //Set Image, Text, EditText
        image = (NetworkImageView) findViewById(R.id.product_img);
        name = (TextView) findViewById(R.id.product_name);
        price = (TextView) findViewById(R.id.product_mass);
        orderQuantity = (EditText) findViewById(R.id.quantity);
        addCart = (Button) findViewById(R.id.add_order_button);
        //Add product to cart
        addCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(PreferencesController.isLoggedIn(getApplicationContext())) { //check user login
                    if(!validateQuantity()) { //validating input
                        return;
                    } else {
                        // add shopping list to database
                        Log.d("ADD", "ADD CART");
                        addShoppingList(UrlList.ADD_CART_URL, ProductParser.id, ProductParser.quantity,
                                ProductParser.priceMin, ProductParser.priceMax);
                    }
                } else {
                    Intent intentLogin = new Intent(ProductActivity.this, LoginActivity.class);
                    intentLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intentLogin);
                }
            }
        });
    }

    private int getPriceMin(int quantity, int default_quantity, int priceMin) {
        return (quantity * priceMin)/default_quantity;
    }

    private int getPriceMax(int quantity, int default_quantity, int priceMax) {
        return (quantity * priceMax)/default_quantity;
    }

    private void addShoppingList(String URL, final String productID, final int oldQuantity, final int oldPriceMin, final int oldPriceMax) {
        //get attribute
        unit = mass.getSelectedItem().toString();
        quantity = orderQuantity.getText().toString().trim();
        selected_unit_id = unit_id;
        Log.d("SELECTEDUNITID", selected_unit_id);
        //Set Price Minimum and Maximum
        int total_quantity = 0;
        if (unit_type.equals("common")) {
            total_quantity = Integer.parseInt(quantity) * ConverterParser.in_gram;
        } else if (unit_type.equals("uncommon")) {
            total_quantity = Integer.parseInt(quantity);
        }
        final int price_min = getPriceMin(total_quantity, oldQuantity, oldPriceMin);
        final int price_max = getPriceMax(total_quantity, oldQuantity, oldPriceMax);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("cart response", response);
                        countItem = countItem + 1;
                        Toast.makeText(ProductActivity.this,response,Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", PreferencesController.getID(ProductActivity.this));
                params.put("product_id", productID);
                params.put("quantity", quantity);
                params.put("unit_id", selected_unit_id);
                params.put("price_min", String.valueOf(price_min));
                params.put("price_max", String.valueOf(price_max));
                params.put("is_priority", String.valueOf(false));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ProductActivity.this);
        requestQueue.add(stringRequest);
    }

    private void getProduct(String URL){
        final ImageLoader imageLoader = AppSingleton.getInstance(ProductActivity.this).getmImageLoader();
        StringRequest stringRequest = new StringRequest(URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ProductParser parser = new ProductParser(response);
                        parser.parseJSON();
                        image.setImageUrl(ProductParser.img, imageLoader);
                        name.setText(ProductParser.name);
                        price.setText(ProductParser.price + " / " + ProductParser.unitProduct);
                        if (ProductParser.unitType.equals("common")) {
                            final String UNITS_URL = UrlList.COMMON_UNITS_URL;
                            getUnit(UNITS_URL);
                        } else {
                            String[] uncommonUnit = new String[]{ProductParser.unit};
                            mass = (Spinner) findViewById(R.id.mass_list);
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ProductActivity.this,
                                    android.R.layout.simple_spinner_item, uncommonUnit);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            mass.setAdapter(adapter);
                            mass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                    String[] unit_ids = new String[]{ProductParser.unitID};
                                    String[] unit_types = new String[]{ProductParser.unitType};
                                    unit_id = unit_ids[position];
                                    unit_type = unit_types[position];
                                }
                                @Override
                                public void onNothingSelected(AdapterView<?> parentView) {
                                    //nothing to do
                                }
                            });

                        }
                        progressDialog.dismiss(); //Dismiss progress Dialog
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ProductActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(ProductActivity.this);
        requestQueue.add(stringRequest);
        //show progress dialog
        progressDialog = new ProgressDialog(ProductActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

    }

    private void getUnit(String URL){
        StringRequest stringRequest = new StringRequest(URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        UnitParser parser = new UnitParser(response);
                        parser.parseJSON();
                        //Spinner
                        mass = (Spinner) findViewById(R.id.mass_list);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ProductActivity.this,
                                android.R.layout.simple_spinner_item, UnitParser.names);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mass.setAdapter(adapter);
                        mass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                String[] unit_ids = UnitParser.ids;
                                String[] unit_types = UnitParser.types;
                                unit_id = unit_ids[position];
                                unit_type = unit_types[position];
                                final String CONVERTER_URL = UrlList.CONVERTER_URL + unit_id;
                                getConverter(CONVERTER_URL);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parentView) {
                                //nothing to do
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ProductActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(ProductActivity.this);
        requestQueue.add(stringRequest);
    }

    private void getConverter(String URL){
        StringRequest stringRequest = new StringRequest(URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ConverterParser parser = new ConverterParser(response);
                        parser.parseJSON();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ProductActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(ProductActivity.this);
        requestQueue.add(stringRequest);
    }

    // Validation Section
    private boolean validateQuantity() {
        if (orderQuantity.getText().toString().trim().isEmpty()) {
            orderQuantity.setError(getString(R.string.err_msg_quantity));
            requestFocus(orderQuantity);
            return false;
        }

        unit = mass.getSelectedItem().toString();
        int input_quantity = Integer.parseInt(orderQuantity.getText().toString().trim());
        if (unit.equals("gram")) {
            if ((input_quantity < 100) || (input_quantity > 5000)) {
                orderQuantity.setError(getString(R.string.err_msq_valid_gram));
                requestFocus(orderQuantity);
                return false;
            }
        } else if (unit.equals("kilogram")) {
            if ((input_quantity < 1) || (input_quantity > 5)) {
                orderQuantity.setError(getString(R.string.err_msq_valid_kilogram));
                requestFocus(orderQuantity);
                return false;
            }
        } else if (unit.equals("ons")) {
            if ((input_quantity < 1) || (input_quantity > 50)) {
                orderQuantity.setError(getString(R.string.err_msq_valid_ons));
                requestFocus(orderQuantity);
                return false;
            }
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
