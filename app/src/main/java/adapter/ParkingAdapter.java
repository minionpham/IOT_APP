package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iot_app.R;

import java.util.List;

import model.Parking;

public class ParkingAdapter extends RecyclerView.Adapter<ParkingAdapter.ParkingViewHolder> {
    private Context context;
    private List<Parking> parkingList;

    public ParkingAdapter(Context context, List<Parking> parkingList) {
        this.context = context;
        this.parkingList = parkingList;
    }

    @NonNull
    @Override
    public ParkingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.parking_item,parent,false);
        return new ParkingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParkingViewHolder holder, int position) {
        Parking parking = parkingList.get(position);
        holder.parkingName.setText(parking.getParkingName());

        // Thay đổi màu sắc của container dựa trên trạng thái
        if (parking.getState().equals("ON")) {
            holder.parkingContainer.setBackgroundColor(context.getResources().getColor(R.color.red));
        } else {
            holder.parkingContainer.setBackgroundColor(context.getResources().getColor(R.color.green));
        }
    }

    @Override
    public int getItemCount() {
        return parkingList.size();
    }

    public class ParkingViewHolder extends RecyclerView.ViewHolder{
        TextView parkingName;
        LinearLayout parkingContainer;
        public ParkingViewHolder(@NonNull View itemView) {
            super(itemView);
            parkingName = itemView.findViewById(R.id.parking_name);
            parkingContainer = itemView.findViewById(R.id.parking_container);
        }
    }
}
