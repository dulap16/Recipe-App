package com.example.recipeai;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ShoppingListAdapter extends ArrayAdapter<Food> {

    private List<Food> list;
    private Context context;

    TextView currentFoodName, removeMeal, colorIndicator;

    public ShoppingListAdapter(Context context, List<Food> myIngredients) {
        super(context, 0, myIngredients);
        this.list = myIngredients;
        this.context = context;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        MainActivity mainActivity = (MainActivity)context;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.list_item, parent, false);

        final Food currentFood = list.get(position);

        currentFoodName = convertView.findViewById(R.id.food_item_name);
        removeMeal = convertView.findViewById(R.id.remove_item);
        colorIndicator = convertView.findViewById(R.id.color_indicator);

        currentFoodName.setText(currentFood.getName());
        colorIndicator.setBackgroundColor(Color.BLACK);

        removeMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.remove(position);
                mainActivity.removeShoppingListItem(currentFood.getName());
                mainActivity.addIngredient(currentFood.getName());
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

}
