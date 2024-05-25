package com.example.recipeai;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProfileFragment extends Fragment {

    TextView favouriteRecipesButtonView, shoppingListButtonView, familyAccountButtonView, usernameView;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        favouriteRecipesButtonView = view.findViewById(R.id.favourite_recipes_button);
        shoppingListButtonView = view.findViewById(R.id.shopping_list_button);
        familyAccountButtonView = view.findViewById(R.id.family_account_button);
        usernameView = view.findViewById(R.id.username_text);

        usernameView.setText("Hello, User");
        favouriteRecipesButtonView.setText("Your favourite recipes");
        shoppingListButtonView.setText("Your shopping list");
        familyAccountButtonView.setText("Your family account");



        favouriteRecipesButtonView = view.findViewById(R.id.favourite_recipes_button);
        shoppingListButtonView = view.findViewById(R.id.shopping_list_button);
        familyAccountButtonView = view.findViewById(R.id.family_account_button);

        favouriteRecipesButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment currentFragment = new FavouriteFragment();
                getMainActivity().changeCurrentFragment(currentFragment);
                getMainActivity().replaceFragment(currentFragment);
            }
        });

        shoppingListButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment currentFragment = new ShoppingListFragment();
                getMainActivity().changeCurrentFragment(currentFragment);
                getMainActivity().replaceFragment(currentFragment);
            }
        });

        familyAccountButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Fragment currentFragment = new FamilyAccountFragment();
                getMainActivity().changeCurrentFragment(currentFragment);
                getMainActivity().replaceFragment(currentFragment);*/
            }
        });
    }

    private MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }
}