package com.example.iot_app;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.gson.JsonObject;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;
import Api.ParkingApi;
import Api.RetrofitClient;
import Api.UserApi;
import model.Parking;
import model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity  {
    MqttAndroidClient client;
    MqttConnectOptions options;
    private static final String BROKER_URL = "tcp://broker.hivemq.com:1883";
//    private static final String CLIENT_ID = "AndroidClient";
    private static final String TOPIC_SERVO = "servo_topic";
    private static final String TOPIC_RFID = "rfid_topic";
    private static final String TOPIC_INFRARED = "infrared_topic";
    private static final String TOPIC_STATE_PARKING="stateparking";
    private static final String mqtt_username = "minionpham";
    private static final String mqtt_password = "123";
    private String TAG="MQTT";
    private String TAGAPI="API";
    private String TagUser="User";
    private ParkingFragment parkingFragment;
    private ListUserFragment listUserFragment;
    private UserApi userApi;
    private ParkingApi parkingApi;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // LET CODE HERE
        initView();
        // setup toolbar
        setSupportActionBar(toolbar);

        String clientId = MqttClient.generateClientId();
        // Khoi tao client
        client = new MqttAndroidClient(this.getApplicationContext(), BROKER_URL, clientId);
        // Set option
        options = new MqttConnectOptions();
        options.setUserName(mqtt_username);
        options.setPassword(mqtt_password.toCharArray());
        options.setAutomaticReconnect(true);
        options.setCleanSession(false);
        options.setKeepAliveInterval(60);
        options.setConnectionTimeout(40);
        connectToBroker();

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.e(TAG, "Connection lost", cause);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String payload = new String(message.getPayload());
                Log.d(TAG, "Message received from topic: " + topic + " - Message: " + payload);
                // neu nhan message tu TOPIC cam bien hong ngoai
                if(topic.equals(TOPIC_INFRARED)){
                    JSONObject jsonObject = new JSONObject(payload);
                    String ParkingId = jsonObject.getString("ParkingId");
                    String ParkingState = jsonObject.getString("State");
                    updateStateParking(ParkingId,ParkingState);
                }

                if(topic.equals(TOPIC_RFID)){
                    // check so luong trong trg bai xe neu day hien ra man hinh thong bao
                    userApi.getCountUserOn().enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            String CountOn=response.body();
                            if(CountOn.equals("3")){
                                // Thong bao ra man hinh la bai xe full
                                Toast.makeText(MainActivity.this, "FULL PARKING!", Toast.LENGTH_SHORT).show();
                                publishMessage(TOPIC_STATE_PARKING,"Full Parking!");
                            }
                            else {
                                userApi.getUserById(payload).enqueue(new Callback<User>() {
                                    @Override
                                    public void onResponse(Call<User> call, Response<User> response) {
                                        User user = response.body();
                                        if(user != null){
                                            if(user.getState().equals("OFF")){
                                                updateStateUser(user,"ON"); // Chinh sua trang thai
                                            }
                                            else{
                                                parkingfee(user);
                                            }
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<User> call, Throwable t) {
                                        Log.d("Loi goi api","Loi api getuserbyid");
                                    }
                                });
                            }
                        }
                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.d("API Count","Loi goi api Count");
                        }
                    });
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d(TAG, "Message delivery complete!");
            }
        });
    }

    private void updateStateParking(String id, String state) {
        JsonObject parkingupdate= new JsonObject();
        parkingupdate.addProperty("State",state);
        parkingApi.updateParking(id,parkingupdate).enqueue(new Callback<Parking>() {
            @Override
            public void onResponse(Call<Parking> call, Response<Parking> response) {
                if(response.body()!= null){
                    updateStateListParking();
                }
            }
            @Override
            public void onFailure(Call<Parking> call, Throwable t) {
                Log.d("API Parking","Loi khi goi api parking");
            }
        });
    }

    private void updateStateListParking() {
        parkingFragment.getParkingViewModel().updateData();
        Log.d("Update Parking","Update Parking thành công.");
    }

    private void connectToBroker() {
        try {
            //Ket noi den broker
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this, "Connected!!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"Connected!");

                    // Subscribe topic
                    subscribeToTopic(TOPIC_SERVO);
                    subscribeToTopic(TOPIC_RFID);
                    subscribeToTopic(TOPIC_INFRARED);
                    subscribeToTopic(TOPIC_STATE_PARKING);
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this, "Connect failed....", Toast.LENGTH_SHORT).show();
                    // thuc hien ket noi lai tai day
                    Log.d(TAG,"Connect failed....");
                    // Thử lại kết nối sau 5 giây
                    new Handler().postDelayed(() -> connectToBroker(), 3000);
                }
            });
        } catch (MqttException  e) {
            e.printStackTrace();
        }
    }

    private void updateStateList() {
        listUserFragment.getUserViewModel().updateListdata();
        Log.d(TagUser,"Update user thanh cong!");
    }

    private void subscribeToTopic(String topic) {
        try {
            client.subscribe(topic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // Log và thông báo khi subscribe thành công
                    Log.d(TAG, "Successfully subscribed to topic: " + topic);
                    Toast.makeText(MainActivity.this, "Subscribed to topic: " + topic, Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Log lỗi và thông báo khi subscribe thất bại
                    Log.e(TAG, "Failed to subscribe to topic: " + topic + ", Error: " + exception.getMessage());
                    Toast.makeText(MainActivity.this, "Failed to subscribe to topic: " + topic, Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            Log.e(TAG, "Exception while subscribing to topic: " + topic + ", Error: " + e.getMessage());
            Toast.makeText(MainActivity.this, "Exception occurred during subscription", Toast.LENGTH_SHORT).show();
        }
    }

    private void publishMessage(String topic, String message){
        try {
            MqttMessage messageToPublish = new MqttMessage();
            messageToPublish.setPayload(message.getBytes());
            messageToPublish.setQos(0); // QoS: 0, 1 hoặc 2
            client.publish(topic, messageToPublish);
            Log.d(TAG, "Message published to topic: " + topic);
            Toast.makeText(MainActivity.this, "Message published!", Toast.LENGTH_SHORT).show();
        } catch (MqttException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to publish message.", e);
        }
    }
    private void initView(){
        toolbar=findViewById(R.id.toolbarmain);
        parkingFragment = (ParkingFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_parking);
        listUserFragment= (ListUserFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_users);
        userApi= RetrofitClient.getInstance().create(UserApi.class);
        parkingApi = RetrofitClient.getInstance().create(ParkingApi.class);
//        swipeRefreshLayout = findViewById(R.id.swiperefresh);
    }
    private void updateStateUser(User user, String state){
        JsonObject userUpdate = new JsonObject();
        userUpdate.addProperty("State", state);
        userApi.updateUser(user.getUserId(),userUpdate).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.body()!=null){
                    Log.d(TAG,"Chinh sua State thanh cong!");
                    // tao 1 dialog thong bao trong android
                    // Tbao ra man cho biet quet thanh cong ?
                    publishMessage(TOPIC_STATE_PARKING,"Welcome!");
                    publishMessage(TOPIC_SERVO,"ON"); // Mo thanh chan
                    updateStateList();
                }
                else{
                    Log.d(TAG,"Loi khi chinh sua State");
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d(TAGAPI,"Error call api update state");
            }
        });
    }

    private void parkingfee(User user) {
            if(user.getBalance() >= 10){
                int balanceUser=user.getBalance();
                updateBalanceUser(user.getUserId(),balanceUser-10,"OFF");
            }
            else{
                Toast.makeText(MainActivity.this, "Số dư không đủ!", Toast.LENGTH_SHORT).show();
                publishMessage(TOPIC_STATE_PARKING,"So du khong du!");
            }
    }
    private void updateBalanceUser(String userid,int newBalance,String state) {
        JsonObject userUpdate = new JsonObject();
        userUpdate.addProperty("Balance", newBalance);
        userUpdate.addProperty("State", state);
        userApi.updateUser(userid,userUpdate).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.body()!=null){
                    Log.d(TAGAPI,"Chinh sua State thanh cong!");
                    updateStateList(); // hien thi lai state list
                    // tao 1 dialog thong bao thanh toan thanh cong
                    Toast.makeText(MainActivity.this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                    publishMessage(TOPIC_STATE_PARKING,"Thanh toan thanh cong! Bye.");
                    publishMessage(TOPIC_SERVO,"ON");// tu mo chan
                }
                else{
                    Log.d(TAGAPI,"Loi khi chinh sua State");
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                    Log.d(TAGAPI,"Error call API Update Balance");
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (client != null && client.isConnected()) {
            try {
                client.disconnect();
                Log.d(TAG, "Disconnected from MQTT Broker");
            } catch (MqttException e) {
                e.printStackTrace();
                Log.e(TAG, "Failed to disconnect: " + e.getMessage());
            }
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_control,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_open_dialog){
            opendialogControl();
            return true;
        }
        if(item.getItemId() == R.id.action_add_user){
            opendialogAddUser();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void opendialogAddUser() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_user);

        Window window = dialog.getWindow();
        if(window == null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity= Gravity.CENTER;
        window.setAttributes(windowAttributes);
        dialog.setCancelable(true);
        EditText edtUserId,edtUserName,edtBalance,edtState;
        Button btnBack,btnAdd;
        edtUserId = dialog.findViewById(R.id.edtuserId);
        edtUserName = dialog.findViewById(R.id.edtuserName);
        edtBalance = dialog.findViewById(R.id.edtuserBalance);
        edtState = dialog.findViewById(R.id.edtuserState);
        btnAdd=dialog.findViewById(R.id.btnadd);
        btnBack=dialog.findViewById(R.id.btnthoat);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String UserId=edtUserId.getText().toString().trim();
                String UserName=edtUserName.getText().toString().trim();
                String Balance=edtBalance.getText().toString().trim();
                String State=edtState.getText().toString().trim();
                if(!UserId.isEmpty() && !UserName.isEmpty() && !Balance.isEmpty() && !State.isEmpty()) {
                    // add user
                    User user = new User(UserName,Integer.valueOf(Balance),UserId,State);
                    userApi.createUser(user).enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if(response.body() != null){
                                updateStateList();
                                Toast.makeText(MainActivity.this, "Thêm User thành công.", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                                Log.d("API ADD USER","Loi goi api add");
                        }
                    });
                }
                else{
                    Toast.makeText(MainActivity.this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    private void opendialogControl() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_control);

        Window window = dialog.getWindow();
        if(window == null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity= Gravity.CENTER;
        window.setAttributes(windowAttributes);
        dialog.setCancelable(true);
        Button btn_on,btn_off;
        btn_on=dialog.findViewById(R.id.btn_servo_on);
        btn_off=dialog.findViewById(R.id.btn_servo_off);
        btn_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishMessage(TOPIC_SERVO,"ON");
            }
        });
        btn_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishMessage(TOPIC_SERVO,"OFF");
            }
        });
        dialog.show();
    }

}