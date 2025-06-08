package com.example.garage.functions;

import static com.example.garage.functions.formatUtils.formatCount;

import android.content.Context;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.garage.R;
import com.example.garage.adapters.PostAdapter;
import com.example.garage.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class postUtils {

    public interface PostLoadCallback {
        void onComplete();
    }

    public static void toggleLikePost(String postId, ImageButton likeBtn, TextView likeCountText) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DocumentReference postRef = db.collection("posts").document(postId);
        String currentUserId = auth.getCurrentUser().getUid();

        postRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> likes = (List<String>) documentSnapshot.get("likes");
                int likeCount = documentSnapshot.getLong("likeCount").intValue();
                if (likes == null) likes = new ArrayList<>();

                boolean liked = likes.contains(currentUserId);
                WriteBatch batch = db.batch();

                if (liked) {
                    likes.remove(currentUserId);
                    likeCount--;
                    likeBtn.setImageResource(R.drawable.heart);
                    likeCountText.setText(formatCount(likeCount));
                } else {
                    likes.add(currentUserId);
                    likeCount++;
                    likeBtn.setImageResource(R.drawable.heart_filled);
                    likeCountText.setText(formatCount(likeCount));
                }

                batch.update(postRef, "likes", likes);
                batch.update(postRef, "likeCount", likeCount);
                batch.commit();
            }
        }).addOnFailureListener(e -> {
        });
    }

    public static void toggleSavePost(String postId, ImageButton saveBtn) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserId = auth.getCurrentUser().getUid();
        DocumentReference userRef = db.collection("users").document(currentUserId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> saves = (List<String>) documentSnapshot.get("savedPosts");
                if (saves == null) saves = new ArrayList<>();

                boolean saved = saves.contains(postId);
                WriteBatch batch = db.batch();

                if (saved) {
                    saves.remove(postId);
                    saveBtn.setImageResource(R.drawable.bookmark);
                } else {
                    saves.add(postId);
                    saveBtn.setImageResource(R.drawable.bookmark_filled);
                }

                batch.update(userRef, "savedPosts", saves);
                batch.commit();
            }
        }).addOnFailureListener(e -> {
        });
    }

    public static void toggleHidePost(String postId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserId = auth.getCurrentUser().getUid();
        DocumentReference userRef = db.collection("users").document(currentUserId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> hidden = (List<String>) documentSnapshot.get("hiddenPosts");
                if (hidden == null) hidden = new ArrayList<>();

                boolean saved = hidden.contains(postId);
                WriteBatch batch = db.batch();

                if (saved) {
                    hidden.remove(postId);
                } else {
                    hidden.add(postId);
                }

                batch.update(userRef, "hiddenPosts", hidden);
                batch.commit();
            }
        });
    }

    public static void loadHomePosts(Context context, List<Post> postList, PostAdapter adapter, @NonNull PostLoadCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(currentUserId).get()
                .addOnSuccessListener(userSnapshot -> {
                    List<String> hiddenPosts = (List<String>) userSnapshot.get("hiddenPosts");

                    db.collection("posts")
                            .whereEqualTo("archived", false)
                            .orderBy("timestamp", Query.Direction.DESCENDING)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                postList.clear();
                                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                    String postId = doc.getId();
                                    if (hiddenPosts != null && hiddenPosts.contains(postId)) {
                                        continue;
                                    }

                                    Post post = doc.toObject(Post.class);
                                    post.setPostId(postId);
                                    postList.add(post);
                                }
                                adapter.notifyDataSetChanged();
                                callback.onComplete();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Failed to load posts", Toast.LENGTH_SHORT).show();
                                callback.onComplete();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to load user data", Toast.LENGTH_SHORT).show();
                    callback.onComplete();
                });
    }

    public static void loadSavedPosts(Context context, List<Post> postList, PostAdapter adapter, @NonNull PostLoadCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(currentUserId).get()
                .addOnSuccessListener(userSnapshot -> {
                    List<String> savedPosts = (List<String>) userSnapshot.get("savedPosts");
                    if (savedPosts == null || savedPosts.isEmpty()) {
                        Toast.makeText(context, "No saved posts found", Toast.LENGTH_SHORT).show();
                        callback.onComplete();
                        return;
                    }

                    postList.clear();
                    for (String postId : savedPosts) {
                        db.collection("posts").document(postId).get()
                                .addOnSuccessListener(postSnapshot -> {
                                    if (postSnapshot.exists()) {
                                        Post post = postSnapshot.toObject(Post.class);
                                        post.setPostId(postSnapshot.getId());
                                        if (!post.getArchived()) {
                                            postList.add(post);
                                        }
                                    }
                                    adapter.notifyDataSetChanged();
                                    callback.onComplete();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Failed to load post: " + postId, Toast.LENGTH_SHORT).show();
                                    callback.onComplete();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to load user data", Toast.LENGTH_SHORT).show();
                    callback.onComplete();
                });
    }

    public static void loadArchivedPosts(Context context, List<Post> postList, PostAdapter adapter, @NonNull PostLoadCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("posts")
                .whereEqualTo("authorId", currentUserId)
                .whereEqualTo("archived", true)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    postList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Post post = doc.toObject(Post.class);
                        post.setPostId(doc.getId());
                        postList.add(post);
                    }
                    adapter.notifyDataSetChanged();
                    callback.onComplete();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to load archived posts", Toast.LENGTH_SHORT).show();
                    callback.onComplete();
                });
    }

    public static void loadHiddenPosts(Context context, List<Post> postList, PostAdapter adapter, @NonNull PostLoadCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(currentUserId).get()
                .addOnSuccessListener(userSnapshot -> {
                    List<String> hiddenPosts = (List<String>) userSnapshot.get("hiddenPosts");
                    if (hiddenPosts == null || hiddenPosts.isEmpty()) {
                        Toast.makeText(context, "No hidden posts found", Toast.LENGTH_SHORT).show();
                        callback.onComplete();
                        return;
                    }

                    postList.clear();
                    for (String postId : hiddenPosts) {
                        db.collection("posts").document(postId).get()
                                .addOnSuccessListener(postSnapshot -> {
                                    if (postSnapshot.exists()) {
                                        Post post = postSnapshot.toObject(Post.class);
                                        post.setPostId(postSnapshot.getId());
                                        if (!post.getArchived()) {
                                            postList.add(post);
                                        }
                                    }
                                    adapter.notifyDataSetChanged();
                                    callback.onComplete();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Failed to load post: " + postId, Toast.LENGTH_SHORT).show();
                                    callback.onComplete();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to load user data", Toast.LENGTH_SHORT).show();
                    callback.onComplete();
                });
    }
}
