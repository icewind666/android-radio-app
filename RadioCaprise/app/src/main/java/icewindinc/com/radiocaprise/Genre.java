package icewindinc.com.radiocaprise;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * Created by icewind on 18.02.17.
 */

public class Genre {

    public ArrayList<CapriseStation> stations;
    public String name;

    public Genre(String name) {
        this.name = name;
        stations = new ArrayList<>();
    }

    public void addStation(CapriseStation s) {
        stations.add(s);
    }

    public void addStation(String name, String stream, String picture) {
        CapriseStation newStation = new CapriseStation(name, stream, picture);
        newStation.setGenre(this);
        addStation(newStation);
    }

    public ArrayList<CapriseStation> getStations() {
        return stations;
    }
}
