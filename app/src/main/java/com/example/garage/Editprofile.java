package com.example.garage;

import static android.app.Activity.RESULT_OK;
import static com.example.garage.functions.ImageUtils.getImageFromFirestore;
import static com.example.garage.functions.ImageUtils.uploadImageToFirestore;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Editprofile extends Fragment {

    String previousProfilePicture = null;

    ImageButton backBtn, settingsBtn;
    TextView screenTitle;
    EditText editName, editUsername;
    Button saveBtn;
    FrameLayout pictureFrameLayout;
    ImageView imageProfile;

    FirebaseAuth auth;
    FirebaseFirestore db;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    imageProfile.setImageURI(selectedImageUri);
                }
            });

    public Editprofile() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        Activity activity = getActivity();

        screenTitle = activity.findViewById(R.id.screenTitle);
        screenTitle.setText("Edit Profile");

        settingsBtn = activity.findViewById(R.id.settingsBtn);
        settingsBtn.setVisibility(View.GONE);

        backBtn = activity.findViewById(R.id.backBtn);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        editName = view.findViewById(R.id.editName);
        editUsername = view.findViewById(R.id.editUsername);

        imageProfile = view.findViewById(R.id.imageProfile);

        pictureFrameLayout = view.findViewById(R.id.pictureFrameLayout);
        pictureFrameLayout.setOnClickListener(v -> openSystemImagePicker());

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();


        db.collection("users").document(currentUser.getUid()).get().addOnSuccessListener(documentSnapshot -> {
            editName.setText(documentSnapshot.getString("name"));
            editUsername.setText(documentSnapshot.getString("username"));
            previousProfilePicture = documentSnapshot.getString("profilePicture");
            getImageFromFirestore(previousProfilePicture).addOnSuccessListener(bitmap -> imageProfile.setImageBitmap(bitmap));

        });

        saveBtn = view.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(v -> {
            String updatedName = editName.getText().toString();
            String updatedUsername = editUsername.getText().toString().trim();

            if (updatedUsername.isEmpty()) {
                Toast.makeText(getContext(), "Username cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            uploadImageToFirestore(((BitmapDrawable) imageProfile.getDrawable()).getBitmap()).addOnSuccessListener(docId -> {
                db.collection("users").document(currentUser.getUid())
                        .update("name", updatedName, "username", updatedUsername, "profilePicture", docId)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            if (previousProfilePicture != null) {
                                db.collection("images").document(previousProfilePicture).delete();
                            }
                            getParentFragmentManager().popBackStack();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                        });
            });
        });

        return view;
    }

    private void openSystemImagePicker() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            Intent chooser = Intent.createChooser(galleryIntent, "Select or take a new picture");
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});
            imagePickerLauncher.launch(chooser);
        } else {
            imagePickerLauncher.launch(galleryIntent);
        }
    }
}