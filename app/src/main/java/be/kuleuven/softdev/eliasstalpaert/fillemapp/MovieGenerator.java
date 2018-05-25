package be.kuleuven.softdev.eliasstalpaert.fillemapp;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;

public class MovieGenerator {

    public static final String EXTRA_JSONSTRING = "be.kuleuven.softdev.eliasstalpaert.fillemapp.JSONSTRING";
    public static final String EXTRA_BEGINYEAR = "be.kuleuven.softdev.eliasstalpaert.fillemapp.BEGINYEAR";
    public static final String EXTRA_ENDYEAR = "be.kuleuven.softdev.eliasstalpaert.fillemapp.ENDYEAR";
    public static final String EXTRA_RATING = "be.kuleuven.softdev.eliasstalpaert.fillemapp.RATING";
    public static final String EXTRA_MINVOTES = "be.kuleuven.softdev.eliasstalpaert.fillemapp.MINVOTES";

    private Context context;
    private JSONObject movie;
    private String current_movie_id;
    private String jsonString;
    private Toast fetchMovie;
    private RequestQueue requestQueue;
    private Map<String,MenuItem> genres;
    private Menu menu;
    private Float rating_float;
    private Integer beginyear;
    private Integer endyear;
    private Integer minVotes;
    private Integer generateTries;

    public void generate(){
        generateTries = 0;
        generateMovie();
    }

    public String getJsonString() {
        return jsonString;
    }

    public void setRating_float(Float rating_float) {
        this.rating_float = rating_float;
    }

    public void setBeginyear(Integer beginyear) {
        this.beginyear = beginyear;
    }

    public void setEndyear(Integer endyear) {
        this.endyear = endyear;
    }

    public void setMinVotes(Integer minVotes) {
        this.minVotes = minVotes;
    }

    public MovieGenerator(Context context, Menu menu) {
        genres = new TreeMap<>();
        requestQueue = Volley.newRequestQueue(context);
        this.context = context;
        this.menu = menu;

        genres.put("action",getMenuItem(R.id.actionGenre));
        genres.put("adventure",getMenuItem(R.id.adventureGenre));
        genres.put("animation",getMenuItem(R.id.animationGenre));
        genres.put("biography",getMenuItem(R.id.biographyGenre));
        genres.put("comedy",getMenuItem(R.id.comedyGenre));
        genres.put("crime",getMenuItem(R.id.crimeGenre));
        genres.put("documentary",getMenuItem(R.id.documentaryGenre));
        genres.put("drama",getMenuItem(R.id.dramaGenre));
        genres.put("family",getMenuItem(R.id.familyGenre));
        genres.put("fantasy",getMenuItem(R.id.fantasyGenre));
        genres.put("game-show",getMenuItem(R.id.gameshowGenre));
        genres.put("history",getMenuItem(R.id.historyGenre));
        genres.put("horror",getMenuItem(R.id.horrorGenre));
        genres.put("music",getMenuItem(R.id.musicGenre));
        genres.put("musical",getMenuItem(R.id.musicalGenre));
        genres.put("mystery",getMenuItem(R.id.mysteryGenre));
        genres.put("news",getMenuItem(R.id.newsGenre));
        genres.put("reality-tv",getMenuItem(R.id.realityGenre));
        genres.put("romance",getMenuItem(R.id.romanceGenre));
        genres.put("sci-fi",getMenuItem(R.id.scifiGenre));
        genres.put("sport",getMenuItem(R.id.sportGenre));
        genres.put("talk-show",getMenuItem(R.id.talkshowGenre));
        genres.put("thriller",getMenuItem(R.id.thrillerGenre));
        genres.put("war",getMenuItem(R.id.warGenre));
        genres.put("western",getMenuItem(R.id.westernGenre));
    }

    private void startDisplayActivity() {
        Intent intent = new Intent(context, DisplayMovieActivity.class);
        intent.putExtra(EXTRA_JSONSTRING, jsonString); //jsonstring aangepast
        intent.putExtra(EXTRA_BEGINYEAR, beginyear);
        intent.putExtra(EXTRA_ENDYEAR, endyear);
        intent.putExtra(EXTRA_RATING, rating_float);
        intent.putExtra(EXTRA_MINVOTES, minVotes);
        context.startActivity(intent);
    }

    private MenuItem getMenuItem(int id){
        return menu.findItem(id);
    }

    private void generateMovie() {
        fetchMovie = Toast.makeText(context, "Fetching movie... "+generateTries.toString(), Toast.LENGTH_LONG);
        fetchMovie.show();
        generateTries++;

        String queryUrl = buildUrl();

        JsonArrayRequest request = new JsonArrayRequest(queryUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        try {
                            movie = jsonArray.getJSONObject(0);
                            current_movie_id = movie.getString("imdbId");
                            checkResponse();
                        }
                        catch(JSONException e) {
                            fetchMovie.cancel();
                            Toast.makeText(context, "No movies found", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        fetchMovie.cancel();
                        Toast.makeText(context, "Unable to fetch data: please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(request);
    }

    private void checkResponse() {
        String queryUrl = "http://www.omdbapi.com/?i=" + current_movie_id + "&apikey=e2383f7f";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, queryUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject responseObject) {
                        try {
                            String response = responseObject.getString("Response");
                            if(response.equals("True")){
                                jsonString = responseObject.toString(); // save JSONObject to String
                                fetchMovie.cancel(); // cancel Toast "Fetching movie..."
                                startDisplayActivity(); // goto displayactivity
                            }
                            else{
                                generateMovie();
                            }
                        } catch (JSONException e) {
                            fetchMovie.cancel();
                            Toast.makeText(context, "Error finding details", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        generateMovie();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    private String buildUrl(){
        String url = "http://api.a17-sd206.studev.groept.be/query_movies_new";
        StringBuilder urlBuilder = new StringBuilder(url);
        int emptyCount = 0;
        for(String s : genres.keySet()){
            MenuItem m = genres.get(s);
            if(m.isChecked()){
                urlBuilder.append("/" + s);
            }
            else{
                urlBuilder.append("/.*");
                emptyCount++;
            }
        }
        int ratingInt = rating_float.intValue();
        urlBuilder.append("/" + 0);
        urlBuilder.append("/" + minVotes);
        urlBuilder.append("/" + ratingInt);
        urlBuilder.append("/" + beginyear);
        urlBuilder.append("/" + endyear);
        String queryUrl;
        if(emptyCount < 25){
            queryUrl = urlBuilder.toString();
        }
        else{
            StringBuilder urlEmptyBuilder = new StringBuilder(url);
            for(String s : genres.keySet()){
                urlEmptyBuilder.append("/" + s);
            }
            urlEmptyBuilder.append("/" + 0);
            urlEmptyBuilder.append("/" + minVotes);
            urlEmptyBuilder.append("/" + ratingInt);
            urlEmptyBuilder.append("/" + beginyear);
            urlEmptyBuilder.append("/" + endyear);
            queryUrl = urlEmptyBuilder.toString();
        }
        return queryUrl;
    }
}
