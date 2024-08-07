package com.example.carsandchronos;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.carsandchronos.Mechanic_Activities.Mech_Assigned_Jobs;
import com.example.carsandchronos.Models.Job;
import com.example.carsandchronos.Models.JobCard;
import com.example.carsandchronos.Models.booking;
import com.example.carsandchronos.Utility.ImageUploader;
import com.example.carsandchronos.Utility.Utility;
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
    JobCard job;
    private OkHttpClient client;
    int imageCounter = 0;
    int numImages = 0 ; //stores number of images
    private Gson gson;
    RelativeLayout loading_wall, ButtonBlock;
    boolean ShowButtons = false;
    TextView loading_wall_text;
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
        imageBack = findViewById(R.id.back_button);
        Api_String ="http://"+ Utility.IP_Adress +":5132/api/Job/";
        ButtonBlock = findViewById(R.id.below_buttons);
        Accept = findViewById(R.id.View_job_Accept);
        decline = findViewById(R.id.View_job_Decline);
        linear_layout_images = findViewById(R.id.linear_layout_images);
        reference = findViewById(R.id.JD_reference);
        startDate = findViewById(R.id.JD_startDate);
        Vehicle =findViewById(R.id.JD_vehicleDetails);
        imageButton =findViewById(R.id.ImageButton);
        job_details = findViewById(R.id.JD_job_details);
        loading_wall = findViewById(R.id.RL_loading_wall);
        loading_wall_text = findViewById(R.id.LL_text);
        loading_wall_progressBar = findViewById(R.id.LL_progress_Bar);
        LoadingWall_Activator(true);
        retrived_images = new ArrayList<>();

        imageBack.setOnClickListener(v -> {
            finish();
        });
        if (getIntent() != null && getIntent().hasExtra("Job")) {
            Current_booking = getIntent().getBooleanExtra("bool",false);
            ShowButtons = getIntent().getBooleanExtra("ShowButtons",false);
            Log.d("ShowButtons", "onCreate: clicked "+getIntent().hasExtra("ShowButtons") +" and is"+ShowButtons);
            job =(JobCard) getIntent().getSerializableExtra("Job");
            // Use the job object to populate the views
            Log.d("tester", "onCreate: " + job.getJobDescription());
            Log.d("tester", "onCreate: "+job.getBookingID());
            Booking_ID = job.getBookingID();
            /* */
            get_BookingData(Booking_ID);


                RetriveImage();


        }
        if (ShowButtons)
        {
            Log.d("ShowButtons", "onCreate: clicked");
            ButtonBlock.setVisibility(View.VISIBLE);
        }
        if(!Current_booking)
        {//if true , it keeps the upload button
            imageButton.setVisibility(View.GONE);
        }
        Accept.setOnClickListener(v -> {
            job.setJobStatus("PROGRESS");
            post_It(job);
        });
    }
    public void post_It(JobCard job)
    {
        Api_String ="http://"+ Utility.IP_Adress +":5132/api/Job/UpdateMechanicJobCard/"+job.getJobId()+"/"+job.getMechId()+"/PROGRESS";
        Gson gson = new Gson();
        job.setEndDate(Utility.getCurrentDate());
        String json = gson.toJson(job);
        RequestBody body = RequestBody.create( MediaType.parse("application/json; charset=utf-8"), json);

        Request request = new Request.Builder()
                .url(Api_String)
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
                    Log.d("Tester","onResponse: Job updated successfully."+response.message());
                    finish();

                } else {
                    System.out.println("Failed to update job. Response code: " + response.code());
                    Log.d("Tester", "onResponse: "+"Failed to update job. Response code: " + response.message());
                }
            }
        });
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
        imageView.setScaleType(ImageView.ScaleType.CENTER);
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
    private void get_BookingData(int booking_ID) {
        client = new OkHttpClient();
        gson = new Gson();
        String url = "http://" + Utility.IP_Adress + ":5132/api/Booking/get/" + booking_ID;
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
                        startDate.setText("Scheduled Date : " + book.getBookingDate() + " " + book.getBookingTime());
                        Vehicle.setText("Vehicle : " + book.getVehicleMake() + " " + book.getVehicleModel() + " " + book.getVehicleYear());
                        job_details.setText(book.getBookingDetail());
                    });

                } else {
                    Log.d("tester", "onResponse: failed with code " + response.message());
                }
            }
        });
    }



    public void pickImage() {
        // Start the image picker intent
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
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
    public void SendImage(View view) {
        pickImage();
    }
    private Uri photoUri;

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




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                imageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    imageBit = bitmap;
                    SetImages(bitmap); // Set the images received
                    File imageFile = bitmapToFile(bitmap);
                    uploadImage(imageFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == CAMERA_INTENT_REQUEST_CODE) {
                if (photoUri != null) {
                    try {
                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                        imageBit = imageBitmap;
                        SetImages(imageBitmap); // Set the images received
                        File imageFile = bitmapToFile(imageBitmap);
                        uploadImage(imageFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void uploadImage(File file) {
        String fileName = Utility.generateRandomString()+".jpg";
        Log.d("Upload", "uploadImage: imge name "+file.getName());
        Log.d("Upload", "uploadImage: timestamp "+fileName);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("bookingId", String.valueOf(Booking_ID)) // Replace with actual booking ID
                .addFormDataPart("file", fileName,
                        RequestBody.create(MediaType.parse("image/jpeg"), file))
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

    private Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
    }

}
/****************************************************Implement loading screen*********************/
// booking and job , include vehicle ID
// implement loading screen
// make potrait photos land scape , must be able to click and view the photo
//fix past jobs activities
//see if we could do notifications