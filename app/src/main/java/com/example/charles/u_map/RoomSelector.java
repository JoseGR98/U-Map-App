package com.example.charles.u_map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.charles.u_map.utils.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static com.example.charles.u_map.utils.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;

public class RoomSelector extends AppCompatActivity {

    GridView roomGridView;
    String[] classrooms;

    private boolean locationPermision = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_selector);
        Resources resources = getResources();
        roomGridView = (GridView) findViewById(R.id.roomGridView);
        classrooms = resources.getStringArray(R.array.classrooms);

        final Intent in = getIntent();


        TextView area = (TextView) findViewById(R.id.areaTextView);
        area.setText(in.getStringExtra("com.example.charles.u_map.AREA"));
        TextView fav = (TextView) findViewById(R.id.favView);
        roomAdapter roomA = new roomAdapter(this, classrooms);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Add the room adapter to the view.

        roomGridView.setAdapter(roomA);


        //Start the favourite activity when the label is clicked.

        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToFav = new Intent(getApplicationContext(), FavouriteClassrooms.class);
                goToFav.putExtra("com.example.charles.u_map.ID", in.getStringExtra("com.example.charles.u_map.ID"));
                startActivity(goToFav);
            }
        });

        //When an item is clicked, init the google map with the classroom number and area.

        roomGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent goToMap = new Intent(getApplicationContext(), MapsActivity.class);
                goToMap.putExtra("com.example.charles.u_map.LOCATION_PERMISSION",locationPermision);
                goToMap.putExtra("com.example.charles.u_map.AREA",in.getStringExtra("com.example.charles.u_map.AREA"));
                goToMap.putExtra("com.example.charles.u_map.CLASSROOM", classrooms[position]);
                startActivity(goToMap);
            }
        });

        //When long clicked, add the classroom to favourite list.

        roomGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                CharSequence text = "Salón : " + in.getStringExtra("com.example.charles.u_map.AREA") + "-" + classrooms[position] + " agregado a favoritos";
                DataBase db = new DataBase();
                db.makeUpdateQuery("INSERT INTO FAVORITOS VALUES (" +  in.getStringExtra("com.example.charles.u_map.ID") + ",'" +  in.getStringExtra("com.example.charles.u_map.AREA") + "'," + classrooms[position] + ")");
                Toast t = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
                t.show();

                return  true;
            }
        });
    }

    //Check if the phone has google play services. These are needed for using the map.

    public  boolean hasGoogleServices(){

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(RoomSelector.this);

        //Everything is ok
        if (available == ConnectionResult.SUCCESS){
            return true;
        }
        //An error occurred, solve it.

        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(RoomSelector.this,available, Constants.ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(this,"You can't use the map",Toast.LENGTH_SHORT).show();
        }

        return false;
    }


    //If GPS is not enabled, ask the user to do so.

    private void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, Constants.PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });

        final AlertDialog alert = builder.create();

        alert.show();
    }

    //Check if the GPS is enabled.

    public boolean isGPSEnabled(){

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    //If the app hasn't the location permission, ask the user to aneable it.

    private void getLocationPermission() {

        //Check if we got the location permission

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermision = true;

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    //Check if the necessary resources are ready.

    private boolean checkMapServices(){

        // hasGoogleServices() && isGPSEnabled() ? return true : return false;

        if(hasGoogleServices()){
            if(isGPSEnabled()){
                return true;
            }
        }
        return false;
    }


    //Once the activity for enabling the GPS is done, check that is truly ready.

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(!locationPermision){
                    getLocationPermission();;
                }
            }
        }

    }

    //Once the activity for enabling permissions is done, check that are truly ready.

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        locationPermision = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermision = true;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(checkMapServices()){
            if(! locationPermision){
                getLocationPermission();
            }
        }
    }


}
