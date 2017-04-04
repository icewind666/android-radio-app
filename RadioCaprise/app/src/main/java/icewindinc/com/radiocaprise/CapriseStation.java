package icewindinc.com.radiocaprise;

/**
 * Created by icewind on 18.02.17.
 */

public class CapriseStation {
    public String name;
    public String stream;
    public Genre genre;
    public String picture;

    public CapriseStation() {

    }

    public CapriseStation(String name, String stream, String pic) {
        this.name = name;
        this.stream = stream;
        this.picture = pic;
    }

    public void setGenre(Genre g) {
        genre = g;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) { return false; }
        if (!(o instanceof CapriseStation)) { return false; }
        CapriseStation aStation = (CapriseStation) o;
        return aStation.name.equals(this.name);
    }
}
