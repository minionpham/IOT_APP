package viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import Api.RetrofitClient;
import Api.UserApi;
import model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserViewModel extends ViewModel {
    private UserApi userApi;
    private MutableLiveData<List<User>> listMutableLiveData;
    private List<User> userList;
    public UserViewModel(){
        listMutableLiveData=new MutableLiveData<>();
        initData();
    }

    public MutableLiveData<List<User>> getListMutableLiveData() {
        return listMutableLiveData;
    }

    public void initData() {
        userList=new ArrayList<>();
        userApi = RetrofitClient.getInstance().create(UserApi.class);
        userApi.getUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                userList=response.body();
                listMutableLiveData.setValue(userList);
                Log.d("Users","Goi thanh cong list user");
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });
    }
    public void updateListdata(){
        userApi.getUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                userList=response.body();
                listMutableLiveData.setValue(userList);
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.d("Loi goi api all USER","Loi goi api users");
            }
        });
    }


}
