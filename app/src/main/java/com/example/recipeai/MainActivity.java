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
import android.widget.Button;
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
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
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
    public static final int API_GIVE_RECIPIES = 2;
    public static final int API_GIVE_COLOR = 3;

    public static final int REQUEST_CAMERA_CODE = 100;

    Button takePhotoBtn;
    ImageView imagePreview;
    TextView scannedTextView;
    ActivityResultLauncher<Intent> activityResultLauncher;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    private String currentPhotoPath;

    HashMap<String, String> questionTemplate;

    private ArrayList<String> availableIngredients;

    private ActivityMainBinding binding;

    public class ChatGPT {
        public String generateChatGPTResponse(String userPrompt) {
            String apiURL = "https://api.openai.com/v1/chat/completions";
            String apiKey = "sk-proj-ORSDepYCISfSahYvLbk8T3BlbkFJSbvuBcGEMaZ0Q8csRa9E";
            String LLMname = "gpt-3.5-turbo";
            try {
                // Create URL object
                URL url = new URL(apiURL);
                // Open connection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // Set the request method to POST
                connection.setRequestMethod("POST");
                // Set request headers
                connection.setRequestProperty("Authorization", "Bearer " + apiKey);
                connection.setRequestProperty("Content-Type", "application/json");
                // Create the request body
                String requestBody = "{\"model\": \"" + LLMname + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + userPrompt + "\"}]}";
                // Enable input/output streams
                connection.setDoOutput(true);
                // Write the request body
                try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
                    writer.write(requestBody);
                    writer.flush();
                }
                // Read the response
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    return getLLMResponse(response.toString());
                }
            } catch (IOException e) {
                throw new RuntimeException("Error interacting with the ChatGPT API: " + e.getMessage(), e);
            }
        }
        private String getLLMResponse(String response) {
            int firstChar = response.indexOf("content") + 11;
            int lastChar = response.indexOf("\"", firstChar);
            return response.substring(firstChar, lastChar);
        }
    }
    ChatGPT chatGPT;

    public String ingredientResponse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        takePhotoBtn = findViewById(R.id.takePhotoBtn);
        imagePreview = findViewById(R.id.photoPreview);
        scannedTextView = findViewById(R.id.scannedTextView);

        questionTemplate = new HashMap<>();
        questionTemplate.put("EN", "I am going to give you a list of words, separated by the space character. I want you to look through that list of words and respond with only the words that resemble an ingredient that you can cook food with. Most of the words are gibberish, but you neet do return a list of words, separated by the space character, that resemble an ingredient that you can cook food with. Here is the list of words:");
        questionTemplate.put("RO", "Vă voi oferi o listă de cuvinte, separate prin caracterul spațiu. Vreau să te uiți prin acea listă de cuvinte și să răspunzi doar cu cuvintele care seamănă cu un ingredient cu care poți găti mâncarea. Majoritatea cuvintelor sunt farfurii, dar trebuie să returnați o listă de cuvinte, separate prin caracterul spațiu, care seamănă cu un ingredient cu care puteți găti mâncarea. Iată lista de cuvinte:");

        availableIngredients = new ArrayList<>();
        chatGPT = new ChatGPT();

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {

                Bitmap bitmap = rotateBitmap90(BitmapFactory.decodeFile(currentPhotoPath));
                showImage(bitmap);

                String text = readPhotoText(bitmap);
                scannedTextView.setText(text);
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        callApi(composeQuestion("asd alsdk a lkjsdfhj h w oei wpo po  alskd fja chicken jadsl efij ljl akj h carrot jkl lkajs alkjf wekl salmon klj alkh alk jlkw egg"), API_SCAN_INGREDIENTS);
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

        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
    }


    private void takePhoto() {
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

    private void showImage(Bitmap bitmap) {
        imagePreview.setImageBitmap(bitmap);
    }

    private String readPhotoText(Bitmap bitmap) {
        TextRecognizer recognizer = new TextRecognizer.Builder(this).build();
        if (!recognizer.isOperational()) {
            Toast.makeText(this, "Error occured", Toast.LENGTH_SHORT);
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

    private String composeQuestion(String scannedText) {
        String question = questionTemplate.get(LANGUAGE) + scannedText;
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
                Toast.makeText(MainActivity.this, "Failed to load response: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                ingredientResponse = "Error";
            }
        });
    }

    private JSONObject createJSONBodyForAPI(String question) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "gpt-3.5-turbo");
            jsonBody.put("max_tokens", 100);
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
        }
    }
    private void processRecognizedIngredients(String ingredientResponse) {
        String[] newIngredients = ingredientResponse.split(",");
        for (String ingredient : newIngredients) {
            addNewIngredient(ingredient);
        }

        scannedTextView.setText(ingredientResponse);
    }

    private String getAPIResponseContent(JSONObject json) throws JSONException {
        String response = json.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
        return response;
    }

    private void addNewIngredient(String ingredient) {
        availableIngredients.add(ingredient);
    }

    private void removeIngredient(String ingredient) {
        availableIngredients.remove(ingredient);
    }

    private void changeLanguage(String newLanguage) {
        LANGUAGE = newLanguage;
    }
}