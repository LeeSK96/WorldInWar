package com.koreait.project_war;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.koreait.project_war.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap war_map;
    LocationManager locationManager;
    TextView user_id, node_name, user_level_nick, myinfo_node_name, myinfo_color, myinfo_id, user_color;
    Button btn_atk, btn_can, btn_invent, btn_map, btn_market, btn_mynodes, btn_deploy, btn_logout;
    Dialog my_dialog, dialog, dialog2;
    MediaPlayer mp;

    List<NodeVO> my_node_list;

    double lng, lat; // ??????, ??????
    List<NodeVO> node_list; // ???????????? ?????? ?????????
    ArrayList<UnitVO> unit_list = new ArrayList<UnitVO>(); // ???????????? ?????? ?????????
    String nickname, color, level, nodename, u_idx, current_owner_nickname; // ?????? ?????? ?????? ????????????
    int node_idx, current_owner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mp = MediaPlayer.create(this, R.raw.map);
        mp.setLooping(true);
        mp.start();
d
        btn_invent = findViewById(R.id.btn_invent);
        btn_map = findViewById(R.id.btn_map);
        btn_market = findViewById(R.id.btn_market);
        btn_mynodes = findViewById(R.id.btn_mynodes);
        btn_logout = findViewById(R.id.btn_logout);

        btn_invent.setOnClickListener(mainClick);
        btn_map.setOnClickListener(mainClick);
        btn_market.setOnClickListener(mainClick);
        btn_mynodes.setOnClickListener(mainClick);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                SharedPreferences auto = getSharedPreferences("setting", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = auto.edit();
                editor.clear();
                editor.commit();
                Toast.makeText(MainActivity.this, "???????????? ???????????????", Toast.LENGTH_SHORT).show();
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
        });

        my_node_list = new ArrayList<>();

        Intent userintent = getIntent();
        u_idx = userintent.getStringExtra("user_idx");
        String result = "idx=" + u_idx;

        //???????????? ????????? ?????? ????????????
        new UserTask().execute(result);
    }// onCreate()

    @Override
    protected void onPause() {
        super.onPause();
        mp.stop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mp = MediaPlayer.create(this, R.raw.map);
        mp.setLooping(true);
        mp.start();
    }

    // ????????????
    private void setPermission(){
        TedPermission.with( this )
                .setPermissionListener(permissionListener)
                .setDeniedMessage("??? ????????? ???????????? ????????? ????????????.\n[??????]->[??????]?????? ????????? ????????????")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .check();
    }

    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Intent i = new Intent(MainActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            finish();
        }
    };

    // ?????? ?????? ????????? (????????????, ???, ??????)
    View.OnClickListener mainClick = new View.OnClickListener() {
        Intent i;
        @Override
        public void onClick(View view) {
            switch (view.getId()){

                case R.id.btn_invent:// ???????????? ?????? ?????????
                    mp.stop();
                    i = new Intent(MainActivity.this, InventoryActivity.class);
                    i.putExtra("user_idx", Integer.parseInt(u_idx));
                    startActivity(i);
                    break;

                case R.id.btn_map:// ??? ?????? ?????????
                    i = new Intent(MainActivity.this, MainActivity.class);
                    i.putExtra("user_idx", u_idx);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    break;

                case R.id.btn_market:// ?????? ?????? ?????????
                    mp.stop();
                    i = new Intent(MainActivity.this, MarketActivity.class);
                    i.putExtra("user_idx", Integer.parseInt(u_idx));
                    startActivity(i);
                    break;

                case R.id.btn_mynodes:
                    dialog2.show();
                    break;

            }// switch
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        war_map = googleMap;
        war_map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        oneMarker();
        manyMarker();
    }// onMapReady()

    // ?????? ?????? ?????? ?????? ??????
    public void oneMarker() {
        // ????????????(??????, ??????) lat, lng
        // 37.554296, 126.935970
        LatLng current_loc = new LatLng(lat, lng);

        // ?????? ?????? ????????? ????????? ?????? ?????? ??????
        MarkerOptions makerOptions = new MarkerOptions();
        makerOptions
                .position(current_loc) // ????????????
                //.title("????????? ??????(??????, ??????)??? ????????? ??????????????????.") // ?????????
                //.snippet("????????? ????????????????????????!!") // ????????????
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)) // ?????? ??????
                .alpha(0.5f); // ?????????

        // ????????? ????????????. showInfoWindow??? ?????? ???????????? ????????? ??????????????? ????????????. (????????? ??????????????????)
        war_map.addMarker(makerOptions); //.showInfoWindow();

        //????????? ?????? ?????????
        //war_map.setOnInfoWindowClickListener(infoWindowClickListener);

        //?????? ?????? ?????????
        war_map.setOnMarkerClickListener(markerClickListener);

        //???????????? ??????
        //war_map.moveCamera(CameraUpdateFactory.newLatLng(seoul));
        //?????? ???, ?????? ????????? ???????????? ?????? ?????? ?????????
        war_map.moveCamera(CameraUpdateFactory.newLatLngZoom(current_loc, 19));

    }

    // ?????? ?????? ????????? ??????
    public void manyMarker() {

        // ???????????? ?????? ??????
        for (int idx = 0; idx < node_list.size(); idx++) {

            NodeVO vo = node_list.get(idx);

            int node_idx = vo.getIdx();
            double gps_x = vo.getGps_x();
            double gps_y = vo.getGps_y();
            int current_owner = vo.getCurrent_owner();
            String node_name = vo.getNode_name();

            // 1. ?????? ?????? ?????? (????????? ??????)
            MarkerOptions makerOptions = new MarkerOptions();

            // ???????????? ????????? ??????
            if( current_owner == Integer.parseInt(u_idx) ){

                makerOptions
                        .position(new LatLng(gps_x, gps_y))
                        .title(node_name)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                        .alpha(0.5f);
            }else {

                makerOptions // LatLng??? ?????? ???????????? ???????????? ????????? ?????? ??????.
                        .position(new LatLng(gps_x, gps_y))
                        .title(node_name); // ?????????.
            }

            // 2. ?????? ?????? (????????? ?????????)

            war_map.addMarker(makerOptions);
        }
        //????????? ?????? ?????????
        //war_map.setOnInfoWindowClickListener(infoWindowClickListener);

        //?????? ?????? ?????????
        war_map.setOnMarkerClickListener(markerClickListener);

        // ???????????? ????????? ?????????.
        //war_map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(37.52487, 126.92723)));

        //war_map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.554296, 126.935954), 17));


    }// manyMarker()

    // ?????? ?????? ?????????
    GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {

            //????????? ????????????
            LatLng location = marker.getPosition();
            //Toast.makeText(MainActivity.this, "?????? ?????? Marker ID : "+markerId+"("+location.latitude+" "+location.longitude+")", Toast.LENGTH_SHORT).show();

            double loc_lat = location.latitude;
            double loc_lng = location.longitude;

            String result = "gps_x=" + loc_lat + "&gps_y=" + loc_lng;

            // ?????? ??????, ????????? ????????????
            new LocTask().execute(result);

            return false;
        }
    };

    // GPS ?????? ?????????
    LocationListener gpsLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            lng = location.getLongitude();
            lat = location.getLatitude();
        }
    };

    // ?????? ???????????? ????????? ????????? AsyncTask ??????
    class UserTask extends AsyncTask<String, Void, String>{
        String ip = Util.IP;
        String sendMsg, receiveMsg;
        String svr_addr = "http://" + ip + ":9090/Project_war/user_info.jsp";

        @Override
        protected String doInBackground(String... strings) {
            try
            {
                String str = "";
                URL url = new URL(svr_addr);

                //?????? ??????
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                //list.jsp?id=aa&pwd=111&type="type_login"
                sendMsg = strings[0];

                //????????? ???????????? ??????
                osw.write(sendMsg);
                osw.flush();

                if(conn.getResponseCode() == conn.HTTP_OK)
                {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");

                    BufferedReader reader = new BufferedReader(tmp);

                    StringBuffer buffer = new StringBuffer();

                    while((str = reader.readLine()) != null)
                    {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();


                    JSONArray jarray = new JSONObject(receiveMsg).getJSONArray("res");
                    JSONObject jObject = jarray.getJSONObject(0);
                    nickname = jObject.getString("nickname");

                    jObject = jarray.getJSONObject(1);
                    color = jObject.getString("color");

                    jObject = jarray.getJSONObject(2);
                    level = jObject.getString("level");
                }
            }
            catch (Exception e)
            {
            }

            return receiveMsg;

        }

        @Override
        protected void onPostExecute(String s) {
            user_level_nick = findViewById(R.id.user_level_nick);
            user_color = findViewById(R.id.user_color);

            user_color.setBackgroundColor(Color.parseColor("#"+color));
            user_level_nick.setText("LV. "+level + " / " + nickname);

            Toast.makeText(MainActivity.this, "#"+color, Toast.LENGTH_SHORT).show();

            // ?????? ?????? ????????????
            new NodeTask().execute();
        }
    } // UserTask

    // ?????? ????????? ????????? ?????? ???????????? AsyncTask ??????
    class LocTask extends AsyncTask<String, Void, String>{

        String ip = Util.IP;
        String sendMsg, receiveMsg;
        String svr_addr = "http://" + ip + ":9090/Project_war/loc_info.jsp";

        @Override
        protected String doInBackground(String... strings) {
            try
            {
                String str = "";
                URL url = new URL(svr_addr);

                //?????? ??????
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                //"gps_x=" + loc_lat + "&gps_y=" + loc_lng
                sendMsg = strings[0];

                //????????? ???????????? ??????
                osw.write(sendMsg);
                osw.flush();

                if(conn.getResponseCode() == conn.HTTP_OK)
                {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");

                    BufferedReader reader = new BufferedReader(tmp);

                    StringBuffer buffer = new StringBuffer();

                    while((str = reader.readLine()) != null)
                    {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();

                    JSONArray jarray = new JSONObject(receiveMsg).getJSONArray("res");
                    JSONObject jObject = jarray.getJSONObject(0);
                    nodename = jObject.getString("node_name");

                    jObject = jarray.getJSONObject(1);
                    current_owner = jObject.getInt("current_owner");

                    jObject = jarray.getJSONObject(2);
                    node_idx = jObject.getInt("node_idx");
                }
            }
            catch (Exception e)
            {
            }

            return sendMsg;

        }

        @Override
        protected void onPostExecute(String s) {

            /*dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.user_dialog);
            node_name = dialog.findViewById(R.id.node_name);

            node_name.setText(nodename);*/
            //current Owner ?????? ??????
            //Toast.makeText(MainActivity.this, "???????????? :"+current_owner, Toast.LENGTH_SHORT).show();

            String result2 = "current_owner=" + current_owner+"&"+s;
            // ?????? ????????? ?????? ????????????
            new LocOwnerTask().execute(result2);
        }
    }// LocTask

    class LocOwnerTask extends AsyncTask<String, Void, String>{
        String ip = Util.IP;
        String sendMsg, receiveMsg, sendMsgFull;
        String serverip = "http://" + ip + ":9090/Project_war/loc_owner.jsp";

        @Override
        protected String doInBackground(String... strings) {

            String nickname_s ="", color_s="";
            try {
                String str = "";
                URL url = new URL(serverip);

                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST"); //????????????
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                sendMsgFull = strings[0];
                sendMsg = sendMsgFull.split("&")[0];

                osw.write(sendMsg);
                osw.flush();

                if(conn.getResponseCode() == conn.HTTP_OK){
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");

                    BufferedReader reader = new BufferedReader(tmp);

                    StringBuffer buffer = new StringBuffer();
                    while((str = reader.readLine())!=null){
                        buffer.append(str);
                    }

                    receiveMsg = buffer.toString();

                    JSONArray jarray = new JSONObject(receiveMsg).getJSONArray("res");
                    JSONObject jObject = jarray.getJSONObject(0);
                    nickname_s = jObject.getString("nickname");

                    jObject = jarray.getJSONObject(1);
                    color_s = jObject.getString("color");
                }// if

            }catch (Exception e){

            }

            return sendMsgFull+"&"+nickname_s+"&"+color_s;
        }

        @Override
        protected void onPostExecute(String s) {

            String[] lat_lon = s.split("&");

            String loc_lat = lat_lon[1].substring(lat_lon[1].lastIndexOf("=")+1);
            String loc_lng = lat_lon[2].substring(lat_lon[2].lastIndexOf("=")+1);
            current_owner_nickname = lat_lon[3];

            GradientDrawable gd1 = (GradientDrawable) ContextCompat.getDrawable(MainActivity.this, R.drawable.owner_color);
            gd1.setColor(Color.parseColor("#"+lat_lon[4]));

            dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.user_dialog);
            node_name = dialog.findViewById(R.id.node_name);
            user_id = dialog.findViewById(R.id.user_id);

            node_name.setText(nodename);
            user_id.setText(current_owner_nickname);
            user_id.setTextColor(Color.parseColor("#"+lat_lon[4]));
            // ??? ?????? ?????????
            if(loc_lat.equals(Double.toString(lat)) && loc_lng.equals(Double.toString(lng))){
                Toast.makeText(getApplicationContext(),"?????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
            }

            else if(u_idx.equals(Integer.toString(current_owner))){

                my_dialog = new Dialog(MainActivity.this);
                my_dialog.setContentView(R.layout.myinfo_dialog);
                myinfo_node_name = my_dialog.findViewById(R.id.myinfo_node_name);
                myinfo_node_name.setText(nodename);

                btn_deploy = my_dialog.findViewById(R.id.btn_deploy);
                btn_can = my_dialog.findViewById(R.id.btn_can);

                btn_deploy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(MainActivity.this, DeployActivity.class);
                        i.putExtra("u_idx", Integer.parseInt(u_idx));
                        i.putExtra("node_name", nodename);
                        i.putExtra("node_idx", node_idx);
                        startActivity(i);
                        my_dialog.dismiss();
                    }
                });

                btn_can.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        my_dialog.dismiss();
                    }
                });

                my_dialog.show();

            }else { // ??? ????????? ????????? ?????? ???????????? ?????????
                btn_atk = dialog.findViewById(R.id.btn_atk);
                btn_can = dialog.findViewById(R.id.btn_can);

                btn_atk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // ???????????? ?????????

                        String param = "current_owner_idx=" + current_owner
                                + "&node_idx=" + node_idx
                                + "&user_idx=" + u_idx;

                        //Toast.makeText(MainActivity.this, param, Toast.LENGTH_SHORT).show();
                        new AttackTask().execute(param);
                        dialog.dismiss();
                    }
                });

                btn_can.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // ???????????? ?????????
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        }
    }// LocOwnerTask

    // ?????? ????????? ????????? ?????? AsyncTask ??????
    class NodeTask extends AsyncTask<String, Void, String>{

        String ip = Util.IP;
        String sendMsg, receiveMsg;
        String serverip = Util.SERVER_IP;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String str = "";
                URL url = new URL(serverip);

                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST"); //????????????
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                sendMsg = "";

                osw.write(sendMsg);
                osw.flush();

                if(conn.getResponseCode() == conn.HTTP_OK){
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");

                    BufferedReader reader = new BufferedReader(tmp);

                    StringBuffer buffer = new StringBuffer();
                    while((str = reader.readLine())!=null){
                        buffer.append(str);
                    }

                    receiveMsg = buffer.toString();

                    node_list = new ArrayList<>();

                    JSONArray jarray = new JSONArray(receiveMsg);//.getJSONArray("res");
                    Log.i("MY", "" + jarray);

                    for (int i = 0; i < jarray.length(); i++){

                        JSONArray jObject = jarray.getJSONObject(i).getJSONArray("res");

                        for (int j = 0; j < jObject.length(); j++){
                            String idx = jObject.getJSONObject(j).getString("idx");
                            String gps_x  = jObject.getJSONObject(j).getString("gps_x");
                            String gps_y  = jObject.getJSONObject(j).getString("gps_y");
                            String node_name  = jObject.getJSONObject(j).getString("node_name");
                            String current_owner  = jObject.getJSONObject(j).getString("current_owner");

                            NodeVO nodeVO = new NodeVO();
                            nodeVO.setIdx(Integer.parseInt(idx));
                            nodeVO.setGps_x(Double.parseDouble(gps_x));
                            nodeVO.setGps_y(Double.parseDouble(gps_y));
                            nodeVO.setNode_name(node_name);
                            nodeVO.setCurrent_owner(Integer.parseInt(current_owner));

                            NodeVO vo = new NodeVO();
                            if(u_idx.equals(current_owner)){
                                vo.setNode_name(node_name);
                                vo.setGps_x(Double.parseDouble(gps_x));
                                vo.setGps_y(Double.parseDouble(gps_y));
                                my_node_list.add(vo);
                            }

                            node_list.add(i,nodeVO);

                        }// inner for

                    }// for

                }// if

            }catch (Exception e){

            }

            return receiveMsg;
        }

        @Override
        protected void onPostExecute(String s) {
            // ?????? ?????? ????????????
            new UnitTask().execute();
        }
    }// NodeTask

    class AttackTask extends AsyncTask<String, Void, String>{
        String serverip = Util.SERVER_IP_ATTACK;
        String sendMsg, receiveMsg;
        @Override
        protected String doInBackground(String... strings) {
            //strings[0] = //current_owner_idx & node_idx & user_idx
            try {
                String str = "";
                URL url = new URL(serverip);

                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST"); //????????????
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                sendMsg = strings[0];

                osw.write(sendMsg);
                osw.flush();

                if(conn.getResponseCode() == conn.HTTP_OK){
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");

                    BufferedReader reader = new BufferedReader(tmp);

                    StringBuffer buffer = new StringBuffer();
                    while((str = reader.readLine())!=null){
                        buffer.append(str);
                    }

                    receiveMsg = buffer.toString();

                }// if

            }catch (Exception e){

            }

            return receiveMsg;
        }

            @Override
        protected void onPostExecute(String s) {
                //Toast.makeText(MainActivity.this, receiveMsg, Toast.LENGTH_SHORT).show();

                JSONArray jsonArray;
                List<InvenVO> inven_list = new ArrayList<InvenVO>();
                int owner_def=0, owner_atk=0, user_def=0, user_atk=0;

                try {
                    jsonArray = new JSONArray(s);

                    for (int i = 0; i < jsonArray.length(); i++){

                    JSONArray jObject = jsonArray.getJSONObject(i).getJSONArray("res");

                    for (int j = 0; j < jObject.length(); j++){
                            String idx = jObject.getJSONObject(j).getString("idx");
                            String user_idx  = jObject.getJSONObject(j).getString("user_idx");
                            String unit_idx  = jObject.getJSONObject(j).getString("unit_idx");
                            String unit_amount  = jObject.getJSONObject(j).getString("unit_amount");
                            String unit_position = jObject.getJSONObject(j).getString("unit_position");
                            String unit_state  = jObject.getJSONObject(j).getString("unit_state");

                            InvenVO vo = new InvenVO();
                            vo.setIdx(Integer.parseInt(idx));
                            vo.setUser_idx(Integer.parseInt(user_idx));
                            vo.setUnit_idx(Integer.parseInt(unit_idx));
                            vo.setUnit_amount(Integer.parseInt(unit_amount));
                            vo.setUnit_position(Integer.parseInt(unit_position));
                            vo.setUnit_state(Integer.parseInt(unit_state));

                            inven_list.add(i,vo);


                        }// inner for

                    }// for

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for(int i=0;i<inven_list.size();i++){
                    if(Integer.toString(inven_list.get(i).getUser_idx()).equals(u_idx)){
                        user_atk += getATK(inven_list.get(i).getUnit_idx())*inven_list.get(i).getUnit_amount();
                        user_def += getDEF(inven_list.get(i).getUnit_idx())*inven_list.get(i).getUnit_amount();
                    }else{
                        owner_atk += getATK(inven_list.get(i).getUnit_idx())*inven_list.get(i).getUnit_amount();
                        owner_def += getDEF(inven_list.get(i).getUnit_idx())*inven_list.get(i).getUnit_amount();
                    }
                }

                Log.i("onAttack", "user_def" + user_def + "user_atk" + user_atk + "owner_def" + owner_def + "owner_atk"+ owner_atk);

                Intent i = new Intent(MainActivity.this, AttackActivity.class);
                i.putExtra("user_nick", nickname);
                i.putExtra("owner_nick", current_owner_nickname);
                i.putExtra("user_def", user_def);
                i.putExtra("user_atk", user_atk);
                i.putExtra("owner_def", owner_def);
                i.putExtra("owner_atk", owner_atk);
                startActivity(i);

        }

        public int getATK(int unit_idx){
            for(int i=0;i<unit_list.size();i++){
                if(unit_list.get(i).getIdx() == unit_idx){
                    return unit_list.get(i).getUnit_attack();
                }
            }
            return 0;
        }

        public int getDEF(int unit_idx){
            for(int i=0;i<unit_list.size();i++){
                if(unit_list.get(i).getIdx() == unit_idx){
                    return unit_list.get(i).getUnit_defence();
                }
            }
            return 0;
        }

        public UnitVO findUnitVO(int unit_idx){
            for(int i=0;i<unit_list.size();i++){
                if(unit_list.get(i).getIdx() == unit_idx){
                    return unit_list.get(i);
                }
            }
            return null;
        }

    }

    class UnitTask extends AsyncTask<Void, Void, ArrayList<UnitVO>>{

        String serverip = Util.SERVER_IP_TEST;
        String sendMsg, receiveMsg;

        @Override
        protected ArrayList<UnitVO> doInBackground(Void... voids) {
            try {
                String str = "";
                URL url = new URL(serverip);

                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST"); //????????????
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                sendMsg = "";

                osw.write(sendMsg);
                osw.flush();

                if(conn.getResponseCode() == conn.HTTP_OK){
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");

                    BufferedReader reader = new BufferedReader(tmp);

                    StringBuffer buffer = new StringBuffer();
                    while((str = reader.readLine())!=null){
                        buffer.append(str);
                    }

                    receiveMsg = buffer.toString();

                    JSONArray jsonArray = new JSONObject(receiveMsg).getJSONArray("result");

                    for (int i = 0; i < jsonArray.length(); i++) { //????????? ??????

                        JSONObject jObject = jsonArray.getJSONObject(i);
                        String unit_name = jObject.getString("unit_name");
                        String can_defence = Integer.toString(jObject.getInt("can_defence"));
                        String can_attack  = Integer.toString(jObject.getInt("can_attack"));
                        String unit_defence = Integer.toString(jObject.getInt("unit_defence"));
                        String unit_img = jObject.getString("unit_img");
                        String unit_price = Integer.toString(jObject.getInt("unit_price"));
                        String idx = Integer.toString(jObject.getInt("idx"));
                        String unit_attack = Integer.toString(jObject.getInt("unit_attack"));

                        UnitVO vo = new UnitVO();
                        vo.setUnit_defence(Integer.parseInt(unit_defence));
                        vo.setUnit_attack(Integer.parseInt(unit_attack));
                        vo.setUnit_name(unit_name);
                        vo.setUnit_price(Integer.parseInt(unit_price));
                        vo.setUnit_img(unit_img);
                        vo.setIdx(Integer.parseInt(idx));
                        vo.setCan_attack(Integer.parseInt(can_attack));
                        vo.setCan_defence(Integer.parseInt(can_defence));

                        unit_list.add(vo);
                    }
                }// if

            }catch (Exception e){

            }

            return unit_list;
        }

        @Override
        protected void onPostExecute(ArrayList<UnitVO> unitVOS) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(MainActivity.this);

            // GPS ?????? ??????
            if(ActivityCompat.checkSelfPermission( MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission( MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED){
                setPermission();
                return;
            }

        /*locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        Location first_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        lng = first_loc.getLongitude();// ??????
        lat = first_loc.getLatitude();// ??????*/

            lng = 126.935970;// ??????
            lat = 37.554296;// ??????
            Toast.makeText(MainActivity.this,"?????? :" + lat + " / ?????? :" + lng,Toast.LENGTH_SHORT).show();

        /* gps ??????,???????????? ??????????????? ?????????
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                                10000,
                                                10,
                                                gpsLocationListener);

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                                10000,
                                                10,
                                                gpsLocationListener);*/

            MyNodesAdapter adapter = new MyNodesAdapter(MainActivity.this, R.layout.nodename, my_node_list);
            dialog2 = new Dialog(MainActivity.this);
            dialog2.setContentView(R.layout.dialog_mynodes);

            ListView mynode_list = dialog2.findViewById(R.id.mynode_list);
            mynode_list.setAdapter(adapter);

            mynode_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    LatLng current_loc = new LatLng(my_node_list.get(position).getGps_x(), my_node_list.get(position).getGps_y());
                    dialog2.dismiss();
                    war_map.moveCamera(CameraUpdateFactory.newLatLngZoom(current_loc, 19));
                }
            });
        }
    }
}




















