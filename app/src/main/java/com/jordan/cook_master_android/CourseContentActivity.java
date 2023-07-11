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
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
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
    private Boolean courseFinished;
    private TextView courseStatus;
    private Button backButton;
    private Integer fcoursesCount;
    private Integer formationId;
    private static final String SHARED_PREFS_NAME = "MySharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_content);

        // Récupérer l'ID du cours depuis l'intent
        Intent intent = getIntent();
        if (intent != null) {
            CourseId = intent.getIntExtra("course_id", -1);
            formationId = intent.getIntExtra("formation_id", -1);
            fcoursesCount = intent.getIntExtra("course_count", -1);
            courseFinished = intent.getBooleanExtra("is_finished", false);
        }


        CourseName = findViewById(R.id.course_name);
        CourseDescription = findViewById(R.id.course_description);
        CourseImage = findViewById(R.id.course_image);
        CourseDuration = findViewById(R.id.course_duration);
        CourseDifficulty = findViewById(R.id.course_difficulty);
        completeButton = findViewById(R.id.complete_button);
        courseStatus = findViewById(R.id.course_status);
        backButton = findViewById(R.id.back_button);
        String comingFrom = intent.getStringExtra("coming_from");

        /*if(courseFinished){
            completeButton.setVisibility(View.GONE);
            courseStatus.setText("Cours terminé");
        }else{
            completeButton.setVisibility(View.VISIBLE);
            courseStatus.setText("Cours en cours");
            completeButton.setOnClickListener(v -> markCourseAsFinished());
        }*/

        backButton.setOnClickListener(v -> {
            if("FormationContentActivity".equals(comingFrom)){
                Intent intent1 = new Intent(CourseContentActivity.this, FormationContentActivity.class);
                intent1.putExtra("is_finished", courseFinished);
                intent1.putExtra("formation_id", formationId);
                intent1.putExtra("course_count", fcoursesCount);
                startActivity(intent1);
            }else if("CourseActivity".equals(comingFrom)){
                Intent intent1 = new Intent(CourseContentActivity.this, CourseActivity.class);
                startActivity(intent1);
            }
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

//                        finished();

                        // Définir le statut du bouton
                        if(courseFinished){
                            completeButton.setText("Terminé");
                            completeButton.setEnabled(false);  // Désactiver le bouton
                            courseStatus.setText("Terminé");
                        } else {
                            completeButton.setText("Marquer comme terminé");
                            completeButton.setEnabled(true);   // Activer le bouton
                            completeButton.setOnClickListener(v -> markCourseAsFinished());
                            courseStatus.setText("Cours en cours");
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Récupérer le message d'erreur du VolleyError
                    String errorMessage = error.getMessage();
                    Log.e("TAG", "Error message: " + errorMessage);

                    // Créer un nouvel Intent pour démarrer l'activité précédente
                    String comingFrom = getIntent().getStringExtra("coming_from");
                    Intent intent;
                    if ("FormationContentActivity".equals(comingFrom)) {
                        intent = new Intent(CourseContentActivity.this, FormationContentActivity.class);
                    } else { // Assuming the default is CourseActivity
                        intent = new Intent(CourseContentActivity.this, CourseActivity.class);
                    }

                    // Passer le message d'erreur à l'activité précédente
                    intent.putExtra("error_message", errorMessage);

                    // Passer les valeurs de formationId, is_finished et course_count à l'activité précédente
                    intent.putExtra("formation_id", formationId);
                    intent.putExtra("is_finished", courseFinished);
                    intent.putExtra("course_count", fcoursesCount);

                    startActivity(intent);
                }

        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
                    VolleyError error = new VolleyError(new String(volleyError.networkResponse.data, StandardCharsets.UTF_8));
                    volleyError = error;
                }

                return volleyError;
            }
        };


        // Ajouter la requête à la file d'attente Volley
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void markCourseAsFinished(){
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        String apiKey = preferences.getString("api_key", "");
        int finishedCoursesCount = preferences.getInt("finished_courses_count", 0);

        finishedCoursesCount++;
        preferences.edit().putInt("finished_courses_count", finishedCoursesCount).apply();

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

/*    private void finished (){
        if(courseFinished){
            completeButton.setVisibility(View.GONE);
            courseStatus.setText("Cours terminé");
        } else {
            completeButton.setEnabled(true);
            completeButton.setText("Marquer comme terminé");
        }
    }*/

}
