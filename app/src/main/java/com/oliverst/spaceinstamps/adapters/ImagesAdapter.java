package com.oliverst.spaceinstamps.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oliverst.spaceinstamps.R;
import com.oliverst.spaceinstamps.data.ImageUrl;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder> {
    private List<ImageUrl> imagesUrl;
    OnImageClickListener onImageClickListener;

    public ImagesAdapter() {
        imagesUrl = new ArrayList<>();
    }

    public void setImagesUrl(List<ImageUrl> imagesUrl) {
        this.imagesUrl = imagesUrl;
        notifyDataSetChanged();
    }

    public List<ImageUrl> getImagesUrl() {
        return imagesUrl;
    }

    //Метод очистки recyclerView
    public void clear() {
        this.imagesUrl.clear();
        notifyDataSetChanged();  //всегда оповещаем адаптер об изменении массива
    }

    //----Слушатель на клик
    public interface OnImageClickListener {
        void onImageClick(int position);
    }

    public void setOnImageClickListener(OnImageClickListener onImageClickListener) {
        this.onImageClickListener = onImageClickListener;
    }

    @NonNull
    @Override
    public ImagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.images_item, parent, false);
        return new ImagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesViewHolder holder, int position) {
        holder.textViewNumber.setText(Integer.toString(position + 1) + ":");
        ImageUrl imageUrl = imagesUrl.get(position);
        String url = imageUrl.getUrl();

        Picasso.get().load(url).placeholder(R.drawable.placeholder).into(holder.imageViewBigStamp);
    }

    @Override
    public int getItemCount() {
        return imagesUrl.size();
    }

    public class ImagesViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewBigStamp;
        private TextView textViewNumber;

        public ImagesViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewBigStamp = itemView.findViewById(R.id.imageViewBigStamp);
            textViewNumber = itemView.findViewById(R.id.textViewNumber);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onImageClickListener != null) {
                        onImageClickListener.onImageClick(getAdapterPosition());
                    }
                }
            });

        }
    }
}



