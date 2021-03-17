package com.plorrios.medialists.Adapters.Games;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.plorrios.medialists.Objects.Games.Game;
import com.plorrios.medialists.Objects.Games.GamesList;
import com.plorrios.medialists.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

public class GamesListSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static GamesListSearchAdapter.IClickListener onClk;
    private ArrayList<Game> games;
    Context context;
    int AD_TYPE = 0;
    private static final int LIST_AD_DELTA = 4;
    private static final int CONTENT = 0;
    private static final int AD = 1;


    public static class GamesListViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;
        public AdView adView;

        public GamesListViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.textViewGameListItem);
            imageView = (ImageView) view.findViewById(R.id.imageViewGameListItem);
        }
    }

    public static class AdViewHolder extends RecyclerView.ViewHolder {
        public AdView adView;

        public AdViewHolder(View view) {
            super(view);
            adView = (AdView) view.findViewById(R.id.adView_adItem);
        }
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public GamesListSearchAdapter(GamesList myGamesList, GamesListSearchAdapter.IClickListener monClk, Context mcontext) {
        Log.d("Adapter","Adapter Started");
        games = new ArrayList<>();
        games.addAll(Arrays.asList(myGamesList.getGames()));
        onClk = monClk;
        context = mcontext;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.d("CREATED", "View Holder created");


        if (viewType == CONTENT) {
            View firstView;
            firstView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_list, parent, false);
            final GamesListSearchAdapter.GamesListViewHolder vh = new GamesListSearchAdapter.GamesListViewHolder(firstView);

            firstView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClk.onClickListener(vh.getAdapterPosition());
                }
            });

            return vh;

        } else {
            return new GamesListSearchAdapter.AdViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ad, parent, false));
        }
    }

    public Game getGame(int position){
        return games.get(getRealPosition(position));
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder mholder, int position) {


        if (getItemViewType(position) == CONTENT) {
            final GamesListSearchAdapter.GamesListViewHolder holder = (GamesListSearchAdapter.GamesListViewHolder) mholder;
            final Game game = games.get(getRealPosition(position));

            holder.textView.setText(context.getString(R.string.ItemTitle,game.getName()));
            Picasso.get().load(game.getBackgroundImage()).into(holder.imageView, new Callback() {
                @Override
                public void onSuccess() {
                    holder.imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);//Or ScaleType.FIT_CENTER
                }
                @Override
                public void onError(Exception e) {
                    //nothing for now
                    e.printStackTrace();
                }
            });
            holder.imageView.setAdjustViewBounds(true);
        } else {
            GamesListSearchAdapter.AdViewHolder holder = (GamesListSearchAdapter.AdViewHolder) mholder;
            AdRequest adRequest = new AdRequest.Builder().build();
            if (adRequest != null && holder.adView != null){
                holder.adView.loadAd(adRequest);
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        int additionalContent = 0;
        if (LIST_AD_DELTA > 0 && games.size() >= LIST_AD_DELTA) {
            additionalContent = games.size() / (LIST_AD_DELTA - 1);
        }
        return games.size() + additionalContent;
    }

    public void addGames(GamesList myGamesList) {

        games.addAll(Arrays.asList(myGamesList.getGames()));

    }

    public void clear(){
        games.clear();
    }

    public interface IClickListener { void onClickListener(int position); }

    @Override
    public int getItemViewType(int position) {
        position++;
        if (position % LIST_AD_DELTA == 0) {
            return AD;
        }
        return CONTENT;
    }

    private int getRealPosition(int position) {
        if (LIST_AD_DELTA == 0) {
            return position;
        } else {
            return position - position / LIST_AD_DELTA;
        }
    }

}
