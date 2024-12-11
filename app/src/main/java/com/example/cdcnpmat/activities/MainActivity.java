//package com.example.cdcnpmat.activities;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.content.Intent;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.os.Bundle;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ListView;
//import android.widget.Toast;
//
//import com.example.cdcnpmat.Model.News;
//import com.example.cdcnpmat.R;
//import com.example.cdcnpmat.adapters.ItemAdapter;
//import com.google.android.material.textfield.TextInputEditText;
//import com.google.gson.JsonObject;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.util.ArrayList;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.MediaType;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//
//public class MainActivity extends AppCompatActivity {
//
//    ListView lv_main;
//    View view_add;
//    Dialog dialog;
//    TextInputEditText ed_name, ed_link;
//    Button btn_add, btn_del, btn_cancel;
//    ItemAdapter adapter;
//    ArrayList<Item> list;
//
//    // SupabaseClient
//    SupabaseClient supabaseClient;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        lv_main = findViewById(R.id.lv_main);
//        view_add = findViewById(R.id.view_add);
//
//        supabaseClient = new SupabaseClient();
//
//        UpdateLV(); // Load data từ Supabase vào ListView
//
//        view_add.setOnClickListener(view -> openDialog());
//
//        lv_main.setOnItemClickListener((adapterView, view, i, l) -> {
//            if (checkNetwork()) {
//                String link = list.get(i).getLink();
//                if (!link.isEmpty()) {
//                    Intent intent = new Intent(MainActivity.this, NewsActivity.class);
//                    intent.putExtra("link", link);
//                    startActivity(intent);
//                }
//            } else {
//                NoInternetToast();
//            }
//        });
//
//        lv_main.setOnItemLongClickListener((adapterView, view, i, l) -> {
//            delete(list.get(i).getId());
//            return true;
//        });
//    }
//
//    public void openDialog() {
//        dialog = new Dialog(MainActivity.this);
//        dialog.setContentView(R.layout.dialog_add);
//        ed_name = dialog.findViewById(R.id.ed_name);
//        ed_link = dialog.findViewById(R.id.ed_link);
//        btn_add = dialog.findViewById(R.id.btn_add);
//        btn_add.setOnClickListener(view -> {
//            String name = ed_name.getText().toString().trim();
//            String link = ed_link.getText().toString().trim();
//            if (name.isEmpty() || link.isEmpty()) {
//                Toast.makeText(getApplicationContext(), "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
//            } else {
//                addNews(name, link);
//            }
//        });
//        dialog.show();
//    }
//
//    public void NoInternetToast() {
//        LayoutInflater inflater = getLayoutInflater();
//        View v = inflater.inflate(R.layout.no_internet_toast, null);
//        Toast toast = new Toast(getApplicationContext());
//        toast.setDuration(Toast.LENGTH_SHORT);
//        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//        toast.setView(v);
//        toast.show();
//    }
//
//    private boolean checkNetwork() {
//        ConnectivityManager conManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
//        return networkInfo != null && networkInfo.isConnected();
//    }
//
//    public void delete(String id) {
//        dialog = new Dialog(MainActivity.this);
//        dialog.setContentView(R.layout.dialog_del);
//        btn_cancel = dialog.findViewById(R.id.btn_cancel);
//        btn_del = dialog.findViewById(R.id.btn_del);
//        btn_cancel.setOnClickListener(view -> dialog.dismiss());
//        btn_del.setOnClickListener(view -> {
//            deleteNews(id);
//            dialog.dismiss();
//        });
//        dialog.show();
//    }
//
//    public void UpdateLV() {
//        supabaseClient.getData("news", new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to load data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    try {
//                        JSONArray jsonArray = new JSONArray(response.body().string());
//                        list = new ArrayList<>();
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            JSONObject jsonObject = jsonArray.getJSONObject(i);
//                            String id = jsonObject.getString("id");
//                            String name = jsonObject.getString("name");
//                            String link = jsonObject.getString("link");
//                            list.add(new News(id, name, link));
//                        }
//                        runOnUiThread(() -> {
//                            adapter = new ItemAdapter(getApplicationContext(), MainActivity.this, list);
//                            lv_main.setAdapter(adapter);
//                        });
//                    } catch (JSONException e) {
//                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to parse data", Toast.LENGTH_SHORT).show());
//                    }
//                } else {
//                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to load data: " + response.message(), Toast.LENGTH_SHORT).show());
//                }
//            }
//        });
//    }
//
//    private void addNews(String name, String link) {
//        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("name", name);
//        jsonObject.addProperty("link", link);
//
//        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json"));
//        Request request = new Request.Builder()
//                .url(supabaseClient.baseUrl + "news")
//                .header("apikey", SupabaseClient.SUPABASE_KEY)
//                .header("Authorization", "Bearer " + SupabaseClient.SUPABASE_KEY)
//                .post(body)
//                .build();
//
//        supabaseClient.client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to add news: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) {
//                if (response.isSuccessful()) {
//                    runOnUiThread(() -> {
//                        Toast.makeText(MainActivity.this, "News added successfully", Toast.LENGTH_SHORT).show();
//                        UpdateLV();
//                        dialog.dismiss();
//                    });
//                } else {
//                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to add news: " + response.message(), Toast.LENGTH_SHORT).show());
//                }
//            }
//        });
//    }
//
//    private void deleteNews(String id) {
//        Request request = new Request.Builder()
//                .url(supabaseClient.baseUrl + "news?id=eq." + id)
//                .header("apikey", SupabaseClient.SUPABASE_KEY)
//                .header("Authorization", "Bearer " + SupabaseClient.SUPABASE_KEY)
//                .delete()
//                .build();
//
//        supabaseClient.client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to delete news: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) {
//                if (response.isSuccessful()) {
//                    runOnUiThread(() -> {
//                        Toast.makeText(MainActivity.this, "News deleted successfully", Toast.LENGTH_SHORT).show();
//                        UpdateLV();
//                    });
//                } else {
//                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to delete news: " + response.message(), Toast.LENGTH_SHORT).show());
//                }
//            }
//        });
//    }
//}
