package com.example.carsandchronos;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.carsandchronos.Mechanic_Activities.Mech_Assigned_Jobs;
import com.example.carsandchronos.Models.Job;
import com.example.carsandchronos.Models.booking;
import com.example.carsandchronos.Utility.ImageUploader;
import com.example.carsandchronos.Utility.Utility;
import com.example.carsandchronos.Utility.WatermarkCallback;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class View_job_Details_Activity extends AppCompatActivity {
    private static final String BASE_URL = "http://" + Utility.IP_Adress + ":5132/api/Image/Upload";
    private static final int PICK_IMAGE_REQUEST = 1;
    String Api_String ;
    private static final int CAMERA_INTENT_REQUEST_CODE = 102;
    TextView reference , startDate , Vehicle,job_details;
    LinearLayout linear_layout_images;
    int Booking_ID;
    Uri imageUri;
    Bitmap imageBit ;
    ArrayList<Bitmap> retrived_images;
    ImageButton imageButton,imageBack;
    ImageButton Accept,decline;
    Boolean Current_booking =false;
    MaterialCardView cardView;
    Job job;
    private OkHttpClient client;
    int imageCounter = 0;
    int numImages = 0 ; //stores number of images
    private Gson gson;
    RelativeLayout loading_wall, ButtonBlock;
    boolean ShowButtons = false;
    TextView loading_wall_text;
    String[] image_names;
    ProgressBar loading_wall_progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_job_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            return insets;
        });
        cardView = findViewById(R.id.cardview_display);
        image_names = new String[]{"Pre-Assesment :", "In Progress :","Post Assesment :"};
        imageBack = findViewById(R.id.back_button);
        Api_String ="http://"+ Utility.IP_Adress +":5132/api/Job/";
        ButtonBlock = findViewById(R.id.below_buttons);
        Accept = findViewById(R.id.View_job_Accept);
        decline = findViewById(R.id.View_job_Decline);
        linear_layout_images = findViewById(R.id.linear_layout_images);
        reference = findViewById(R.id.JD_reference);
        startDate = findViewById(R.id.JD_startDate);
        Vehicle =findViewById(R.id.JD_vehicleDetails);
        imageButton =findViewById(R.id.ImageButton);//this is the upload button
        job_details = findViewById(R.id.JD_job_details);
        loading_wall = findViewById(R.id.RL_loading_wall);
        loading_wall_text = findViewById(R.id.LL_text);
        loading_wall_progressBar = findViewById(R.id.LL_progress_Bar);
        LoadingWall_Activator(true);
        retrived_images = new ArrayList<>();

        imageBack.setOnClickListener(v -> {
            finish();//returns back to the recycler view activities
        });
        if (getIntent() != null && getIntent().hasExtra("Job")) {
            Current_booking = getIntent().getBooleanExtra("bool",false);//
            ShowButtons = getIntent().getBooleanExtra("ShowButtons",false);//this is for the accept or decline buttons
            Log.d("ShowButtons", "onCreate: clicked "+getIntent().hasExtra("ShowButtons") +" and is"+ShowButtons);
            job = (Job) getIntent().getSerializableExtra("Job");
            // Use the job object to populate the views
            Log.d("tester", "onCreate: " + job.getJobDescription());
            Log.d("tester", "onCreate: "+job.getBookingId());
            Booking_ID = job.getBookingId();
            /* */
            get_BookingData(Booking_ID);


            if(!ShowButtons)//soon to be depriciated
            {//so that it doesnt waste time on stages that dont require images
                RetriveImage();
            }else
            {
                LoadingWall_Activator(false);
            }



        }
        if (ShowButtons)
        {
            Log.d("ShowButtons", "onCreate: clicked");
            ButtonBlock.setVisibility(View.VISIBLE);
        }
        if(!Current_booking && job.getJobStatus() ==2)// this makes the upload button visible
        {//if true , it keeps the upload button
            imageButton.setVisibility(View.INVISIBLE);
            cardView.setVisibility(View.INVISIBLE);
        }
        Accept.setOnClickListener(v -> {
            job.setJobStatus(1);
            post_It(job);
        });
    }

    private void get_BookingData(int booking_ID) {
        client = new OkHttpClient();
        gson = new Gson();
        String url = "http://" + Utility.IP_Adress + ":5132/api/Booking/get/"+ booking_ID;
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d("tester", "onFailure: failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    booking book = gson.fromJson(json, booking.class);
                    Log.d("tester", "onResponse: success");

                    Log.d("tester", "CustomerId: " + book.getCustomerId());
                    Log.d("tester", "ReferenceNo: " + book.getReferenceNo());
                    Log.d("tester", "BookingDetail: " + book.getBookingDetail());

                    runOnUiThread(() -> {
                        reference.setText("Reference : " + book.getBookingId());
                        startDate.setText("Scheduled Date : " + job.getStartDate() + " " + book.getBookingTime());
                        Vehicle.setText("Vehicle : " + book.getVehicleMake() + " " + book.getVehicleModel() + " " + book.getVehicleYear());
                        job_details.setText(job.getJobDescription());
                    });

                } else {
                    Log.d("tester", "onResponse: failed with code " + response.message());
                }
            }
        });
    }





    public void post_It(Job job)
    {//this post the job to the data base , with the status
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();
        String json = gson.toJson(job);
        RequestBody body = RequestBody.create( MediaType.parse("application/json; charset=utf-8"), json);

        Request request = new Request.Builder()
                .url(Api_String+job.getJobId())
                .put(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    System.out.println("Job updated successfully.");
                    Log.d("Tester", "onResponse: Job updated successfully.");
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("deleted_position", job.getJobId());
                    setResult(RESULT_OK, resultIntent);
                    Redirect();
                } else {
                    System.out.println("Failed to update job. Response code: " + response.code());
                    Log.d("Tester", "onResponse: "+"Failed to update job. Response code: " + response.message());
                }
            }
        });
    }
    private void Redirect()
    {
        Intent intent = new Intent(this,Mech_Assigned_Jobs.class);
        startActivity(intent);
        finish();
    }


    //retrive images from API

    private void RetriveImage()
    {
        OkHttpClient client = new OkHttpClient();
        String url = "http://" + Utility.IP_Adress + ":5132/api/Image/GetImages/"+Booking_ID;


        // Fetch image URLs from the API
        new Thread(() -> {
            try {
                Log.d("counter", "RetriveImage: got here");
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = new BufferedInputStream(connection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONArray imageUrls = new JSONArray(result.toString());
                numImages = imageUrls.length();
                Log.d("counter", "RetriveImage: size "+ numImages);
                if(numImages <= 0)
                {
                    runOnUiThread(()->{
                        LoadingWall_Activator(false);
                    });

                    return;
                }

                for (int i = 0; i < imageUrls.length(); i++) {
                    String imageUrl = imageUrls.getString(i);
                    Log.d("counter", "RetriveImage: getting "+imageUrl);
                    downloadImage(imageUrl);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("counter", "RetriveImage: in the catch "+e.getMessage());
                runOnUiThread(()->{
                    LoadingWall_Activator(false);
                });

            }
        }).start();
    }

    private void downloadImage(String imageUrl) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();

                int responseCode = connection.getResponseCode();
                Log.d("downloadImage", "Response code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream input = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(input);

                    runOnUiThread(() -> {
                        imageCounter++;
                        loading_wall_text.setText("retrieved : " + imageCounter + " of " + numImages + " images...");
                        Log.d("counter", "downloadImage: we are checking : counter = " + imageCounter + " images :" + numImages);
                        SetImages(bitmap);
                        retrived_images.add(bitmap);
                        if (imageCounter == numImages) {
                            Log.d("counter", "downloadImage: loading screen should move now : counter = " + imageCounter + " images :" + numImages);
                            imageCounter = 1; // Reset image counter
                            LoadingWall_Activator(false);
                        }
                    });
                } else {
                    Log.d("fetch_image", "downloadImage: image not found, skipping " + imageUrl);
                    runOnUiThread(() -> {
                        imageCounter++;
                        loading_wall_text.setText("retrieved : " + imageCounter + " of " + numImages + " images...");
                        if (imageCounter == numImages) {
                            Log.d("counter", "downloadImage: loading screen should move now : at catch counter = " + imageCounter + " images :" + numImages);
                            imageCounter = 1; // Reset image counter
                            LoadingWall_Activator(false);
                        }
                    });
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("counter", "downloadImage: errorhere " + e.getMessage());
                runOnUiThread(() -> {
                    imageCounter++;
                    loading_wall_text.setText("retrieved : " + imageCounter + " of " + numImages + " images...");
                    if (imageCounter == numImages) {
                        Log.d("counter", "downloadImage: loading screen should move now : counter = " + imageCounter + " images :" + numImages);
                        imageCounter = 1; // Reset image counter
                        LoadingWall_Activator(false);
                    }
                });
            }
        });
    }

    private void SetImages(Bitmap bitmap) {
        // Create and configure ImageView
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                // LinearLayout.LayoutParams.MATCH_PARENT, // Set width to match parent
                //  LinearLayout.LayoutParams.WRAP_CONTENT // Set height to wrap content
                LinearLayout.LayoutParams.WRAP_CONTENT,500
        );

        layoutParams.setMargins(0, 2, 0, 2);
        imageView.setLayoutParams(layoutParams);

        imageView.setImageBitmap(bitmap);
        // imageView.setScaleType();
        imageView.setPadding(0, 0, 0, 0);

        // Set click listener to open the image
        imageView.setOnClickListener(view -> openImage(bitmap));

        // Add ImageView to the linear layout
        linear_layout_images.addView(imageView);
    }


    private void openImage(Bitmap bitmap) {
        // creates dialog window to view car picture
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_image);

        ImageView imageView = dialog.findViewById(R.id.dialog_image_view);
        imageView.setImageBitmap(bitmap);

        dialog.show();
    }


    public void pickImage() {
        // Start the image picker intent
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public void SendImage(View view) {
        pickImage();
    }
    private Uri photoUri;



    private void uploadImage(File file) {
        String fileName = Utility.generateRandomString()+".jpg";
        Log.d("Upload", "uploadImage: imge name "+file.getName());
        Log.d("Upload", "uploadImage: timestamp "+fileName);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("bookingId", String.valueOf(Booking_ID)) // Replace with actual booking ID
                .addFormDataPart("file", fileName,RequestBody.create(MediaType.parse("image/jpeg"), file))
                //.addFormDataPart("image_name")
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d("Upload", "Image upload failed "+e.getMessage());
                runOnUiThread(() -> Toast.makeText(View_job_Details_Activity.this, "Image upload failed", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("Upload", "Image uploaded successfully");
                    runOnUiThread(() -> Toast.makeText(View_job_Details_Activity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show());
                } else {
                    Log.d("Upload", "Image upload failed "+response.message());
                    runOnUiThread(() -> Toast.makeText(View_job_Details_Activity.this, "Image upload failed", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    public void Upload(View view) {
        //pickImage();
        openCamera();
    }
    private void LoadingWall_Activator(boolean bool)
    {
        if(bool){
            loading_wall.setVisibility(View.VISIBLE);
        }
        else
        {
            loading_wall.setVisibility(View.GONE);
        }
    }

    private File bitmapToFile(Bitmap bitmap) {
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "image.jpg");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (photoUri != null) {
            outState.putString("photoUri", photoUri.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("photoUri")) {
            photoUri = Uri.parse(savedInstanceState.getString("photoUri"));
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap bitmap = null;

            try {
                if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                    imageUri = data.getData();
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    bitmap = rotateImageIfRequired(bitmap, imageUri);
                } else if (requestCode == CAMERA_INTENT_REQUEST_CODE) {
                    if (photoUri != null) {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                        bitmap = rotateImageIfRequired(bitmap, photoUri);
                    }
                }

                if (bitmap != null) {
                    // Show watermark dialog and apply watermark after dialog closes
                    showWatermarkDialog(bitmap, new WatermarkCallback() {
                        @Override
                        public void onWatermarkAdded(Bitmap watermarkedBitmap) {
                            imageBit = watermarkedBitmap;
                            Log.d("Watermark", "Custom Watermark: this ran before");
                            SetImages(watermarkedBitmap); // Set the images received
                            File imageFile = bitmapToFile(watermarkedBitmap);
                            uploadImage(imageFile);
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //*******************************************************************************
    public void showWatermarkDialog(Bitmap originalBitmap, WatermarkCallback callback) {
        // Use the activity context
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Image Details");

        // Inflate the custom layout with an EditText
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.watermark_dialog, null);
        EditText editText = dialogView.findViewById(R.id.watermark);

        builder.setView(dialogView);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String customWatermark = editText.getText().toString().trim();
                Bitmap watermarkedBitmap;

                if (!customWatermark.isEmpty()) {
                    // Add the watermark with user input
                    Log.d("Watermark", "Custom Watermark: " + customWatermark);
                    watermarkedBitmap = addWatermark(originalBitmap, customWatermark);
                    Log.d("Watermark", "Custom Watermark: should raun after" );
                } else {
                    // Apply empty watermark if no input
                    watermarkedBitmap = addWatermark(originalBitmap, "no custom ");
                }

                // Call the callback with the watermarked bitmap
                callback.onWatermarkAdded(watermarkedBitmap);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Show the dialog
        builder.create().show();
    }

    //*******************************************************************************


//adds the water mark to photos
private Bitmap addWatermark(Bitmap originalBitmap, String custom) {
    Bitmap mutableBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
    Canvas canvas = new Canvas(mutableBitmap);
    Paint paint = new Paint();
    paint.setColor(Color.WHITE); // Watermark text color
    paint.setTextSize(100); // Watermark text size
    paint.setAntiAlias(true); // Make the text smooth
    paint.setTextAlign(Paint.Align.LEFT); // Align text to the left

    // Get the current date
    String watermarkText = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    String complete_watermark;

    if (job.getJobStatus() == 0) {
        complete_watermark = custom + "\nPre Assessed on: " + watermarkText + " " + Utility.get_time();
    } else if (job.getJobStatus() == 1) {
        complete_watermark = custom + "\nProgress on: " + watermarkText + " " + Utility.get_time();
    } else {
        complete_watermark = "Post Assessed on: " + watermarkText + " " + Utility.get_time();
    }

    // Split the watermark text into multiple lines by \n
    String[] lines = complete_watermark.split("\n");

    // Starting position for the first line
    int x = 10; // Horizontal position
    int y = mutableBitmap.getHeight() - 200; // Starting from 200 pixels above the bottom

    // Draw each line of the watermark text on the canvas
    for (String line : lines) {
        canvas.drawText(line, x, y, paint);
        y += paint.getTextSize() + 10; // Move to the next line, with a little padding
    }

    return mutableBitmap;
}
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Create a file to store the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // Continue only if the file was successfully created
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this,
                        "com.example.carsandchronos.fileprovider", // Change to your package name
                        photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(cameraIntent, CAMERA_INTENT_REQUEST_CODE);
            }
        }
    }


    private Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {
        InputStream input = getContentResolver().openInputStream(selectedImage);
        ExifInterface ei = new ExifInterface(input);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }
    private Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
    }

}
