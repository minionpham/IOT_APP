package Api;

import com.google.gson.JsonObject;

import java.util.List;

import model.Parking;
import model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ParkingApi {
    @GET("parkings/allparking")
    Call<List<Parking>> getParkings();
    @PUT("parkings/update/{ParkingId}")
    Call<Parking> updateParking(@Path("ParkingId") String parkingId, @Body JsonObject parkingupdate);

    @GET("parkings/count-on")
    Call<String> getCountStateOn();
}
