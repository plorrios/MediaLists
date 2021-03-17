package com.plorrios.medialists.Main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.plorrios.medialists.Adapters.Lists.ListAdapter;
import com.plorrios.medialists.R;

import java.util.ArrayList;

import static androidx.core.content.ContextCompat.getDrawable;

public class ListsFragment extends Fragment {

    private Context context;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public String tipolistas;
    private ProgressBar loading;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.lists_fragment, container, false);

        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) view.findViewById(R.id.ListsToolbar);
        toolbar.inflateMenu(R.menu.menu_with_search);
        toolbar.setTitle(R.string.AppTitle);

        Resources res = getResources();
        SharedPreferences obtainedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String style = obtainedPreferences.getString("AppliedStyle",res.getString(R.string.Light));
        if (style.equals(res.getString(R.string.Light))) {
            toolbar.setBackground(getDrawable(getContext(),R.drawable.bottom_navbar_drawable_light));
        }else {
            toolbar.setBackground(getDrawable(getContext(),R.drawable.toolbar_drawable_dark));
        }

        tipolistas = getArguments().getString("tipo");

        loading = view.findViewById(R.id.ListsProgressBar);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewRemoveMusic);

        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();

    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.TVButton:
                            tipolistas = "TVLists";
                        case R.id.SongsButton:
                            tipolistas = "MusicLists";
                        case R.id.GamesButton:
                            tipolistas = "GamesLists";
                        case R.id.BooksButton:
                            tipolistas = "BooksLists";
                        case R.id.HomeButton:

                    }
                    return true;
                }
            };


    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();
        //Refresh your stuff here
        getInfo();
    }

    private void getInfo() {


        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        final String personEmail = account.getEmail();
        final ArrayList<String> Lists = new ArrayList<String>();
        final ArrayList<String> subtipo = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference userRef = db.collection("Users").document(personEmail).collection(tipolistas);
        userRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            loading.setVisibility(View.INVISIBLE);
                            Log.d("ABC", task.getResult().toString());
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("SUCCESS", document.getId() + " => " + document.getData());
                                Lists.add(document.getId());
                                subtipo.add((String)document.get("type"));
                            }

                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference();
                            layoutManager = new LinearLayoutManager(context);
                            recyclerView.setLayoutManager(layoutManager);
                            mAdapter = new ListAdapter(Lists, personEmail, storageRef, tipolistas, new ListAdapter.IClickListener() {
                                @Override
                                public void onClickListener(int position) {
                                    //showToast(position);
                                    FragmentManager manager = getActivity().getSupportFragmentManager();
                                    FragmentTransaction transaction = manager.beginTransaction();
                                    Fragment newFragment = new MusicListDetailsFragment();
                                    transaction.replace(R.id.MainFragment, newFragment);
                                    transaction.addToBackStack("null");
                                    Bundle bundle = new Bundle();
                                    bundle.putString("type",subtipo.get(position));
                                    Log.d("SUBTIPO",subtipo.get(position));
                                    bundle.putString("tipo",tipolistas);
                                    bundle.putString("elemento", Lists.get(position));

                                    newFragment.setArguments(bundle);

                                    transaction.commit();
                                }
                            });
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                                    linearLayoutManager.getOrientation());
                            recyclerView.addItemDecoration(dividerItemDecoration);
                            recyclerView.setAdapter(mAdapter);
                        } else {
                            Log.d("ERROR", "Error getting documents: ", task.getException());
                        }
                    }
                });


    }


}
