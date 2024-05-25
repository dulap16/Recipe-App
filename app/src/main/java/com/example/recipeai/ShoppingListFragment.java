package com.example.recipeai;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class ShoppingListFragment extends Fragment {

    private ListView shoppingListView;
    private Button addItemButton;
    private EditText itemEditText;

    public ShoppingListFragment() {
        // Required empty public constructor
    }

    public static ShoppingListFragment newInstance() {
        ShoppingListFragment fragment = new ShoppingListFragment();
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
        return inflater.inflate(R.layout.fragment_shopping_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shoppingListView = view.findViewById(R.id.shopping_list_view);
        addItemButton = view.findViewById(R.id.new_shopping_item_button);
        itemEditText = view.findViewById(R.id.new_shopping_item_edittext);

        updateList();

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemEditText.getText().toString() != "") {
                    addItem(itemEditText.getText().toString());
                    itemEditText.setText("");
                    updateList();
                }
            }
        });
    }

    private void addItem(String name) {
        if(name == "")
            return ;

        getMainActivity().addShoppingListItem(name);
    }

    private MainActivity getMainActivity() {
        return ((MainActivity)getContext());
    }

    private ShoppingListManager getShoppingListManager() {
        return getMainActivity().getShoppingListManager();
    }

    private void updateList() {
        ShoppingListAdapter adapter = new ShoppingListAdapter(getContext(), getShoppingListManager().getShoppingList());
        shoppingListView.setAdapter(adapter);
    }
}