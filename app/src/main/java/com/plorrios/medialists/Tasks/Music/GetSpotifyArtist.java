package com.plorrios.medialists.Tasks.Music;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.plorrios.medialists.Extras.Base64;
import com.plorrios.medialists.Main.MusicListDetailsFragment;
import com.plorrios.medialists.Music.ArtistDetailsActivity;
import com.plorrios.medialists.Music.DeleteMusicListActivity;
import com.plorrios.medialists.Objects.Music.Artist;
import com.plorrios.medialists.Objects.Music.Spotify_acces_token;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class GetSpotifyArtist extends AsyncTask<Void, Void, Artist> {

    WeakReference<ArtistDetailsActivity> PetitionsWeakReference;
    WeakReference<MusicListDetailsFragment> PetitionsWeakReference2;
    WeakReference<DeleteMusicListActivity> PetitionsWeakReference3;
    String idItem;
    int pageLocal = 1;
    Context context;
    String token = null;
    boolean islast;

    public GetSpotifyArtist(ArtistDetailsActivity activity, String id, int page, Context mcontext) {
        idItem = id;
        this.PetitionsWeakReference = new WeakReference<ArtistDetailsActivity>(activity);
        pageLocal = page;
        context = mcontext;
    }

    public GetSpotifyArtist(MusicListDetailsFragment activity, String id, int page, Context mcontext, boolean mislast) {
        idItem = id;
        this.PetitionsWeakReference2 = new WeakReference<MusicListDetailsFragment>(activity);
        pageLocal = page;
        context = mcontext;
        islast = mislast;
    }

    public GetSpotifyArtist(DeleteMusicListActivity activity, String id, int page, Context mcontext, boolean mislast) {
        idItem = id;
        this.PetitionsWeakReference3 = new WeakReference<DeleteMusicListActivity>(activity);
        pageLocal = page;
        context = mcontext;
        islast = mislast;
    }

    // Para usar en otro metodo crear otra WeakReference para la nueva actividad y un nuevo constructor.

    @Override
    protected Artist doInBackground(java.lang.Void... voids) {
        //return getGame();
        Artist artist = getArtist(false);
        return artist;
    }

    private Artist getArtist(boolean asktoken) {


        final SharedPreferences obtainedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        token = obtainedPreferences.getString("SpotifyToken", null);

        //Log.d("SAVED TOKEN IS NULL", String.valueOf(token == null));

        if (token == null || asktoken) {
            return getResult();
        } else {
            return getToken();
        }
    }

    private Artist getToken() {

        final SharedPreferences obtainedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("accounts.spotify.com");
        builder.appendPath("api");
        builder.appendPath("token");

        String auth = "a85217df9f3245918e7928c4f97f8627:7411c46b7c7c4a48a0193c58e0bbff02";
        String authencoded = Base64.encodeBytes(auth.getBytes());

        try {

            URL url = new URL(builder.build().toString());
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setRequestProperty("Authorization", "Basic " + authencoded);
            connection.getOutputStream().write(("grant_type=client_credentials").getBytes());

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                Gson gsonfile = new Gson();
                //Log.d("",gsonfile.toString());
                Spotify_acces_token tokenObject = gsonfile.fromJson(reader, Spotify_acces_token.class);
                token = tokenObject.getAccess_token();
                reader.close();
                //Log.d("TOKEN", token);
                SharedPreferences.Editor editor = obtainedPreferences.edit();
                editor.putString("SpotifyToken", token);
                editor.apply();
            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                Log.d("Answer", reader.toString());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d("error", "error1");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("error", "error2");
        }
        /*if (music != null) {
            Log.d("search", Integer.toString(music.GetCount()));
        }*/

        return getResult();
    }


    private Artist getResult() {

        Artist artist = null;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("api.spotify.com");
        builder.appendPath("v1");
        builder.appendPath("artists");
        builder.appendPath(idItem);
        try {

            URL url = new URL(builder.build().toString());
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setRequestProperty("Authorization", "Bearer " + token);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                Gson gsonfile = new Gson();
                //Log.d("GSON",gsonfile.toString());
                artist = gsonfile.fromJson(reader, Artist.class);
                reader.close();
                //Log.d("RESULT", song.getId());
            } else {
                return getToken();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d("error", "error1");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("error", "error2");
        }
        return artist;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Artist artist) {
        if (PetitionsWeakReference != null) {
            PetitionsWeakReference.get().AddInfo(artist);
        } else if (PetitionsWeakReference2 != null) {
            PetitionsWeakReference2.get().AddInfo(artist, islast);
        } else if (PetitionsWeakReference3 != null) {
            PetitionsWeakReference3.get().AddInfo(artist, islast);
        }
        super.onPostExecute(artist);
    }
}
