package edu.brown.cs.student.Place;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;

public class PlaceAPIUtilities {
  public static Place deserializePlace(String jsonPlace) {
    try {
      // Initializes Moshi
      Moshi moshi = new Moshi.Builder().build();

      // Initializes an adapter to an Place class then uses it to parse the JSON.
      JsonAdapter<Place> adapter = moshi.adapter(Place.class);
      Place place = adapter.fromJson(jsonPlace);

      return place;
    }
    // Returns an empty activity... Probably not the best handling of this error case...
    // Notice an alternative error throwing case to the one done in OrderHandler. This catches
    // the error instead of pushing it up.
    catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}
