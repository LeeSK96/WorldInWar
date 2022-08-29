package com.koreait.project_war;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AttackActivity extends AppCompatActivity {

    TextView owner_nickname, user_nickname, owner_atk_def, user_atk_def;
    Button atk_btn, cancel_btn;

    int owner_def, owner_atk, user_def, user_atk;
    String user_nick, current_owner_nick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attack);

        // intent 파라미터 받아오기
        Intent i = getIntent();
        owner_def = i.getIntExtra("owner_def", 0);
        owner_atk = i.getIntExtra("owner_atk", 0);
        user_def = i.getIntExtra("user_def", 0);
        user_atk = i.getIntExtra("user_atk", 0);
        user_nick = i.getStringExtra("user_nick");
        current_owner_nick = i.getStringExtra("owner_nick");

        // View 찾기
        owner_nickname = findViewById(R.id.owner_nickname);
        user_nickname = findViewById(R.id.user_nickname);
        owner_atk_def = findViewById(R.id.owner_atk_def);
        user_atk_def = findViewById(R.id.user_atk_def);
        atk_btn = findViewById(R.id.atk_btn);
        cancel_btn = findViewById(R.id.cancel_btn);

        // Text View 설정
        owner_nickname.setText(current_owner_nick);
        user_nickname.setText(user_nick);
        owner_atk_def.setText(owner_atk + "\n" + owner_def);
        user_atk_def.setText(user_atk + "\n" + user_def);

        // Button View 설정
        atk_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}