package com.example.iot_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import Api.RetrofitClient;
import Api.UserApi;
import adapter.UserAdapter;
import model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import viewmodel.UserViewModel;


public class ListUserFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView recyclerViewUser;
    private UserAdapter userAdapter;
    private List<User> userList;
    private UserApi userApi;
    private String TAG="API";
    private UserViewModel userViewModel;
    SwipeRefreshLayout swipeRefreshLayout;

    public UserViewModel getUserViewModel() {
        return userViewModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_list_user,container,false);
        recyclerViewUser = view.findViewById(R.id.list_user);
        swipeRefreshLayout=view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        userApi = RetrofitClient.getInstance().create(UserApi.class);
        // Goi api de get userList
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewUser.setLayoutManager(linearLayoutManager);

        userViewModel=new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getListMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                if(users != null){
                    Log.d("ListUser",users.get(0).getUserName());
                    userAdapter=new UserAdapter(getContext(),users);
                    recyclerViewUser.setAdapter(userAdapter);
                }
                else{
                    Log.d("ListUser","Deo co List User");
                }
            }
        });
        return view;
    }

    @Override
    public void onRefresh() {
        userViewModel.updateListdata();
        swipeRefreshLayout.setRefreshing(false);
    }
}