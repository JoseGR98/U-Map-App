package com.example.charles.u_map;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //Attribute declaration

    Intent in;
    GridView myGridView;
    String[] areas; //areas from college
    String[] description; //meaning of the acronym of the areas.
    TextView fav; //textview of favourites

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get the resources file from the project directory.

        Resources resources = getResources();

        //get the grid element from the view.

        myGridView = (GridView) findViewById(R.id.myGridView);

        //get the areas and descriptions from the string file.

        areas = resources.getStringArray(R.array.areas);
        description = resources.getStringArray(R.array.areasdescription);
        fav = (TextView) findViewById(R.id.favMain);

        //get information from the previous activity

        in = getIntent();

        //adapt the view to the areaAdapter format.
        areaAdapter areaA = new areaAdapter(this, areas, description);
        myGridView.setAdapter(areaA);


        //When favs label os clicked, start favs activity.

        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToFav = new Intent(getApplicationContext(), FavouriteClassrooms.class);
                goToFav.putExtra("com.example.charles.u_map.ID", in.getStringExtra("com.example.charles.u_map.ID"));
                startActivity(goToFav);
            }
        });

        //When any of the items is clicked, start the RoomSelector activity and pass the area selected and the user id.

        myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent goToRoom = new Intent(getApplicationContext(), RoomSelector.class);
                goToRoom.putExtra("com.example.charles.u_map.AREA",areas[position]);
                goToRoom.putExtra("com.example.charles.u_map.ID", in.getStringExtra("com.example.charles.u_map.ID"));
                startActivity(goToRoom);
            }
        });



    }
}
