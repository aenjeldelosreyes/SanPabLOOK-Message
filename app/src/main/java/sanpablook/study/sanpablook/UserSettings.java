package sanpablook.study.sanpablook;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.study.sanpablook.R;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserSettings extends AppCompatActivity {

    //buttons and clickable
    ImageButton btnBackUS;
    TextView txtEditUsername, txtEditPassword, txtEditBio, txtEditPhoneNumber, txtEditEmail;
    Button btnLogout, btnDeleteAccount;

    //Firebase
    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseUser user;
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    String userID;

    //User profiles
    TextView valueOfUsername, valueOfBio, valueOfEmail, valueofPhoneNumber;

    //profilepic
    FloatingActionButton fabEditProfile;
    ShapeableImageView profilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        //find view by id
        btnBackUS = findViewById(R.id.buttonBackUS);
        txtEditUsername = findViewById(R.id.editTheUsername);
        txtEditPassword = findViewById(R.id.editThePassword);
        txtEditBio = findViewById(R.id.editTheBio);
        txtEditPhoneNumber = findViewById(R.id.editThePhoneNumber);
        txtEditEmail = findViewById(R.id.editTheEmail);
        btnLogout = findViewById(R.id.buttonLogout);
        btnDeleteAccount = findViewById(R.id.buttonDeleteAccount);
        valueOfUsername = findViewById(R.id.valueOfUsername);
        valueOfBio = findViewById(R.id.valueOfBio);
        valueOfEmail = findViewById(R.id.valueOfEmail);
        valueofPhoneNumber = findViewById(R.id.valueOfPhoneNumber);


        //Firebase Auth
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        userID = auth.getCurrentUser().getUid();

        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        DocumentReference documentReference = fireStore.collection("users").document(userID);
        StorageReference profilePicRef = storage.getReference().child("profilePictures/" + userID + ".jpg");

        // Get user's first name
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String firstName = document.getString("firstName");
                    valueOfUsername.setText(firstName);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        // Get user's bio
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String bio = document.getString("bio");
                    valueOfBio.setText(bio);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        // Get user's email
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String email = document.getString("email");
                    valueOfEmail.setText(email);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        //Get users profile picture
 profilePicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
    @Override
    public void onSuccess(Uri uri) {
        Glide.with(UserSettings.this)
            .load(uri)
            .into(profilePicture);
    }
}).addOnFailureListener(new OnFailureListener() {
    @Override
    public void onFailure(Exception e) {
        Log.d(TAG, "Failed to load profile picture");
    }
});
        //Get user's phone number
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String phoneNumber = document.getString("phoneNumber");
                    valueofPhoneNumber.setText(phoneNumber);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        //profilepic
        fabEditProfile = findViewById(R.id.fabEditProfilePicture);
        profilePicture = findViewById(R.id.profilePicture);

        fabEditProfile.setOnClickListener(view1 -> {
            ImagePicker.Companion.with(this)
                    .crop()                 // Crop image(Optional), Check Customization for more option
                    .compress(1024)         // Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)   // Final image resolution will be less than 1080 x 1080(Optional)
                    .start();
        });

        //onclick
        btnBackUS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        txtEditUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogEditUsername(view);
            }
        });

        txtEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogEditPassword(view);
            }
        });

        txtEditBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogEditBio(view);
            }
        });
        txtEditPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogEditPhoneNumber(view);
            }
        });
        txtEditEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogEditEmail(view);
            }
        });
        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogDeleteAccount(view);
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(UserSettings.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Get the image URI
        if (resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            profilePicture.setImageURI(imageUri);

            uploadProfilePicture(imageUri);
        }
    }

    private void uploadProfilePicture(Uri imageUri) {
    // Firebase Storage
    storage = FirebaseStorage.getInstance();
    storageRef = storage.getReference();

    // Create a reference to the location where you want to upload the image
    StorageReference profilePicRef = storageRef.child("profilePictures/" + userID + ".jpg");

    // Log the URI
    Log.d(TAG, "Uploading file from URI: " + imageUri.toString());

    // Upload the image file to Firebase Storage
    profilePicRef.putFile(imageUri)
        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // The image upload was successful
                Log.d(TAG, "Profile picture uploaded successfully");
                Toast.makeText(UserSettings.this, "Profile picture uploaded", Toast.LENGTH_SHORT).show();

                // Refresh the activity
                UserSettings.this.recreate();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // The image upload failed
                if (exception instanceof FileNotFoundException) {
                    Log.e(TAG, "File not found", exception);
                    Toast.makeText(UserSettings.this, "File not found", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Failed to upload profile picture", exception);
                    Toast.makeText(UserSettings.this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
                }
            }
        });
}

    private void showDialogEditUsername (View view) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_username);

        Button btnSave = dialog.findViewById(R.id.buttonSave);
        Button btnCancel = dialog.findViewById(R.id.buttonCancel);
        EditText editTextUsername = dialog.findViewById(R.id.editTextUsername);

        // Firebase Auth
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        userID = auth.getCurrentUser().getUid();

        // Get user's first name
        DocumentReference documentReference = fStore.collection("users").document(userID);

        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String firstName = document.getString("firstName");
                    editTextUsername.setText(firstName);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        btnSave.setEnabled(false);
        btnSave.setAlpha(0.5f);

        editTextUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                btnSave.setEnabled(true);
                btnSave.setAlpha(1.0f);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed for this case
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newFirstName = editTextUsername.getText().toString();

                // Mapping
                Map<String, Object> user = new HashMap<>();
                user.put("firstName", newFirstName);

                // Update the first name
                documentReference.set(user, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                                Toast.makeText(UserSettings.this, "Username updated", Toast.LENGTH_SHORT).show();
                                UserSettings.this.recreate();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                                Toast.makeText(UserSettings.this, "Error updating username", Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private EditText editTextCurrentPasswordChange;

private void changePassword() {
    // Get the current user
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    editTextCurrentPasswordChange = findViewById(R.id.editTextCurrentPasswordChange);

    // Get the current password, new password, and retype password from the EditText fields
    String currentPassword = editTextCurrentPasswordChange.getText().toString();
    String newPassword = editTextNew.getText().toString();
    String retypePassword = editTextRetype.getText().toString();

    // Create an AuthCredential object
    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

    // Re-authenticate the user
    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()) {
                // The re-authentication was successful

                // Check if the new password and the retype password match
                if (newPassword.equals(retypePassword)) {
                    // The new password and the retype password match

                    // Update the user's password
                    user.updatePassword(newPassword)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // The password update was successful
                                Toast.makeText(UserSettings.this, "Password updated", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // The password update failed
                                Toast.makeText(UserSettings.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                            }
                        });
                } else {
                    // The new password and the retype password do not match
                   editTextRetype.setError("Passwords do not match");
                    editTextRetype.requestFocus();
                }
            } else {
                // The re-authentication failed
                Toast.makeText(UserSettings.this, "Please check your current password.", Toast.LENGTH_SHORT).show();
            }
        }
    });
}
    //for edit password
    private EditText editTextNew;
    private EditText editTextRetype;
    private Button btnSave;

    private void showDialogEditPassword (View view) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_password);

        editTextCurrentPasswordChange = dialog.findViewById(R.id.editTextCurrentPasswordChange);
        btnSave = dialog.findViewById(R.id.buttonSave);
        Button btnCancel = dialog.findViewById(R.id.buttonCancel);
        editTextNew = dialog.findViewById(R.id.editTextNewPassword);
        editTextRetype = dialog.findViewById(R.id.editTextRetypePassword);
        btnSave.setEnabled(false);
        btnSave.setAlpha(0.5f);  // initial color is opaque

        editTextNew.addTextChangedListener(passwordTextWatcher);
        editTextRetype.addTextChangedListener(passwordTextWatcher);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
                dialog.dismiss();
            }
     });

    btnCancel.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dialog.dismiss();
        }
    });

    dialog.show();
    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    dialog.getWindow().setGravity(Gravity.BOTTOM);
}

    private TextWatcher passwordTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // Not needed for this case
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // Check if the text in both EditTexts match
            boolean passwordsMatch = editTextNew.getText().toString().equals(editTextRetype.getText().toString());

            // Enable or disable the Save button accordingly
            btnSave.setEnabled(passwordsMatch);
            btnSave.setAlpha(passwordsMatch ? 1.0f : 0.5f);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // Not needed for this case
        }
    };

    private void showDialogEditBio (View view) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_bio);

        Button btnSave = dialog.findViewById(R.id.buttonSave);
        Button btnCancel = dialog.findViewById(R.id.buttonCancel);
        EditText editTextBio = dialog.findViewById(R.id.editTextBio);

        // Firebase Auth
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        userID = auth.getCurrentUser().getUid();

        // Get user's Bio
        DocumentReference documentReference = fStore.collection("users").document(userID);

        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String bio = document.getString("bio");
                    editTextBio.setText(bio);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        btnSave.setEnabled(false);
        btnSave.setAlpha(0.5f);

        editTextBio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed for this case
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                btnSave.setEnabled(true);
                btnSave.setAlpha(1.0f);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed for this case
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newBio = editTextBio.getText().toString();

                // Mapping
                Map<String, Object> user = new HashMap<>();
                user.put("bio", newBio);

                // Update the first name
                documentReference.set(user, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                                Toast.makeText(UserSettings.this, "Bio updated", Toast.LENGTH_SHORT).show();
                                UserSettings.this.recreate();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                                Toast.makeText(UserSettings.this, "Error updating Bio", Toast.LENGTH_SHORT).show();
                            }
                        });

                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void showDialogEditPhoneNumber (View view) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_phone_number);

        final Button btnSave = dialog.findViewById(R.id.buttonSave);
        Button btnCancel = dialog.findViewById(R.id.buttonCancel);
        final EditText editTextPhoneNumber = dialog.findViewById(R.id.editTextPhoneNumber);

        // Firebase Auth
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        userID = auth.getCurrentUser().getUid();

        //Get user's phone number
        DocumentReference documentReference = fStore.collection("users").document(userID);

        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String phoneNumber = document.getString("phoneNumber");
                    editTextPhoneNumber.setText(phoneNumber);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        btnSave.setEnabled(false);
        btnSave.setAlpha(0.5f);

        editTextPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // Not needed for this case
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (validateMobile(editTextPhoneNumber.getText().toString())) {
                    btnSave.setEnabled(true);
                    btnSave.setAlpha(1.0f);
                }
                else {
                    btnSave.setEnabled(false);
                    editTextPhoneNumber.setError("Invalid phone number");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed for this case
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPhoneNumber = editTextPhoneNumber.getText().toString();

                //Check if the phone number is empty or invalid
                if (newPhoneNumber.isEmpty() || !validateMobile(newPhoneNumber)) {
                    editTextPhoneNumber.setError("Invalid phone number");
                    editTextPhoneNumber.requestFocus();
                }

                // Mapping
                Map<String, Object> user = new HashMap<>();
                user.put("phoneNumber", newPhoneNumber);

                // Update the phone number
                documentReference.set(user, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                                Toast.makeText(UserSettings.this, "Phone number updated", Toast.LENGTH_SHORT).show();
                                UserSettings.this.recreate();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                                Toast.makeText(UserSettings.this, "Error updating phone number", Toast.LENGTH_SHORT).show();
                            }
                        });

                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

   private void showDialogEditEmail (View view) {
    final Dialog dialog = new Dialog(this);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setContentView(R.layout.dialog_edit_email);

    Button btnSave = dialog.findViewById(R.id.buttonSave);
    Button btnCancel = dialog.findViewById(R.id.buttonCancel);
    final EditText editTextEmail = dialog.findViewById(R.id.editTextEmail);
    final EditText currentPassword = dialog.findViewById(R.id.editTextCurrentPassword);


       // Firebase Auth
    auth = FirebaseAuth.getInstance();
    user = auth.getCurrentUser();
    fStore = FirebaseFirestore.getInstance();
    userID = auth.getCurrentUser().getUid();

    // Get user's Email
    DocumentReference documentReference = fStore.collection("users").document(userID);

    documentReference.get().addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
            DocumentSnapshot document = task.getResult();
            if (document.exists()) {
                String email = document.getString("email");
                editTextEmail.setText(email);
            } else {
                Log.d(TAG, "No such document");
            }
        } else {
            Log.d(TAG, "get failed with ", task.getException());
        }
    });

    btnSave.setEnabled(false);
    btnSave.setAlpha(0.5f);

    editTextEmail.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // Not needed for this case
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            btnSave.setEnabled(true);
            btnSave.setAlpha(1.0f);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // Not needed for this case
        }
    });




       btnSave.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
                changeEmailWithVerification(editTextEmail, currentPassword);
                dialog.dismiss();
              }
       });

    btnCancel.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dialog.dismiss();
        }
    });

    dialog.show();
    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    dialog.getWindow().setGravity(Gravity.BOTTOM);
}

private void changeEmailWithVerification(EditText editTextEmail, EditText currentPassword) {
    // Get the current user
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    // Get the new email and current password from the EditText fields
    String newEmail = editTextEmail.getText().toString();
    String userCurrentPassword = currentPassword.getText().toString();

    // Check if the new email is valid
    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
        // The new email is not valid
        editTextEmail.setError("Invalid email address");
        editTextEmail.requestFocus();
    } else {
        // Re-authenticate the user
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), userCurrentPassword);
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Send verification email to the new email address
                    user.verifyBeforeUpdateEmail(newEmail)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Verification email sent successfully
                                Toast.makeText(UserSettings.this, "Verification email sent to " + newEmail, Toast.LENGTH_SHORT).show();

                                // Update the email in Firestore
                                DocumentReference documentReference = fStore.collection("users").document(userID);
                                documentReference.update("email", newEmail)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                                            Toast.makeText(UserSettings.this, "Email updated", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error updating document", e);
                                            Toast.makeText(UserSettings.this, "Error updating email in Firestore", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Failed to send verification email
                                Toast.makeText(UserSettings.this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
                            }
                        });
                } else {
                    // Failed to re-authenticate the user
                    Toast.makeText(UserSettings.this, "Failed to re-authenticate. Please check your current password.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

    private void showDialogDeleteAccount (View view) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete_account);

        Button btnConfirm = dialog.findViewById(R.id.buttonConfirm);
        Button btnCancel = dialog.findViewById(R.id.buttonCancel);
        EditText txtDeleteAccount = dialog.findViewById(R.id.editTextDeleteAccount);

        btnConfirm.setEnabled(false);
        btnConfirm.setAlpha(0.5f);  // initial color is opaque

        txtDeleteAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed for this case
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // if text is "Delete account"
                boolean isDeleteAccount = charSequence.toString().equals("Delete account");

                // Enable or disable button
                btnConfirm.setEnabled(isDeleteAccount);
                btnConfirm.setAlpha(isDeleteAccount ? 1.0f : 0.5f);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed for this case
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UserSettings.this , "Your account has been deleted", Toast.LENGTH_SHORT);

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    boolean validateMobile(String input) {
        Pattern p = Pattern.compile("[0][9][0-9]{9}");
        Matcher m = p.matcher(input);
        return m.matches();
    }
}