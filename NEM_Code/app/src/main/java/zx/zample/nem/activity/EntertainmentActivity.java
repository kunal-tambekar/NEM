package zx.zample.nem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import zx.zample.nem.R;
import zx.zample.nem.adapter.MoviesAdapter;
import zx.zample.nem.app.NemApp;
import zx.zample.nem.model.MovieModel;
import zx.zample.nem.model.MovieResponse;
import zx.zample.nem.network.ApiClient;
import zx.zample.nem.network.ApiInterface;
import zx.zample.nem.util.Constants;
import zx.zample.nem.widget.DividerItemDecorator;
import zx.zample.nem.widget.RecyclerviewTouchListner;

public class EntertainmentActivity extends AppCompatActivity implements RecyclerviewTouchListner.ClickListener,
        Callback<MovieResponse> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private MoviesAdapter moviesAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshMovies;
    private List<MovieModel> moviesList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entertainment);

        swipeRefreshMovies = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_movies);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecorator(this, LinearLayoutManager.VERTICAL));
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
        moviesList.clear();
        ApiInterface apiService = ApiClient.getEInstance().create(ApiInterface.class);
        swipeRefreshMovies.setRefreshing(true);
        for (int i = 1; i <= 20; i++) {
            Call<MovieResponse> topMoviesApiCall = apiService.getTopRatedMovies(Constants.ENTERTAINMENT_API_KEY, i);
            topMoviesApiCall.enqueue(this);
        }
    }

    @Override
    public void onClick(View view, int position) {
        MovieModel movie = moviesAdapter.getMovies().get(position);
        Intent intent = new Intent(EntertainmentActivity.this, MovieDetailsActivity.class);
        intent.putExtra("MOVIE_MODEL", movie);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(EntertainmentActivity.this,
                        view.findViewById(R.id.iv_movie_thumb),
                        ViewCompat.getTransitionName(view.findViewById(R.id.iv_movie_thumb)));
        startActivity(intent, options.toBundle());
    }

    @Override
    public void onLongClick(View view, int position) {

    }

    @Override
    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
        List<MovieModel> movies = response.body().getResults();

        moviesList.addAll(movies);
        Collections.sort(moviesList, new Comparator<MovieModel>() {
            public int compare(MovieModel obj1, MovieModel obj2) {
                return obj2.getVote_average().compareTo(obj1.getVote_average());
            }
        });
        Log.d(TAG, "No. of Movies returned " + moviesList.size());
        if (moviesAdapter == null) {
            moviesAdapter = new MoviesAdapter(moviesList);
            recyclerView.setAdapter(moviesAdapter);
            return;
        }
        moviesAdapter.setMovies(moviesList);
        swipeRefreshMovies.setRefreshing(false);
    }

    @Override
    public void onFailure(Call<MovieResponse> call, Throwable t) {
        Log.e(TAG, "Failed to get movies response " + t.toString());
        swipeRefreshMovies.setRefreshing(false);
        t.printStackTrace();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_e, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.watchlist_menu:
                startActivity(new Intent(this, WatchlistActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}