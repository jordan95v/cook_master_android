package com.jordan.cook_master_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    EditText username_field;
    EditText email_field;
    EditText password_field;
    EditText confirm_password_field;
    EditText godfather_key_field;
    TextView errors;
    boolean call_success = false;

    private void set_error(String error) {
        /* Set the error message. */

        this.errors.setText(error);
        this.errors.setVisibility(TextView.VISIBLE);
    }

    private boolean check_fields() {
        /* Check if the fields are not empty. */

        int error_count = 0;
        /* Check if the fields are not empty. */
        EditText[] fields = new EditText[]{this.username_field, this.email_field, this.password_field, this.confirm_password_field};
        for (EditText field : fields) {
            if (field.getText().toString().isEmpty()) {
                field.setError(getResources().getText(R.string.field_cannot_be_empty));
                error_count++;
            }
        }
        /* Check if the password are the same. */
        if (!this.password_field.getText().toString().equals(this.confirm_password_field.getText().toString())) {
            this.confirm_password_field.setError(getResources().getText(R.string.password_no_match));
            error_count++;
        }
        return error_count == 0;
    }

    private JSONObject create_body() {
        /* Create the body to send. */

        JSONObject body = new JSONObject();
        try {
            body.put("name", this.username_field.getText().toString());
            body.put("email", this.email_field.getText().toString());
            body.put("password", this.password_field.getText().toString());
            body.put("godfather_key", this.godfather_key_field.getText().toString());
            body.put("lang", Locale.getDefault().getLanguage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
    }

    private void call_api() {
        /* Call the API. */

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = BuildConfig.API_URL + "register";
        JSONObject body = this.create_body();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, body, response -> {
            String api_key = null;
            try {
                api_key = response.getString("key");
            } catch (JSONException e) {
                this.set_error(getResources().getText(R.string.error_happened).toString());
                return;
            }
            SharedPreferences preferences = this.getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("api_key", api_key);
            editor.apply();
            this.call_success = true;
        }, error -> {
            try {
                JSONObject ret = new JSONObject(new String(error.networkResponse.data, StandardCharsets.UTF_8));
                JSONArray errors = ret.getJSONArray("errors");
                StringBuilder errors_str = new StringBuilder();
                for (int i = 0; i < errors.length(); i++) {
                    errors_str.append("- ").append(errors.getString(i)).append("\n");
                }
                this.set_error(errors_str.toString());
            } catch (JSONException e) {
                this.set_error(getResources().getText(R.string.error_happened).toString());
            }
        });
        queue.add(request);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        /* Get back to login activity */
        Button login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(v -> {
            this.finish();
        });

        /* Setup fields */
        this.username_field = findViewById(R.id.username_field);
        this.email_field = findViewById(R.id.email_field);
        this.password_field = findViewById(R.id.password_field);
        this.confirm_password_field = findViewById(R.id.confirm_password_field);
        this.godfather_key_field = findViewById(R.id.godfather_key_field);
        this.errors = findViewById(R.id.errors);

        Button register_button = findViewById(R.id.register_button);
        register_button.setOnClickListener(v -> {
            if (this.check_fields()) {
                this.call_api();
                if (this.call_success) {
                    Intent intent = new Intent(this, FormationActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}