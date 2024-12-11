package com.example.cdcnpmat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.cdcnpmat.Model.Bean.Categories;
import com.example.cdcnpmat.R;

import java.util.List;

public class CategoryAdapter extends ArrayAdapter<Categories> {

    private final Context context;
    private final List<Categories> categories;

    public CategoryAdapter(Context context, List<Categories> categories) {
        super(context, 0, categories);
        this.context = context;
        this.categories = categories;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        }

        TextView tvCategoryName = convertView.findViewById(R.id.tv_category_name);

        Categories category = categories.get(position);
        tvCategoryName.setText(category.getNameCategory());

        return convertView;
    }
}
