package com.jordan.cook_master_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CourseContentActivity extends AppCompatActivity {

    private TextView CourseName;
    private TextView CourseDescription;
    private ImageView CourseImage;
    private TextView CourseDuration;
    private TextView CourseDifficulty;
    private Integer CourseId;
    private Button completeButton;
    private Boolean courseFinished = false;
    private TextView courseStatus;
    private static final String SHARED_PREFS_NAME = "MySharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_content);

        // Récupérer l'ID du cours depuis l'intent
        Intent intent = getIntent();
        if (intent != null) {
            CourseId = intent.getIntExtra("course_id", -1);
            courseFinished = intent.getBooleanExtra("is_finished", false);
        }


        CourseName = findViewById(R.id.course_name);
        CourseDescription = findViewById(R.id.course_description);
        CourseImage = findViewById(R.id.course_image);
        CourseDuration = findViewById(R.id.course_duration);
        CourseDifficulty = findViewById(R.id.course_difficulty);
        completeButton = findViewById(R.id.complete_button);
        courseStatus = findViewById(R.id.course_status);

        completeButton.setOnClickListener(v -> {
            markCourseAsFinished();
        });
        getCourses();

    }

    private void getCourses() {
        // Récupérer l'API key depuis les préférences partagées
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        String apiKey = preferences.getString("api_key", "");

        // Créer l'URL de la requête GET
        String url = BuildConfig.API_URL + "courses/" + CourseId;

        // Créer les en-têtes de la requête avec l'API key
        Map<String, String> headers = new HashMap<>();
        headers.put("API-KEY",  apiKey);

        // Créer la requête GET
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d("CourseActivity", "Response received");
                    // Traitement de la réponse JSON
                    try {
                        JSONObject courseObject = response;
                        String name = courseObject.getString("name");
                        Log.d("CourseActivity", "Course name: " + name);
                        String description = courseObject.getString("content");
                        String image = courseObject.getString("image");
                        String duration = courseObject.getString("duration");
                        String difficulty = courseObject.getString("difficulty");

                        // Construire le chemin d'accès complet de l'image
                        String baseUrl = "https://kavita.jordan95v.fr/storage/";
                        String imageUrl = baseUrl + image;

                        // Afficher les données de la formation dans les vues correspondantes
                        CourseName.setText(name);
                        CourseDescription.setText(description);
                        Picasso.get().load(imageUrl).into(CourseImage);
                        CourseDuration.setText("Durée du Cours : " + duration + " minutes");
                        CourseDifficulty.setText("Difficulté : " + difficulty + "/5");

                        // Mettre à jour l'état du bouton
                        if(courseFinished){
                            completeButton.setVisibility(View.GONE);
                            courseStatus.setText("Cours terminé");
                        } else {
                            completeButton.setEnabled(true);
                            completeButton.setText("Marquer comme terminé");
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Gérer les erreurs de la requête
                    Toast.makeText(this, "Erreur lors de la récupération des cours: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("CourseActivity", "Erreur lors de la récupération des cours", error);
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

    private void markCourseAsFinished(){
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        String apiKey = preferences.getString("api_key", "");

        // Créer l'URL de la requête POST
        String url = BuildConfig.API_URL + "courses/" + CourseId + "/finished";

        // Créer les en-têtes de la requête avec l'API key
        Map<String, String> headers = new HashMap<>();
        headers.put("API-KEY", apiKey);

        // Créer la requête POST
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
                response -> {
                    // Traitement de la réponse JSON
                    try {
                        String message = response.getString("message");

                        // Afficher un Toast avec le message de la réponse
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();


                        completeButton.setVisibility(View.GONE);
                        courseStatus.setText("Cours terminé");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Gérer les erreurs de la requête
                    Toast.makeText(this, "Erreur lors de la mise à jour du cours: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("CourseActivity", "Erreur lors de la mise à jour du cours", error);
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
