package com.example.quoteapp;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getFilters();
    }

    public ArrayList<String> getFilters(){

        String url = "https://api.quotable.io/tags";
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        ArrayList<String> filterList = new ArrayList<String>();
        JsonArrayRequest filterRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

            ChipGroup filterChips = findViewById(R.id.filterChips);

            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0 ; i < response.length() ; i++){

                    try {

                        JSONObject tag = new JSONObject(response.get(i).toString());
                        //Log.i("My Messages", tag.get("name").toString());
                        filterList.add(tag.get("name").toString());

                    } catch (JSONException e) {
                        Log.e("My Errors", "Can not create JSONObject for filter");
                    }

                }

                for (int i = 0 ; i < filterList.size() ; i++){
                    Chip categoryChip = new Chip(MainActivity.this);
                    categoryChip.setText(filterList.get(i));
                    categoryChip.setCheckable(true);
                    filterChips.addView(categoryChip);

                    Log.i("My Messages", filterList.get(i));
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("My Errors", "Error getting filters");
            }
        });

        queue.add(filterRequest);

        return filterList;

    }

    public void generateRandomQuote (View view) {

        TextView quoteBox = findViewById(R.id.quoteBox);
        ChipGroup filterChips = findViewById(R.id.filterChips);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        String url = "https://api.quotable.io/random";

        String selectedFilters = "";
        List<Integer> chipIds = filterChips.getCheckedChipIds();
        for (Integer id:chipIds){
            Chip checkChip = filterChips.findViewById(id);
            selectedFilters = selectedFilters + checkChip.getText();
        }

        quoteBox.setText(selectedFilters);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    quoteBox.setText(response.get("content").toString());
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, "Error Printing Quote", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Something is wrong", Toast.LENGTH_SHORT).show();
            }
        });




        // Request a string response from the provided URL.
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(MainActivity.this, "Error with Volley", Toast.LENGTH_SHORT).show();
//            }
//        });

        // Add the request to the RequestQueue.
//        queue.add(request);
    }
}