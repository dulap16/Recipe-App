package com.example.recipeai;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.recipeai.databinding.ActivityMainBinding;
import com.example.recipeai.databinding.LeftoverItemBinding;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public String LANGUAGE = "EN";

    public static final int API_SCAN_INGREDIENTS = 1;
    public static final int API_GIVE_RECIPES = 2;
    public static final int API_GIVE_COLOR = 3;
    public static final int API_GIVE_LEFT_OVER = 4;

    public static final int REQUEST_CAMERA_CODE = 100;

    private FragmentManager fm;
    private FavouriteRecipeManager favouriteRecipeManager;
    private ShoppingListManager shoppingListManager;
    private Fragment currentFragment;
    private TextView favouriteRecipesButtonView, shoppingListButtonView, familyAccountButtonView;
    private ImageView profileView;

    ActivityResultLauncher<Intent> activityResultLauncher;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    private String currentPhotoPath;

    private HashMap<String, String> recognizeQuestionTemplate;
    private HashMap<String, String> findColorQuestionTemplate;
    private HashMap<String, String> giveRecipeQuestionTemplate;
    private HashMap<String, String> giveLeftOverQuestionTemplate;

    private ArrayList<Food> availableIngredients;
    private ArrayList<Recipe> recipeArrayList;
    private ArrayList<Leftover> leftoverArrayList;

    private ActivityMainBinding binding;


    public String ingredientResponse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        availableIngredients = new ArrayList<>();
        recipeArrayList = new ArrayList<>();
        leftoverArrayList = new ArrayList<>();
        shoppingListManager = new ShoppingListManager();
        favouriteRecipeManager = new FavouriteRecipeManager();


        fm = getSupportFragmentManager();

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.kitchen) {
                currentFragment = new TheKitchenFragment();

                replaceFragment(currentFragment);
            } else if (id == R.id.your_fridge) {
                currentFragment = YourFridgeFragment.newInstance(foodToStringList(availableIngredients));

                replaceFragment(currentFragment);
            } else if (id == R.id.take_photo) {
                currentFragment = new TakePhotoFragment();

                replaceFragment(currentFragment);
            }
            return true;
        });

        bottomNav.setSelectedItemId(R.id.take_photo);

        recognizeQuestionTemplate = new HashMap<>();
        recognizeQuestionTemplate.put("EN", "I am going to give you a list of words, separated by the space character. I want you to look through that list of words and respond with only the words that resemble an ingredient that you can cook food with. Most of the words are gibberish, but you need do return a list of words, separated by the \";\" character, that resemble ingredients that you can cook food with. Here is the list of words:");
        recognizeQuestionTemplate.put("RO", "Vă voi oferi o listă de cuvinte, separate prin caracterul spațiu. Vreau să te uiți prin acea listă de cuvinte și să răspunzi doar cu cuvintele care seamănă cu un ingredient cu care poți găti mâncarea. Majoritatea cuvintelor sunt farfurii, dar trebuie să returnați o listă de cuvinte, separate prin caracterul spațiu, care seamănă cu un ingredient cu care puteți găti mâncarea. Iată lista de cuvinte:");

        findColorQuestionTemplate = new HashMap<>();
        findColorQuestionTemplate.put("EN", "I am going to give you a list of ingredients, separated by the space character. I want you to look through that list of ingredients and respond, for each ingredient, the hexcode of the color that resembles it the best. So for each ingredient given, you respond in the format \"[That ingredient] [hexcode]\" starting with #, the hexcodes being seperated by '\\n' character. Here is the list of ingredients: ");
        findColorQuestionTemplate.put("RO", "O să vă dau o listă de ingrediente, separate prin caracterul spațiu. Vreau să te uiți prin acea listă de ingrediente și să răspunzi, pentru fiecare ingredient, codul hexadecimal al culorii care îi seamănă cel mai bine. Deci, pentru fiecare ingredient dat, răspundeți in formatul \"[Acel ingredient] [cod hexadecimal]\", codul hexadecimal incepand cu #, codurile hexadecimale fiind separate printr-un caracter '\\n'. Iată lista ingredientelor:");

        giveRecipeQuestionTemplate = new HashMap<>();
        giveRecipeQuestionTemplate.put("EN", "I am going to give you a list of ingredients, separated by the space character. What I want you to do is give me 3 different recipes, each one of them using as many of the ingredients given as possible. It's ok to use other ingredients as well by try to find dishes centered around the ingredients given. Not every dish needs to use all of the ingredients given, it's ok to only have some of them! Add grammage, too. This is the format I want you to give the recipes in:\n" +
                "\"FIRST RECIPE: [Recipe no. 1 name] - [cooking time] - [calories]\n" +
                "INGREDIENTS\n" +
                "1. [ingredient no. 1]\n" +
                "2. [ingredient no.2]\n" +
                "3. [so on...]\n" +
                "INSTRUCTIONS\n" +
                "1. [instruction no. 1]\n" +
                "2. [instruction no.2]\n" +
                "3. [so on...]\n" +
                "\n" +
                "SECOND RECIPE: [Recipe no. 2 name] \" and so on.\n" +
                "This is the list of ingredients:");
        giveRecipeQuestionTemplate.put("RO", "");

        giveLeftOverQuestionTemplate = new HashMap<>();
        giveLeftOverQuestionTemplate.put("EN", "I am going to give you a list of ingredients that were used in a recipe. We are trying to prevent food waste, so what I need you to do is select which of these ingredients may leave something behind, that is not normally used in food buy may be used for something else. For example, using eggs in a recipe will leave behind eggshells, at which point you will tell me some things I could do with eggshells. You can give me any number of uses. Ignore any numbers may appear in the ingredients given. This is the format I want you to answer in:\n" +
                "\"LEFTOVER:[leftover name]\n" +
                "USE:[how can I use it]\n" +
                "USE:[another way to use it]\n" +
                "USE:[so on...]\n" +
                "LEFTOVER:... and so on\"\n" +
                "Here is the list of ingredients: ");
        giveLeftOverQuestionTemplate.put("RO", "");

        availableIngredients = new ArrayList<>();

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {

                TakePhotoFragment f = (TakePhotoFragment)currentFragment;

                Bitmap bitmap = rotateBitmap90(BitmapFactory.decodeFile(currentPhotoPath));
                f.changePhotoPreview(bitmap);

                String text = readPhotoText(bitmap);
                f.changeScannedText(text);
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        callApi(composeRecognizeIngredients(text), API_SCAN_INGREDIENTS);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    processAPIResponse(ingredientResponse, API_SCAN_INGREDIENTS);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
                    }
                });

                thread.start();

                // scannedTextView.setText(response);
            }
        });


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA
            }, REQUEST_CAMERA_CODE);
        }

        profileView = findViewById(R.id.profile_view);
        favouriteRecipesButtonView = findViewById(R.id.favourite_recipes_button);
        shoppingListButtonView = findViewById(R.id.shopping_list_button);
        familyAccountButtonView = findViewById(R.id.family_account_button);

        profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentFragment = new ProfileFragment();

                replaceFragment(currentFragment);
            }
        });
    }


    public void takePhoto() {
        String fileName = "photo";
        File storageDirectory = createPhotoFolder();

        try {
            Uri imageUri = createImageUri(fileName, storageDirectory);

            launchTakePhotoActivity(imageUri);
        } catch(IOException e) {
        }
    }

    private File createPhotoFolder() {
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return storageDirectory;
    }

    private Uri createImageUri(String fileName, File storageDirectory) throws IOException {
        File imageFile = File.createTempFile(fileName, ".jpg", storageDirectory);
        currentPhotoPath = imageFile.getAbsolutePath();

        return FileProvider.getUriForFile(this, "com.example.recipeai.fileprovider", imageFile);
    }

    private void launchTakePhotoActivity(Uri imageUri) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        activityResultLauncher.launch(intent);
    }

    private String readPhotoText(Bitmap bitmap) {
        TextRecognizer recognizer = new TextRecognizer.Builder(this).build();
        if (!recognizer.isOperational()) {
            Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT);
        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> array = recognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < array.size(); i++) {
                TextBlock textBlock = array.valueAt(i);
                stringBuilder.append(textBlock.getValue());
                stringBuilder.append('\n');
            }
            return stringBuilder.toString();
        }
        return "Error";
    }

    private Bitmap rotateBitmap90(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90f);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    // sk-proj-BDMJPGscVuXibChUTrnyT3BlbkFJSfNYDqaTBOSQsSoYs8Uq

    private String composeRecognizeIngredients(String scannedText) {
        String question = recognizeQuestionTemplate.get(LANGUAGE) + scannedText;
        return question;
    }

    private String composeFindColor(String ingredients) {
        String question = findColorQuestionTemplate.get(LANGUAGE) + ingredients;
        return question;
    }

    private String composeGiveRecipe(String ingredients) {
        String question = giveRecipeQuestionTemplate.get(LANGUAGE) + ingredients;
        return question;
    }

    private String composeGiveLeftovers(String ingredients) {
        String question = giveLeftOverQuestionTemplate.get(LANGUAGE) + ingredients;
        return question;
    }

    private void callApi(String question, int requestCode) {
        JSONObject jsonBody = createJSONBodyForAPI(question);

        Request request = createRequestForAPI(jsonBody);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());

                    processAPIResponse(getAPIResponseContent(jsonObject), requestCode);
                } catch (JSONException e) {
                    Log.i("wtferror", e.toString());

                    ingredientResponse = "Error";
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ingredientResponse = "Error";
            }
        });
    }

    private JSONObject createJSONBodyForAPI(String question) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "gpt-3.5-turbo");
            jsonBody.put("max_tokens", 1000);
            jsonBody.put("temperature", 0);

            JSONArray jsonArray = new JSONArray();
            JSONObject jsonMessage1 = new JSONObject();
            jsonMessage1.put("role", "user");
            jsonMessage1.put("content", question);

            jsonArray.put(jsonMessage1);

            jsonBody.put("messages", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonBody;
    }

    private Request createRequestForAPI(JSONObject jsonObject) {
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer sk-proj-We1WBjAjIQJAtjeDjfoaT3BlbkFJy6V2vsvQapVMbcP8znos")
                .post(body)
                .build();

        return request;
    }

    private void processAPIResponse(String response, int requestCode) throws JSONException {
        switch (requestCode) {
            case API_SCAN_INGREDIENTS:
                processRecognizedIngredients(response);
                break;
            case API_GIVE_COLOR:
                processColorsResponse(response);
                break;
            case API_GIVE_RECIPES:
                processRecipeResponse(response);
                break;
            case API_GIVE_LEFT_OVER:
                processLeftoverResponse(response);
                break;
            default:
        }
    }
    private void processRecognizedIngredients(String response) {
        try {
            String[] newIngredients = response.split(";");
            for (String ingredient : newIngredients) {
                addIngredient(ingredient);
            }
        } catch (Exception e) {
            return ;
        }
    }

    private void processColorsResponse(String response) {
        String[] colors = response.split("\n");

        // GIVE FOOD COLOR
    }
    private void processRecipeResponse(String response) {
        try {
            recipeArrayList = new ArrayList<>();

            String recipes[] = response.split("RECIPE:");
            for(int i = 1; i < recipes.length; i++) {
                String recipe = recipes[i];
                Recipe r = Recipe.parseTextToRecipe(recipe);

                Log.i("Recipe", r.toString());

                recipeArrayList.add(r);
            }

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                getKitchenFragment().setRecipes(recipeArrayList);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }
            });

            thread.start();
        } catch (Exception e) {
            Log.i("normalError", e.toString());
        }
    }

    private void processLeftoverResponse(String response) {
        leftoverArrayList = new ArrayList<>();

        String tokens[] = response.split("LEFTOVER:");
        for(int i = 1; i < tokens.length; i++) {
            Leftover l = Leftover.parseTextToLeftover(tokens[i]);

            leftoverArrayList.add(l);
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getLeftoversFragment().responseReceived(leftoverArrayList);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });

        thread.start();
    }

    public void getRecipes(ArrayList<Food> ingredients) {
        String ingredientsString = "";
        for (Food food : ingredients) {
            ingredientsString += food.getName() + " ";
        }

        String finalIngredientsString = ingredientsString;

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                callApi(composeGiveRecipe(finalIngredientsString), API_GIVE_RECIPES);
            }
        });

        thread.start();
    }

    public void getRecipes() {
        String ingredientsString = "";
        for (Food food : availableIngredients) {
            ingredientsString += food.getName() + " ";
        }

        String finalIngredientsString = ingredientsString;
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                callApi(composeGiveRecipe(finalIngredientsString), API_GIVE_RECIPES);
            }
        });

        thread.start();
    }

    public void getLeftovers(String ingredients) {
        goToLeftoverFragment();

        callApi(composeGiveLeftovers(ingredients), API_GIVE_LEFT_OVER);
    }

    private TakePhotoFragment getTakePhotoFragment() {
        try {
            return (TakePhotoFragment) currentFragment;
        } catch (Exception e) {
            return null;
        }
    }

    private TheKitchenFragment getKitchenFragment() {
        try {
            return (TheKitchenFragment) currentFragment;
        } catch (Exception e) {
            return null;
        }
    }

    private SettingsFragment getSettingsFragment() {
        try {
            return (SettingsFragment) currentFragment;
        } catch (Exception e) {
            return null;
        }
    }

    private YourFridgeFragment getFridgeFragment() {
        try {
            return (YourFridgeFragment) currentFragment;
        } catch (Exception e) {
            return null;
        }
    }

    private LeftoversFragment getLeftoversFragment() {
        try {
            return (LeftoversFragment) currentFragment;
        } catch (Exception e) {
            return null;
        }
    }

    public void goToLeftoverFragment() {
        currentFragment = new LeftoversFragment();

        replaceFragment(currentFragment);
    }


    private String getAPIResponseContent(JSONObject json) throws JSONException {
        String response = json.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
        return response;
    }

    private ArrayList<String> foodToStringList(ArrayList<Food> foodList) {
        ArrayList<String> stringList = new ArrayList<>();
        for (Food food : foodList) {
            stringList.add(food.getName());
        }
        return stringList;
    }


    public void addIngredient(String ingredient) {
        availableIngredients.add(new Food(ingredient));
    }

    public void removeIngredient(String ingredient) {
        for(Food food : availableIngredients) {
            if(food.getName().equals(ingredient)) {
                availableIngredients.remove(food);
                break;
            }
        }
    }

    public void addShoppingListItem(String item) {
        shoppingListManager.addItem(new Food(item));
    }

    public void removeShoppingListItem(String item) {
        shoppingListManager.removeItem(new Food(item));
    }

    public void addShoppingListItems(ArrayList<String> items) {
        for(String item : items) {

            if(item.equals(""))
                continue;

            shoppingListManager.addItem(new Food(parseNumberedItem(item)));
        }
    }

    private String parseNumberedItem(String item) {
        String[] parts = item.split(" ");
        String finalString = "";
        for(int i = 1; i < parts.length; i++)
        {
            if(parts[i].contains("("))
                break;
            else finalString = finalString + parts[i] + " ";
        }

        return finalString.trim();
    }

    public void changeCurrentFragment(Fragment f) {
        currentFragment = f;
    }

    private void changeLanguage(String newLanguage) {
        LANGUAGE = newLanguage;
    }

    public FavouriteRecipeManager getFavouriteRecipeManager() {
        return favouriteRecipeManager;
    }

    public ShoppingListManager getShoppingListManager() { return shoppingListManager; }

    public ArrayList<Leftover> getLeftoverArrayList() { return leftoverArrayList; }

    public void replaceFragment(Fragment fragment) {
        Log.i("kkkk", "fragm");

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }


    // TODO:
    //        - ADD FOOD COLOR
}