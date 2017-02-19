package com.yztc.android_1021;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.yztc.zxinglibrary.android.CaptureActivity;
import com.yztc.zxinglibrary.encode.CodeCreator;


/**
 * 扫描二维码最关键的一个类是zxinglibrary中的CaptureActivity
 * 里面有一个 扫描成功，处理反馈信息的方法handleDecode（）
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    public static final int REQUEST_CODE_SCANE = 100;//扫描的请求码

    private Button scanBtn,createBtn;

    private TextView resultTv; //二维码扫描后的结果

    private EditText inputEt; //输入内容

    private ImageView createIv; //生成的二维码的图片

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        scanBtn = (Button) findViewById(R.id.btn_scan);
        createBtn = (Button) findViewById(R.id.btn_create);
        resultTv = (TextView) findViewById(R.id.tv_result);
        inputEt = (EditText) findViewById(R.id.input_et);
        createIv = (ImageView) findViewById(R.id.make_iv);
        scanBtn.setOnClickListener(this);
        createBtn.setOnClickListener(this);
    }

    private void scan() {
        //获取扫描结果
        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
        startActivityForResult(intent,REQUEST_CODE_SCANE);
    }

    //生成二维码
    private void make(){
        //获取输入内容
        String inputContent = inputEt.getText().toString();
        Bitmap logo = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
        try {
            //创建二维码
            Bitmap qrBmp = CodeCreator.createQRCodeWithIcon("https://www.baidu.com/",logo);
            //显示生成的二维码
            createIv.setImageBitmap(qrBmp);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    //扫描后返回结果的处理
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SCANE && resultCode == RESULT_OK){
            Bundle bundle = data.getExtras();
            //返回结果
            String result = bundle.getString("codedContent"); //结果文字
            //Bitmap resultBmp = bundle.getParcelable("codedBitmap"); //结果图片
            //createIv.setImageBitmap(resultBmp);
            //设置返回结果
            if(!TextUtils.isEmpty(result)){
                if(result.startsWith("http")){
                    Toast.makeText(this, "扫描成功", Toast.LENGTH_SHORT).show();
                    //通过WebView打开扫描结果对应的网页
                    Intent intent = new Intent(this, WebActivity.class);
                    intent.putExtra("key_url",result);
                    startActivity(intent); //通过WebView打开扫描的结果的网址对应的网页
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_scan:
               //获取扫码结果
                scan();
                break;
            case R.id.btn_create:
                //生成二维码
                make();
                break;
        }
    }
}
