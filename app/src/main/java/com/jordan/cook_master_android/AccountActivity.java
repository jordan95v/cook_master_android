package com.jordan.cook_master_android;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class    AccountActivity extends AppCompatActivity {

    private TextView textViewName;
    private TextView textViewEmail;
    private ImageView imageViewUser;
    private  TextView textViewTotalDiscount;


    private static final String SHARED_PREFS_NAME = "MySharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        textViewName = findViewById(R.id.txt_user_name);
        textViewEmail = findViewById(R.id.txt_user_email);
        imageViewUser = findViewById(R.id.user_image);
        textViewTotalDiscount = findViewById(R.id.txt_total_discount);



        getUserAccount();


        Button buttonLogout = findViewById(R.id.btn_logout);

        //Logout
        buttonLogout.setOnClickListener(v -> {
            // Supprimer l'API key des préférences partagées
            SharedPreferences preferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
            preferences.edit().remove("api_key").apply();
            preferences.edit().remove("subscription_name").apply();
            preferences.edit().remove("is_finished").apply();


            // Rediriger vers l'activité de connexion
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        BottomNavigationView navbar = findViewById(R.id.bottom_navigation);
        navbar.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.formations) {
                Intent intent = new Intent(this, FormationActivity.class);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.courses) {
                Intent intent = new Intent(this, CourseActivity.class);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.user_profile) {
                Intent intent = new Intent(this, AccountActivity.class);
                startActivity(intent);
                return true;
            }
            return true;
        });
    }

    private void getUserAccount() {
        // Récupérer l'API key depuis les préférences partagées
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        String apiKey = preferences.getString("api_key", "");

        // Créer l'URL de la requête GET
        String url = BuildConfig.API_URL + "user";

        System.out.println("API KEY : " + apiKey);

        // Créer les en-têtes de la requête avec l'API key
        Map<String, String> headers = new HashMap<>();
        headers.put("API-KEY",  apiKey);

        // Créer la requête GET
        @SuppressLint("SetTextI18n") JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    // Traitement de la réponse JSON
                    try {
                        String name = response.getString("name");
                        String email = response.getString("email");
                        String image = response.getString("avatar");
                        int total_discount = response.getInt("total_discount");

                        // Construire le chemin d'accès complet de l'image
                        String baseUrl = "https://kavita.jordan95v.fr/storage/users-avatar/";
                        String imageUrl = baseUrl + image;

                        // Définir le texte des TextViews avec les informations de l'utilisateur
                        textViewName.setText("Pseudo : " + name);
                        textViewEmail.setText("Email : " + email);
                        textViewTotalDiscount.setText("Portefeuille: " + total_discount + " €");

                        // Définir l'image de l'ImageView avec l'image de l'utilisateur
                        Picasso.get().load(imageUrl).into(imageViewUser);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Gérer les erreurs de la requête
                    Toast.makeText(this, "Erreur lors de la récupération des informations de l'utilisateur: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("AccountActivity", "Erreur lors de la récupération des informations de l'utilisateur", error);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        };

        // Ajouter la requête à la file d'attente Volley
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }


    private void checkApiKey() {
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        String apiKey = preferences.getString("api_key", "");


        if (apiKey.isEmpty()) {
            // La clé n'est pas stockée dans les préférences
            Toast.makeText(this, "La clé d'API n'est pas stockée dans les préférences", Toast.LENGTH_SHORT).show();
        } else {
            // La clé est stockée dans les préférences
            Toast.makeText(this, "La clé d'API est correctement stockée dans les préférences", Toast.LENGTH_SHORT).show();
        }
    }
}
