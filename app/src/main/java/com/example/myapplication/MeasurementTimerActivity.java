package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.os.CountDownTimer;
import android.widget.TextView;

import java.util.Date;

public class MeasurementTimerActivity extends AppCompatActivity {

    CountDownTimer CountDownTimer;
    long IntervalTotal = 0;
    long Miniute = 0;
    int term = 0;
    int stopterm = 0;
    private AWSconnectTimer con;
    private TextView text;
    private TextView setNumber;
    private TextView timerMiniute;
    private TextView timerSecond;
    private TextView intervalMiniute;
    private TextView intervalSecond;
    String Point = "29";
    private MyOpenHelper helper;
    private SQLiteDatabase db;
    int spare[] = new int[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement_timer);

        Intent intent = getIntent();
        String Trainname = intent.getStringExtra("InputTrainName"); //InputTrainNameActivity,InputTrainNameActivityから受け取った値を表示
        TextView Textview = (TextView) this.findViewById(R.id.TimerTrainName);
        assert Trainname != null;
        if (!Trainname.equals("")){//Trainname != null であることを想定している
            Textview.setText(Trainname);
            setNumber = findViewById(R.id.SetNumber);
            timerMiniute = findViewById(R.id.TimerCountTimeMiniute);
            timerSecond = findViewById(R.id.TimerCountTimeSecond);
            intervalMiniute = findViewById(R.id.IntervalMiniute);
            intervalSecond = findViewById(R.id.IntervalSecond);
            con = new AWSconnectTimer(setNumber, timerMiniute, timerSecond, intervalMiniute, intervalSecond);//idをawsconnectに送る
            String URL = new String("http://13.113.228.107/ShowMeasurementTimerMET.php");//接続するphpファイルの決定
            String Values = new String("a="+Trainname);//androidstudioからphpに値を送る文字列(phpにはaと設定しているためa=XXXとする)
            con.execute(URL,Values);//第一引数にURL、第二引数以降にphpに送りたいのを入れる
        }else{
            Textview.setText("トレーニングが入力されていません");
        }

        helper = new MyOpenHelper(getApplicationContext()); //DB用宣言
        db = helper.getWritableDatabase();
    }

    public void onClick0(View v) {//RESET
        if (stopterm == 0) {
            ((EditText) findViewById(R.id.SetNumber)).setText("" + 0);
            ((EditText) findViewById(R.id.IntervalMiniute)).setText("" + 0);
            ((EditText) findViewById(R.id.IntervalSecond)).setText("" + 0);
            ((EditText) findViewById(R.id.TimerCountTimeMiniute)).setText("" + 0);
            ((EditText) findViewById(R.id.TimerCountTimeSecond)).setText("" + 0);
        }
    }

    public void onClick1(View v) { //START
        if (stopterm == 0) {
            EditText TimerSetNumber = (EditText) findViewById(R.id.SetNumber);//セット数
            int Int_TimerSetNumber = Integer.parseInt(TimerSetNumber.getText().toString());
            EditText TimerIntervalMiniute = (EditText) findViewById(R.id.IntervalMiniute);//インターバル分
            int Int_TimerIntervalMiniute = Integer.parseInt(TimerIntervalMiniute.getText().toString());
            EditText TimerIntervalSecond = (EditText) findViewById(R.id.IntervalSecond);//インターバル秒
            int Int_TimerIntervalSecond = Integer.parseInt(TimerIntervalSecond.getText().toString());
            EditText TimerCountTimeMiniute = (EditText) findViewById(R.id.TimerCountTimeMiniute);//時間
            int Int_TimerCountTimeMiniute = Integer.parseInt(TimerCountTimeMiniute.getText().toString());
            EditText TimerCountTimeSecond = (EditText) findViewById(R.id.TimerCountTimeSecond);//時間
            int Int_TimerCountTimeSecond = Integer.parseInt(TimerCountTimeSecond.getText().toString());

            if (Int_TimerCountTimeMiniute == 0 && Int_TimerCountTimeSecond == 0) {//時間が0分0秒の時
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("時間が設定されていません");
                builder.setMessage("時間を設定してください。");
                AlertDialog dialog = builder.create();
                dialog.show();

            }else if(Int_TimerIntervalMiniute == 0 && Int_TimerIntervalSecond == 0){//インターバルが0分0秒の時
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("インターバルが設定されていません");
                builder.setMessage("インターバルを設定してください。");
                AlertDialog dialog = builder.create();
                dialog.show();

            } else {
                stopterm = 1;

                Miniute = (Int_TimerCountTimeMiniute * 60 + Int_TimerCountTimeSecond) * 1000 + 100;//カウントダウンタイマー/時間カウント
                IntervalTotal = (Int_TimerIntervalMiniute * 60 + Int_TimerIntervalSecond) * 1000;//カウントダウンタイマー/インターバルカウント

                if (term == 0) {

                    CountDownTimer = new CountDownTimer(Miniute, 1000) {    //時間
                        EditText TimerSetNumber = (EditText) findViewById(R.id.SetNumber);//セット数
                        int Int_TimerSetNumber = Integer.parseInt(TimerSetNumber.getText().toString());
                        EditText TimerIntervalMiniute = (EditText) findViewById(R.id.IntervalMiniute);//インターバル分
                        int Int_TimerIntervalMiniute = Integer.parseInt(TimerIntervalMiniute.getText().toString());
                        EditText TimerIntervalSecond = (EditText) findViewById(R.id.IntervalSecond);//インターバル秒
                        int Int_TimerIntervalSecond = Integer.parseInt(TimerIntervalSecond.getText().toString());
                        EditText TimerCountTimeMiniute = (EditText) findViewById(R.id.TimerCountTimeMiniute);//時間
                        int Int_TimerCountTimeMiniute = Integer.parseInt(TimerCountTimeMiniute.getText().toString());
                        EditText TimerCountTimeSecond = (EditText) findViewById(R.id.TimerCountTimeSecond);//時間
                        int Int_TimerCountTimeSecond = Integer.parseInt(TimerCountTimeSecond.getText().toString());

                        int FirstTime = Int_TimerCountTimeMiniute * 60 + Int_TimerCountTimeSecond;//画面が切り替わる際に元の値に戻す用
                        int DBSetNumber = Int_TimerSetNumber;//端末DBに保存するための初期セット数

                        public void onTick(long millisUntilFinished) {
                            int sparetime = (int)millisUntilFinished;//時間をカウントするための処理
                            int sparetime2 = (int)millisUntilFinished;
                            int a = 0;
                            for(int i = 0; i < 4; i++) {
                                spare[a] = sparetime % 10;
                                sparetime = sparetime / 10;
                                a++;
                            }

                            int Mintime = sparetime2 / 60000;
                            int Sectime = sparetime2 % 60000;
                            int SEctime = Sectime / 1000;

                            if( 5 <= spare[2] && spare[2] <= 9){
                                SEctime++;
                            }
                            //減らした秒数を表示
                                ((TextView) findViewById(R.id.TimerCountTimeMiniute)).setText(String.valueOf(Mintime));/////////////////
                                ((TextView) findViewById(R.id.TimerCountTimeSecond)).setText(String.valueOf(SEctime));/////////////////更新される
                        }

                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onFinish() {
                            term = 1;
                            if(Int_TimerSetNumber != 0) {//セット数が残っている場合
                                Int_TimerSetNumber--;
                                ((TextView) findViewById(R.id.SetNumber)).setText(String.valueOf(Int_TimerSetNumber));/////

                                AlertDialog.Builder builder = new AlertDialog.Builder(MeasurementTimerActivity.this);
                                builder.setTitle("1セットが終了しました");
                                builder.setMessage("スタートボタンでインターバルを開始します。");
                                AlertDialog dialog = builder.create();
                                dialog.show();
                                stopterm = 0;

                            }else{
                                //履歴に登録する。
                                ContentValues values = new ContentValues();
                                Date date = new Date();
                                SimpleDateFormat ymd = new SimpleDateFormat("yyyyMMdd");
                                String String_ymd = ymd.format(new Date());
                                //int Int_ymd = Integer.valueOf(String_ymd);
                                TextView Textview = (TextView) findViewById(R.id.TimerTrainName);
                                String String_inputtrainname = Textview.getText().toString();
                                values.put("date", String_ymd);
                                values.put("trainname", String_inputtrainname);
                                values.put("setnum", DBSetNumber);
                                values.put("time", FirstTime);

                                db.insert("trainrecorddb", null, values);
                                Intent intent = new Intent(MeasurementTimerActivity.this, MeasurementTimerEndActivity.class);
                                startActivity(intent);
                            }

                            ((TextView) findViewById(R.id.TimerCountTimeMiniute)).setText(String.valueOf(Int_TimerCountTimeMiniute));/////////////////
                            ((TextView) findViewById(R.id.TimerCountTimeSecond)).setText(String.valueOf(Int_TimerCountTimeSecond));/////////////////更新される
                        }
                    }.start();
                } else if (term == 1) {

                    CountDownTimer = new CountDownTimer(IntervalTotal, 1000) {  //インターバル
                        EditText TimerSetNumber = (EditText) findViewById(R.id.SetNumber);//セット数
                        int Int_TimerSetNumber = Integer.parseInt(TimerSetNumber.getText().toString());
                        EditText TimerIntervalMiniute = (EditText) findViewById(R.id.IntervalMiniute);//インターバル分
                        int Int_TimerIntervalMiniute = Integer.parseInt(TimerIntervalMiniute.getText().toString());
                        EditText TimerIntervalSecond = (EditText) findViewById(R.id.IntervalSecond);//インターバル秒
                        int Int_TimerIntervalSecond = Integer.parseInt(TimerIntervalSecond.getText().toString());

                        public void onTick(long millisUntilFinished) {

                            int intMUF = (int)millisUntilFinished;
                            int intMUF2 = (int)millisUntilFinished;
                            int a = 0;
                            for(int i = 0; i < 4; i++) {
                                spare[a] = intMUF % 10;
                                intMUF = intMUF / 10;
                                a++;
                            }

                            IntervalTotal = intMUF2 / 1000;
                            int Mt = intMUF2 / 60000;
                            int St = intMUF2 % 60000;
                            int ST = St / 1000;

                            if( 5 <= spare[2] && spare[2] <= 9){
                                ST++;
                            }

                            ((TextView) findViewById(R.id.IntervalMiniute)).setText("" + Mt);
                            ((TextView) findViewById(R.id.IntervalSecond)).setText("" + ST);
                        }

                        @Override
                        public void onFinish() {
                            ((TextView) findViewById(R.id.IntervalMiniute)).setText("" + 0);
                            ((TextView) findViewById(R.id.IntervalSecond)).setText("" + 0);
                            term = 0;

                            ((TextView) findViewById(R.id.IntervalMiniute)).setText("" + Int_TimerIntervalMiniute);
                            ((TextView) findViewById(R.id.IntervalSecond)).setText("" + Int_TimerIntervalSecond);

                            AlertDialog.Builder builder = new AlertDialog.Builder(MeasurementTimerActivity.this);
                            builder.setTitle("インターバルを終了しました");
                            builder.setMessage("スタートボタンでタイマーを開始します。");
                            AlertDialog dialog = builder.create();
                            dialog.show();
                            stopterm = 0;
                        }
                    }.start();
                }
            }
        }
    }

    public void onClick2(View v) {//STOP
        if (stopterm == 1) {
            EditText TimerSetNumber = (EditText) findViewById(R.id.SetNumber);//セット数
            int Int_TimerSetNumber = Integer.parseInt(TimerSetNumber.getText().toString());
            EditText TimerIntervalMiniute = (EditText) findViewById(R.id.IntervalMiniute);//インターバル分
            int Int_TimerIntervalMiniute = Integer.parseInt(TimerIntervalMiniute.getText().toString());
            EditText TimerIntervalSecond = (EditText) findViewById(R.id.IntervalSecond);//インターバル秒
            int Int_TimerIntervalSecond = Integer.parseInt(TimerIntervalSecond.getText().toString());
            EditText TimerCountTimeMiniute = (EditText) findViewById(R.id.TimerCountTimeMiniute);//時間
            int Int_TimerCountTimeMiniute = Integer.parseInt(TimerCountTimeMiniute.getText().toString());
            EditText TimerCountTimeSecond = (EditText) findViewById(R.id.TimerCountTimeSecond);//時間
            int Int_TimerCountTimeSecond = Integer.parseInt(TimerCountTimeSecond.getText().toString());
            CountDownTimer.cancel();
            ((EditText) findViewById(R.id.SetNumber)).setText("" + Int_TimerSetNumber);
            ((EditText) findViewById(R.id.IntervalMiniute)).setText("" + Int_TimerIntervalMiniute);
            ((EditText) findViewById(R.id.IntervalSecond)).setText("" + Int_TimerIntervalSecond);
            ((EditText) findViewById(R.id.TimerCountTimeMiniute)).setText("" + Int_TimerCountTimeMiniute);
            ((EditText) findViewById(R.id.TimerCountTimeSecond)).setText("" + Int_TimerCountTimeSecond);
            stopterm = 0;
            term = 0;
        }
    }

    public void onClick3(View v) {//計測テンポ画面に遷移
        if(term == 1) {
            CountDownTimer.cancel();
            stopterm = 0;
        }
        TextView textview = (TextView) findViewById(R.id.TimerTrainName);
        String TimerTrainName = textview.getText().toString();//editText14の値を取り出す
        Intent intent = new Intent(this, MeasurementTempoActivity.class);
        intent.putExtra("TimerTrainName", TimerTrainName); //遷移先にこの情報をその名前で引き継ぐ //////////////
        startActivity(intent);
    }

    public void onClick4(View v) {//ホーム画面に遷移
        CountDownTimer.cancel();
        stopterm = 0;
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }
}
