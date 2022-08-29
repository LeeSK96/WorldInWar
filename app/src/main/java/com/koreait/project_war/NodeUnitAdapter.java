package com.koreait.project_war;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.koreait.project_war.util.Util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class NodeUnitAdapter extends ArrayAdapter<InvenUnitVO> {

    Context context;
    int resource;
    int user_idx;
    int node_idx;
    List<InvenUnitVO> list;

    ImageView unit_img;
    TextView txt_name, txt_atk, txt_def, txt_cnt;
    Button btn_remove;

    TextView nodename;

    Button cnt_down, cnt_up, btn_toDefence, btn_cancel;
    TextView cnt;

    Dialog dialog;


    public NodeUnitAdapter(@NonNull Context context, int resource, @NonNull List<InvenUnitVO> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.list = objects;
        this.user_idx = objects.get(0).getUser_idx();
        this.node_idx = objects.get(0).getUnit_position();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater vlinf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = vlinf.inflate(resource, null);

        final InvenUnitVO vo = list.get(position);

        txt_name = convertView.findViewById(R.id.txt_name);
        txt_atk = convertView.findViewById(R.id.txt_atk);
        txt_def = convertView.findViewById(R.id.txt_def);
        txt_cnt = convertView.findViewById(R.id.txt_cnt);
        btn_remove = convertView.findViewById(R.id.btn_remove);
        unit_img = convertView.findViewById(R.id.unit_img);

        txt_name.setText(vo.getUnit_name());
        txt_atk.setText(""+vo.getUnit_attack());
        txt_def.setText(""+vo.getUnit_defence());
        txt_cnt.setText(""+vo.getUnit_amount());
        unit_img.setImageResource(context.getResources().getIdentifier(vo.getUnit_img(),"drawable",context.getPackageName()));

        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_nodeunits);

                cnt_down = dialog.findViewById(R.id.cnt_down);
                cnt_up = dialog.findViewById(R.id.cnt_up);
                btn_toDefence = dialog.findViewById(R.id.btn_toDefence);
                btn_cancel = dialog.findViewById(R.id.btn_cancel);
                cnt = dialog.findViewById(R.id.cnt);

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

                btn_toDefence.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int sendAmount = Integer.parseInt(cnt.getText().toString());
                        if( sendAmount > 0){
                            // 0번 state 의 amount 감소, 1번 state 의 amount 증가(혹은 값 추가)

                            /*Toast.makeText(getContext(), user_idx + " / " +
                                    vo.getUnit_idx() + " / " + sendAmount + " / " + vo.getUnit_amount()
                                    + " / " + vo.getUnit_position(), Toast.LENGTH_SHORT).show();*/
                            new ToMainDefenceTask().execute(user_idx, vo.getUnit_idx(), sendAmount, vo.getUnit_amount(), vo.getUnit_position());
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

                dialog.show();
            }
        });

        return convertView;
    }

    class ToMainDefenceTask extends AsyncTask<Integer, Void, String> {

        String ip = Util.IP;
        String sendMsg, receiveMsg;
        String svr_addr = "http://" + ip + ":9090/Project_war/to_Maindefence.jsp";

        @Override
        protected String doInBackground(Integer... integers) {

            int user_idx = integers[0];
            int unit_idx = integers[1];
            int moving_unit_amount = integers[2];
            int original_unit_amount = integers[3];
            int position = integers[4];

            try
            {
                String str = "";
                URL url = new URL(svr_addr);

                //서버 연결
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                sendMsg = String.format("user_idx=%d&unit_idx=%d&original_amount=%d&" +
                        "moving_unit_amount=%d&position=%d",user_idx, unit_idx,
                        original_unit_amount, moving_unit_amount,position);

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
            Intent i = new Intent(getContext(), MainActivity.class);
            i.putExtra("user_idx", Integer.toString(user_idx));
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ((DeployActivity)context).startActivity(i);


        }
    }
}
