package com.example.garage.functions;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ImageUtils {

    public static Task<String> uploadImageToFirestore(Bitmap bitmap) {
        Bitmap resizedBitmap = resizeImage(bitmap, 1024, 1024);
        String base64Image = convertImageToBase64(resizedBitmap);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        CollectionReference imagesRef = db.collection("images");

        Map<String, String> imageData = new HashMap<>();
        imageData.put("imageData", base64Image);
        imageData.put("authorId", auth.getCurrentUser().getUid());

        return imagesRef.add(imageData)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        return task.getResult().getId();
                    } else {
                        throw task.getException();
                    }
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
                        taskCompletionSource.setResult(null);
                    }
                })
                .addOnFailureListener(e -> {
                    taskCompletionSource.setException(e);
                });

        return taskCompletionSource.getTask();
    }

    public static Task<Void> deleteImageFromFirebase(String imageId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("images").document(imageId).delete();
    }

    private static Bitmap resizeImage(Bitmap original, int maxWidth, int maxHeight) {
        int width = original.getWidth();
        int height = original.getHeight();
        float aspectRatio = (float) width / height;

        if (width > maxWidth || height > maxHeight) {
            if (aspectRatio > 1) {
                width = maxWidth;
                height = Math.round(width / aspectRatio);
            } else {
                height = maxHeight;
                width = Math.round(height * aspectRatio);
            }
        }

        return Bitmap.createScaledBitmap(original, width, height, true);
    }

    private static Bitmap decodeBase64ToBitmap(String base64String) {
        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    private static String convertImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
