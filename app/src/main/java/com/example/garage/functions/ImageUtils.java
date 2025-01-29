package com.example.garage.functions;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ImageUtils {

    public static void uploadImageToFirestore(Bitmap bitmap) {
        String base64Image = convertImageToBase64(bitmap);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference imagesRef = db.collection("images");

        Map<String, String> imageData = new HashMap<>();
        imageData.put("imageData", base64Image);

        imagesRef.add(imageData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Image uploaded successfully!");
                })
                .addOnFailureListener(e -> {
                    Log.d("Firestore", "Error uploading image: ", e);
                });
    }

    public static Task<Bitmap> getImageFromFirestore(String documentId) {
        TaskCompletionSource<Bitmap> taskCompletionSource = new TaskCompletionSource<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("images").document(documentId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String base64ImageData = documentSnapshot.getString("imageData");
                        Bitmap bitmap = decodeBase64ToBitmap(base64ImageData);
                        taskCompletionSource.setResult(bitmap);
                    } else {
                        Log.d("Firestore", "No such document!");
                        taskCompletionSource.setResult(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("Firestore", "Error retrieving image: ", e);
                    taskCompletionSource.setException(e);
                });

        return taskCompletionSource.getTask();
    }

    private static Bitmap decodeBase64ToBitmap(String base64String) {
        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    private static String convertImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
