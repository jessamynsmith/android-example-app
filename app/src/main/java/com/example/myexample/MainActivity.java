package com.example.myexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONArray;
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
    JSONArray detailData;

    String authorName;
    TextView authorNameView;

    RecyclerView recyclerView;
    ArrayList<ListItem> myListData = new ArrayList<ListItem>();
    MyListAdapter recyclerAdapter;

    private void retrieveAutocompleteData(String searchParam){
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

    private void retrieveDetailData(String searchParam){
        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        String authorUrl = "https://underquoted.herokuapp.com/api/v2/author_details/";
        String url = authorUrl + "?name=" + searchParam;
        JsonArrayRequest mRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, response -> {
                    Log.d("response", response.toString());
                    detailData = response;
                    if (response.length() > 0) {
                        try {
                            JSONObject firstItem = response.getJSONObject(0);
                            authorName = firstItem.getString("name");
                            Log.d("authorName", authorName);
                            authorNameView.setText(authorName);

                            JSONArray quotations = firstItem.getJSONArray("underquoted");
                            for (int i = 0; i < quotations.length(); i++) {
                                JSONObject item = quotations.getJSONObject(i);
                                myListData.add(new ListItem((String)item.get("text")));
                            }

                            ListItem[] arr = new ListItem[myListData.size()];
                            recyclerAdapter = new MyListAdapter(myListData.toArray(arr));

                            recyclerView.setAdapter(recyclerAdapter);
                        } catch (org.json.JSONException e) {
                            Log.e("JSON exception", e.toString());
                        }
                    }
                }, error -> Log.e("JsonArrayRequest error", error.toString()));

        mRequestQueue.add(mRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authorNameView = findViewById(R.id.authorName);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        MenuItem searchMenu = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenu);
        searchView.setSubmitButtonEnabled(true);
        
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("onQueryTextSubmit", query);
                retrieveDetailData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        final SearchView.SearchAutoComplete autocomplete = searchView.findViewById(androidx.appcompat.R.id.search_src_text);

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, suggestions);
        autocomplete.setAdapter(adapter);
        autocomplete.setDropDownBackgroundResource(android.R.color.background_light);

        autocomplete.setOnItemClickListener((parent, view, position, id) -> {
            Log.d("onItemClick", String.valueOf(position));
            String queryString = (String)parent.getItemAtPosition(position);
            autocomplete.setText(queryString);
        });

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
                retrieveAutocompleteData(editable.toString());
            }
        });

        return true;
    }

}
