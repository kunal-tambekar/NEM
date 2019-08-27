package zx.zample.nem.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import zx.zample.nem.R;
import zx.zample.nem.adapter.WatchListAdapter;
import zx.zample.nem.app.NemApp;
import zx.zample.nem.model.MovieModel;
import zx.zample.nem.widget.RecyclerviewTouchListner;

public class WatchlistActivity extends AppCompatActivity implements RecyclerviewTouchListner.ClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshMovies;
    private List<MovieModel> moviesList = new ArrayList<>();
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private WatchListAdapter mAdapter;
    private TextView txtNoMovie;
    private ValueEventListener fbdbListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            moviesList.clear();
            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                MovieModel movie = postSnapshot.getValue(MovieModel.class);
                moviesList.add(movie);
            }
            mAdapter = new WatchListAdapter(moviesList);
            recyclerView.setAdapter(mAdapter);
            swipeRefreshMovies.setRefreshing(false);
            if(moviesList.isEmpty()){
                txtNoMovie.setText("No movies in your watchlist");
                txtNoMovie.setVisibility(View.VISIBLE);
            }else{
                txtNoMovie.setVisibility(View.GONE);
            }

        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            txtNoMovie.setVisibility(View.VISIBLE);
            txtNoMovie.setText("Sign in with Google to save movie to Watchlist");
            Toast.makeText(WatchlistActivity.this, "Sign in with Google to save movie to Watchlist", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Failed to read app title value.", error.toException());
        }
    };


    @Override
    public void onClick(View view, int position) {
        MovieModel movie = mAdapter.getMovies().get(position);
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra("MOVIE_MODEL", movie);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this,
                        view.findViewById(R.id.iv_movie_thumb),
                        ViewCompat.getTransitionName(view.findViewById(R.id.iv_movie_thumb)));
        startActivity(intent, options.toBundle());
    }

    @Override
    public void onLongClick(View view, int position) {
        final MovieModel movie = moviesList.get(position);
        new AlertDialog.Builder(this)
                .setTitle("Remove from Watchlist")
                .setIcon(android.R.drawable.ic_delete)
                .setMessage("Are you sure you want to remove this movie from watchlist?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mFirebaseDatabase.child(movie.getId().toString()).removeValue();
                    }

                })
                .setNegativeButton("No", null)
                .show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);
        swipeRefreshMovies = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_watchlist);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_watchlist);
        txtNoMovie =(TextView) findViewById(R.id.txt_err_watchlist);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("favorites" + NemApp.getInstance().getUsername());
        mFirebaseDatabase.addValueEventListener(fbdbListener);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnItemTouchListener(new RecyclerviewTouchListner(this, recyclerView, this));

        swipeRefreshMovies.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchData();
            }
        });
        if (NemApp.getInstance().getConnectionDetector().isConnectedToInternet()) {
            fetchData();
        } else {
            Toast.makeText(this, "Check network connection and try again", Toast.LENGTH_SHORT).show();
        }

    }

    public void fetchData() {
        mFirebaseDatabase.removeEventListener(fbdbListener);
        mFirebaseDatabase.addValueEventListener(fbdbListener);

    }


}
