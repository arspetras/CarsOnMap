package com.example.carsmap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars);
        GetAllFormCarsApi ();
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
                int i=0;
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
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("1", plateNumber);
        intent.putExtra("2", latitude);
        intent.putExtra("3", longitude);
        intent.putExtra("4", address);
        intent.putExtra("5", title);
        intent.putExtra("6", photoUrl);
        startActivity(intent);
    }

}
