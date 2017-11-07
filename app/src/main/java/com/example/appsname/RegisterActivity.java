package com.example.appsname;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by asus on 6/18/2017.
 */
public class RegisterActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks  {

    private static final String LOG_TAG = "RegisterActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private String name, address, phone, username, password, addressNote;
    private EditText inputName, inputPhone, inputUserName, inputPassword, inputAddressNote;
    private AutoCompleteTextView inputAddress;
    private Button registerButton;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_PAYAKUMBUH = new LatLngBounds(
            new LatLng(-0.2749049, 100.5795769), new LatLng(-0.184086, 100.6859289));

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //get user address
        mGoogleApiClient = new GoogleApiClient.Builder(RegisterActivity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        inputAddress = (AutoCompleteTextView) findViewById(R.id
                .input_address);
        inputAddress.setThreshold(3);
        inputAddress.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_PAYAKUMBUH, null);
        inputAddress.setAdapter(mPlaceArrayAdapter);
        inputName = (EditText) findViewById(R.id.input_name);
        inputPhone = (EditText) findViewById(R.id.input_phone);
        inputUserName = (EditText) findViewById(R.id.input_username);
        inputPassword = (EditText) findViewById(R.id.input_password);
        inputAddressNote = (EditText) findViewById(R.id.input_address_note);
        // Add User to Database
        registerButton = (Button) findViewById(R.id.register_btn);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateName()) {
                    return;
                } else if (!validateAddress()) {
                    return;
                } else if (!validatePhone()) {
                    return;
                } else if (!validateUserName()) {
                    return;
                } else if (!validatePassword()) {
                    return;
                } else {
                    addUser(UrlList.REGISTER_URL);
                    Intent intentLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intentLogin);
                }
            }
        });
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            Log.i(LOG_TAG, "Selected: " + item.description);
            address = item.description.toString();
        }
    };

    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
    }

    private void addUser(String URL) {
        name = inputName.getText().toString().trim();
        phone = inputPhone.getText().toString().trim();
        username = inputUserName.getText().toString().trim();
        password = inputPassword.getText().toString().trim();
        addressNote = inputAddressNote.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("register response", response);
                        Toast.makeText(RegisterActivity.this,response,Toast.LENGTH_LONG).show();
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
                params.put("name", name);
                params.put("username", username);
                params.put("address", address);
                params.put("address_note", addressNote);
                params.put("phone", phone);
                params.put("password", password);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
        requestQueue.add(stringRequest);
    }

    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputName.setError(getString(R.string.err_msg_name));
            requestFocus(inputName);
            return false;
        }
        return true;
    }

    private boolean validateAddress() {
        if (inputAddress.getText().toString().trim().isEmpty()) {
            inputAddress.setError(getString(R.string.err_msg_address));
            requestFocus(inputAddress);
            return false;
        }
        return true;
    }

    private boolean validatePhone() {
        if (inputPhone.getText().toString().trim().isEmpty()) {
            inputPhone.setError(getString(R.string.err_msg_phone));
            requestFocus(inputPhone);
            return false;
        }
        return true;
    }

    private boolean validateUserName() {
        if (inputUserName.getText().toString().trim().isEmpty()) {
            inputUserName.setError(getString(R.string.err_msg_username));
            requestFocus(inputUserName);
            return false;
        }
        return true;
    }

    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            inputPassword.setError(getString(R.string.err_msg_password));
            requestFocus(inputPassword);
            return false;
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
