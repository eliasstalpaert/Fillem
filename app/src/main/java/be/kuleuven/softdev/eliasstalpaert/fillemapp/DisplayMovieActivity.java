package be.kuleuven.softdev.eliasstalpaert.fillemapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.ContentHandler;

public class DisplayMovieActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private TextView yeaman;
    private Context context = this;

    private String movie_title;
    private String movie_year;
    private String movie_runtime;
    private String movie_genre;
    private String movie_plot;
    private String movie_language;
    private String movie_country;
    private String posterUrl;

    private Drawable poster;
    private ImageView imageView_internet;
    private Target target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_movie);
        //initialize variables
        yeaman = findViewById(R.id.textView_yeaman);
        imageView_internet = findViewById(R.id.imageView_internet);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        initMovie();
        loadImageByUrl(posterUrl);

    }

    private void loadImageByUrl(String posterUrl) {
        Picasso.get()
                .load(posterUrl)
                .fit()
                .centerInside()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher_round)
                .into(imageView_internet);
    }

    private void initMovie() {
        try {
            JSONObject movieJson = new JSONObject(getIntent().getStringExtra(MainActivity.EXTRA_JSONSTRING));
            movie_title = movieJson.getString("Title");
            movie_year = movieJson.getString("Year");
            movie_runtime = movieJson.getString("Runtime");
            movie_genre = movieJson.getString("Genre");
            movie_plot = movieJson.getString("Plot");
            movie_language = movieJson.getString("Language");
            movie_country = movieJson.getString("Country");
            posterUrl = movieJson.getString("Poster");
            yeaman.setText(movie_title);
        } catch (JSONException e) {
            Toast.makeText(DisplayMovieActivity.this, "Exception occurred", Toast.LENGTH_SHORT).show();
        }
    }
}