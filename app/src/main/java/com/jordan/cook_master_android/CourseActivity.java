package com.jordan.cook_master_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseActivity extends AppCompatActivity {

    private ListView listViewCourses;
    private List<Course> Courses;

    private static final String SHARED_PREFS_NAME = "MySharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        // Récupérer la ListView depuis le layout XML
        listViewCourses = findViewById(R.id.list_courses);

        getCourses();

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
                return true;
            }
            return true;
        });
    }

    private void getCourses() {
        // Récupérer l'API key depuis les préférences partagées
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        String apiKey = preferences.getString("api_key", "");


        // Créer l'URL de la requête GET
        String url = BuildConfig.API_URL + "courses";

        // Créer les en-têtes de la requête avec l'API key
        Map<String, String> headers = new HashMap<>();
        headers.put("API-KEY",  apiKey);

        // Créer la requête GET
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    // Traitement de la réponse JSON
                    try {
                        JSONArray data = response.getJSONArray("data");
                        Courses = new ArrayList<Course>();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject CourseObject = data.getJSONObject(i);
                            String name = CourseObject.getString("name");
                            String content = CourseObject.getString("content");
                            int difficulty = CourseObject.getInt("difficulty");
                            String image = CourseObject.getString("image");

                            // Construire le chemin d'accès complet de l'image
                            String baseUrl = "https://kavita.jordan95v.fr/storage/";
                            String imageUrl = baseUrl + image;

                            Course course = new Course(name, content, imageUrl, difficulty);
                            Courses.add(course);
                        }

                        // Mettre à jour l'adaptateur de la ListView avec les formations
                        CourseAdapter adapter = new CourseAdapter(this, Courses);
                        listViewCourses.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Gérer les erreurs de la requête
                    Toast.makeText(this, "Erreur lors de la récupération des Cours: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("CourseActivity", "Erreur lors de la récupération des Cours", error);
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
}
