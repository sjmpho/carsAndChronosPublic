package com.example.carsandchronos.Utility;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import okhttp3.*;

public class ImageUploader {

    private static final String BASE_URL = "http://"+ Utility.IP_Adress +":5132/Image/Upload";

    public void uploadImage(Context context, int bookingId, Uri imageUri) {
        OkHttpClient client = new OkHttpClient();

        // Get the file from URI
        File file = new File(getRealPathFromURI(context, imageUri));

        // Create request body
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"),file);

        // MultipartBody.Part is used to send the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        // Add another part within the multipart request
        RequestBody bookingIdBody = RequestBody.create(MediaType.parse("text/plain"),String.valueOf(bookingId));

        // Build multipart request
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("bookingId", String.valueOf(bookingId))
                .addFormDataPart("file", file.getName(), requestFile)
                .build();

        // Build request
        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(requestBody)
                .build();

        // Execute the request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d("taste", "onFailure: "+e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Handle the response
                    Log.d("taste", "onResponse: 1"+response.body().string());
                    String responseBody = response.body().string();
                    // Do something with the response, e.g., display a message
                } else {
                    // Handle the error
                    Log.d("taste", "onResponse: 2"+response.message());
                }
            }
        });
    }

    private String getRealPathFromURI(Context context, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else {
            return null;
        }
    }
}

