package jugendhackt.org.nextperience;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    static final int REQUEST_VIDEO_CAPTURE = 1;
    private TextView uriDisplay;
    private String baseUrl = "http://192.168.0.73:3000/tasks";  // This is the API base URL (GitHub API)
    private String url; // This will hold the full URL which will include the username entered in the etGitHubUser.
    private RequestQueue requestQueue;
    public Intent goInSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final ImageButton recordVideo = (ImageButton) findViewById(R.id.button);
        recordVideo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispatchTakeVideoIntent();
            }
        });
        setSupportActionBar(toolbar);
        uriDisplay = (TextView) findViewById(R.id.textView);
            goInSettings = new Intent(this, SettingsActivity.class);
        requestQueue = Volley.newRequestQueue(this); // This setups up a new request queue which we will need to make HTTP requests.
    }


    @Override
    protected void onResume(){
        super.onResume();
        getContentFromServer();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = intent.getData();
            uriDisplay.setText(videoUri.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            default:
                startActivity(goInSettings);
                break;
        }

        return true;
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    private void getContentFromServer() {
        // First, we insert the username into the repo url.
        // The repo url is defined in GitHubs API docs (https://developer.github.com/v3/repos/).
        this.url = this.baseUrl;

        // Next, we create a new JsonArrayRequest. This will use Volley to make a HTTP request
        // that expects a JSON Array Response.
        // To fully understand this, I'd recommend reading the office docs: https://developer.android.com/training/volley/index.html
        JsonArrayRequest arrReq = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Check the length of our response (to see if the user has any video)
                        if (response.length() > 0) {
                            // Parse them all
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    // For each video, add a new line to our vidoe list.
                                    JSONObject jsonObj = response.getJSONObject(i);
                                    String description = jsonObj.get("description").toString();
                                    String tags = jsonObj.get("tags").toString();
                                    uriDisplay.setText(uriDisplay.getText() + description + tags);
                                } catch (JSONException e) {
                                    // If there is an error then output this to the logs.
                                    Log.e("Volley", "Invalid JSON Object.");
                                }

                            }
                        } else {
                            uriDisplay.setText("No videos found");
                        }

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If there is any Error
                        uriDisplay.setText("Error while calling REST API");
                        Log.e("Volley", error.toString());
                    }
                }
        );
        // Add the request we just defined to our request queue.
        // The request queue will automatically handle the request as soon as it can.
        requestQueue.add(arrReq);
    }


}
