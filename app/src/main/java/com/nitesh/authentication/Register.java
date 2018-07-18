package com.nitesh.authentication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Register extends AppCompatActivity {
    private EditText mUnText, mEmailText, mPassText, mcpassText;
    private Button mSubmmit, mLoginButton;
    private AVLoadingIndicatorView avl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUnText = findViewById(R.id.unText);
        mEmailText = findViewById(R.id.emailText);
        mPassText = findViewById(R.id.pwdText);
        mcpassText = findViewById(R.id.confirmText);
        mSubmmit = findViewById(R.id.submitButton);
        mLoginButton = findViewById(R.id.loginButton);
        avl = findViewById(R.id.avl);
        avl.hide();

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginIntent();
            }
        });

        mSubmmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avl.show();
                if(!(TextUtils.isEmpty(mUnText.getText().toString().trim()) &&
                        TextUtils.isEmpty(mEmailText.getText().toString().trim()) &&
                        TextUtils.isEmpty(mPassText.getText().toString().trim()) &&
                        TextUtils.isEmpty(mcpassText.getText().toString().trim())))
                {
                    register((mUnText.getText().toString().trim()),
                            (mEmailText.getText().toString().trim()),
                            (mPassText.getText().toString().trim()),
                            (mcpassText.getText().toString().trim()));
                }
            }
        });
    }

    private void loginIntent() {
        Intent i = new Intent(Register.this, LogInActivity.class);
        startActivity(i);
        finish();
    }

    private void register(String u, String e, String p, String cp) {
        RequestQueue myRequestQueue = Volley.newRequestQueue(Register.this);
        String url = "http://npmc.herokuapp.com/api/accounts/register/";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("email",e);
        params.put("password", p);
        params.put("username", u);
        params.put("confirm_password", cp);
        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String token = response.getString("token");
                            SharedPreferences.Editor editor = getSharedPreferences("nitesh", MODE_PRIVATE).edit();
                            editor.putString("Token",token);
                            editor.apply();
//                            mainIntent();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String emailjson = null, usernamejson = null,main = null, passwordjson = null;

                NetworkResponse response = error.networkResponse;
                System.out.println("error code is "+response.statusCode);
                if(response != null && response.data != null){
                    switch(response.statusCode){
                        case 400:
                            main = new String(response.data);
                            emailjson = trimMessage(main, "email");


                            usernamejson = trimMessage(main, "username");

                            passwordjson = trimMessage(main, "non_field_errors");


                            if(emailjson != null) displayMessage(emailjson);
                            else if(usernamejson != null) displayMessage(usernamejson);
                            else displayMessage(passwordjson);
                            break;
                    }
                }
            }
            public String trimMessage(String json, String key){
                String trimmedString = null;

                try{
                    JSONObject obj = new JSONObject(json);
                    trimmedString = obj.getString(key);
                } catch(JSONException e){
                    e.printStackTrace();
                    return null;
                }

                return trimmedString;
            }

            //Somewhere that has access to a context
            public void displayMessage(String toastString){
                Toast.makeText(Register.this, (toastString.substring(2, toastString.length()-2)), Toast.LENGTH_LONG).show();
            }
        });

        myRequestQueue.add(req);
    }

    private void mainIntent() {
        avl.hide();
        Intent i = new Intent(Register.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
