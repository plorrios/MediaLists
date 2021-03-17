package com.plorrios.medialists.ListsModifications;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.plorrios.medialists.R;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddListActivity extends AppCompatActivity {

    ImageView Image;
    EditText Title;
    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    byte[] downsizedImageBytes;
    ProgressBar loading;
    boolean iconselected;
    Spinner spinner;
    String tipolistas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Resources res = getResources();
        SharedPreferences obtainedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (obtainedPreferences.getString("AppliedStyle", res.getString(R.string.Light)).equals(res.getString(R.string.Light))) {
            setTheme(R.style.AppThemeLight);
        } else {
            setTheme(R.style.AppThemeDark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);

        tipolistas = getIntent().getStringExtra("tipo");

        Image = (ImageView) findViewById(R.id.imageViewCustomIcon);
        Image.setImageResource(android.R.color.transparent);

        spinner = findViewById(R.id.AddListSpinner);

        if(tipolistas.equals("GamesLists")){
            Spinner spinner = (Spinner)findViewById(R.id.AddListSpinner);
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerAdapter);
            spinnerAdapter.add("Games");
            spinnerAdapter.notifyDataSetChanged();
        }

        loading = (ProgressBar) findViewById(R.id.AddListProgressBar);
        iconselected=false;
        Title = (EditText) findViewById(R.id.AddListTitle);
    }

    public void cancelClick(View view) {
        finish();
    }


    public void addListClick(View view) {
        loading.setVisibility(View.VISIBLE);

        if (Title.getText().toString().equals("")) {
            Toast.makeText(this, getText(R.string.No_Title), Toast.LENGTH_SHORT).show();
            loading.setVisibility(View.INVISIBLE);
            return;
        }
        else if (!iconselected) {
            Toast.makeText(this, getText(R.string.No_Icon), Toast.LENGTH_SHORT).show();
            loading.setVisibility(View.INVISIBLE);
            return;
        }else {

            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            String personEmail = account.getEmail();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Intent intent = getIntent();

            //Code to Create List for Firebase as Array
            ArrayList<String> gamesLists = new ArrayList<String>();
            String ListTitle = Title.getText().toString();
            Map<String, Object> data = new HashMap<>();
            data.put("type", spinner.getSelectedItem().toString().toLowerCase());
            db.collection("Users").document(personEmail).collection(tipolistas).document(ListTitle)
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("TAG", "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("TAG", "Error writing document", e);
                        }
                    });

            //Code to upload Image
            StorageReference storageRef = storage.getReference();
            String imageName = personEmail + tipolistas + ListTitle;
            StorageReference fileRef = storageRef.child(imageName);
            UploadTask uploadTask = fileRef.putBytes(downsizedImageBytes);//putFile(imageUri);


            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Log.d("Error", "Image Upload Error");
                    loading.setVisibility(View.INVISIBLE);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("Succesful", "Image upload Succesful");
                    finish();
                }
            });
        }



    }

    public void SelectImage(View view) {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null
                || data.getData() == null) {
            return;
        }
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            iconselected = true;
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                downsizedImageBytes = (scaleDown(bitmap,1000,true));
            }catch(IOException io){

            }
            Image.setImageURI(imageUri);


        }

    }

    public static byte[] scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        return data;
    }

}
