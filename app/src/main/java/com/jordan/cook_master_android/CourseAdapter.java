package com.jordan.cook_master_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CourseAdapter extends ArrayAdapter<Course> {

        private LayoutInflater inflater;

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
                String truncatedText = originalText.substring(0, 30);
                contentTextView.setText(truncatedText + "...");
            }

            ImageView imageView = itemView.findViewById(R.id.image);
            Picasso.get().load(course.getImage()).into(imageView);

            TextView difficultyTextView = itemView.findViewById(R.id.difficulty);
            difficultyTextView.setText("Difficulté : " + String.valueOf(course.getDifficulty()));

            return itemView;
        }
}
