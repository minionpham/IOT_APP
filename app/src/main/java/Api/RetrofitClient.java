package Api;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://10.0.2.2:5000/api/"; // Đối với máy ảo Android
    private static Retrofit retrofit;
    static Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyy").create();

    static OkHttpClient okHttpClient = new OkHttpClient.Builder().
            readTimeout(2500, TimeUnit.MILLISECONDS)
            .writeTimeout(10,TimeUnit.SECONDS)
            .connectTimeout(10,TimeUnit.SECONDS )
            .retryOnConnectionFailure(true)
            .addInterceptor(new RetryInterceptor(3))
            .protocols(Arrays.asList(Protocol.HTTP_1_1))
            .build();

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }
}
