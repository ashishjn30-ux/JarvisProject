package com.jarvisproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import okhttp3.*;

public class MainActivity extends AppCompatActivity {
    private OkHttpClient client = new OkHttpClient();
    private String BASE_URL = null; // will be loaded dynamically

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText input = findViewById(R.id.promptInput);
        Button sendBtn = findViewById(R.id.sendBtn);
        TextView responseView = findViewById(R.id.responseView);

        // Step 1: Fetch latest BASE_URL from backend /public_url
        new Thread(() -> {
            try {
                Request request = new Request.Builder()
                        .url("https://a669c1fff7d0.ngrok-free.app/public_url") // fallback old link
                        .build();
                Response response = client.newCall(request).execute();
                BASE_URL = response.body().string().trim();
                runOnUiThread(() -> responseView.setText("Connected to: " + BASE_URL));
            } catch (IOException e) {
                runOnUiThread(() -> responseView.setText("Error fetching backend URL"));
            }
        }).start();

        // Step 2: Send prompt when button clicked
        sendBtn.setOnClickListener(v -> {
            if (BASE_URL == null) {
                responseView.setText("Backend URL not loaded yet");
                return;
            }

            String prompt = input.getText().toString();
            String json = "{\"prompt\":\"" + prompt.replace("\"", "\\\"") + "\"}";

            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"),
                    json
            );

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
