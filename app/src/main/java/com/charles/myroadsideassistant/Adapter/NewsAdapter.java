package com.charles.myroadsideassistant.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.charles.myroadsideassistant.Components.NewsData;
import com.charles.myroadsideassistant.Components.WebpageActivity;
import com.charles.myroadsideassistant.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    List<NewsData> newsDataList;

    public NewsAdapter(List<NewsData> newsDataList) {
        this.newsDataList= newsDataList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_row, parent, false);
        return new NewsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsData newsData= newsDataList.get(position);
        holder.title.setText(newsData.getTitle());
        holder.description.setText(newsData.getDescription());
        holder.url= newsData.getUrl();
        Picasso.get().load(newsData.getUrlToImage()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return newsDataList.size();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        ImageView imageView;
        LinearLayout parent;
        String url;
        public NewsViewHolder(@NonNull final View itemView) {
            super(itemView);
            parent= itemView.findViewById(R.id.parent);
            title= itemView.findViewById(R.id.newstile);
            description= itemView.findViewById(R.id.description);
            imageView= itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent= new Intent(itemView.getContext(), WebpageActivity.class);
                    intent.putExtra("url", url);
                    itemView.getContext().startActivity(intent);
                }
            });

        }
    }
}
