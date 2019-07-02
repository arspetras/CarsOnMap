package com.example.carsmap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.service.quicksettings.Tile;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.Toast;

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

    Button showMapButton;
    FloatingActionButton sortingBtn;
    FloatingActionButton filterBtn;
    FloatingActionButton cancelBtn;
    Button filterByPlateBtn;
    Button filterByBatteryBtn;
    FloatingActionButton fullBatteryBtn;
    FloatingActionButton midBatteryBtn;
    FloatingActionButton lowBatteryBtn;
    FloatingActionButton searchBtn;
    TextView plateNumberInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars);

        distanceToUser = getIntent().getStringArrayExtra("distanceToUser");

        GetAllFormCarsApi ();

        showMapButton = findViewById(R.id.ShowMapButton);
        sortingBtn = findViewById(R.id.sortingButton);
        filterBtn = findViewById(R.id.FilterButton);
        cancelBtn = findViewById(R.id.cancel);
        filterByPlateBtn = findViewById(R.id.FilterByPlateNumber);
        filterByBatteryBtn = findViewById(R.id.filterByBattery);
        fullBatteryBtn = findViewById(R.id.fullBatterBtn);
        midBatteryBtn = findViewById(R.id.midBatterBtn);
        lowBatteryBtn = findViewById(R.id.lowBatterBtn);
        searchBtn = findViewById(R.id.searchButton);
        plateNumberInput = findViewById(R.id.PlateNumberSearchBar);


    }

    public void addToList(String url, String cTitle, String cPlateNumber,String cBatteryLife, String userDistance, int btnIndex)
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
        showOnMapButton.setId(btnIndex);
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
        moreInfoButton.setId(btnIndex);
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
                    if(distanceToUser == null) addToList(photoUrl[i],title[i],plateNumber[i],batteryLife[i], "~",i);
                    else addToList(photoUrl[i],title[i],plateNumber[i],batteryLife[i],distanceToUser[i],i);
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

    /**
        Button click method to sort list by distance to user
     */
    public void Sort(View view)
    {
        int[] index = new int[100];
        double[] sortedDistance = new double[100];

        if(distanceToUser == null)
            Toast.makeText(this,"Please Check Map First",Toast.LENGTH_LONG).show();
        else {
            for(int x = 0; x<i;x++)
            {
                index[x] = x;
                sortedDistance[x] =Double.parseDouble(distanceToUser[x]);
            }

            //sorting
            for (int x = 1; x < i; ++x) {
                double  key = sortedDistance[x];
                int key2 = index[x];
                int j = x - 1;

                while (j >= 0 && sortedDistance[j] > key) {
                    sortedDistance[j + 1] = sortedDistance[j];
                    index[j+1] = index[j];
                    j = j - 1;
                }
                sortedDistance[j + 1] = key;
                index[j+1]= key2;
            }
            LinearLayout carsListLL = (LinearLayout) findViewById(R.id.CarsListLayout);
            carsListLL.removeAllViews();
            for(int x = 0; x<i;x++)
            {
                addToList(photoUrl[index[x]],title[index[x]],plateNumber[index[x]],batteryLife[index[x]],distanceToUser[index[x]],x);
            }

        }
    }

    /**
        Method for filter button to show filter options
     */
    public void filter(View view)
    {
        showMapButton.setVisibility(View.INVISIBLE);
        sortingBtn.hide();
        filterBtn.hide();
        cancelBtn.show();
        filterByPlateBtn.setVisibility(View.VISIBLE);
        filterByBatteryBtn.setVisibility(View.VISIBLE);
    }

    /**
     Method to cancel filter options
     */
    public void cancelFilter(View view)
    {
        showMapButton.setVisibility(View.VISIBLE);
        sortingBtn.show();
        filterBtn.show();
        cancelBtn.hide();
        filterByPlateBtn.setVisibility(View.INVISIBLE);
        filterByBatteryBtn.setVisibility(View.INVISIBLE);
        fullBatteryBtn.hide();
        midBatteryBtn.hide();
        lowBatteryBtn.hide();
        plateNumberInput.setVisibility(View.INVISIBLE);
        searchBtn.hide();
        LinearLayout carsListLL = (LinearLayout) findViewById(R.id.CarsListLayout);
        carsListLL.removeAllViews();
        for(int x = 0;x<i;x++)
        {
            if(distanceToUser == null) addToList(photoUrl[x],title[x],plateNumber[x],batteryLife[x], "~",x);
            else addToList(photoUrl[x],title[x],plateNumber[x],batteryLife[x],distanceToUser[x],x);
        }
    }

    /**
     Method to filter by battery
     */
    public void filterBattery(View view)
    {
        filterByPlateBtn.setVisibility(View.INVISIBLE);
        filterByBatteryBtn.setVisibility(View.INVISIBLE);
        fullBatteryBtn.show();
        midBatteryBtn.show();
        lowBatteryBtn.show();
    }

    /**
     Method to filter by battery:
     full battery (75-100%)
     min battery (30 - 74%)
     low battery (0 - 29%)
     */
    public void batteryFilterButtonDetect(View v)
    {
        int start=0, end=0;
        if (v == fullBatteryBtn){start = 75; end = 100;}
        else if (v == midBatteryBtn){start = 30; end = 74;}
        else if (v == lowBatteryBtn){start = 0; end = 29;}
        fullMidLowBatteryFilter(start,end);
    }

    public void fullMidLowBatteryFilter (int startLimit, int endLimit)
    {
        LinearLayout carsListLL = (LinearLayout) findViewById(R.id.CarsListLayout);
        carsListLL.removeAllViews();
        for(int x = 0; x<i;x++)
        {
            if(Integer.parseInt(batteryLife[x]) >= startLimit && Integer.parseInt(batteryLife[x]) <= endLimit)
            {
                if(distanceToUser == null) addToList(photoUrl[x],title[x],plateNumber[x],batteryLife[x], "~",x);
                else addToList(photoUrl[x],title[x],plateNumber[x],batteryLife[x],distanceToUser[x],x);
            }

        }
    }

    /**
        Method to allow filter by plate number
     */
    public void filterByPlateNumber(View view)
    {
        filterByPlateBtn.setVisibility(View.INVISIBLE);
        filterByBatteryBtn.setVisibility(View.INVISIBLE);
        searchBtn.show();
        plateNumberInput.setVisibility(View.VISIBLE);
        plateNumberInput.setText("");
    }

    /**
        Method to filter by plate number by taking input from textView
     */
    public void searchPlateNumber(View view)
    {


       String PN = String.valueOf(plateNumberInput.getText());
        LinearLayout carsListLL = (LinearLayout) findViewById(R.id.CarsListLayout);
        carsListLL.removeAllViews();
        boolean foundIt = false;

       for(int x =0;x<i;x++)
       {

           if(plateNumber[x].equals(PN) || plateNumber[x].equals(PN.toLowerCase()) || plateNumber[x].equals(PN.toUpperCase()))
           {
               if(distanceToUser == null) addToList(photoUrl[x],title[x],plateNumber[x],batteryLife[x], "~",x);
               else addToList(photoUrl[x],title[x],plateNumber[x],batteryLife[x],distanceToUser[x],x);
               foundIt = true;
               break;
           }
       }
       if(foundIt == false) Toast.makeText(this,"Car not found",Toast.LENGTH_LONG).show();
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
