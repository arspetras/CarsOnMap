package com.example.carsmap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.service.quicksettings.Tile;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
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
    public String[] batteryLife;
    public String[] batteryDistance;
    public String[] isBatteryCharging;
    int i=0;
    public String[] distanceToUser;
    public String buttonId = "-1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars);

        distanceToUser = getIntent().getStringArrayExtra("distanceToUser");

        GetAllFormCarsApi ();

    }

    public void addToList(String url, String cTitle, String cPlateNumber,String cBatteryLife, String userDistance)
    {
        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(
                400, 400);

        ImageView img = new ImageView(this);
        new DownloadImageFromInternet(img)
                .execute(url);
        img.setLayoutParams(imgParams);

        TextView titleLabel = new TextView(this);
        titleLabel.setText("Title: ");
        titleLabel.setTypeface(null, Typeface.BOLD);
        titleLabel.setTextColor(Color.BLACK);
        titleLabel.setTextSize(20);

        TextView plateNumberLabel = new TextView(this);
        plateNumberLabel.setText("Plate Number: ");
        plateNumberLabel.setTypeface(null, Typeface.BOLD);
        plateNumberLabel.setTextColor(Color.BLACK);
        plateNumberLabel.setTextSize(20);

        TextView batteryLife = new TextView(this);
        batteryLife.setText("Battery %: ");
        batteryLife.setTypeface(null, Typeface.BOLD);
        batteryLife.setTextColor(Color.BLACK);
        batteryLife.setTextSize(20);

        TextView distance = new TextView(this);
        distance.setText("Distance: ");
        distance.setTypeface(null, Typeface.BOLD);
        distance.setTextColor(Color.BLACK);
        distance.setTextSize(20);

        final Button showOnMapButton = new Button(this);
        showOnMapButton.setText("Show On Map");
        showOnMapButton.setId(i);
        showOnMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonId =String.valueOf(showOnMapButton.getId());
                GoToMap(v);

            }
        });

        // get info
        TextView carTitle = new TextView(this);
        carTitle.setText(cTitle);
        carTitle.setTypeface(null, Typeface.BOLD);
        carTitle.setTextColor(Color.BLACK);
        carTitle.setTextSize(20);

        TextView carPlateNumber = new TextView(this);
        carPlateNumber.setText(cPlateNumber);
        carPlateNumber.setTypeface(null, Typeface.BOLD);
        carPlateNumber.setTextColor(Color.BLACK);
        carPlateNumber.setTextSize(20);

        TextView carBatteryLife = new TextView(this);
        carBatteryLife.setText(cBatteryLife+"%");
        carBatteryLife.setTypeface(null, Typeface.BOLD);
        carBatteryLife.setTextColor(Color.BLACK);
        carBatteryLife.setTextSize(20);

        TextView carDistance = new TextView(this);
        carDistance.setText( userDistance+ " km");

        carDistance.setTypeface(null, Typeface.BOLD);
        carDistance.setTextColor(Color.BLACK);
        carDistance.setTextSize(20);

        final Button moreInfoButton = new Button(this);
        moreInfoButton.setText("More Info");
        moreInfoButton.setId(i);
        moreInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonId = String.valueOf(moreInfoButton.getId());
                showAllInfo();
            }
        });



        TextView forSpacing = new TextView(this);
        forSpacing.setText(" ");
        forSpacing.setTypeface(null, Typeface.BOLD);
        forSpacing.setTextColor(Color.BLACK);
        forSpacing.setTextSize(20);


        LinearLayout carsListLL = (LinearLayout) findViewById(R.id.CarsListLayout);
        LinearLayout carInfoLL = new LinearLayout(this);
        carInfoLL.setOrientation(LinearLayout.HORIZONTAL);
        carInfoLL.addView(img);
        LinearLayout labels = new LinearLayout(this);
        labels.setOrientation(LinearLayout.VERTICAL);
        labels.addView(titleLabel);
        labels.addView(plateNumberLabel);
        labels.addView(batteryLife);
        labels.addView(distance);
        labels.addView(showOnMapButton);
        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.addView(carTitle);
        info.addView(carPlateNumber);
        info.addView(carBatteryLife);
        info.addView(carDistance);
        info.addView(moreInfoButton);
        carInfoLL.addView(labels);
        carInfoLL.addView(info);
        carsListLL.addView(carInfoLL);
        carsListLL.addView(forSpacing);
    }

    public void GetAllFormCarsApi ()
    {

        plateNumber = new String[100];
        latitude = new String[100];
        longitude = new String[100];
        address = new String[100];
        title = new String[100];
        photoUrl = new String[100];
        batteryLife = new String[100];
        batteryDistance = new String[100];
        isBatteryCharging = new String[100];

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


                    plateNumber[i] = post.getPlateNumber();
                    latitude[i] = post.getLocation().get("latitude");
                    longitude[i] = post.getLocation().get("longitude");
                    address[i] = post.getLocation().get("address");
                    title[i] = post.getModel().get("title");
                    photoUrl[i] = post.getModel().get("photoUrl");
                    batteryLife[i] = post.getBatteryPercentage();
                    batteryDistance[i] = post.getBatteryEstimatedDistance();
                    isBatteryCharging[i] = post.isCharging();
                    if(distanceToUser == null) addToList(photoUrl[i],title[i],plateNumber[i],batteryLife[i], "~");
                    else addToList(photoUrl[i],title[i],plateNumber[i],batteryLife[i],distanceToUser[i]);
                    i++;
                }

            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                myText.setText(t.getMessage());
            }
        });
    }

    /**
        Method to show message dialog with full information about the car
     */
    public void showAllInfo()
    {
        StringBuffer buffer = new StringBuffer();

            buffer.append("Plate Number: " + plateNumber[Integer.parseInt(buttonId)] + "\n");
            buffer.append("Model: " + title[Integer.parseInt(buttonId)] + "\n");
            buffer.append("Location: "+ address[Integer.parseInt(buttonId)] + "\n");
            buffer.append("Battery Percentage: "+ batteryLife[Integer.parseInt(buttonId)] + "\n");
            buffer.append("Battery Estimated Distance: " + batteryDistance[Integer.parseInt(buttonId)] + "\n");
            String isCharging;
            if(isBatteryCharging[Integer.parseInt(buttonId)].equals("true")) isCharging = "Yes";
            else isCharging = "No";
            buffer.append("Battery Charging: "+ isCharging + "\n\n");

            // creating dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Information about Car");
            builder.setMessage(buffer.toString());
            builder.show();

    }

    public void GoToMap(View view)
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
        intent.putExtra("8", buttonId);
        startActivity(intent);
    }



}
