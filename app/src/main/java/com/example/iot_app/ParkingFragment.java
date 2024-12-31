package com.example.iot_app;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import adapter.ParkingAdapter;
import model.Parking;
import viewmodel.ParkingViewModel;


public class ParkingFragment extends Fragment  {
RecyclerView recyclerViewParking;
ParkingViewModel parkingViewModel;
ParkingAdapter parkingAdapter;
ImageView imgReload;


    public ParkingViewModel getParkingViewModel() {
        return parkingViewModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_parking, container, false);
        recyclerViewParking = view.findViewById(R.id.recycler_view_parking);
        imgReload = view.findViewById(R.id.reload_icon);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewParking.setLayoutManager(linearLayoutManager);


        parkingViewModel=new ViewModelProvider(this).get(ParkingViewModel.class);
        parkingViewModel.getParkingMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<Parking>>() {
            @Override
            public void onChanged(List<Parking> parkings) {
                parkingAdapter=new ParkingAdapter(getContext(),parkings);
                recyclerViewParking.setAdapter(parkingAdapter);
            }
        });

        imgReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parkingViewModel.updateData();
            }
        });
        return view;
    }
}