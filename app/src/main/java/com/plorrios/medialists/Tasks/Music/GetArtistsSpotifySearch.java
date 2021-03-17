package com.plorrios.medialists.Tasks.Music;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.plorrios.medialists.Extras.Base64;
import com.plorrios.medialists.Music.MusicSearchActivity;
import com.plorrios.medialists.Objects.Music.ArtistList;
import com.plorrios.medialists.Objects.Music.Spotify_acces_token;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class GetArtistsSpotifySearch extends AsyncTask<Void, Void, ArtistList> {

    WeakReference<MusicSearchActivity> PetitionsWeakReference;
    String busquedaLocal;
    int pageLocal = 1;
    Context context;
    String token = null;

    public GetArtistsSpotifySearch(MusicSearchActivity activity, String busqueda, int page, Context mcontext) {
        busquedaLocal = busqueda;
        this.PetitionsWeakReference = new WeakReference<MusicSearchActivity>(activity);
        pageLocal = page;
        context = mcontext;
    }

    // Para usar en otro metodo crear otra WeakReference para la nueva actividad y un nuevo constructor.

    @Override
    protected ArtistList doInBackground(Void... voids) {
        //return getGame();
        ArtistList Artist = getArtistList(false);
        return Artist;
    }

    private ArtistList getArtistList(boolean asktoken) {


        final SharedPreferences obtainedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        token = obtainedPreferences.getString("SpotifyToken", null);

        //Log.d("SAVED TOKEN IS NULL", String.valueOf(token == null));

        if (token == null || asktoken) {
            return getResult();
        } else {
            return getToken();
        }
    }

    private ArtistList getToken() {
        final SharedPreferences obtainedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        ArtistList Artist = null;
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
            } else{
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                Log.d("Answer",reader.toString());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d("error", "error1");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("error", "error2");
        }
        /*if (Artist != null) {
            Log.d("search", Integer.toString(Artist.GetCount()));
        }*/

        return getResult();
    }


    private ArtistList getResult() {

        ArtistList Artist = null;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("api.spotify.com");
        builder.appendPath("v1");

        builder.appendEncodedPath("search?q=" + busquedaLocal + "&type=artist");
        try {

            URL url = new URL(builder.build().toString());
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setRequestProperty("Authorization", "Bearer " + token);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                Gson gsonfile = new Gson();
                Log.d("GSON",gsonfile.toString());
                Artist = gsonfile.fromJson(reader, ArtistList.class);
                reader.close();
                //Log.d("First Result Name", Artist.GetArtist().getItems()[0].getName());
                //Log.d("First Result ID", Artist.GetArtist().getItems()[0].getId());
                //Log.d("SEARCH COUNT", Artist.GetArtist().getItems()[1].getName());
            } else{
                return getToken();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d("error", "error1");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("error", "error2");
        }
        if (Artist != null) {
            Log.d("search", Integer.toString(Artist.GetCount()));
        }
        return Artist;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(ArtistList Artist) {
        PetitionsWeakReference.get().AddArtists(Artist);
        super.onPostExecute(Artist);
    }
}
