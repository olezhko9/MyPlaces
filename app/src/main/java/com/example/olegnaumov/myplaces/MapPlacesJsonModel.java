package com.example.olegnaumov.myplaces;

import android.content.Context;

import com.example.olegnaumov.myplaces.model.MyPlace;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MapPlacesJsonModel implements MapPlacesContract.Model {

    private List<MyPlace> places = new ArrayList<>();
    private String filename = "places.json";
    private Context context;

    public MapPlacesJsonModel(Context context) {
        this.context = context;
    }

    @Override
    public List<MyPlace> getAllPlaces() {
        String jsonPlace = readFromJSON();
        if (jsonPlace != null) {
            places = new Gson().fromJson(jsonPlace, new TypeToken<List<MyPlace>>(){}.getType());
        }
        return places;
    }

    @Override
    public int addPlace(MyPlace place) {
        places.add(place);
        writeToJSON(new Gson().toJson(places));
        return places.size() - 1;
    }

    @Override
    public void deletePlace(int tag) {
        places.remove(tag);
        writeToJSON(new Gson().toJson(places));
    }

    @Override
    public MyPlace getPlace() {
        return places.get(0);
    }

    private void writeToJSON(String jsonString) {
        try {
            File file = new File(context.getFilesDir(), filename);
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(jsonString.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readFromJSON() {
        try {
            File file = new File(context.getFilesDir(), filename);
            FileInputStream inputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String jsonPlace = bufferedReader.readLine();

            inputStream.close();
            return jsonPlace;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
