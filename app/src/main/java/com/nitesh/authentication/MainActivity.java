package com.nitesh.authentication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView mUname, mEmail;
    private Button mLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUname = findViewById(R.id.uname);
        mEmail = findViewById(R.id.email);
        mLogout = findViewById(R.id.btn);

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("nitesh", MODE_PRIVATE).edit();
                editor.putString("Token","not exists");
                editor.apply();
                loginIntent();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkStatus();
    }

    private void checkStatus() {
        String token;
        SharedPreferences abc = getSharedPreferences("nitesh", MODE_PRIVATE);
        token = abc.getString("Token", "not exists");
        checkAPI(token);
    }

    private void checkAPI(final String token) {
        if(token.equals("not exists")){
            System.out.println("Token does not exist");
            loginIntent();
        }
        else {


            RequestQueue MyRequestQueue = Volley.newRequestQueue(MainActivity.this);
            String url = "http://npmc.herokuapp.com/api/accounts/me/";
            System.out.println("MY Token is "+token);
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url,
                    null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    System.out.println("MY Response is : "+response);
                    try {
                        mEmail.setText(response.getString("email"));
                        mUname.setText(response.getString("username"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("Error"+ error.getMessage());
                    loginIntent();


                }
            })
            {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", "jwt "+token);
                    System.out.println("Tokeen  jwot "+token);
                    return headers;
                }
            };
            MyRequestQueue.add(req);



        }
    }

    private void loginIntent() {
        Intent i = new Intent(MainActivity.this, LogInActivity.class);
        startActivity(i);
        finish();
    }
}
