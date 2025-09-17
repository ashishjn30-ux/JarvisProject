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
    private String BASE_URL = null; // will fetch dynamically

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText input = findViewById(R.id.promptInput);
        Button sendBtn = findViewById(R.id.sendBtn);
        TextView responseView = findViewById(R.id.responseView);

        // Step 1: fetch ngrok URL from backend
        new Thread(() -> {
            Request req = new Request.Builder()
                .url("https://raw.githubusercontent.com/ashishjn30-ux/JarvisProject/main/public_url.txt")
                .build();
            try {
                Response res = client.newCall(req).execute();
                if (res.isSuccessful() && res.body() != null) {
                    BASE_URL = res.body().string().trim();
                    runOnUiThread(() -> responseView.setText("Connected to: " + BASE_URL));
                } else {
                    runOnUiThread(() -> responseView.setText("Failed to fetch backend URL"));
                }
            } catch (IOException e) {
                runOnUiThread(() -> responseView.setText("Error: " + e.getMessage()));
            }
        }).start();

        // Step 2: send prompt
        sendBtn.setOnClickListener(v -> {
            if (BASE_URL == null) {
                responseView.setText("Backend not ready yet!");
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
                    if (response.isSuccessful() && response.body() != null) {
                        final String res = response.body().string();
                        runOnUiThread(() -> responseView.setText(res));
                    } else {
                        runOnUiThread(() -> responseView.setText("Request failed"));
                    }
                } catch (IOException e) {
                    runOnUiThread(() -> responseView.setText("Error: " + e.getMessage()));
                }
            }).start();
        });
    }
}
