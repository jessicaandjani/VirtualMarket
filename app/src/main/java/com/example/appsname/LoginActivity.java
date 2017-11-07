package com.example.appsname;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by asus on 6/14/2017.
 */
public class LoginActivity extends AppCompatActivity {
    private String username, password;
    private EditText inputUsername, inputPassword;
    private Button loginBtn, registerBtn;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // edit text
        inputUsername = (EditText) findViewById(R.id.input_username);
        inputPassword = (EditText) findViewById(R.id.input_password);
        //register button
        registerBtn = (Button) findViewById(R.id.register_btn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        loginBtn = (Button) findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateUsername()) {
                    return;
                } else if (!validatePassword()) {
                    return;
                } else {
                    login(UrlList.LOGIN_URL);
                }
            }
        });
    }

    private void login(String URL) {
        username = inputUsername.getText().toString().trim();
        password = inputPassword.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("register response", response);

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            String error_response = jsonObject.getString("error");
                            if (error_response == "false") {
                                String user_id = jsonObject.getString("user_id"); // get user id
                                PreferencesController.setUser(getApplicationContext(), user_id); // set user id in shared preference
                                Intent intentHome = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intentHome);
                            } else {
                                String error_message = jsonObject.getString("message");
                                Toast.makeText(LoginActivity.this, error_message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(stringRequest);
    }

    private boolean validateUsername() {
        if (inputUsername.getText().toString().trim().isEmpty()) {
            inputUsername.setError(getString(R.string.err_msg_username));
            requestFocus(inputUsername);
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
