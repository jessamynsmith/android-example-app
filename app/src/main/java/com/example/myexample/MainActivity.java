package com.example.myexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private RequestQueue mRequestQueue;
    private JsonArrayRequest mRequest;
    private String authorUrl = "https://underquoted.herokuapp.com/api/v2/author_summary/";

    ArrayAdapter<String> adapter;

    List<String> suggestions = new ArrayList<>();

    private void retrieveData(String searchParam){
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        String url = authorUrl + "?name=" + searchParam;
        mRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("response", response.toString());
                        adapter.clear();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject item = response.getJSONObject(i);
                                adapter.add((String)item.get("name"));
                            }
                        } catch (org.json.JSONException e) {
                            Log.e("JSON exception", e.toString());
                        }
                        adapter.notifyDataSetChanged();
                        adapter.getFilter().filter(searchParam);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("JsonArrayRequest error", error.toString());

                    }
                });

        mRequestQueue.add(mRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AutoCompleteTextView autocomplete = (AutoCompleteTextView)findViewById(R.id.autoComplete);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, suggestions);
        autocomplete.setThreshold(1);
        autocomplete.setAdapter(adapter);

        autocomplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d("afterTextChanged", editable.toString());
                retrieveData(editable.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        return true;
    }


}
