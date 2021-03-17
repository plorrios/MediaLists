package com.plorrios.medialists.Adapters.Games;

import android.content.Context;
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
import com.plorrios.medialists.Objects.Games.Developer;
import com.plorrios.medialists.Objects.Games.DevelopersList;
import com.plorrios.medialists.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

public class DevelopersListSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static DevelopersListSearchAdapter.IClickListener onClk;
    private ArrayList<Developer> developers;
    Context context;
    int AD_TYPE = 0;
    private static final int LIST_AD_DELTA = 4;
    private static final int CONTENT = 0;
    private static final int AD = 1;


    public static class DevelopersListViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;
        public AdView adView;

        public DevelopersListViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.textViewDeveloperListItem);
            imageView = (ImageView) view.findViewById(R.id.imageViewDeveloperListItem);
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
    public DevelopersListSearchAdapter(DevelopersList myDevelopersList, DevelopersListSearchAdapter.IClickListener monClk, Context mcontext) {
        Log.d("Adapter","Adapter Started");
        developers = new ArrayList<>();
        developers.addAll(Arrays.asList(myDevelopersList.getDevs()));
        onClk = monClk;
        context = mcontext;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.d("CREATED", "View Holder created");


        if (viewType == CONTENT) {
            View firstView;
            firstView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_developer_list, parent, false);
            final DevelopersListSearchAdapter.DevelopersListViewHolder vh = new DevelopersListSearchAdapter.DevelopersListViewHolder(firstView);

            firstView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClk.onClickListener(vh.getAdapterPosition());
                }
            });

            return vh;

        } else {
            return new DevelopersListSearchAdapter.AdViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ad, parent, false));
        }
    }

    public Developer getDeveloper(int position){
        return developers.get(getRealPosition(position));
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder mholder, int position) {


        if (getItemViewType(position) == CONTENT) {
            final DevelopersListSearchAdapter.DevelopersListViewHolder holder = (DevelopersListSearchAdapter.DevelopersListViewHolder) mholder;
            final Developer developer = developers.get(getRealPosition(position));

            holder.textView.setText(context.getString(R.string.ItemTitle,developer.getName()));
            Picasso.get().load(developer.getBackgroundImage()).into(holder.imageView, new Callback() {
                @Override
                public void onSuccess() {
                    holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);//Or ScaleType.FIT_CENTER
                }
                @Override
                public void onError(Exception e) {
                    //nothing for now
                    e.printStackTrace();
                }
            });
        } else {
            DevelopersListSearchAdapter.AdViewHolder holder = (DevelopersListSearchAdapter.AdViewHolder) mholder;
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
        if (LIST_AD_DELTA > 0 && developers.size() >= LIST_AD_DELTA) {
            additionalContent = developers.size() / (LIST_AD_DELTA - 1);
        }
        return developers.size() + additionalContent;
    }

    public void addDevelopers(DevelopersList myDevelopersList) {

        developers.addAll(Arrays.asList(myDevelopersList.getDevs()));

    }

    public void clear(){
        developers.clear();
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
