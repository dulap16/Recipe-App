package com.example.recipeai;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FavouriteAdapter extends ArrayAdapter<Recipe> {

    private List<Recipe> list;
    private Context context;

    TextView currentRecipeName, recipeIngredients, recipeInstructions, colorIndicator,
            INGREDIENTS, INSTRUCTIONS;
    ImageView unfavoriteRecipe, useFavourite;

    public FavouriteAdapter(Context context, List<Recipe> myRecipes) {
        super(context, 0, myRecipes);
        this.list = myRecipes;
        this.context = context;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.favourite_item, parent, false);

        final Recipe currentRecipe = list.get(position);

        currentRecipeName = convertView.findViewById(R.id.favourite_name);
        recipeIngredients = convertView.findViewById(R.id.favourite_ingredients);
        recipeInstructions = convertView.findViewById(R.id.favourite_instructions);
        INGREDIENTS = convertView.findViewById(R.id.favourite_INGREDIENTS);
        INSTRUCTIONS = convertView.findViewById(R.id.favourite_INSTRUCTIONS);
        colorIndicator = convertView.findViewById(R.id.favourite_color);
        unfavoriteRecipe = convertView.findViewById(R.id.unfavorite_recipe);
        useFavourite = convertView.findViewById(R.id.use_favourite);

        currentRecipeName.setText(currentRecipe.getName() + " - " + currentRecipe.getCalories() + " calories - " + currentRecipe.getCookingTime() + " minutes");
        recipeIngredients.setText(stringListToString(currentRecipe.getIngredients()));
        recipeInstructions.setText(stringListToString(currentRecipe.getInstructions()));
        INGREDIENTS.setText("INGREDIENTS");
        INSTRUCTIONS.setText("INSTRUCTIONS");
        colorIndicator.setBackgroundColor(Color.BLACK);

        unfavoriteRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMAFavouriteRecipeManager().removeRecipe(currentRecipe);
                notifyDataSetChanged();
            }
        });

        useFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getContext()).getLeftovers(stringListToString(currentRecipe.getIngredients()));
            }
        });

        return convertView;
    }

    public static String stringListToString(ArrayList<String> list) {
        String result = "";
        for(String item : list) {
            result += item + "\n";
        }

        return result;
    }

    public FavouriteRecipeManager getMAFavouriteRecipeManager() {
        return ((MainActivity)getContext()).getFavouriteRecipeManager();
    }

    public int colorToInt(Color color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return color.toArgb();
        }

        return 0;
    }

}
