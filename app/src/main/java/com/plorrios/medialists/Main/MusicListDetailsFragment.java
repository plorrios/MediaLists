package com.plorrios.medialists.Main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.plorrios.medialists.Adapters.Music.AlbumListSearchAdapter;
import com.plorrios.medialists.Adapters.Music.ArtistListSearchAdapter;
import com.plorrios.medialists.Adapters.Music.MusicListAdapter;
import com.plorrios.medialists.Adapters.Music.MusicListSearchAdapter;
import com.plorrios.medialists.Music.AlbumDetailsActivity;
import com.plorrios.medialists.Objects.Music.Album;
import com.plorrios.medialists.Objects.Music.Artist;
import com.plorrios.medialists.Objects.Music.Song;
import com.plorrios.medialists.R;
import com.plorrios.medialists.Music.SongDetailsActivity;
import com.plorrios.medialists.Tasks.Music.GetSpotifyAlbum;
import com.plorrios.medialists.Tasks.Music.GetSpotifyArtist;
import com.plorrios.medialists.Tasks.Music.GetSpotifyTrack;

import java.util.ArrayList;
import java.util.Map;

import static androidx.core.content.ContextCompat.getDrawable;

public class MusicListDetailsFragment extends Fragment {

    public String tipolistas;
    public String subtipolistas;
    public String titulo;
    private MusicListAdapter musicAdapter;
    private AlbumListSearchAdapter albumAdapter;
    private ArtistListSearchAdapter artistAdapter;
    ProgressBar progressBar;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.music_list_details_fragment, container, false);

        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) view.findViewById(R.id.ListDetailsToolbar);
        toolbar.inflateMenu(R.menu.menu);
        toolbar.setTitle(titulo);

        Resources res = getResources();
        SharedPreferences obtainedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String style = obtainedPreferences.getString("AppliedStyle",res.getString(R.string.Light));
        if (style.equals(res.getString(R.string.Light))) {
            toolbar.setBackground(getDrawable(getContext(),R.drawable.bottom_navbar_drawable_light));
        }else {
            toolbar.setBackground(getDrawable(getContext(),R.drawable.toolbar_drawable_dark));
        }

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewListDetails);

        progressBar = view.findViewById(R.id.ListDetailsProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        if (subtipolistas.equals("song")) {
            ArrayList<Song> songs =  new ArrayList<>();
            musicAdapter = new MusicListAdapter(songs, new MusicListSearchAdapter.IClickListener() {
                @Override
                public void onClickListener(int position) {
                    if (musicAdapter.getSong(position) == null) {
                        Toast.makeText(getContext(), getText(R.string.SongNotFount), Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent(getActivity(), SongDetailsActivity.class);
                        intent.putExtra("id", musicAdapter.getSong(position).getId());
                        startActivity(intent);
                        StopMusic();
                        //Toast.makeText(context, adapter.getSong(position).getName(), Toast.LENGTH_SHORT).show();
                        //Log.d("",adapter.getSong(position).getId());
                    }
                }
            }, getContext());
            recyclerView.setAdapter(musicAdapter);
        }else if (subtipolistas.equals("album")){
            final ArrayList<Album> albumList =  new ArrayList<>();
            albumAdapter = new AlbumListSearchAdapter(albumList, new AlbumListSearchAdapter.IClickListener() {
                @Override
                public void onClickListener(int position) {
                    if (albumAdapter.getAlbum(position) == null) {
                        Toast.makeText(getContext(), getText(R.string.SongNotFount), Toast.LENGTH_LONG).show();
                    }else {
                        Intent intent = new Intent(getActivity(), AlbumDetailsActivity.class);
                        intent.putExtra("id", albumAdapter.getAlbum(position).getId());
                        startActivity(intent);
                        //Log.d("",musicAdapter.getSong(position).getId());
                    }
                }
            },getContext());
            recyclerView.setAdapter(albumAdapter);
        }else{
            ArrayList<Artist> artistList =  new ArrayList<>();
            artistAdapter = new ArtistListSearchAdapter(artistList, new ArtistListSearchAdapter.IClickListener() {
                @Override
                public void onClickListener(int position) {
                    if (artistAdapter.getArtist(position) == null) {
                        Toast.makeText(getContext(), getText(R.string.SongNotFount), Toast.LENGTH_LONG).show();
                    }else {
                        Intent intent = new Intent(getActivity(), AlbumDetailsActivity.class);
                        intent.putExtra("id", artistAdapter.getArtist(position).getId());
                        startActivity(intent);
                        //Log.d("",musicAdapter.getSong(position).getId());
                    }
                }
            },getContext());
            recyclerView.setAdapter(artistAdapter);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(linearLayoutManager);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        final String personEmail = account.getEmail();

        Bundle bundle = this.getArguments();
        titulo = bundle.getString("elemento");


        tipolistas = bundle.getString("tipo");
        subtipolistas = bundle.getString("type");


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
                                        GetInfo(ids.get(i), true);
                                    } else {
                                        GetInfo(ids.get(i), false);
                                    }
                                }
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            Log.d("ERROR", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    public void GetInfo(String id, boolean isLast){
        if (subtipolistas.equals("song")) {
            GetSpotifyTrack task = new GetSpotifyTrack(this, id, 0, getContext(), isLast);
            task.execute();
        }else if (subtipolistas.equals("album")){
            GetSpotifyAlbum task = new GetSpotifyAlbum(this, id, 0, getContext(), isLast);
            task.execute();
        }else{
            GetSpotifyArtist task = new GetSpotifyArtist(this, id, 0, getContext(), isLast);
            task.execute();
        }

    }


    public void AddInfo(Song song, Boolean last){
        musicAdapter.addSong(song);
        if (last){
            musicAdapter.notifyDataSetChanged();
            Log.d("SONG COUNT",String.valueOf(musicAdapter.getItemCount()));
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void AddInfo(Album album, Boolean last){
        albumAdapter.addAlbum(album);
        Log.d("ALBUM",album.getName());
        if (last){
            albumAdapter.notifyDataSetChanged();
            Log.d("ALBUM COUNT",String.valueOf(albumAdapter.getItemCount()));
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void AddInfo(Artist artist, Boolean last){
        artistAdapter.addArtist(artist);
        if (last){
            artistAdapter.notifyDataSetChanged();
            Log.d("ARTIST COUNT",String.valueOf(artistAdapter.getItemCount()));
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void StopMusic() {
            musicAdapter.stopPlaying();
    }

}
