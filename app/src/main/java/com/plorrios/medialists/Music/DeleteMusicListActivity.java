package com.plorrios.medialists.Music;

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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.plorrios.medialists.Adapters.Music.RemoveAlbumsAdapter;
import com.plorrios.medialists.Adapters.Music.RemoveArtistsAdapter;
import com.plorrios.medialists.Adapters.Music.RemoveMusicAdapter;
import com.plorrios.medialists.Objects.Music.Album;
import com.plorrios.medialists.Objects.Music.Artist;
import com.plorrios.medialists.Objects.Music.Song;
import com.plorrios.medialists.R;
import com.plorrios.medialists.Tasks.Music.GetSpotifyAlbum;
import com.plorrios.medialists.Tasks.Music.GetSpotifyArtist;
import com.plorrios.medialists.Tasks.Music.GetSpotifyTrack;

import java.util.ArrayList;
import java.util.Map;

public class DeleteMusicListActivity extends AppCompatActivity {

    Context context;
    ProgressBar loading;
    FirebaseFirestore db;
    String personEmail;
    String tipolistas;
    String titulo;
    RemoveMusicAdapter musicAdapter;
    RemoveAlbumsAdapter albumAdapter;
    RemoveArtistsAdapter artistAdapter;
    Toolbar toolbar;
    String subtipolistas;


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

        toolbar = findViewById(R.id.AlbumDetailsToolbar);
        if (style.equals(res.getString(R.string.Light))) {
            toolbar.setBackground(getDrawable(R.drawable.bottom_navbar_drawable_light));
        }else {
            toolbar.setBackground(getDrawable(R.drawable.toolbar_drawable_dark));
        }
        toolbar.inflateMenu(R.menu.accept_cancel_menu);

        context = this;
        loading = (ProgressBar) findViewById(R.id.RemoveListsProgressBar);
        loading.setVisibility(View.VISIBLE);

        subtipolistas = getIntent().getStringExtra("type");

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        personEmail = account.getEmail();
        tipolistas = getIntent().getStringExtra("tipo");
        titulo = getIntent().getStringExtra("elemento");
        db = FirebaseFirestore.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("Users").document(personEmail).collection(tipolistas).document(titulo);

        userRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Log.d("DETAILSUCCESS", document.getId() + " => " + document.getData());
                            Map<String,Object> map = document.getData();
                            ArrayList<String> ids= (ArrayList<String>)map.get("id");
                            if (ids != null) {
                                for (int i = 0; i < ids.size(); i++) {
                                    if (i == ids.size() - 1) {
                                        Log.d("POSITION",ids.get(i));

                                        GetInfo(ids.get(i), true);
                                    } else {
                                        Log.d("POSITION",ids.get(i));
                                        GetInfo(ids.get(i), false);
                                    }
                                }
                            }
                        } else {
                            Log.d("ERROR", "Error getting documents: ", task.getException());
                        }
                    }
                });

        RecyclerView recyclerView = findViewById(R.id.recyclerViewRemoveMusic);
        if (subtipolistas.equals("song")) {
            ArrayList<Song> songs =  new ArrayList<>();
            musicAdapter = new RemoveMusicAdapter(songs, this);
            recyclerView.setAdapter(musicAdapter);
        } else if (subtipolistas.equals("album")) {
            ArrayList<Album> albums =  new ArrayList<>();
            albumAdapter = new RemoveAlbumsAdapter(albums, this);
            recyclerView.setAdapter(albumAdapter);
        } else{
            ArrayList<Artist> artists =  new ArrayList<>();
            artistAdapter = new RemoveArtistsAdapter(artists, this);
            recyclerView.setAdapter(artistAdapter);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.setLayoutManager(linearLayoutManager);
    }

    public void GetInfo(String id, boolean isLast){
        if (subtipolistas.equals("song")) {
            GetSpotifyTrack task = new GetSpotifyTrack(this, id, 0, this, isLast);
            task.execute();
        }else if (subtipolistas.equals("album")){
            GetSpotifyAlbum task = new GetSpotifyAlbum(this, id, 0, this, isLast);
            task.execute();
        }else{
            GetSpotifyArtist task = new GetSpotifyArtist(this, id, 0, this, isLast);
            task.execute();
        }
    }

    public void AddInfo(Song song, Boolean last){
        musicAdapter.addSong(song);
        if (last){
            musicAdapter.notifyDataSetChanged();
            Log.d("SONG COUNT",String.valueOf(musicAdapter.getItemCount()));
            loading.setVisibility(View.INVISIBLE);
        }
    }

    public void AddInfo(Album album, Boolean last){
        albumAdapter.addAlbum(album);
        Log.d("ALBUM",album.getName());
        if (last){
            albumAdapter.notifyDataSetChanged();
            Log.d("ALBUM COUNT",String.valueOf(albumAdapter.getItemCount()));
            loading.setVisibility(View.INVISIBLE);
        }
    }

    public void AddInfo(Artist artist, Boolean last){
        artistAdapter.addArtist(artist);
        if (last){
            artistAdapter.notifyDataSetChanged();
            Log.d("ARTIST COUNT",String.valueOf(artistAdapter.getItemCount()));
            loading.setVisibility(View.INVISIBLE);
        }
    }




    public void cancelClick(MenuItem item) {
        finish();
    }

    public void acceptClick(MenuItem item) {
        if (musicAdapter == null && albumAdapter == null && artistAdapter == null) {
            this.finish();
        }
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<String> ids;
        if (subtipolistas.equals("song")) {
            ids = musicAdapter.listsToRemove();
        } else if (subtipolistas.equals("album")) {
            ids = albumAdapter.listsToRemove();
        } else{
            ids = artistAdapter.listsToRemove();
        }
        for (String id : ids) {
            DocumentReference itemRef = db.collection("Users").document(personEmail).collection(tipolistas).document(titulo);
            itemRef.update("id", FieldValue.arrayRemove(id));
            finish();
        }

    }

}
