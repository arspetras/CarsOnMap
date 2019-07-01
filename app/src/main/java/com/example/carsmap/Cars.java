package com.example.carsmap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.maps.android.clustering.ClusterManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Cars extends AppCompatActivity {

    public TextView myText;

    //vars for car
    public String[] plateNumber;
    public String[] latitude;
    public String[] longitude;
    public String[] address;
    public String[] title;
    public String[] photoUrl;
    int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars);
        GetAllFormCarsApi ();


        // Image link from internet

        new DownloadImageFromInternet((ImageView) findViewById(R.id.image_view))
                .execute("https://s3-eu-west-1.amazonaws.com/rideshareuploads/uploads/f403e69d-13ea-4e7f-aa09-f68e2a91f540.jpeg");



    }

    public void GetAllFormCarsApi ()
    {
        myText = findViewById(R.id.textViewResults);

        plateNumber = new String[100];
        latitude = new String[100];
        longitude = new String[100];
        address = new String[100];
        title = new String[100];
        photoUrl = new String[100];


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://development.espark.lt/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CarsApi carsApi = retrofit.create(CarsApi.class);

        Call<List<Post>> call = carsApi.getPosts();

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if(!response.isSuccessful()){
                    myText.setText("Code:" + response.code());
                    return;
                }

                List<Post> posts = response.body();

                for(Post post: posts)
                {
                    String content = "";

                    plateNumber[i] = post.getPlateNumber();
                    latitude[i] = post.getLocation().get("latitude");
                    longitude[i] = post.getLocation().get("longitude");
                    address[i] = post.getLocation().get("address");
                    title[i] = post.getModel().get("title");
                    photoUrl[i] = post.getModel().get("photoUrl");
                    //i++;

                    //testing

                    content += "plateNumber: " + plateNumber[i] + "\n";
                    content += "latitude: " + latitude[i] + "\n";
                    content += "longitude: " + longitude[i] + "\n";
                    content += "address: " + address[i] + "\n";
                    content += "title: " + title[i] + "\n";
                    content += "photoUrl: " + photoUrl[i] + "\n\n";
                    i++;
                    myText.append(content);
                }

            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                myText.setText(t.getMessage());
            }
        });
    }

    public void back(View view)
    {
        String length = String.valueOf(i);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("1", plateNumber);
        intent.putExtra("2", latitude);
        intent.putExtra("3", longitude);
        intent.putExtra("4", address);
        intent.putExtra("5", title);
        intent.putExtra("6", photoUrl);
        intent.putExtra("7", length);
        startActivity(intent);
    }

}
