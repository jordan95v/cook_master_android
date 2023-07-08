package com.jordan.cook_master_android;

import static android.os.SystemClock.sleep;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormationContentActivity extends AppCompatActivity {

    private int formationId;
    private static final String SHARED_PREFS_NAME = "MySharedPrefs";

    private ImageView formationImage;
    private TextView formationName;
    private TextView formationDescription;
    private TextView coursesCount;
    private int fcoursesCount;
    private Button backButton;
    private Button certificateButton;
    private int finishedCoursesCount = 0;
    private Button subscribeButton;

    private ImageView certificateImage;
    private boolean allcourseChecked = false;
    private List<View> courseViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formation_content);

        Intent intent = getIntent();
        if (intent != null) {
            formationId = intent.getIntExtra("formation_id", -1);
            fcoursesCount = intent.getIntExtra("course_count", -1);
        }

        formationImage = findViewById(R.id.formation_image);
        formationName = findViewById(R.id.formation_name);
        formationDescription = findViewById(R.id.formation_description);
        coursesCount = findViewById(R.id.courses_count);
        backButton = findViewById(R.id.back_button);
        certificateButton = findViewById(R.id.certificate_button);
        subscribeButton = findViewById(R.id.subscribe_button);
        certificateImage = findViewById(R.id.certificate_image);

        subscribeButton.setOnClickListener(v -> subscribeToFormation());

        getFormation();
        isSubscribed();

        backButton.setOnClickListener(v -> {
            Intent intent1 = new Intent(FormationContentActivity.this, FormationActivity.class);
            startActivity(intent1);
        });
    }
    protected void onResume() {
        super.onResume();
        getFormation();
    }

    private void getFormation() {
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        String apiKey = preferences.getString("api_key", "");

        String url = BuildConfig.API_URL + "formations/" + formationId;

        Map<String, String> headers = new HashMap<>();
        headers.put("API-KEY", apiKey);

        @SuppressLint("SetTextI18n") JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject formationObject = response;
                        JSONArray formationCoursesArray = formationObject.getJSONArray("formation_courses");
                        LinearLayout coursesContainer = findViewById(R.id.coursesContainer);
                        coursesContainer.removeAllViews();
                        finishedCoursesCount = 0;
                        for (int i = 0; i < formationCoursesArray.length(); i++) {
                            JSONObject formationCourseObject = formationCoursesArray.getJSONObject(i);
                            int courseId = formationCourseObject.getInt("id");
                            String courseName = formationCourseObject.getString("name");
                            String courseImage = formationCourseObject.getString("image");
                            String baseUrl = "https://kavita.jordan95v.fr/storage/";
                            String imageUrl = baseUrl + courseImage;
                            int courseDuration = formationCourseObject.getInt("duration");
                            int courseDifficulty = formationCourseObject.getInt("difficulty");
                            String courseContent = formationCourseObject.getString("content");
                            View courseView = getLayoutInflater().inflate(R.layout.item_course_container, null);
                            ImageView checkImageView = courseView.findViewById(R.id.course_check);

                            if(formationCourseObject.getBoolean("is_finished")){
                                finishedCoursesCount++;
                                checkImageView.setVisibility(View.VISIBLE);
                            }




                            TextView courseNameTextView = courseView.findViewById(R.id.course_name);
                            courseViews.add(courseNameTextView);
                            ImageView courseImageView = courseView.findViewById(R.id.course_image);
                            courseViews.add(courseImageView);
                            TextView courseDurationTextView = courseView.findViewById(R.id.course_duration);
                            courseViews.add(courseDurationTextView);
                            TextView courseContentTextView = courseView.findViewById(R.id.course_content);
                            courseViews.add(courseContentTextView);
                            TextView courseDifficultyTextView = courseView.findViewById(R.id.course_difficulty);
                            courseViews.add(courseDifficultyTextView);

                            courseNameTextView.setText(courseName);
                            Picasso.get().load(imageUrl).into(courseImageView);
                            if (courseContent.length() > 30) {
                                String truncatedText = courseContent.substring(0, 30);
                                courseContentTextView.setText(truncatedText + "...");
                            }
                            courseDurationTextView.setText("Duration: " + courseDuration);
                            courseDifficultyTextView.setText("Difficulty: " + courseDifficulty);

                            final int finalCourseId = courseId;
                            courseView.setOnClickListener(v -> {
                                Intent intent = new Intent(FormationContentActivity.this, CourseContentActivity.class);
                                intent.putExtra("course_id", finalCourseId);
                                intent.putExtra("formation_id", formationId);
                                intent.putExtra("course_count", fcoursesCount);
                                intent.putExtra("coming_from","FormationContentActivity");
                                try {
                                    boolean isFinished = formationCourseObject.getBoolean("is_finished");
                                    intent.putExtra("is_finished", isFinished);
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }
                                startActivity(intent);
                            });

                            coursesContainer.addView(courseView);
                        }

                        String name = formationObject.getString("name");
                        String description = formationObject.getString("description");
                        String image = formationObject.getString("image");

                        String baseUrl = "https://kavita.jordan95v.fr/storage/";
                        String imageUrl = baseUrl + image;

                        formationName.setText(name);
                        formationDescription.setText(description);
                        coursesCount.setText("number of Courses: " + fcoursesCount);
                        Picasso.get().load(imageUrl).into(formationImage);
                        allcourseChecked = true;
                        getCertificateImage();
                        getCertificate();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(this, "Erreur lors de la récupération des formations: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("FormationActivity", "Erreur lors de la récupération des formations", error);
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


    private void getCertificate () {
        if (subscribeButton.getVisibility() == View.GONE && finishedCoursesCount == fcoursesCount) {
            Toast.makeText(this, "Vous avez terminé tous les cours de la formation", Toast.LENGTH_SHORT).show();
            for (View courseView : courseViews) {
                courseView.setOnClickListener(null);
                courseView.setClickable(false);
                courseView.setBackgroundColor(Color.parseColor("#E0E0E0"));
            }
            certificateButton.setVisibility(View.VISIBLE);

            // Ajouter un listener pour le bouton du certificat
            certificateButton.setOnClickListener(v -> {
                // Récupérer le chemin de l'image du certificat depuis les préférences partagées
                SharedPreferences preferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
                String imageCertification = preferences.getString("certification_image", "");
                if (!imageCertification.isEmpty()) {
                    // Construire le chemin d'accès complet de l'image
                    String baseUrl = "https://kavita.jordan95v.fr/storage/";
                    String imageUrl = baseUrl + imageCertification;

                    // Charger l'image dans le ImageView pour le certificat
                    Picasso.get().load(imageUrl).into(certificateImage);

                    // Rendre le ImageView pour le certificat visible
                    certificateImage.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(this, "Erreur lors de la récupération de l'image du certificat.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            certificateButton.setVisibility(View.GONE);
        }
    }


    private void getCertificateImage() {
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        String apiKey = preferences.getString("api_key", "");

        String url = BuildConfig.API_URL + "formations/" + formationId;

        Map<String, String> headers = new HashMap<>();
        headers.put("API-KEY", apiKey);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
                response -> {
                    try {
                        // Stocker le chemin de l'image du certificat dans les préférences partagées
                        String imageCertification = response.getString("image");
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("certification_image", imageCertification);
                        editor.apply();
                    } catch (JSONException e) {
                        //...
                    }
                },
                error -> {
                    //...
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


    private void subscribeToFormation() {
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        String apiKey = preferences.getString("api_key", "");

        String url = BuildConfig.API_URL + "formations/" + formationId + "/take";

        Map<String, String> headers = new HashMap<>();
        headers.put("API-KEY", apiKey);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
                response -> {
                    // Do something when the user is subscribed to the formation
                    getFormation();
                },
                error -> {
                    Toast.makeText(this, "Erreur lors de la souscription à la formation: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("FormationContentActivity", "Erreur lors de la souscription à la formation", error);
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

    private void isSubscribed() {
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        String apiKey = preferences.getString("api_key", "");

        String url = BuildConfig.API_URL + "formations/" + formationId;

        Map<String, String> headers = new HashMap<>();
        headers.put("API-KEY", apiKey);

        JsonObjectRequest checkRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                response -> {
                    // L'utilisateur est déjà inscrit à la formation, cachez le bouton de souscription
                    subscribeButton.setVisibility(View.GONE);
                },
                error -> {
                    // L'utilisateur n'est pas encore inscrit à la formation, montrez le bouton de souscription
                    subscribeButton.setVisibility(View.VISIBLE);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        };

        RequestQueue checkQueue = Volley.newRequestQueue(this);
        checkQueue.add(checkRequest);
    }


}
