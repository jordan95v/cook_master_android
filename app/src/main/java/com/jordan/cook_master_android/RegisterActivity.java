package com.jordan.cook_master_android;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    EditText email_field;
    EditText password_field;
    EditText confirm_password_field;
    EditText godfather_key_field;

    private boolean check_fields() {
        /* Check if the fields are not empty. */

        int error_count = 0;
        /* Check if the fields are not empty. */
        EditText[] fields = new EditText[]{this.email_field, this.password_field, this.confirm_password_field, this.godfather_key_field};
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
            body.put("email", this.email_field.getText().toString());
            body.put("password", this.password_field.getText().toString());
            body.put("godfather_key", this.godfather_key_field.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
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
        this.email_field = findViewById(R.id.email_field);
        this.password_field = findViewById(R.id.password_field);
        this.confirm_password_field = findViewById(R.id.confirm_password_field);
        this.godfather_key_field = findViewById(R.id.godfather_key_field);

        Button register_button = findViewById(R.id.register_button);
        register_button.setOnClickListener(v -> {
            if (this.check_fields()) {
                /* Call the API. */
                JSONObject body = this.create_body();
            }
        });
    }
}