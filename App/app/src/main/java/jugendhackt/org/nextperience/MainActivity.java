package jugendhackt.org.nextperience;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.VideoView;

import com.loopj.android.http.RequestParams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static android.os.AsyncTask.Status.PENDING;
import static android.os.AsyncTask.Status.RUNNING;


public class MainActivity extends AppCompatActivity {

    static final int REQUEST_VIDEO_CAPTURE = 1;
    private TextView uriDisplay;
    private String ip = "";
    private String response;


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

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection myConnection = null;
                // Create URL
                URL endpoint = null;
                try {
                    endpoint = new URL("http://jsonplaceholder.typicode.com/posts");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                if (endpoint != null) {
                    try {
                        myConnection = (HttpURLConnection) endpoint.openConnection();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                myConnection.setRequestProperty("User-Agent", "my-rest-app-v0.1");
                try {
                    if (myConnection.getResponseCode() == 200) {
                        InputStream responseBody = myConnection.getInputStream();
                        InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                        JsonReader jsonReader = new JsonReader(responseBodyReader);
                    }
                    else {
                        Log.d("RestAufruf","Fehler!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = intent.getData();
            uriDisplay.setText(videoUri.toString());
        }
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }


}
