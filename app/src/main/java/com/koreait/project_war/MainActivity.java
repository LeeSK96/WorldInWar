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

    double lng, lat; // 경도, 위도
    List<NodeVO> node_list; // 노드들을 담을 리스트
    ArrayList<UnitVO> unit_list = new ArrayList<UnitVO>(); // 유닛들을 담을 리스트
    String nickname, color, level, nodename, u_idx, current_owner_nickname; // 해당 유저 정보 가져오기
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
                Toast.makeText(MainActivity.this, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
        });

        my_node_list = new ArrayList<>();

        Intent userintent = getIntent();
        u_idx = userintent.getStringExtra("user_idx");
        String result = "idx=" + u_idx;

        //로그인한 사용자 정보 가져오기
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

    // 권한설정
    private void setPermission(){
        TedPermission.with( this )
                .setPermissionListener(permissionListener)
                .setDeniedMessage("이 앱에서 요구하는 권한이 있습니다.\n[설정]->[권한]에서 활성화 해주세요")
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

    // 버튼 클릭 리스너 (인벤토리, 맵, 상점)
    View.OnClickListener mainClick = new View.OnClickListener() {
        Intent i;
        @Override
        public void onClick(View view) {
            switch (view.getId()){

                case R.id.btn_invent:// 인벤토리 버튼 클릭시
                    mp.stop();
                    i = new Intent(MainActivity.this, InventoryActivity.class);
                    i.putExtra("user_idx", Integer.parseInt(u_idx));
                    startActivity(i);
                    break;

                case R.id.btn_map:// 맵 버튼 클릭시
                    i = new Intent(MainActivity.this, MainActivity.class);
                    i.putExtra("user_idx", u_idx);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    break;

                case R.id.btn_market:// 상점 버튼 클릭시
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

    // 현재 나의 위치 마커 찍기
    public void oneMarker() {
        // 위치설정(위도, 경도) lat, lng
        // 37.554296, 126.935970
        LatLng current_loc = new LatLng(lat, lng);

        // 구글 맵에 표시할 마커에 대한 옵션 설정
        MarkerOptions makerOptions = new MarkerOptions();
        makerOptions
                .position(current_loc) // 마커위치
                //.title("원하는 위치(위도, 경도)에 마커를 표시했습니다.") // 타이틀
                //.snippet("여기는 여의도인거같네여!!") // 부가설명
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)) // 색상 모양
                .alpha(0.5f); // 투명도

        // 마커를 생성한다. showInfoWindow를 쓰면 처음부터 마커에 상세정보가 뜨게한다. (안쓰면 마커눌러야뜸)
        war_map.addMarker(makerOptions); //.showInfoWindow();

        //정보창 클릭 리스너
        //war_map.setOnInfoWindowClickListener(infoWindowClickListener);

        //마커 클릭 리스너
        war_map.setOnMarkerClickListener(markerClickListener);

        //카메라를 위치
        //war_map.moveCamera(CameraUpdateFactory.newLatLng(seoul));
        //처음 줌, 위의 코드와 중첩되면 아래 코드 우선시
        war_map.moveCamera(CameraUpdateFactory.newLatLngZoom(current_loc, 19));

    }

    // 주변 모든 노드들 찍기
    public void manyMarker() {

        // 여러개의 마커 찍기
        for (int idx = 0; idx < node_list.size(); idx++) {

            NodeVO vo = node_list.get(idx);

            int node_idx = vo.getIdx();
            double gps_x = vo.getGps_x();
            double gps_y = vo.getGps_y();
            int current_owner = vo.getCurrent_owner();
            String node_name = vo.getNode_name();

            // 1. 마커 옵션 설정 (만드는 과정)
            MarkerOptions makerOptions = new MarkerOptions();

            // 사용자의 노드인 경우
            if( current_owner == Integer.parseInt(u_idx) ){

                makerOptions
                        .position(new LatLng(gps_x, gps_y))
                        .title(node_name)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                        .alpha(0.5f);
            }else {

                makerOptions // LatLng에 대한 어레이를 만들어서 이용할 수도 있다.
                        .position(new LatLng(gps_x, gps_y))
                        .title(node_name); // 타이틀.
            }

            // 2. 마커 생성 (마커를 나타냄)

            war_map.addMarker(makerOptions);
        }
        //정보창 클릭 리스너
        //war_map.setOnInfoWindowClickListener(infoWindowClickListener);

        //마커 클릭 리스너
        war_map.setOnMarkerClickListener(markerClickListener);

        // 카메라를 위치로 옮긴다.
        //war_map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(37.52487, 126.92723)));

        //war_map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.554296, 126.935954), 17));


    }// manyMarker()

    // 마커 클릭 리스너
    GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {

            //선택한 타겟위치
            LatLng location = marker.getPosition();
            //Toast.makeText(MainActivity.this, "마커 클릭 Marker ID : "+markerId+"("+location.latitude+" "+location.longitude+")", Toast.LENGTH_SHORT).show();

            double loc_lat = location.latitude;
            double loc_lng = location.longitude;

            String result = "gps_x=" + loc_lat + "&gps_y=" + loc_lng;

            // 노드 이름, 소유자 가져오기
            new LocTask().execute(result);

            return false;
        }
    };

    // GPS 위치 리스너
    LocationListener gpsLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            lng = location.getLongitude();
            lat = location.getLatitude();
        }
    };

    // 해당 사용자의 정보를 가져올 AsyncTask 생성
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

                //서버 연결
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                //list.jsp?id=aa&pwd=111&type="type_login"
                sendMsg = strings[0];

                //서버로 파라미터 전달
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

            // 모든 노드 가져오기
            new NodeTask().execute();
        }
    } // UserTask

    // 해당 좌표로 유저의 정보 가져오는 AsyncTask 생성
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

                //서버 연결
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                //"gps_x=" + loc_lat + "&gps_y=" + loc_lng
                sendMsg = strings[0];

                //서버로 파라미터 전달
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
            //current Owner 설정 완료
            //Toast.makeText(MainActivity.this, "마커클릭 :"+current_owner, Toast.LENGTH_SHORT).show();

            String result2 = "current_owner=" + current_owner+"&"+s;
            // 노드 소유자 정보 가져오기
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
                conn.setRequestMethod("POST"); //전송방식
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
            // 내 자신 정보창
            if(loc_lat.equals(Double.toString(lat)) && loc_lng.equals(Double.toString(lng))){
                Toast.makeText(getApplicationContext(),"현재 나의 위치입니다.", Toast.LENGTH_SHORT).show();
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

            }else { // 내 자신을 제외한 다른 노드들의 정보창
                btn_atk = dialog.findViewById(R.id.btn_atk);
                btn_can = dialog.findViewById(R.id.btn_can);

                btn_atk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 공격버튼 클릭시

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
                        // 취소버튼 클릭시
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        }
    }// LocOwnerTask

    // 모든 노드의 위치를 찍을 AsyncTask 생성
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
                conn.setRequestMethod("POST"); //전송방식
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
            // 모든 유닛 가져오기
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
                conn.setRequestMethod("POST"); //전송방식
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
                conn.setRequestMethod("POST"); //전송방식
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

                    for (int i = 0; i < jsonArray.length(); i++) { //예비군 포함

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

            // GPS 권한 체크
            if(ActivityCompat.checkSelfPermission( MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission( MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED){
                setPermission();
                return;
            }

        /*locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        Location first_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        lng = first_loc.getLongitude();// 경도
        lat = first_loc.getLatitude();// 위도*/

            lng = 126.935970;// 경도
            lat = 37.554296;// 위도
            Toast.makeText(MainActivity.this,"위도 :" + lat + " / 경도 :" + lng,Toast.LENGTH_SHORT).show();

        /* gps 거리,시간마다 초기화되는 메서드
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




















