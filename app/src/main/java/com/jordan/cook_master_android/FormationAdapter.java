package com.jordan.cook_master_android;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FormationAdapter extends ArrayAdapter<Formation> {

    private LayoutInflater inflater;

    public FormationAdapter(Context context, List<Formation> formations) {
        super(context, 0, formations);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = inflater.inflate(R.layout.item_formation, parent, false);
        }

        Formation formation = getItem(position);

        TextView titleTextView = itemView.findViewById(R.id.text_title);
        titleTextView.setText(formation.getName());

        TextView descriptionTextView = itemView.findViewById(R.id.text_description);
        descriptionTextView.setText(Html.fromHtml(formation.getDescription(), Html.FROM_HTML_MODE_COMPACT));

        ImageView imageView = itemView.findViewById(R.id.image_formation);
        Picasso.get().load(formation.getImage()).into(imageView);

        TextView coursesCountTextView = itemView.findViewById(R.id.text_courses_count);
        coursesCountTextView.setText("Nombre de cours : " + String.valueOf(formation.getCoursesCount()));

        return itemView;
    }

}
