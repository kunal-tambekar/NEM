package zx.zample.nem.adapter;

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
import zx.zample.nem.model.MovieModel;
import zx.zample.nem.util.Constants;


/**
 * Created by kunal on 4/12/17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {


    private List<MovieModel> movies;
    private final int rowLayout;

    public MoviesAdapter(List<MovieModel> movies) {
        this.movies = movies;
        this.rowLayout = R.layout.list_item_movie;
    }

    @Override
    public MoviesAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new MovieViewHolder(view);
    }


    @Override
    public void onBindViewHolder(MovieViewHolder holder, final int position) {
        holder.movieTitle.setText(movies.get(position).getTitle());
        holder.movieDescription.setText(movies.get(position).getOverview());
        holder.rating.setText(movies.get(position).getVote_average().toString());
        Glide.with(NemApp.getInstance()).load(Constants.URL_MOVIE_THUMB_BASE+movies.get(position).getPosterPath())
                .thumbnail(0.5f)
                .crossFade().placeholder(R.mipmap.ic_launcher)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivThumb);

    }


    @Override
    public int getItemCount() {
        return movies.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder{

        LinearLayout moviesLayout;
        TextView movieTitle;
        TextView movieDescription;
        TextView rating;
        ImageView ivThumb;


        public MovieViewHolder(View v) {
            super(v);
            moviesLayout = (LinearLayout) v.findViewById(R.id.movies_layout);
            movieTitle = (TextView) v.findViewById(R.id.title);
            movieDescription = (TextView) v.findViewById(R.id.description);
            rating = (TextView) v.findViewById(R.id.rating);
            ivThumb = (ImageView) v.findViewById(R.id.iv_movie_thumb);
        }
    }

    public void setMovies(List<MovieModel> movies){
        this.movies = movies;
        notifyDataSetChanged();
    }

    public List<MovieModel> getMovies() {
        return movies;
    }
}
