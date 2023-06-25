package com.jordan.cook_master_android;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    EditText email_field;
    EditText password_field;
    TextView invalid_credentials;

    private boolean check_input_are_not_empty() {
        /* Check if the input are not empty. */

        int error_count = 0;
        if (this.email_field.getText().toString().isEmpty()) {
            this.email_field.setError("Username cannot be empty");
            error_count++;
        }
        if (this.password_field.getText().toString().isEmpty()) {
            this.password_field.setError("Password cannot be empty");
            error_count++;
        }
        return error_count == 0;
    }

    private JSONObject create_body() {
        /* Create the body to send. */

        JSONObject body = new JSONObject();
        try {
            body.put("email", this.email_field.getText().toString());
            body.put("password", this.password_field.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
    }

    private void call_api() {
        /* Call the API. */

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://kavita.jordan95v.fr/api/v1/login";
        JSONObject body = this.create_body();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, body, response -> {
            String answer = response.toString();
            Toast.makeText(this, answer, Toast.LENGTH_LONG).show();
        }, error -> {
            this.invalid_credentials.setVisibility(TextView.VISIBLE);
        });
        queue.add(request);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Get the input. */
        this.email_field = findViewById(R.id.email_field);
        this.password_field = findViewById(R.id.password_field);
        this.invalid_credentials = findViewById(R.id.invalid_credentials);

        /* This handle the login. */
        Button login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(v -> {
            this.invalid_credentials.setVisibility(TextView.GONE);
            if (this.check_input_are_not_empty()) {
                this.call_api();
            }
        });
    }
}