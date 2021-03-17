package com.plorrios.medialists.ListsModifications;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.plorrios.medialists.Adapters.Lists.RemoveAddToListsAdapter;
import com.plorrios.medialists.R;

import java.util.ArrayList;

import static androidx.core.content.ContextCompat.getDrawable;

public class RemoveAddListsActivity extends AppCompatActivity {

    Context context;
    private RecyclerView recyclerView;
    private RemoveAddToListsAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    ProgressBar loading;
    FirebaseFirestore db;
    String personEmail;
    String tipolistas;
    String subtipolistas;
    String use;
    Toolbar toolbar;
    Query query;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Resources res = getResources();
        SharedPreferences obtainedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String style = obtainedPreferences.getString("AppliedStyle",res.getString(R.string.Light));
        if (style.equals(res.getString(R.string.Light))) {
            setTheme(R.style.AppThemeLight);
        }else {
            setTheme(R.style.AppThemeDark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_add_lists);

        context = this;
        loading = (ProgressBar) findViewById(R.id.RemoveListsProgressBar);
        loading.setVisibility(View.VISIBLE);

        toolbar = findViewById(R.id.DeleteAddToListToolbar);
        toolbar.inflateMenu(R.menu.accept_cancel_menu);


        if (style.equals(res.getString(R.string.Light))) {
            toolbar.setBackground(getDrawable(R.drawable.bottom_navbar_drawable_light));
        }else {
            toolbar.setBackground(getDrawable(R.drawable.toolbar_drawable_dark));
        }

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        personEmail = account.getEmail();
        tipolistas = getIntent().getStringExtra("tipo");
        subtipolistas = getIntent().getStringExtra("type");
        use = getIntent().getStringExtra("use");
        db = FirebaseFirestore.getInstance();
        Log.d("possible error",tipolistas);
        CollectionReference userRef = db.collection("Users").document(personEmail).collection(tipolistas);
        if (use.equals("add")) {
            query = userRef.whereEqualTo("type", subtipolistas);
        }else{
            query = userRef;
        }
        Log.d("USE",use);

        final ArrayList<String> Lists = new ArrayList<String>();
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("SUCCESS", document.getId() + " => " + document.getData());
                                Lists.add(document.getId());
                            }
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference();

                            recyclerView = (RecyclerView) findViewById(R.id.recyclerViewRemoveMusic);
                            layoutManager = new LinearLayoutManager(context);
                            recyclerView.setLayoutManager(layoutManager);

                            mAdapter = new RemoveAddToListsAdapter(Lists, personEmail, storageRef, tipolistas, use);
                            recyclerView.setAdapter(mAdapter);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                                    linearLayoutManager.getOrientation());
                            recyclerView.addItemDecoration(dividerItemDecoration);
                            recyclerView.setAdapter(mAdapter);
                            recyclerView.setLayoutManager(linearLayoutManager);
                            loading.setVisibility(View.INVISIBLE);

                            Log.d("SUCCESS", String.valueOf(mAdapter.getItemCount()));

                        } else {
                            Log.d("ERROR", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void cancelClick(MenuItem item) {
        finish();
    }

    public void acceptClick(MenuItem item) {
        if (mAdapter == null) {
            this.finish();
        }
        if (use.equals("delete")) {
            ArrayList<String> rLists = mAdapter.listsToRemove();
            for (String list : rLists) {

                CollectionReference userRef = db.collection("Users").document(personEmail).collection(tipolistas);
                userRef.document(list)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("TAG", "DocumentSnapshot successfully deleted!");
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("TAG", "Error deleting document", e);
                                Toast.makeText(context, R.string.Error_deleting_list, Toast.LENGTH_LONG);
                            }
                        });

            }
        } else if (use.equals("add")) {
            Log.d("ACCESSED ADD","true");
            String id = getIntent().getStringExtra("id");
            ArrayList<String> rLists = mAdapter.listsToRemove();
            for (String list : rLists) {
                DocumentReference itemRef = db.collection("Users").document(personEmail).collection(tipolistas).document(list);
                itemRef.update("id", FieldValue.arrayUnion(id));
                Log.d("ADDED",id);
            }
        }
        finish();
    }


}
