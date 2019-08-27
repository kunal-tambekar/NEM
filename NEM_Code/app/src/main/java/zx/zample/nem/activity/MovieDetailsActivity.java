package zx.zample.nem.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import zx.zample.nem.R;
import zx.zample.nem.app.NemApp;
import zx.zample.nem.model.MovieModel;
import zx.zample.nem.util.Constants;
import zx.zample.nem.util.DateUtil;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String TAG = "MOVIE DETAILS SCREEN";
    private MovieModel mMovie;
    private ImageView ivThumb;
    private ImageView ivBackdrop;
    private TextView tvOverview;
    private TextView tvReleaseDate;
    private TextView tvOriginalTitle;
    private TextView tvTitle;
    private TextView tvVoteAverage;
    private FloatingActionButton fabFav;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private boolean isInWatchList = false;
    private ValueEventListener fbdbListener =new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            MovieModel movie = dataSnapshot.child(mMovie.getId().toString()).getValue(MovieModel.class);
            if (movie != null) {
                isInWatchList=true;
                Toast.makeText(MovieDetailsActivity.this, movie.getTitle() + " is in your Watchlist", Toast.LENGTH_SHORT).show();
            }else {
                isInWatchList = false;
            }
        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read valu
            Toast.makeText(MovieDetailsActivity.this, "Sign in with Google to save movie to Watchlist", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Failed to read app title value.", error.toException());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        mMovie = (MovieModel) getIntent().getExtras().get("MOVIE_MODEL");
        setTitle(mMovie.getTitle());

        ivThumb = (ImageView) findViewById(R.id.iv_movie_thumb);
        ivBackdrop = (ImageView) findViewById(R.id.iv_movie_backdrop);
        tvTitle = (TextView) findViewById(R.id.tv_movie_title);
        tvOverview = (TextView) findViewById(R.id.tv_movie_overview);
        tvOriginalTitle = (TextView) findViewById(R.id.tv_original_name);
        tvReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        tvVoteAverage = (TextView) findViewById(R.id.tv_movie_rating);
        fabFav = (FloatingActionButton) findViewById(R.id.fab_favorite);

        tvTitle.setText(mMovie.getTitle());
        tvOriginalTitle.setText(mMovie.getOriginal_title());
        tvOverview.setText(mMovie.getOverview());
        tvReleaseDate.setText(DateUtil.convertDate(mMovie.getRelease_date(), "yyyy-MM-DD", "DD MMM yyyy"));
        tvVoteAverage.setText(mMovie.getVote_average().toString());

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("favorites"+NemApp.getInstance().getUsername());
        mFirebaseDatabase.addValueEventListener(fbdbListener);

        Glide.with(NemApp.getInstance()).load(Constants.URL_MOVIE_THUMB_BASE + mMovie.getBackdrop_path())
                .thumbnail(0.1f)
                .crossFade().placeholder(R.mipmap.ic_launcher)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivBackdrop);

        Glide.with(NemApp.getInstance()).load(Constants.URL_MOVIE_THUMB_BASE + mMovie.getPosterPath())
                .thumbnail(0.5f)
                .crossFade().placeholder(R.mipmap.ic_launcher)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivThumb);

        fabFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInWatchList){
                    Toast.makeText(MovieDetailsActivity.this, "Already in Watchlist", Toast.LENGTH_SHORT).show();
                }
                mFirebaseDatabase.child(mMovie.getId().toString()).setValue(mMovie);
            }
        });
        fabFav.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(MovieDetailsActivity.this, "Tap me to add this movie to your Watchlist", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

    }

    @Override
    protected void onStop() {
        mFirebaseDatabase.removeEventListener(fbdbListener);
        super.onStop();
    }
}
