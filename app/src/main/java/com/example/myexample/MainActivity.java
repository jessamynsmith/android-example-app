package com.example.myexample;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<String> suggestions = new ArrayList<>();
    ArrayAdapter<String> adapter;

    private void retrieveData(String s){
        //Do your stuff here with the String s and store the list of your results in the list suggestions
        ArrayList<String> yourList = new ArrayList();
        suggestions = yourList;
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AutoCompleteTextView autocomplete = (AutoCompleteTextView)findViewById(R.id.autoComplete);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, suggestions);
        autocomplete.setAdapter(adapter);
        Activity activity = this;

        autocomplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //retrieveData(s);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d("Main", "text changed" + editable.toString());
                retrieveData(editable.toString());
            }
        });
    }
}