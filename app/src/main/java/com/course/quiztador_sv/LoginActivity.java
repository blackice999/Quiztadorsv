package com.course.quiztador_sv;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvSignup;
    private EditText etUsername;
    private EditText etPassword;
    private Button button;
    public static final String LOGIN_LINK = "http://192.168.0.103/svcourse2018.1/users/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvSignup = findViewById(R.id.tv_signup);
        tvSignup.setOnClickListener(this);


        etUsername = findViewById(R.id.et_login_username);
        etPassword = findViewById(R.id.et_login_password);

        etUsername.addTextChangedListener(mTextWatcher);
        etPassword.addTextChangedListener(mTextWatcher);

        button = findViewById(R.id.btn_login);
        button.setEnabled(false);
        button.setOnClickListener(this);
        checkFieldsForEmptyValues();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_signup:
                Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.btn_login:
                sendRequest(etUsername.getText().toString(), etPassword.getText().toString());

        }
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            checkFieldsForEmptyValues();
        }
    };

    void checkFieldsForEmptyValues() {

        if (isEmpty(etUsername)) {
            etUsername.setError("Username can't be empty");
            button.setEnabled(false);
        }

        if (isEmpty(etPassword)) {
            etPassword.setError("Password can't be empty");
            button.setEnabled(false);
        }

        if (!isEmpty(etUsername) && !isEmpty(etPassword)) {
            button.setEnabled(true);
        }
    }

    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    private void sendRequest(String username, String password) {

        Gson gson = new Gson();
        try {
            JSONObject jsonRequest = new JSONObject(gson.toJson(new Credentials(username, password)));

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    LOGIN_LINK,
                    jsonRequest,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (response.has("authorizationToken")) {
                                try {
                                    String authToken = response.getString("authorizationToken");
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("authorizationToken", authToken);
                                    startActivity(intent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            int response = error.networkResponse.statusCode;

                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                Toast.makeText(LoginActivity.this, "No connection to server", Toast.LENGTH_LONG).show();
                                error.printStackTrace();
                            } else if (error instanceof AuthFailureError) {
                                if (response == 401) {
                                    Toast.makeText(LoginActivity.this, "Unauthorized/Please create an account", Toast.LENGTH_SHORT).show();
                                }
                                //TODO
                            } else if (error instanceof ServerError) {
                                if (response == 401) {
                                    Toast.makeText(LoginActivity.this, "Unauthorized af", Toast.LENGTH_SHORT).show();
                                }
                                //TODO
                            } else if (error instanceof NetworkError) {
                                //TODO
                            } else if (error instanceof ParseError) {
                                //TODO
                            }

                            error.printStackTrace();
                        }
                    }
            );

            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
