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
import com.plorrios.medialists.Objects.Games.Publisher;
import com.plorrios.medialists.Objects.Games.PublishersList;
import com.plorrios.medialists.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

public class PublishersListSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static PublishersListSearchAdapter.IClickListener onClk;
    private ArrayList<Publisher> publishers;
    Context context;
    int AD_TYPE = 0;
    private static final int LIST_AD_DELTA = 4;
    private static final int CONTENT = 0;
    private static final int AD = 1;


    public static class PublishersListViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;
        public AdView adView;

        public PublishersListViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.textViewPublisherListItem);
            imageView = (ImageView) view.findViewById(R.id.imageViewPublisherListItem);
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
    public PublishersListSearchAdapter(PublishersList myPublishersList, PublishersListSearchAdapter.IClickListener monClk, Context mcontext) {
        Log.d("Adapter", "Adapter Started");
        publishers = new ArrayList<>();
        publishers.addAll(Arrays.asList(myPublishersList.getPublishers()));
        onClk = monClk;
        context = mcontext;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.d("CREATED", "View Holder created");


        if (viewType == CONTENT) {
            View firstView;
            firstView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_publisher_list, parent, false);
            final PublishersListSearchAdapter.PublishersListViewHolder vh = new PublishersListSearchAdapter.PublishersListViewHolder(firstView);

            firstView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClk.onClickListener(vh.getAdapterPosition());
                }
            });

            return vh;

        } else {
            return new PublishersListSearchAdapter.AdViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ad, parent, false));
        }
    }

    public Publisher getPublisher(int position) {
        return publishers.get(getRealPosition(position));
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder mholder, int position) {


        if (getItemViewType(position) == CONTENT) {
            final PublishersListSearchAdapter.PublishersListViewHolder holder = (PublishersListSearchAdapter.PublishersListViewHolder) mholder;
            final Publisher publisher = publishers.get(getRealPosition(position));

            holder.textView.setText(context.getString(R.string.ItemTitle, publisher.getName()));
            Picasso.get().load(publisher.getBackgroundImage()).into(holder.imageView, new Callback() {
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
            PublishersListSearchAdapter.AdViewHolder holder = (PublishersListSearchAdapter.AdViewHolder) mholder;
            AdRequest adRequest = new AdRequest.Builder().build();
            if (adRequest != null && holder.adView != null) {
                holder.adView.loadAd(adRequest);
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        int additionalContent = 0;
        if (LIST_AD_DELTA > 0 && publishers.size() >= LIST_AD_DELTA) {
            additionalContent = publishers.size() / (LIST_AD_DELTA - 1);
        }
        return publishers.size() + additionalContent;
    }

    public void addPublishers(PublishersList myPublishersList) {

        publishers.addAll(Arrays.asList(myPublishersList.getPublishers()));

    }

    public void clear() {
        publishers.clear();
    }

    public interface IClickListener {
        void onClickListener(int position);
    }

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

