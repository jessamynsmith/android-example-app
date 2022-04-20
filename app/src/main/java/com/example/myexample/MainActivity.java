package com.example.myexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    ArrayAdapter<String> adapter;

    List<String> suggestions = new ArrayList<>();

    private void retrieveData(String searchParam){
        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        String authorUrl = "https://underquoted.herokuapp.com/api/v2/author_summary/";
        String url = authorUrl + "?name=" + searchParam;
        JsonArrayRequest mRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, response -> {
                    Log.d("response", response.toString());
                    adapter.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject item = response.getJSONObject(i);
                            adapter.add((String) item.get("name"));
                        }
                    } catch (org.json.JSONException e) {
                        Log.e("JSON exception", e.toString());
                    }
                    adapter.notifyDataSetChanged();
                    adapter.getFilter().filter(searchParam);
                }, error -> Log.e("JsonArrayRequest error", error.toString()));

        mRequestQueue.add(mRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AutoCompleteTextView autocomplete = findViewById(R.id.autoComplete);
        adapter = new ArrayAdapter<>(this,
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

        MenuItem searchMenu = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenu);

        final SearchView.SearchAutoComplete autocomplete = searchView.findViewById(androidx.appcompat.R.id.search_src_text);

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, suggestions);
        autocomplete.setAdapter(adapter);
        autocomplete.setDropDownBackgroundResource(android.R.color.background_light);

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

        return true;
    }


}
