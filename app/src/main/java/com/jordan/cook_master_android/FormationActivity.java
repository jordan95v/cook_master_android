package com.jordan.cook_master_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FormationActivity extends AppCompatActivity {
    private ListView listViewFormations;
    private List<Formation> formations;
    private static final String SHARED_PREFS_NAME = "MySharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formations);

        SharedPreferences preferences1 = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        String api_key = preferences1.getString("api_key", "");

        getSubscriptionName();

        // Récupérer la ListView depuis le layout XML
        listViewFormations = findViewById(R.id.list_formations);

        getFormations();

        // Créer le listener pour la navigation
        listViewFormations.setOnItemClickListener((parent, view, position, id) -> {
            // Récupérer la formation sélectionnée
            Formation selectedFormation = formations.get(position);

            // Récupérer l'ID de la formation
            int formationId = selectedFormation.getId();

            // Lancer une nouvelle activité pour afficher le contenu de la formation
            Intent intent = new Intent(FormationActivity.this, FormationContentActivity.class);
            intent.putExtra("formation_id", formationId);
            intent.putExtra("course_count", selectedFormation.getCoursesCount());
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

    private void getFormations() {
        // Récupérer l'API key depuis les préférences partagées
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        String apiKey = preferences.getString("api_key", "");

        // Créer l'URL de la requête GET
        String url = BuildConfig.API_URL + "formations";

        // Créer les en-têtes de la requête avec l'API key
        Map<String, String> headers = new HashMap<>();
        headers.put("API-KEY",  apiKey);

        // Créer la requête GET
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    // Traitement de la réponse JSON
                    try {
                        formations = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject formationObject = response.getJSONObject(i);
                            int id = formationObject.getInt("id");
                            String name = formationObject.getString("name");
                            String description = formationObject.getString("description");
                            int coursesCount = formationObject.getInt("courses_count");
                            String image = formationObject.getString("image");

                            // Construire le chemin d'accès complet de l'image
                            String baseUrl = "https://kavita.jordan95v.fr/storage/";
                            String imageUrl = baseUrl + image;

                            Formation formation = new Formation(id,name, description, imageUrl, coursesCount);
                            formations.add(formation);
                        }

                        // Mettre à jour l'adaptateur de la ListView avec les formations
                        FormationAdapter adapter = new FormationAdapter(this, formations);
                        listViewFormations.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Gérer les erreurs de la requête
                    Toast.makeText(this, "Erreur lors de la récupération des formations: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("FormationActivity", "Erreur lors de la récupération des formations", error);
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

    private void getSubscriptionName() {
        /* Get the subscription name. */
        String url = BuildConfig.API_URL + "user";
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        String api_key = preferences.getString("api_key", "");
        Map<String, String> headers = new HashMap<>();
        headers.put("API-KEY",  api_key);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    String subscription_name = null;
                    try {
                        subscription_name = response.getString("subscription_name");
                    } catch (JSONException e) {
                        Toast.makeText(this, "Erreur lors de la récupération du nom d'abonnement: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("subscription_name", subscription_name);
                    editor.apply();
                    Log.d("FormationActivity", "Subscription name: " + subscription_name);

                    if(subscription_name.equals("free")) {
                        MobileAds.initialize(this, new OnInitializationCompleteListener() {
                            @Override
                            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                            }
                        });

                        AdView mAdView = findViewById(R.id.adView);
                        AdRequest adRequest = new AdRequest.Builder().build();
                        mAdView.loadAd(adRequest);
                    }
                }, error -> {
            Toast.makeText(this, "Erreur lors de la récupération du nom d'abonnement: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        };

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