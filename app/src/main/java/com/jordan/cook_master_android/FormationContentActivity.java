package com.jordan.cook_master_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FormationContentActivity extends AppCompatActivity {

    private int formationId;
    private static final String SHARED_PREFS_NAME = "MySharedPrefs";

    private ImageView formationImage;
    private TextView formationName;
    private TextView formationDescription;
    private TextView coursesCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formation_content);

        // Récupérer l'ID de la formation depuis l'intent
        Intent intent = getIntent();
        if (intent != null) {
            formationId = intent.getIntExtra("formation_id", -1);
        }

        formationImage = findViewById(R.id.formation_image);
        formationName = findViewById(R.id.formation_name);
        formationDescription = findViewById(R.id.formation_description);
        coursesCount = findViewById(R.id.courses_count);

        getFormations();
    }

    private void getFormations() {
        // Récupérer l'API key depuis les préférences partagées
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        String apiKey = preferences.getString("api_key", "");

        // Créer l'URL de la requête GET
        String url = BuildConfig.API_URL + "formations/" + formationId;

        // Créer les en-têtes de la requête avec l'API key
        Map<String, String> headers = new HashMap<>();
        headers.put("API-KEY", apiKey);

        // Créer la requête GET

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    // Traitement de la réponse JSON
                    try {
                        JSONObject formationObject = response;
                        JSONArray formationCoursesArray = formationObject.getJSONArray("formation_courses");
                        LinearLayout coursesContainer = findViewById(R.id.coursesContainer);
                        for (int i = 0; i < formationCoursesArray.length(); i++) {
                            JSONObject formationCourseObject = formationCoursesArray.getJSONObject(i);
                            int courseId = formationCourseObject.getInt("id");
                            // Extraire les données spécifiques de chaque formation_course
                            String courseName = formationCourseObject.getString("name");
                            String courseImage = formationCourseObject.getString("image");
                            // Construire le chemin d'accès complet de l'image
                            String baseUrl = "https://kavita.jordan95v.fr/storage/";
                            String imageUrl = baseUrl + courseImage;
                            int courseDuration = formationCourseObject.getInt("duration");
                            int courseDifficulty = formationCourseObject.getInt("difficulty");
                            String courseContent = formationCourseObject.getString("content");

                            // Créer une vue pour afficher les données du cours de formation
                            View courseView = getLayoutInflater().inflate(R.layout.item_course_container, null);

                            // Récupérer les vues dans la vue du cours de formation
                            TextView courseNameTextView = courseView.findViewById(R.id.course_name);
                            ImageView courseImageView = courseView.findViewById(R.id.course_image);
                            TextView courseDurationTextView = courseView.findViewById(R.id.course_duration);
                            TextView courseContentTextView = courseView.findViewById(R.id.course_content);
                            TextView courseDifficultyTextView = courseView.findViewById(R.id.course_difficulty);

                            // Définir les valeurs des vues avec les données du cours de formation
                            courseNameTextView.setText(courseName);
                            Picasso.get().load(imageUrl).into(courseImageView);
                            if (courseContent.length() > 30) {
                                String truncatedText = courseContent.substring(0, 30);
                                courseContentTextView.setText(truncatedText + "...");
                            }
                            courseDurationTextView.setText("Duration: " + courseDuration);
                            courseDifficultyTextView.setText("Difficulty: " + courseDifficulty);

                            // Ajouter un listener pour détecter le clic sur le cours
                            final int finalCourseId = courseId; // Besoin d'une variable finale pour l'utiliser dans le listener
                            courseView.setOnClickListener(v -> {
                                // Lancer l'activité CourseContentActivity avec l'ID du cours en tant qu'extra
                                Intent intent = new Intent(FormationContentActivity.this, CourseContentActivity.class);
                                intent.putExtra("course_id", finalCourseId);
                                startActivity(intent);
                            });

                            // Ajouter la vue du cours de formation au conteneur
                            coursesContainer.addView(courseView);
                        }

                        String name = formationObject.getString("name");
                        String description = formationObject.getString("description");
                        String image = formationObject.getString("image");

                        // Construire le chemin d'accès complet de l'image
                        String baseUrl = "https://kavita.jordan95v.fr/storage/";
                        String imageUrl = baseUrl + image;

                        // Afficher les données de la formation dans les vues correspondantes
                        formationName.setText(name);
                        formationDescription.setText(description);
                        Picasso.get().load(imageUrl).into(formationImage);
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

