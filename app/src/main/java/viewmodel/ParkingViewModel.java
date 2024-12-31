package viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import Api.ParkingApi;
import Api.RetrofitClient;
import model.Parking;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParkingViewModel extends ViewModel {
    private MutableLiveData<List<Parking>> parkingMutableLiveData;
    private List<Parking> parkingList;
    private ParkingApi parkingApi;

    public MutableLiveData<List<Parking>> getParkingMutableLiveData() {
        return parkingMutableLiveData;
    }

    public ParkingViewModel(){
        parkingMutableLiveData=new MutableLiveData<>();
        initdata();
    }

    public void initdata() {
        parkingList=new ArrayList<>();
        parkingApi = RetrofitClient.getInstance().create(ParkingApi.class);
        parkingApi.getParkings().enqueue(new Callback<List<Parking>>() {
            @Override
            public void onResponse(Call<List<Parking>> call, Response<List<Parking>> response) {
                if(response.body() != null){
                    parkingList = response.body();
                    parkingMutableLiveData.setValue(parkingList);
                }
            }
            @Override
            public void onFailure(Call<List<Parking>> call, Throwable t) {
                Log.d("API Parking","Loi goi api parking");
            }
        });
    }

    public void updateData(){
        parkingApi.getParkings().enqueue(new Callback<List<Parking>>() {
            @Override
            public void onResponse(Call<List<Parking>> call, Response<List<Parking>> response) {
                if(response.body() != null){
                    parkingList=response.body();
                    parkingMutableLiveData.setValue(parkingList);
                }
            }
            @Override
            public void onFailure(Call<List<Parking>> call, Throwable t) {
                Log.d("API Parking","Loi goi api parking");
            }
        });
    }
}
