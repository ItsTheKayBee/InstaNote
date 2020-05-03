package com.example.instanote;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class PinnedAdapter extends RecyclerView.Adapter<PinnedAdapter.PinnedViewHolder> {

    private ArrayList<String> cardTitleData;
    private ArrayList<String> cardTextData;
    private ArrayList<String> cardLinkData;
    private CardClickListener mClickListener;
    private ArrayList<Integer> selectedList;
    private CardLongClickListener mLongClickListener;

    PinnedAdapter(Context ctx, ArrayList<String> title, ArrayList<String> link, ArrayList<String> text) {
        this.inflater = LayoutInflater.from(ctx);
        this.cardTitleData = title;
        this.cardTextData = text;
        this.cardLinkData = link;
        selectedList = new ArrayList<>();
    }

    private LayoutInflater inflater;

    void setLongClickListener(CardLongClickListener mLongClickListener) {
        this.mLongClickListener = mLongClickListener;
    }

    @NonNull
    @Override
    public PinnedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.pinned_card, parent, false);
        return new PinnedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PinnedViewHolder holder, int position) {
        String title = cardTitleData.get(position);
        String text = cardTextData.get(position);
        String link = cardLinkData.get(position);
        holder.cardTitle.setText(title);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.cardText.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.cardText.setText(Html.fromHtml(text));
        }

        holder.cardLink.setText(link);
        MaterialCardView materialCardView = (MaterialCardView) holder.itemView;

        if (getSelectedList().contains(position)) {
            materialCardView.setChecked(true);
        } else {
            materialCardView.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return cardTitleData.size();
    }

    String getCardText(int id) {
        return cardTextData.get(id);
    }

    String getCardTitle(int id) {
        return cardTitleData.get(id);
    }

    String getCardLink(int id) {
        return cardLinkData.get(id);
    }

    void setClickListener(CardClickListener cardClickListener) {
        this.mClickListener = cardClickListener;
    }

    private ArrayList<Integer> getSelectedList() {
        return selectedList;
    }

    void setSelectedList(ArrayList<Integer> selectedList) {
        this.selectedList = selectedList;
    }

    public interface CardClickListener {
        void onCardClick(View view, int position);
    }

    public interface CardLongClickListener {
        void onCardLongClick(View view, int position);
    }

    public class PinnedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView cardTitle;
        TextView cardText;
        TextView cardLink;

        PinnedViewHolder(@NonNull View itemView) {
            super(itemView);
            cardTitle = itemView.findViewById(R.id.cardTitle);
            cardText = itemView.findViewById(R.id.cardText);
            cardLink = itemView.findViewById(R.id.cardWeb);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onCardClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            if (mLongClickListener != null)
                mLongClickListener.onCardLongClick(view, getAdapterPosition());
            return true;
        }
    }
}