package Api;

import com.google.gson.JsonObject;

import java.util.List;

import model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserApi {
    @GET("users/findall")
    Call<List<User>> getUsers();
    @GET("users/user/{userId}")
    Call<User> getUserById(@Path("userId") String userId);

    @POST("users/add")
    Call<User> createUser(@Body User user);

    @PUT("users/update/{UserId}")
    Call<User> updateUser(@Path("UserId") String userId, @Body JsonObject userupdate);

    @DELETE("users/delete/{UserId}")
    Call<User> deleteUser(@Path("UserId") String userId);
    @GET("users/count-users-on")
    Call<String> getCountUserOn();
}
