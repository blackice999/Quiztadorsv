package com.course.quiztador_sv;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvLogin;
    private EditText etUsername;
    private EditText etPassword;
    private Button button;
    private String REGISTER_LINK = "http://192.168.0.103/svcourse2018.1/users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tvLogin = findViewById(R.id.tv_login);
        tvLogin.setOnClickListener(this);
        etUsername = findViewById(R.id.et_register_username);
        etPassword = findViewById(R.id.et_register_password);

        etUsername.addTextChangedListener(mTextWatcher);
        etPassword.addTextChangedListener(mTextWatcher);

        button = findViewById(R.id.btn_register);
        button.setEnabled(false);
        button.setOnClickListener(this);
        checkFieldsForEmptyValues();
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
            etPassword.setError("Details can't be empty");
            button.setEnabled(false);
        }

        if (!isEmpty(etUsername) && !isEmpty(etPassword)) {
            button.setEnabled(true);
        }
    }

    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_login:
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.btn_register:
                sendRequest(etUsername.getText().toString(), etPassword.getText().toString());

        }
    }

    private void sendRequest(String username, String password) {
        Gson gson = new Gson();
        try {
            JSONObject jsonRequest = new JSONObject(gson.toJson(new Credentials(username, password)));
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    REGISTER_LINK,
                    jsonRequest,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.d("fishJson", response.toString());
                            if (response.has("authorizationToken")) {
                                try {
                                    String authToken = response.getString("authorizationToken");

                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
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

                            Map<String, String> response = error.networkResponse.headers;

                            for (Map.Entry<String, String> entry : response.entrySet()) {
                                Log.d("fishMap", entry.getKey() + "/" + entry.getValue());
                            }
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                Toast.makeText(RegisterActivity.this, "No connection to server", Toast.LENGTH_LONG).show();
                            } else if (error instanceof AuthFailureError) {
                                //TODO
                            } else if (error instanceof ServerError) {
                                for (Map.Entry<String, String> entry : response.entrySet()) {
                                    Log.d("fishMap", entry.getKey() + "/" + entry.getValue());
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
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", "asdfdsafvsdfsda");
                    return params;
                }
            };

            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
