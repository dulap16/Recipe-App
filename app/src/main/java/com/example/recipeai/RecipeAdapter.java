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

public class RecipeAdapter extends ArrayAdapter<Recipe> {

    private FavouriteRecipeManager favouriteRecipeManager;
    private List<Recipe> list;
    private Context context;

    TextView currentRecipeName, recipeIngredients, recipeInstructions, colorIndicator,
    INGREDIENTS, INSTRUCTIONS;
    ImageView favouriteRecipe, useRecipe;

    public RecipeAdapter(Context context, List<Recipe> myRecipes) {
        super(context, 0, myRecipes);
        this.list = myRecipes;
        this.context = context;

        favouriteRecipeManager = ((MainActivity)context).getFavouriteRecipeManager();
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.recipe_item, parent, false);

        final Recipe currentRecipe = list.get(position);

        currentRecipeName = convertView.findViewById(R.id.recipe_name);
        recipeIngredients = convertView.findViewById(R.id.recipe_ingredients);
        recipeInstructions = convertView.findViewById(R.id.recipe_instructions);
        INGREDIENTS = convertView.findViewById(R.id.INGREDIENTS);
        INSTRUCTIONS = convertView.findViewById(R.id.INSTRUCTIONS);
        colorIndicator = convertView.findViewById(R.id.recipe_color);
        favouriteRecipe = convertView.findViewById(R.id.favorite_recipe);
        useRecipe = convertView.findViewById(R.id.use_recipe);

        currentRecipeName.setText(currentRecipe.getName() + " - " + currentRecipe.getCalories() + " calories - " + currentRecipe.getCookingTime() + " minutes");
        recipeIngredients.setText(stringListToString(currentRecipe.getIngredients()));
        recipeInstructions.setText(stringListToString(currentRecipe.getInstructions()));
        INGREDIENTS.setText("INGREDIENTS");
        INSTRUCTIONS.setText("INSTRUCTIONS");
        colorIndicator.setBackgroundColor(Color.BLACK);

        favouriteRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favouriteRecipeManager.addRecipe(currentRecipe);
                // favouriteRecipe.setImageResource(R.drawable.baseline_favorite_24);
                notifyDataSetChanged();
            }
        });

        useRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getContext()).getLeftovers(stringListToString(currentRecipe.getIngredients()));
                ((MainActivity)getContext()).addShoppingListItems(currentRecipe.getIngredients());
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

    public int colorToInt(Color color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return color.toArgb();
        }

        return 0;
    }
}
