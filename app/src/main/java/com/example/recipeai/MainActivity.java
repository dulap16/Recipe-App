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

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

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

    public static final int REQUEST_CAMERA_CODE = 100;
    public static final int CAMERA_ACTION_CODE = 1;

    Button takePhotoBtn;
    ImageView imagePreview;
    TextView scannedTextView;
    ActivityResultLauncher<Intent> activityResultLauncher;

    private String currentPhotoPath;

    HashMap<String, String> questionTemplate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        takePhotoBtn = findViewById(R.id.takePhotoBtn);
        imagePreview = findViewById(R.id.photoPreview);
        scannedTextView = findViewById(R.id.scannedTextView);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {

                Bitmap bitmap = rotateBitmap90(BitmapFactory.decodeFile(currentPhotoPath));
                showImage(bitmap);

                String text = readPhotoText(bitmap);
                scannedTextView.setText(text);
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
            Log.i("kkkk", "merge?");

            launchTakePhotoActivity(imageUri);
        } catch(IOException e) {
            Log.i("kkkk", e.toString());
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


    private void changeLanguage(String newLanguage) {
        LANGUAGE = newLanguage;
    }
}