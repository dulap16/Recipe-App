package com.example.recipeai;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class YourFridgeFragment extends Fragment {

    private ArrayList<Food> availableIngredients;
    private ListView ingredientList;
    private EditText newIngredientEditText;
    private Button newIngredientButton;

    public YourFridgeFragment() {
    }

    public static YourFridgeFragment newInstance(ArrayList<String> param1) {
        YourFridgeFragment fragment = new YourFridgeFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("ingredients", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        availableIngredients = stringToFoodList(getArguments().getStringArrayList("ingredients"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_your_fridge, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ingredientList = view.findViewById(R.id.ingredient_list);
        newIngredientEditText = view.findViewById(R.id.newIngredientEditText);
        newIngredientButton = view.findViewById(R.id.newIngredientButton);
        updateList();

        newIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newIngredientEditText.getText().toString() != "") {
                    addIngredient(newIngredientEditText.getText().toString());
                    newIngredientEditText.setText("");
                    updateList();
                }
            }
        });
    }

    public void setAvailableIngredients(ArrayList<Food> availableIngredients) {
        this.availableIngredients = availableIngredients;
    }

    public void addIngredient(String name) {
        if(name == "")
            return ;

        availableIngredients.add(new Food(name));

        ((MainActivity)getActivity()).addIngredient(name);
    }

    public void addIngredient(Food f) {
        availableIngredients.add(f);

        ((MainActivity)getActivity()).addIngredient(f.getName());
    }

    public void removeIngredient(String name) {
        availableIngredients.remove(name);

        ((MainActivity)getActivity()).removeIngredient(name);
    }

    public void removeIngredient(Food f) {
        availableIngredients.remove(f);

        ((MainActivity)getActivity()).removeIngredient(f.getName());
    }

    private ArrayList<Food> stringToFoodList(ArrayList<String> stringList) {
        ArrayList<Food> foodList = new ArrayList<>();
        for (String s : stringList) {
            foodList.add(new Food(s));
        }
        return foodList;
    }

    public void updateList() {
        ItemAdapter adapter = new ItemAdapter(getContext(), availableIngredients);
        ingredientList.setAdapter(adapter);
    }

    public void test(String text) {
        Log.i("wtferror", text);
    }

}