package com.oliverst.russianstamps.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oliverst.russianstamps.R;
import com.oliverst.russianstamps.data.Stamp;

import java.util.ArrayList;
import java.util.List;

public class StampAdapter extends RecyclerView.Adapter<StampAdapter.StampsViewHolder> {
    private List<Stamp> stamps;
    private OnStampClickListener onStampClickListener;
    private OnReachEndListener onReachEndListener;


    public StampAdapter() {
        stamps = new ArrayList<>();
    }

    public void setStamps(List<Stamp> stamps) {
        this.stamps = stamps;
        notifyDataSetChanged();
    }

    public void addStamps(List<Stamp> stamps) {
        this.stamps.addAll(stamps);
        notifyDataSetChanged();
    }

    public void clearStamps() {
        this.stamps.clear();
        notifyDataSetChanged();
    }

    public List<Stamp> getStamps() {
        return stamps;
    }

    //----Слушатель на клик
    public interface OnStampClickListener {
        void onStampClick(int position);
    }

    public void setOnStampClickListener(OnStampClickListener onStampClickListener) {
        this.onStampClickListener = onStampClickListener;
    }

    //-----Слушатель на достижение конца списка
    public interface OnReachEndListener {
        void onReachEnd();
    }

    public void setOnReachEndListener(OnReachEndListener onReachEndListener) {
        this.onReachEndListener = onReachEndListener;
    }

    @NonNull
    @Override
    public StampsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stamp_item, parent, false);
        return new StampsViewHolder(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull StampsViewHolder holder, int position) {
        if (onReachEndListener != null && position == stamps.size()-10 && stamps.size() >= 100){
                onReachEndListener.onReachEnd();
        }
            Stamp stamp = stamps.get(position);
        holder.textViewNum.setText(Integer.toString(position+1) +":");
        holder.textViewReleaseYear.setText(Integer.toString(stamp.getYear()));
        holder.textViewName.setText(stamp.getName());
        String catalogNumbers = String.format("ИТС:%s СК:%s Михель:%s", stamp.getCatalogNumberITC(), stamp.getCatalogNumberSK(), stamp.getCatalogNumberMich());
       // holder.textViewCatalogNumbers.setText(catalogNumbers);
        holder.textViewQuantity.setText(stamp.getQuantity());
    }

    @Override
    public int getItemCount() {
        return stamps.size();
    }

    class StampsViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewNum;
        private TextView textViewReleaseYear;
        private TextView textViewName;
        private TextView textViewQuantity;

        public StampsViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNum = itemView.findViewById(R.id.textViewNum);
            textViewReleaseYear = itemView.findViewById(R.id.textViewYear);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onStampClickListener != null) {
                        onStampClickListener.onStampClick(getAdapterPosition());
                    }
                }
            });

        }
    }

}
