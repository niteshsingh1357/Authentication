package com.nitesh.authentication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LogInActivity extends AppCompatActivity {

    private EditText mEmail,mPassword;
    private Button mloginButton;
    private TextView mRegisterText;
    private TextInputLayout inputEmail, inputPassword;
    private Toolbar toolbar;
    AVLoadingIndicatorView avi;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mEmail = findViewById(R.id.input_email);
        mPassword = findViewById(R.id.input_password);
        mloginButton = findViewById(R.id.loginButton);
        mRegisterText = findViewById(R.id.register_text);
        inputEmail = findViewById(R.id.input_layout_email);
        inputPassword = findViewById(R.id.input_layout_password);
        avi = findViewById(R.id.avi);

        mEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        mPassword.addTextChangedListener(new MyTextWatcher(inputPassword));


        mRegisterText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnim();
                registerIntent();
            }
        });


        mloginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(TextUtils.isEmpty(mEmail.getText().toString().trim()) &&
                        TextUtils.isEmpty(mPassword.getText().toString().trim()))){

                    login(mEmail.getText().toString().trim(),mPassword.getText().toString().trim());
                }
            }
        });

    }

    private void registerIntent() {
        Intent i = new Intent (LogInActivity.this, Register.class);
        startActivity(i);
        finish();
    }

    private void login(String email, String pass) {
        System.out.println("Email"+ email);
        RequestQueue myRequestQueue = Volley.newRequestQueue(LogInActivity.this);
        String url = "http://npmc.herokuapp.com/api/accounts/login/";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("email",email);
        params.put("password", pass);
        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        stopAnim();
                        try {
                            token = response.getString("token");
                            System.out.println("Token is "+token);
                            SharedPreferences.Editor editor = getSharedPreferences("nitesh", MODE_PRIVATE).edit();
                            editor.putString("Token",token);
                            editor.apply();
                            mainIntent();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LogInActivity.this, "Email/Password do not match.", Toast.LENGTH_SHORT).show();
                mEmail.setText("");
                mPassword.setText("");

            }
        }
        );
        myRequestQueue.add(req);
    }

    private void mainIntent() {
        Intent i = new Intent(LogInActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    void startAnim(){
        avi.show();
        // or avi.smoothToShow();
    }

    void stopAnim(){
        avi.hide();
        // or avi.smoothToHide();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_email:
                   // validateEmail();
                    break;
                case R.id.input_password:
                   // validatePassword();
                    break;
            }
        }
    }

}
