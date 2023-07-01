package com.jordan.cook_master_android;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;



public class FormationActivity extends AppCompatActivity {
    private ListView listViewFormations;
    private List<Formation> formations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formations);

        // Récupérer la ListView depuis le layout XML
        listViewFormations = findViewById(R.id.list_formations);

        // Créer une liste de formations (exemple)
        formations = new ArrayList<>();
        formations.add(new Formation("Formation 1", "Description de la formation 1", "https://via.placeholder.com/150", 2));
        formations.add(new Formation("Formation 2", "Description de la formation 2", "https://via.placeholder.com/150", 3));
        formations.add(new Formation("Formation 3", "Description de la formation 3", "https://via.placeholder.com/150", 4));

        // Créer un adaptateur pour la liste des formations
        FormationAdapter adapter = new FormationAdapter(this, formations);

        // Associer l'adaptateur à la ListView
        listViewFormations.setAdapter(adapter);

        // Gérer les clics sur les éléments de la liste
        listViewFormations.setOnItemClickListener((parent, view, position, id) -> {
            Formation selectedFormation = formations.get(position);
            Toast.makeText(FormationActivity.this, selectedFormation.getName(), Toast.LENGTH_SHORT).show();
            // Vous pouvez ajouter ici le code pour afficher les détails de la formation sélectionnée

        });

        BottomNavigationView navbar = findViewById(R.id.bottom_navigation);
        navbar.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.formations) {
                return true;
            } else if (item.getItemId() == R.id.courses) {
                return true;
            } else if (item.getItemId() == R.id.user_profile) {
                return true;
            }
            return true;
        });
    }
}