package com.example.tayqaprojectv1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rv;
    private TextView baseCode, baseRate;
    private ApiInterface apiInterface;
    public List<rates> ratesList;
    private RateAdapter adapter;
    static final String baseCurrency = "USD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
        adapter = new RateAdapter(this, new ArrayList<rates>());
        rv.setAdapter(adapter);
        getRatesList(baseCurrency);

        adapter.setOnItemClickListener(new RateAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String name = ratesList.get(position).getCode();
                getRatesList(name);
            }
        });

        baseRate.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    int ratio = Integer.parseInt(baseRate.getText().toString());
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.putExtra("ratio", ratio);
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    public void getRatesList(String name) {
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        baseCode.setText(name);
        Call<List<rates>> call = apiInterface.getRates(name);
        call.enqueue(new Callback<List<rates>>() {
            @Override
            public void onResponse(Call<List<rates>> call, Response<List<rates>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ratesList = response.body();
                    adapter.addData(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<rates>> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void bindViews() {
        baseCode = findViewById(R.id.baseCode);
        baseRate = findViewById(R.id.baseRate);
        rv = findViewById(R.id.recyclerView);
    }
}