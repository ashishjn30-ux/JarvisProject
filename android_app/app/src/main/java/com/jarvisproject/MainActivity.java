package com.jarvisproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private OkHttpClient client = new OkHttpClient();
    private static final String BASE_URL = "https://a669c1fff7d0.ngrok-free.app"; // replace if needed
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText input = findViewById(R.id.promptInput);
        Button sendBtn = findViewById(R.id.sendBtn);
        TextView responseView = findViewById(R.id.responseView);

        sendBtn.setOnClickListener(v -> {
            String prompt = input.getText().toString();

            // Build JSON string properly
            String json = "{\"prompt\":\"" + prompt.replace("\"", "\\\"") + "\"}";

            RequestBody body = RequestBody.create(JSON, json);

            Request request = new Request.Builder()
                    .url(BASE_URL + "/jarvis/prompt")
                    .post(body)
                    .build();

            new Thread(() -> {
                try {
                    Response response = client.newCall(request).execute();
                    final String res = response.body().string();
                    runOnUiThread(() -> responseView.setText(res));
                } catch (IOException e) {
                    runOnUiThread(() -> responseView.setText("Error: " + e.getMessage()));
                }
            }).start();
        });
    }
}
