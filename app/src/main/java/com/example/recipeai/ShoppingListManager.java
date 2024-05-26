package com.example.recipeai;

import java.util.ArrayList;

public class ShoppingListManager {

    private ArrayList<Food> shoppingList;

    public ShoppingListManager() {
        shoppingList = new ArrayList<>();
    }

    public void addItem(Food item) {
        shoppingList.add(item);
    }

    public void removeItem(Food item) {
        shoppingList.remove(item);
    }

    public void removeItem(int index) {
        shoppingList.remove(index);
    }

    public ArrayList<Food> getShoppingList() {
        return shoppingList;
    }

    public void setShoppingList(ArrayList<Food> shoppingList) {
        this.shoppingList = shoppingList;
    }
}
