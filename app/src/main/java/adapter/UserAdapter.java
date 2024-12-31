package adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iot_app.R;
import com.google.gson.JsonObject;

import java.util.List;

import Api.RetrofitClient;
import Api.UserApi;
import model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{
    private Context context;
    private List<User> userList;
    private UserApi userApi;

    // Constructor
    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userName.setText(user.getUserName());
        holder.userBalance.setText(String.format("Balance: %s", user.getBalance()));
        holder.userState.setText(user.getState());
        if (user.getState().equals("ON")) {
            holder.userState.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.userState.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }

        // Xu ly su kien khi click nut chinh sua
        holder.editButton.setOnCreateContextMenuListener(((menu, v, menuInfo) -> {
            menu.setHeaderTitle("User Actions");
            menu.add(0, holder.getAdapterPosition(), 0, "View User Info")
                    .setOnMenuItemClickListener(item -> {
                        viewUserInfo(user);
                        return true;
                    });
            menu.add(0, holder.getAdapterPosition(), 1, "Edit User Info")
                    .setOnMenuItemClickListener(item -> {
                        editUserInfo(user);
                        return true;
                    });
            menu.add(0, holder.getAdapterPosition(), 2, "Delete User")
                    .setOnMenuItemClickListener(item -> {
                        deleteUser(user);
                        return true;
                    });
        }));
    }

    private void deleteUser(User user) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_del_user);

        Window window = dialog.getWindow();
        if(window == null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity=Gravity.CENTER;
        window.setAttributes(windowAttributes);
        dialog.setCancelable(true);
        Button btnthoat,btnDel;
        btnthoat=dialog.findViewById(R.id.btnthoat);
        btnDel = dialog.findViewById(R.id.btndel);

        btnthoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // goi api delete user
                userApi = RetrofitClient.getInstance().create(UserApi.class);
                userApi.deleteUser(user.getUserId()).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if(response.body() != null){
                            dialog.dismiss();
                            Toast.makeText(context, "Done Delete!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.d("API DEL","Loi goi api delete");
                    }
                });
            }
        });
    dialog.show();
    }

    private void editUserInfo(User user) {
        // edit dialog
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_user);

        Window window = dialog.getWindow();
        if(window == null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity=Gravity.CENTER;
        window.setAttributes(windowAttributes);
        dialog.setCancelable(true);
        EditText edtUserId,edtUserName,edtUserBalance,edtUserState;
        Button btnThoat,btnEdit;
        edtUserId=dialog.findViewById(R.id.edtuserId);
        edtUserName=dialog.findViewById(R.id.edtuserName);
        edtUserBalance=dialog.findViewById(R.id.edtuserBalance);
        edtUserState=dialog.findViewById(R.id.edtuserState);
        btnThoat=dialog.findViewById(R.id.btnthoat);
        btnEdit=dialog.findViewById(R.id.btnchange);
        edtUserId.setText(user.getUserId());
        edtUserName.setText(user.getUserName());
        edtUserBalance.setText(String.valueOf(user.getBalance()));
        edtUserState.setText(user.getState());
        btnThoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userid=edtUserId.getText().toString();
                String username=edtUserName.getText().toString();
                String userbalance=edtUserBalance.getText().toString();
                String userstate=edtUserState.getText().toString();
                if(userid != "" && username !="" && userbalance != "" && userstate != ""){
                    // goi api update
                    userApi = RetrofitClient.getInstance().create(UserApi.class);
                    JsonObject userUpdate = new JsonObject();
                    userUpdate.addProperty("UserId", userid);
                    userUpdate.addProperty("UserName", username);
                    userUpdate.addProperty("Balance",Integer.valueOf(userbalance));
                    userUpdate.addProperty("State", userstate);
                    userApi.updateUser(userid,userUpdate).enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            dialog.dismiss();
                            Toast.makeText(context, "Edit User thành công.", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Toast.makeText(context, "Error Edit...", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    Toast.makeText(context, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }
    private void viewUserInfo(User user) {
        // hien thi thong tin tren 1 dialog
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_check_user);

        Window window = dialog.getWindow();
        if(window == null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity=Gravity.CENTER;
        window.setAttributes(windowAttributes);
        dialog.setCancelable(true);
        TextView tvUserId,tvUserName,tvUserBalance,tvUserState;
        Button btnThoat;
        tvUserId=dialog.findViewById(R.id.tvuserId);
        tvUserName=dialog.findViewById(R.id.tvuserName);
        tvUserBalance=dialog.findViewById(R.id.tvuserBalance);
        tvUserState=dialog.findViewById(R.id.tvuserState);
        btnThoat=dialog.findViewById(R.id.btnthoat);
        tvUserId.setText(user.getUserId());
        tvUserName.setText(user.getUserName());
        tvUserBalance.setText(String.valueOf(user.getBalance()));
        tvUserState.setText(user.getState());
        btnThoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userBalance, userState;
        ImageView editButton;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            userBalance = itemView.findViewById(R.id.user_balance);
            userState = itemView.findViewById(R.id.user_state);
            editButton = itemView.findViewById(R.id.edit_button);
        }
    }
}
