package com.example.quoteapp;

import android.os.Bundle;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;
import android.util.Log;
import android.widget.ToggleButton;

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

    public void getFilters(){

        //TODO: Exclude empty filters (Ex. Athletics)
        String url = "https://api.quotable.io/tags";
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        ArrayList<String> filterList = new ArrayList<>();
        JsonArrayRequest filterRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

            final ChipGroup filterChips = findViewById(R.id.filterChips);

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

        }, (VolleyError error) -> Log.e("My Errors", "Error getting filters"));

        queue.add(filterRequest);

    }

    public void generateRandomQuote (View view) {

        TextView quoteBox = findViewById(R.id.quoteBox);
        ChipGroup filterChips = findViewById(R.id.filterChips);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        String url = "https://api.quotable.io/random";

        List<Integer> chipIds = filterChips.getCheckedChipIds();

        if (!chipIds.isEmpty()) {

            ToggleButton toggleAndOr = findViewById(R.id.toggleAndOr);
            url = url + "?tags=";
            String delimiter;

            if (toggleAndOr.isChecked()){
                delimiter = ",";
            }
            else{
                delimiter = "|";
            }

            for (Integer id : chipIds) {
                Chip checkChip = filterChips.findViewById(id);
                url = url.concat(checkChip.getText().toString()).concat(delimiter);
            }

            //remove trailing delimiter
            url = url.substring(0, url.length() - 1);

        }

        Log.i("API request", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, (JSONObject response) -> {
            try {

                String quoteText = response.get("content") + "\n\n- " + response.get("author");
                quoteBox.setText(quoteText);

            } catch (JSONException e) {

                Toast.makeText(MainActivity.this, "Error Printing Quote", Toast.LENGTH_SHORT).show();

            }
        }, (VolleyError error) -> {
            String responseBody = new String(error.networkResponse.data);
            try {

                String errMsg;
                JSONObject errResponse = new JSONObject(responseBody);
                if ((int)(errResponse.get("statusCode")) == 404){
                    errMsg = "404: No Quote Found";
                }
                else{
                    errMsg = errResponse.get("statusCode") + ": " + errResponse.get("statusMessage");
                }
                quoteBox.setText(errMsg);

            } catch (JSONException e) {

                throw new RuntimeException(e);

            }
        });

        queue.add(request);

    }
}