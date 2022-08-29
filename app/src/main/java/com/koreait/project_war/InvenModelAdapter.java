package com.koreait.project_war;

import android.app.Activity;
import android.app.AsyncNotedAppOp;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import com.koreait.project_war.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class InvenModelAdapter extends ArrayAdapter<InvenUnitVO> {

    List<InvenUnitVO> list = new ArrayList<>();
    Context context;
    int resource;
    TextView txt_name, txt_atk, txt_def, txt_node, txt_cnt, txt_state;
    Button btn_state;
    Dialog dialog;

    ImageView unit_img;

    int current_units_state, u_idx;

    Button cnt_down, cnt_up, btn_toUndeployed, btn_toAttack, btn_toDefence, btn_cancel;
    TextView cnt;



    public InvenModelAdapter(@NonNull Context context, int resource, List<InvenUnitVO> list) {
        super(context, resource, list);
        this.context = context;
        this.resource = resource;
        this.list = list;
        this.current_units_state = list.get(0).getUnit_state();
        this.u_idx = list.get(0).getUser_idx();

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater vlinf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = vlinf.inflate(resource, null);

        final InvenUnitVO vo = list.get(position);

        btn_state = convertView.findViewById(R.id.btn_state);
        txt_name = convertView.findViewById(R.id.txt_name);
        txt_atk = convertView.findViewById(R.id.txt_atk);
        txt_def = convertView.findViewById(R.id.txt_def);
        txt_cnt = convertView.findViewById(R.id.txt_cnt);
        unit_img = convertView.findViewById(R.id.unit_img);

        txt_name.setText(vo.getUnit_name().toString());
        txt_atk.setText(Integer.toString(vo.getUnit_attack()));
        txt_def.setText(Integer.toString(vo.getUnit_defence()));
        txt_cnt.setText(Integer.toString(vo.getUnit_amount()));
        unit_img.setImageResource(context.getResources().getIdentifier(vo.getUnit_img(),"drawable",context.getPackageName()));

        btn_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //user_idx , unit_amount , unit_state, unit_position , unit_name, unit_attack, unit_defence
                dialog = new Dialog(getContext());
                switch (current_units_state){
                    case 0:
                        dialog.setContentView(R.layout.dialog_undeployed);
                        //기능 설정
                        cnt_down = dialog.findViewById(R.id.cnt_down);
                        cnt_up = dialog.findViewById(R.id.cnt_up);
                        btn_toAttack = dialog.findViewById(R.id.btn_toAttack);
                        btn_toDefence = dialog.findViewById(R.id.btn_toDefence);
                        btn_cancel = dialog.findViewById(R.id.btn_cancel);
                        cnt = dialog.findViewById(R.id.cnt);

                        if(vo.getCan_attack() == 0){
                            btn_toAttack.setVisibility(View.GONE);
                        }else{
                            btn_toAttack.setVisibility(View.VISIBLE);
                        }

                        if(vo.getCan_defence() == 0){
                            btn_toDefence.setVisibility(View.GONE);
                        }else{
                            btn_toDefence.setVisibility(View.VISIBLE);
                        }

                        cnt_down.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(Integer.parseInt(cnt.getText().toString()) == 0){
                                    Toast.makeText(getContext(), "Cannot deploy below 0", Toast.LENGTH_SHORT).show();
                                }else{
                                    cnt.setText(""+(Integer.parseInt(cnt.getText().toString())-1));
                                }
                            }
                        });

                        cnt_up.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int max = vo.getUnit_amount();
                                if(Integer.parseInt(cnt.getText().toString()) >= max){
                                    Toast.makeText(getContext(), "You only have "+max+" unit(s)", Toast.LENGTH_SHORT).show();
                                }else{
                                    cnt.setText(""+(Integer.parseInt(cnt.getText().toString())+1));
                                }
                            }
                        });

                        btn_toAttack.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int sendAmount = Integer.parseInt(cnt.getText().toString());
                                if( sendAmount > 0){
                                    // 0번 state 의 amount 감소, 1번 state 의 amount 증가(혹은 값 추가)
                                    String param = "user_idx="+u_idx+"&unit_idx="+vo.getUnit_idx()+
                                            "&moving_unit_amount="+sendAmount+"&original_unit_amount="+vo.getUnit_amount();

                                    new ToAttackTask().execute(u_idx, vo.getUnit_idx(), sendAmount, vo.getUnit_amount(), current_units_state);
                                }else{
                                    Toast.makeText(getContext(), "You must deploy at least 1 unit", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        btn_toDefence.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int sendAmount = Integer.parseInt(cnt.getText().toString());
                                if( sendAmount > 0){
                                    // 0번 state 의 amount 감소, 1번 state 의 amount 증가(혹은 값 추가)
                                    String param = "user_idx="+u_idx+"&unit_idx="+vo.getUnit_idx()+
                                            "&moving_unit_amount="+sendAmount+"&original_unit_amount="+vo.getUnit_amount();

                                    new ToDefenceTask().execute(u_idx, vo.getUnit_idx(), sendAmount, vo.getUnit_amount(), current_units_state);
                                }else{
                                    Toast.makeText(getContext(), "You must deploy at least 1 unit", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        btn_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        break;
                    case 1:
                        dialog.setContentView(R.layout.dialog_attack);
                        //기능 설정
                        cnt_down = dialog.findViewById(R.id.cnt_down);
                        cnt_up = dialog.findViewById(R.id.cnt_up);
                        btn_toUndeployed = dialog.findViewById(R.id.btn_toUndeployed);
                        btn_toDefence = dialog.findViewById(R.id.btn_toDefence);
                        btn_cancel = dialog.findViewById(R.id.btn_cancel);
                        cnt = dialog.findViewById(R.id.cnt);

                        if(vo.getCan_defence() == 0){
                            btn_toDefence.setVisibility(View.GONE);
                        }else{
                            btn_toDefence.setVisibility(View.VISIBLE);
                        }

                        cnt_down.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(Integer.parseInt(cnt.getText().toString()) == 0){
                                    Toast.makeText(getContext(), "Cannot deploy below 0", Toast.LENGTH_SHORT).show();
                                }else{
                                    cnt.setText(""+(Integer.parseInt(cnt.getText().toString())-1));
                                }
                            }
                        });

                        cnt_up.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int max = vo.getUnit_amount();
                                if(Integer.parseInt(cnt.getText().toString()) >= max){
                                    Toast.makeText(getContext(), "You only have "+max+" unit(s)", Toast.LENGTH_SHORT).show();
                                }else{
                                    cnt.setText(""+(Integer.parseInt(cnt.getText().toString())+1));
                                }
                            }
                        });

                        btn_toUndeployed.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int sendAmount = Integer.parseInt(cnt.getText().toString());
                                if( sendAmount > 0){
                                    // 0번 state 의 amount 감소, 1번 state 의 amount 증가(혹은 값 추가)
                                    String param = "user_idx="+u_idx+"&unit_idx="+vo.getUnit_idx()+
                                            "&moving_unit_amount="+sendAmount+"&original_unit_amount="+vo.getUnit_amount();

                                    new ToUndeployedTask().execute(u_idx, vo.getUnit_idx(), sendAmount, vo.getUnit_amount(), current_units_state);
                                }else{
                                    Toast.makeText(getContext(), "You must deploy at least 1 unit", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        btn_toDefence.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int sendAmount = Integer.parseInt(cnt.getText().toString());
                                if( sendAmount > 0){
                                    // 0번 state 의 amount 감소, 1번 state 의 amount 증가(혹은 값 추가)
                                    String param = "user_idx="+u_idx+"&unit_idx="+vo.getUnit_idx()+
                                            "&moving_unit_amount="+sendAmount+"&original_unit_amount="+vo.getUnit_amount();

                                    new ToDefenceTask().execute(u_idx, vo.getUnit_idx(), sendAmount, vo.getUnit_amount(), current_units_state);
                                }else{
                                    Toast.makeText(getContext(), "You must deploy at least 1 unit", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        btn_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        break;
                    case 2:
                        if(btn_state.getText().toString().equals("DEPLOY")){
                            Toast.makeText(context, "새로운시작~~^^", Toast.LENGTH_SHORT).show();
                            dialog.setContentView(R.layout.dialog_deploy);
                            //기능 설정
                            cnt_down = dialog.findViewById(R.id.cnt_down);
                            cnt_up = dialog.findViewById(R.id.cnt_up);
                            btn_toDefence = dialog.findViewById(R.id.btn_toDefence);
                            btn_cancel = dialog.findViewById(R.id.btn_cancel);
                            cnt = dialog.findViewById(R.id.cnt);

                            cnt_down.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (Integer.parseInt(cnt.getText().toString()) == 0) {
                                        Toast.makeText(getContext(), "Cannot deploy below 0", Toast.LENGTH_SHORT).show();
                                    } else {
                                        cnt.setText("" + (Integer.parseInt(cnt.getText().toString()) - 1));
                                    }
                                }
                            });

                            cnt_up.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    int max = vo.getUnit_amount();
                                    if (Integer.parseInt(cnt.getText().toString()) >= max) {
                                        Toast.makeText(getContext(), "You only have " + max + " unit(s)", Toast.LENGTH_SHORT).show();
                                    } else {
                                        cnt.setText("" + (Integer.parseInt(cnt.getText().toString()) + 1));
                                    }
                                }
                            });

                            btn_toDefence.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    int sendAmount = Integer.parseInt(cnt.getText().toString());
                                    if( sendAmount > 0){
                                        // 0번 state 의 amount 감소, 1번 state 의 amount 증가(혹은 값 추가)
                                        String param = "user_idx="+u_idx+"&unit_idx="+vo.getUnit_idx()+
                                                "&moving_unit_amount="+sendAmount+"&original_unit_amount="+vo.getUnit_amount();

                                        Toast.makeText(getContext(), param, Toast.LENGTH_SHORT).show();
                                        //new ToNodeTask().execute(u_idx, vo.getUnit_idx(), sendAmount, vo.getUnit_amount(), vo.getUnit_position());
                                    }else{
                                        Toast.makeText(getContext(), "You must deploy at least 1 unit", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            btn_cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });


                        }else {
                            dialog.setContentView(R.layout.dialog_defence);

                            //기능 설정
                            cnt_down = dialog.findViewById(R.id.cnt_down);
                            cnt_up = dialog.findViewById(R.id.cnt_up);
                            btn_toUndeployed = dialog.findViewById(R.id.btn_toUndeployed);
                            btn_toAttack = dialog.findViewById(R.id.btn_toAttack);
                            btn_cancel = dialog.findViewById(R.id.btn_cancel);
                            cnt = dialog.findViewById(R.id.cnt);

                            if (vo.getCan_attack() == 0) {
                                btn_toAttack.setVisibility(View.GONE);
                            } else {
                                btn_toAttack.setVisibility(View.VISIBLE);
                            }

                            cnt_down.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (Integer.parseInt(cnt.getText().toString()) == 0) {
                                        Toast.makeText(getContext(), "Cannot deploy below 0", Toast.LENGTH_SHORT).show();
                                    } else {
                                        cnt.setText("" + (Integer.parseInt(cnt.getText().toString()) - 1));
                                    }
                                }
                            });

                            cnt_up.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    int max = vo.getUnit_amount();
                                    if (Integer.parseInt(cnt.getText().toString()) >= max) {
                                        Toast.makeText(getContext(), "You only have " + max + " unit(s)", Toast.LENGTH_SHORT).show();
                                    } else {
                                        cnt.setText("" + (Integer.parseInt(cnt.getText().toString()) + 1));
                                    }
                                }
                            });

                            btn_toUndeployed.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    int sendAmount = Integer.parseInt(cnt.getText().toString());
                                    if (sendAmount > 0) {
                                        // 0번 state 의 amount 감소, 1번 state 의 amount 증가(혹은 값 추가)
                                        String param = "user_idx=" + u_idx + "&unit_idx=" + vo.getUnit_idx() +
                                                "&moving_unit_amount=" + sendAmount + "&original_unit_amount=" + vo.getUnit_amount();

                                        new ToUndeployedTask().execute(u_idx, vo.getUnit_idx(), sendAmount, vo.getUnit_amount(), current_units_state);
                                    } else {
                                        Toast.makeText(getContext(), "You must deploy at least 1 unit", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            btn_toAttack.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    int sendAmount = Integer.parseInt(cnt.getText().toString());
                                    if (sendAmount > 0) {
                                        // 0번 state 의 amount 감소, 1번 state 의 amount 증가(혹은 값 추가)
                                        String param = "user_idx=" + u_idx + "&unit_idx=" + vo.getUnit_idx() +
                                                "&moving_unit_amount=" + sendAmount + "&original_unit_amount=" + vo.getUnit_amount();

                                        new ToAttackTask().execute(u_idx, vo.getUnit_idx(), sendAmount, vo.getUnit_amount(), current_units_state);
                                    } else {
                                        Toast.makeText(getContext(), "You must deploy at least 1 unit", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            btn_cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });
                        }
                        break;
                }

                dialog.show();
            }
        });

        //list.get(position);에 해당하는 unit 정보 들고오기
        // 수량,포지션 inventory테이블에서 가져와야함으로 Async로 인벤토리 접근을 해야함
        return convertView;
    }

    class ToNodeTask extends AsyncTask<Integer, Void, String>{

        String ip = Util.IP;
        String sendMsg, receiveMsg;
        String svr_addr = "http://" + ip + ":9090/Project_war/to_node.jsp";

        @Override
        protected String doInBackground(Integer... integers) {
            int user_idx = integers[0];
            int unit_idx = integers[1];
            int moving_unit_amount = integers[2];
            int original_unit_amount = integers[3];
            int unit_position = integers[4];

            try
            {
                String str = "";
                URL url = new URL(svr_addr);

                //서버 연결
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                sendMsg = String.format("user_idx=%d&unit_idx=%d&moving_unit_amount=%d" +
                        "&original_unit_amount=%d&unit_position=%d",user_idx, unit_idx, moving_unit_amount, original_unit_amount, unit_position);

                Toast.makeText(context, sendMsg, Toast.LENGTH_SHORT).show();
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
                }
            }
            catch (Exception e)
            {
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            /*Intent i = new Intent(getContext(), MainActivity.class);
            i.putExtra("user_idx", Integer.toString(u_idx));
            ((DeployActivity)context).startActivity(i);*/

        }
    }




    //일단 기존 state에서 감소시킨다
    class ToAttackTask extends AsyncTask<Integer, Void, String>{

        String ip = Util.IP;
        String sendMsg, receiveMsg;
        String svr_addr = "http://" + ip + ":9090/Project_war/to_attack.jsp";

        @Override
        protected String doInBackground(Integer... integers) {

            int user_idx = integers[0];
            int unit_idx = integers[1];
            int moving_unit_amount = integers[2];
            int original_unit_amount = integers[3];
            int current_state = integers[4];

            try
            {
                String str = "";
                URL url = new URL(svr_addr);

                //서버 연결
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                if(moving_unit_amount == original_unit_amount){
                    sendMsg = String.format("user_idx=%d&unit_idx=%d&new_amount=0&moving_unit_amount=%d&current_state=%d&pstmt_type=delete",user_idx, unit_idx, moving_unit_amount, current_state);
                }else{
                    sendMsg = String.format("user_idx=%d&unit_idx=%d&new_amount=%d&moving_unit_amount=%d&current_state=%d&pstmt_type=update",user_idx, unit_idx, original_unit_amount-moving_unit_amount, moving_unit_amount, current_state);
                }

                Log.i("toattack", sendMsg);

                //서버로 파라미터 전달
                osw.write(sendMsg);
                osw.flush();

                Log.i("toattack", ""+conn.getResponseCode());
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
                }
            }
            catch (Exception e)
            {
            }

            return "user_idx="+user_idx+"&unit_idx="+unit_idx+"&amount="+moving_unit_amount;
        }

        @Override
        protected void onPostExecute(String s) {

            dialog.dismiss();
            Intent i = new Intent(getContext(), InventoryActivity_attack.class);
            i.putExtra("user_idx", u_idx);
            if(current_units_state == 0){
                ((InventoryActivity)context).startActivity(i);
            }else if(current_units_state == 1){
                ((InventoryActivity_attack)context).startActivity(i);
            }else{
                ((InventoryActivity_defence)context).startActivity(i);
            }
        }
    }

    class ToDefenceTask extends AsyncTask<Integer, Void, String>{

        String ip = Util.IP;
        String sendMsg, receiveMsg;
        String svr_addr = "http://" + ip + ":9090/Project_war/to_defence.jsp";

        @Override
        protected String doInBackground(Integer... integers) {

            int user_idx = integers[0];
            int unit_idx = integers[1];
            int moving_unit_amount = integers[2];
            int original_unit_amount = integers[3];
            int current_state = integers[4];

            try
            {
                String str = "";
                URL url = new URL(svr_addr);

                //서버 연결
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                if(moving_unit_amount == original_unit_amount){
                    sendMsg = String.format("user_idx=%d&unit_idx=%d&new_amount=0&moving_unit_amount=%d&current_state=%d&pstmt_type=delete",user_idx, unit_idx, moving_unit_amount, current_state);
                }else{
                    sendMsg = String.format("user_idx=%d&unit_idx=%d&new_amount=%d&moving_unit_amount=%d&current_state=%d&pstmt_type=update",user_idx, unit_idx, original_unit_amount-moving_unit_amount, moving_unit_amount, current_state);
                }
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
                }
            }
            catch (Exception e)
            {
            }

            return "user_idx="+user_idx+"&unit_idx="+unit_idx+"&amount="+moving_unit_amount;
        }

        @Override
        protected void onPostExecute(String s) {

            dialog.dismiss();
            Intent i = new Intent(getContext(), InventoryActivity_defence.class);
            i.putExtra("user_idx", u_idx);
            if(current_units_state == 0){
                ((InventoryActivity)context).startActivity(i);
            }else if(current_units_state == 1){
                ((InventoryActivity_attack)context).startActivity(i);
            }else{
                ((InventoryActivity_defence)context).startActivity(i);
            }
        }
    }

    class ToUndeployedTask extends AsyncTask<Integer, Void, String>{

        String ip = Util.IP;
        String sendMsg, receiveMsg;
        String svr_addr = "http://" + ip + ":9090/Project_war/to_undeployed.jsp";

        @Override
        protected String doInBackground(Integer... integers) {

            int user_idx = integers[0];
            int unit_idx = integers[1];
            int moving_unit_amount = integers[2];
            int original_unit_amount = integers[3];
            int current_state = integers[4];

            try
            {
                String str = "";
                URL url = new URL(svr_addr);

                //서버 연결
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                if(moving_unit_amount == original_unit_amount){
                    sendMsg = String.format("user_idx=%d&unit_idx=%d&new_amount=0&moving_unit_amount=%d&current_state=%d&pstmt_type=delete",user_idx, unit_idx, moving_unit_amount, current_state);
                }else{
                    sendMsg = String.format("user_idx=%d&unit_idx=%d&new_amount=%d&moving_unit_amount=%d&current_state=%d&pstmt_type=update",user_idx, unit_idx, original_unit_amount-moving_unit_amount, moving_unit_amount, current_state);
                }

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
                }
            }
            catch (Exception e)
            {
            }

            return "user_idx="+user_idx+"&unit_idx="+unit_idx+"&amount="+moving_unit_amount;
        }

        @Override
        protected void onPostExecute(String s) {

            dialog.dismiss();
            Intent i = new Intent(getContext(), InventoryActivity.class);
            i.putExtra("user_idx", u_idx);
            if(current_units_state == 0){
                ((InventoryActivity)context).startActivity(i);
            }else if(current_units_state == 1){
                ((InventoryActivity_attack)context).startActivity(i);
            }else{
                ((InventoryActivity_defence)context).startActivity(i);
            }
        }
    }
}


