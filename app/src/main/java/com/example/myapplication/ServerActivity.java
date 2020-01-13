package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ServerActivity extends AppCompatActivity {

    private AWSconnect con;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        TextView a = findViewById(R.id.textview);//渡したいtextviewのidをaに入れる
        con = new AWSconnect(a);//idをawsconnectに送る
        String DBe = new String("http://13.113.228.107/ShowTrainMET.php");//接続するphpファイルの決定
        String dfaifd = new String("a=三角筋");//androidstudioからphpに値を送る文字列(phpにはaと設定しているためa=XXXとする)
        con.execute(DBe, dfaifd);//第一引数にURL、第二引数以降にphpに送りたいものを入れる
    }
}