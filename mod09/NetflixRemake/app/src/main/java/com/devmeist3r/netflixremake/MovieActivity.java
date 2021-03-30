package com.devmeist3r.netflixremake;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devmeist3r.netflixremake.model.Movie;
import com.devmeist3r.netflixremake.model.MovieDetail;
import com.devmeist3r.netflixremake.util.ImageDownloaderTask;
import com.devmeist3r.netflixremake.util.MovieDetailTask;

import java.util.ArrayList;
import java.util.List;

public class MovieActivity extends AppCompatActivity implements MovieDetailTask.MovieDetailLoader {

  private TextView txtTitle;
  private TextView txtDesc;
  private TextView txtCast;
  private RecyclerView recyclerView;
  private MovieAdapter movieAdapter;
  private ImageView imgCover;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_movie);

    txtTitle = findViewById(R.id.text_view_title);
    txtDesc = findViewById(R.id.text_view_desc);
    txtCast = findViewById(R.id.text_view_cast);
    recyclerView = findViewById(R.id.recycler_view_similar);
    imgCover = findViewById(R.id.image_view_cover);

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back);
      getSupportActionBar().setTitle(null);
    }

    LayerDrawable drawable =
        (LayerDrawable) ContextCompat.getDrawable(this, R.drawable.shadows);

    List<Movie> movies = new ArrayList<>();
    movieAdapter = new MovieAdapter(movies);
    recyclerView.setAdapter(new MovieAdapter(movies));
    recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      int id = extras.getInt("id");
      MovieDetailTask movieDetailTask = new MovieDetailTask(this);
      movieDetailTask.setMovieDetailLoader(this);
      String teste = "https://tiagoaguiar.co/api/netflix/" + id;
      Log.d("Teste", "onCreate: " + teste);
      movieDetailTask.execute("https://tiagoaguiar.co/api/netflix/" + id);
    }

  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onResult(MovieDetail movieDetail) {
    txtTitle.setText(movieDetail.getMovie().getTitle());
    txtDesc.setText(movieDetail.getMovie().getDesc());
    txtCast.setText(movieDetail.getMovie().getCast());

    ImageDownloaderTask imageDownloaderTask = new ImageDownloaderTask(imgCover);
    imageDownloaderTask.setShadowEnabled(true);
    imageDownloaderTask.execute(movieDetail.getMovie().getCoverURL());

    movieAdapter.setMovies(movieDetail.getMoviesSimilar());
    movieAdapter.notifyDataSetChanged();
  }

  private static class MovieHolder extends RecyclerView.ViewHolder {

    final ImageView imageViewCover;

    public MovieHolder(@NonNull View itemView) {
      super(itemView);
      imageViewCover = itemView.findViewById(R.id.image_view_cover);
    }

  }

  private class MovieAdapter extends RecyclerView.Adapter<MovieActivity.MovieHolder> {

    private List<Movie> movies;

    private MovieAdapter(List<Movie> movies) {
      this.movies = movies;
    }

    public void setMovies(List<Movie> movies) {
      this.movies.clear();
      this.movies.addAll(movies);
    }

    @NonNull
    @Override
    public MovieActivity.MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      return new MovieActivity.MovieHolder(
          getLayoutInflater().inflate(
              R.layout.movie_item_similar,
              parent, false
          )
      );
    }

    @Override
    public void onBindViewHolder(@NonNull MovieHolder holder, int position) {
      Movie movie = movies.get(position);
      new ImageDownloaderTask(holder.imageViewCover).execute(movie.getCoverURL());
    }

    @Override
    public int getItemCount() {
      return movies.size();
    }
  }

}