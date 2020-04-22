package com.example.instanote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PinnedAdapter extends RecyclerView.Adapter<PinnedAdapter.PinnedViewHolder> {

    private String[] cardTitleData;
    private String[] cardTextData;
    private CardClickListener mClickListener;
    private LayoutInflater inflater;

    PinnedAdapter(Context ctx, String[] title, String[] text) {
        this.inflater = LayoutInflater.from(ctx);
        this.cardTitleData = title;
        this.cardTextData = text;
    }

    @NonNull
    @Override
    public PinnedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.pinned_card, parent, false);
        return new PinnedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PinnedViewHolder holder, int position) {
        String title = cardTitleData[position];
        String text = cardTextData[position];
        holder.cardTitle.setText(title);
        holder.cardText.setText(text);
    }

    @Override
    public int getItemCount() {
        return cardTitleData.length;
    }

    String getCardText(int id) {
        return cardTextData[id];
    }

    String getCardTitle(int id) {
        return cardTitleData[id];
    }

    void setClickListener(CardClickListener cardClickListener) {
        this.mClickListener = cardClickListener;
    }

    public interface CardClickListener {
        void onCardClick(View view, int position);
    }

    public class PinnedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView cardTitle;
        TextView cardText;

        PinnedViewHolder(@NonNull View itemView) {
            super(itemView);
            cardTitle = itemView.findViewById(R.id.cardTitle);
            cardText = itemView.findViewById(R.id.cardText);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onCardClick(view, getAdapterPosition());
        }
    }
}
