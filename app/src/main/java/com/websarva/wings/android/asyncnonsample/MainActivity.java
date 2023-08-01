package com.websarva.wings.android.asyncnonsample;

import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Callable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {

    @Override
    @UiThread
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ボタンの取得
        Button btSync = findViewById(R.id.btSync);
        Button btAsync = findViewById(R.id.btAsync);
        Button btToast = findViewById(R.id.btToast);

        //ボタンにリスナーを登録
        btSync.setOnClickListener(new SyncClickListener());
        btAsync.setOnClickListener(new AsyncClickListener());
        btToast.setOnClickListener(new ToastClickListener());
    }

    //５秒待機メソッド
    public void SleepMethod(){
        try {
            Log.i("AsyncNonSample", "Sleep開始");
            Thread.sleep(5000); //5000ms
            Log.i("AsyncNonSample", "Sleep終了");
        }catch (Exception ex){}
    }

    private class Receiver implements Callable<String> {

        @WorkerThread
        @Override
        public String call() {
            //UIスレッドに渡すデータ
            String result = "ワーカースレッドで5秒経過";
            //5秒待つ版
            SleepMethod();
            //時間稼ぎ版
            //for(int i= 0; i < 10000; i++){
            //    System.out.println(i);
            //}
            return result;
        }
    }

    //同期ボタン
    private class SyncClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            SleepMethod();
        }
    }

    //非同期ボタン
    private class AsyncClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            Receiver receiver = new Receiver();
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Future<String> future = executorService.submit(receiver);

            String result ="";

            try{
                //ワーカースレッドからのリターンを待つ
                result = future.get();
            }
            catch (Exception ex){
                Log.w("DEBUG_TAG", "非同期処理の例外発生", ex);
            }

            TextView tvMsg = findViewById(R.id.tvMsg);
            tvMsg.setText(result);

        }
    }

    private class ToastClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Toast.makeText(MainActivity.this,"トースト表示",Toast.LENGTH_LONG).show();
        }
    }
}