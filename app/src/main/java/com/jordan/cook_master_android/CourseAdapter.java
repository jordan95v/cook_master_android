package com.jordan.cook_master_android;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CourseAdapter extends ArrayAdapter<Course> {

    // LayoutInflater va servir à instancier le layout XML dans l'objet View
    private LayoutInflater inflater;

    // Constructeur
    public CourseAdapter(Context context, List<Course> courses) {
        super(context, 0, courses);
        inflater = LayoutInflater.from(context);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = inflater.inflate(R.layout.item_course, parent, false);
        }

        Course course = getItem(position);

        TextView titleTextView = itemView.findViewById(R.id.name);
        titleTextView.setText(course.getName());

        TextView contentTextView = itemView.findViewById(R.id.content);
        String originalText = course.getContent();

        if (originalText.length() > 30) {
            String truncatedText = Html.fromHtml(originalText.substring(0, 30), Html.FROM_HTML_MODE_COMPACT).toString();
            contentTextView.setText(truncatedText + "...");
        }

        ImageView imageView = itemView.findViewById(R.id.image);
        Picasso.get().load(course.getImage()).into(imageView);

        TextView difficultyTextView = itemView.findViewById(R.id.difficulty);
        difficultyTextView.setText("Difficulté : " + String.valueOf(course.getDifficulty()));

        // Griser le cours si terminé
        if (course.isFinished()) {
            itemView.setBackgroundColor(Color.GRAY); // Changer cette couleur en fonction de vos besoins
        } else {
            itemView.setBackgroundColor(Color.WHITE); // Assurez-vous de réinitialiser la couleur d'arrière-plan si le cours n'est pas terminé
        }

        return itemView;
    }

}
