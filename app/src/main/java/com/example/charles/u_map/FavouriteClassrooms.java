package com.example.charles.u_map;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FavouriteClassrooms extends AppCompatActivity {

    ArrayList<String> areas  = new ArrayList<>();
    ArrayList<String> rooms = new ArrayList<>();

    GridView favGridView;
    private Intent in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_classrooms);

        //get data needed from previews activity, mainly the user ID.

        in = getIntent();

        //get the grid object from the view.

        favGridView = (GridView) findViewById(R.id.favouriteView);

        try {
            getFavouriteClassroom(in.getStringExtra("com.example.charles.u_map.ID"));

            //Convert the array lists into pure arrays

            String[] areasD = new String[areas.size()];
            String[] roomsD = new String[rooms.size()];

            areasD = areas.toArray(areasD);
            roomsD = rooms.toArray(roomsD);

            //init the adapter
            FavAdapter favAdapter = new FavAdapter(this, areasD, roomsD);

            //set the adapter to the view
            favGridView.setAdapter(favAdapter);

            //start the map activity whenever a object is clicked.

            favGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent goToMap = new Intent(getApplicationContext(), MapsActivity.class);
                    goToMap.putExtra("com.example.charles.u_map.LOCATION_PERMISSION",true);
                    goToMap.putExtra("com.example.charles.u_map.AREA",areas.get(position));
                    goToMap.putExtra("com.example.charles.u_map.CLASSROOM", rooms.get(position));
                    startActivity(goToMap);
                }
            });

        } catch (SQLException e) {
            System.out.println("Error getting fav");
            e.printStackTrace();
        }
    }

    //get the favourite classrooms of the user.

    private void getFavouriteClassroom(String id) throws SQLException {
        DataBase db = new DataBase();
        ResultSet result = db.makeQuery("SELECT * FROM FAVORITOS WHERE id = " + id);

        try {
            areas.add(result.getString("edificio"));
            rooms.add(result.getString("salon"));

            while (result.next()) {
                areas.add(result.getString("edificio"));
                rooms.add(result.getString("salon"));
            }
        } catch (Exception e){
            areas.add("Su lista está");
            rooms.add("Vacía");
            return;
        }
    }
}
