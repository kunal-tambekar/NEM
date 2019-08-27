package zx.zample.nem.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import zx.zample.nem.R;
import zx.zample.nem.app.NemApp;
import zx.zample.nem.model.NewsModel;
import zx.zample.nem.util.Constants;


/**
 * Created by kunal on 4/12/17.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {


    private List<NewsModel> news;
    private Context mContext;

    public NewsAdapter(Context context,List<NewsModel> news) {
        this.mContext = context;
        this.news = news;
    }

    @Override
    public NewsAdapter.NewsViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_news, parent, false);
        return new NewsViewHolder(view);
    }


    @Override
    public void onBindViewHolder(NewsViewHolder holder, final int position) {
        holder.newsTitle.setText(news.get(position).getTitle());
        holder.newsContent.setText(news.get(position).getDescription());
//        holder.newsBy.setText(news.get(position).getAuthor()!=null?(news.get(position).getAuthor().toString().trim()!=""?news.get(position).getAuthor().toString():"NA"):"NA");
        holder.newsCard.setTag(news.get(position).getUrl());
        Glide.with(NemApp.getInstance()).load(news.get(position).getUrlToImage())
                .thumbnail(0.5f)
                .crossFade().placeholder(R.mipmap.ic_launcher)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivNewsPic);

    }


    @Override
    public int getItemCount() {
        return news.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder{

        CardView newsCard;
        TextView newsTitle;
        TextView newsContent;
        ImageView ivNewsPic;
//        TextView newsBy;


        public NewsViewHolder(View v) {
            super(v);
            newsCard = (CardView) v.findViewById(R.id.cv_news_card);
            newsTitle = (TextView) v.findViewById(R.id.txt_article_title);
            newsContent = (TextView) v.findViewById(R.id.txt_article_description);
            ivNewsPic = (ImageView) v.findViewById(R.id.iv_news_thumb);
//            newsBy = (TextView) v.findViewById(R.id.txt_article_by);
        }
    }

    public void setNews(List<NewsModel> news){
        this.news = news;
        notifyDataSetChanged();
    }

    public List<NewsModel> getNews() {
        return news;
    }
}
