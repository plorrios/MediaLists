package com.plorrios.medialists.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.plorrios.medialists.Games.GameSearchActivity;
import com.plorrios.medialists.ListsModifications.AddListActivity;
import com.plorrios.medialists.Music.DeleteMusicListActivity;
import com.plorrios.medialists.R;
import com.plorrios.medialists.ListsModifications.RemoveAddListsActivity;
import com.plorrios.medialists.Music.MusicSearchActivity;
import com.plorrios.medialists.Settings.Settings;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;
    FirebaseFirestore db;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Resources res = getResources();
        SharedPreferences obtainedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String style = obtainedPreferences.getString("AppliedStyle",res.getString(R.string.Light));
        if (style.equals(res.getString(R.string.Light))) {
            setTheme(R.style.AppThemeLight);
            //Log.d("comparison:",res.getString(R.string.Light));
        }else {
            //Log.d("comparison:",res.getString(R.string.Dark));
            setTheme(R.style.AppThemeDark);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("OnCreate","Created");

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setSelectedItemId(R.id.HomeButton);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        if (style.equals(res.getString(R.string.Light))) {
            bottomNavigation.setBackground(getDrawable(R.drawable.bottom_navbar_drawable_light));
        }else {
            bottomNavigation.setBackground(getDrawable(R.drawable.bottom_navbar_drawable_dark));
        }

        Intent intent = getIntent();

        bundle = new Bundle();

        if (!intent.getBooleanExtra("BackFromSettings",false)) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.MainFragment, new MainFragment());
            ft.commit();


            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 1);

            MobileAds.initialize(this, "ca-app-pub-6776033603377109~4638946993");

        }else{
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            String tipoFragment = intent.getStringExtra("tipoFragment");
            String tipolista = intent.getStringExtra("tipolista");
            Fragment newFragment;
            if (tipoFragment.equals("Lists")){
                newFragment = new ListsFragment();
            }
            else if (tipoFragment.equals("ListDetails")){
                newFragment = new MusicListDetailsFragment();
                bundle.putString("elemento",intent.getStringExtra("elemento"));
            }else {
                newFragment = new MainFragment();
            }

            transaction.replace(R.id.MainFragment, newFragment);


            bundle.putString("tipo", tipolista);

            if(tipolista==null) {

            }else if(tipolista.equals("TVLists")) {
                bottomNavigation.getMenu().findItem(R.id.TVButton).setChecked(true);
            }else if (tipolista.equals("MusicLists")) {
                bottomNavigation.getMenu().findItem(R.id.SongsButton).setChecked(true);
            }else if (tipolista.equals("GamesLists")) {
                bottomNavigation.getMenu().findItem(R.id.GamesButton).setChecked(true);
            }else if (tipolista.equals("BooksLists")) {
                bottomNavigation.getMenu().findItem(R.id.BooksButton).setChecked(true);
            }

            newFragment.setArguments(bundle);

            transaction.commit();

        }

            db = FirebaseFirestore.getInstance();


    }

    @Override
    public void onBackPressed() {
        // Write your code here
        Fragment fragmentInFrame = (Fragment)getSupportFragmentManager().findFragmentById(R.id.MainFragment);
        if (fragmentInFrame instanceof ListsFragment)
        {
            bottomNavigation.getMenu().findItem(R.id.HomeButton).setChecked(true);
        }else if (fragmentInFrame instanceof MusicListDetailsFragment){
            if ( ((MusicListDetailsFragment) fragmentInFrame).subtipolistas.equals("song")) {
                ((MusicListDetailsFragment) fragmentInFrame).StopMusic();
            }
        }
        super.onBackPressed();
    }


    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    changeFragment(item.getItemId());
                    return true;
                }
            };

    public void click(View v){
        changeFragment(v.getId());
    }

    public void changeFragment(int id){

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment newFragment = new ListsFragment();
        transaction.replace(R.id.MainFragment, newFragment);
        manager.popBackStack();

        Fragment fragmentInFrame = (Fragment)getSupportFragmentManager().findFragmentById(R.id.MainFragment);
        if (fragmentInFrame instanceof MusicListDetailsFragment) {
            if ( ((MusicListDetailsFragment) fragmentInFrame).subtipolistas.equals("song")) {
                ((MusicListDetailsFragment) fragmentInFrame).StopMusic();
            }
        }

        if(id == R.id.TVButton || id == R.id.imageButtonTV ) {
            bundle.putString("tipo", "TVLists");
            transaction.addToBackStack(null);
            bottomNavigation.getMenu().findItem(R.id.TVButton).setChecked(true);
        }else if(id == R.id.SongsButton || id == R.id.imageButtonMusic) {
            bundle.putString("tipo", "MusicLists");
            transaction.addToBackStack(null);
            bottomNavigation.getMenu().findItem(R.id.SongsButton).setChecked(true);
        }else if(id == R.id.GamesButton || id == R.id.imageButtonGames) {
            bundle.putString("tipo", "GamesLists");
            transaction.addToBackStack(null);
            bottomNavigation.getMenu().findItem(R.id.GamesButton).setChecked(true);
        }else if (id == R.id.BooksButton || id == R.id.imageButtonBooks) {
            bundle.putString("tipo", "BooksLists");
            transaction.addToBackStack(null);
            bottomNavigation.getMenu().findItem(R.id.BooksButton).setChecked(true);
        }else if (!findViewById(R.id.MainFragment).toString().contains("main_fragment")){
            newFragment = new MainFragment();
            transaction.replace(R.id.MainFragment, newFragment);
        }

        Log.d("aqui",newFragment.toString());
        newFragment.setArguments(bundle);

        transaction.commit();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 1) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            //updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("SignInError", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    public void updateUI(GoogleSignInAccount account) {
        if (account == null) {
            Toast.makeText(this,R.string.Error_signing_in,Toast.LENGTH_LONG);
        }

    }

    public void openSettings(MenuItem item){
        Intent intent = new Intent (MainActivity.this, Settings.class);
        Fragment fragmentInFrame = (Fragment)getSupportFragmentManager().findFragmentById(R.id.MainFragment);

        if (fragmentInFrame instanceof ListsFragment) {
            intent.putExtra("tipoFragment", "Lists");
            intent.putExtra("tipolista",((ListsFragment) fragmentInFrame).tipolistas);
        }else if (fragmentInFrame instanceof MusicListDetailsFragment){
            intent.putExtra("tipoFragment", "ListDetails");
            intent.putExtra("tipolista",((MusicListDetailsFragment) fragmentInFrame).tipolistas);
            intent.putExtra("elemento",((MusicListDetailsFragment) fragmentInFrame).titulo);
        }else{
            intent.putExtra("tipoFragment", "Main");
        }
        startActivity(intent);
    }

    public void openDelete(MenuItem item) {
        Fragment fragmentInFrame = (Fragment)getSupportFragmentManager().findFragmentById(R.id.MainFragment);
        if (fragmentInFrame instanceof ListsFragment) {
            Intent intent = new Intent(MainActivity.this, RemoveAddListsActivity.class);
            intent.putExtra("use","delete");
            changeActivity(intent);
        }
    }

    public void openDeleteFromList(MenuItem item){

        //if (fragmentInFrame instanceof MusicListDetailsFragment){
        MusicListDetailsFragment f = (MusicListDetailsFragment)getSupportFragmentManager().findFragmentById(R.id.MainFragment);
        Log.d("DELETE","OPENED");
        Intent intent = new Intent(MainActivity.this, DeleteMusicListActivity.class);
        intent.putExtra("elemento",f.titulo);
        intent.putExtra("type",f.subtipolistas);
        changeActivity(intent);
        //}
    }

    public void addList(MenuItem item) {
        ListsFragment fragmentInFrame = (ListsFragment) getSupportFragmentManager().findFragmentById(R.id.MainFragment);
        Intent intent = new Intent(MainActivity.this, AddListActivity.class);
        intent.putExtra("tipo", fragmentInFrame.tipolistas);
        changeActivity(intent);

    }
    public void openEdit(MenuItem item) {

    }

    public void changeActivity(Intent intent){

        String tipo = bundle.getString("tipo");

        intent.putExtra("tipo", tipo);

        startActivity(intent);
    }

    @Override
    protected void onStop() {
        Fragment fragmentInFrame = (Fragment)getSupportFragmentManager().findFragmentById(R.id.MainFragment);
        if (fragmentInFrame instanceof MusicListDetailsFragment){
            if ( ((MusicListDetailsFragment) fragmentInFrame).subtipolistas.equals("song")) {
                ((MusicListDetailsFragment) fragmentInFrame).StopMusic();
            }
        }
        super.onStop();
    }

    public void searchItem(MenuItem item){
        String tipo = bundle.getString("tipo");
        if (tipo.equals("MusicLists")){
            Intent intent = new Intent(MainActivity.this, MusicSearchActivity.class);
            changeActivity(intent);
        }
        if (tipo.equals("GamesLists")){
            Intent intent = new Intent(MainActivity.this, GameSearchActivity.class);
            changeActivity(intent);
        }

    }


}
