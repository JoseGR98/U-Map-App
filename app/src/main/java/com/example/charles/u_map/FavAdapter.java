package com.example.charles.u_map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class FavAdapter extends  BaseAdapter{

    LayoutInflater layoutI;
    String areas[];
    String rooms[];

    //Get the area and classrooms info

    public FavAdapter(Context c, String[]  a, String[] r){
        areas = a;
        rooms = r;
        layoutI = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //Obligatory methods for the adapter.


    //Get the number of elements to be added.

    @Override
    public int getCount() {
        return areas.length;
    }

    //get the element at the n position.
    @Override
    public Object getItem(int position) {
        return areas[position];
    }

    //since there is no ID, the position is returned.
    @Override
    public long getItemId(int position) {
        return position;
    }

    //Iterate over the elements and add them into the grid view with the format fav_nav.xml
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = layoutI.inflate(R.layout.fav_nav, null);
        TextView roomTextView = (TextView) v.findViewById(R.id.roomTextView);
        TextView areaTextView = (TextView) v.findViewById(R.id.areaTextView);

        String room = rooms[position];
        String area = areas[position];

        roomTextView.setText(room);
        areaTextView.setText(area);

        return v;
    }
}
