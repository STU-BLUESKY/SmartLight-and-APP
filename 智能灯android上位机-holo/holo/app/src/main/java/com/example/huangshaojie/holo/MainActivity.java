package com.example.huangshaojie.holo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.lang.reflect.InvocationTargetException;
import java.io.InputStream;

import ch.ielse.view.SwitchView;
import picker.ugurtekbas.com.Picker.Picker;
import picker.ugurtekbas.com.Picker.TimeChangedListener;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private int BT_CONNECTED=0;
    private boolean powerUpFlag=false;

    private BluetoothAdapter myBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private BluetoothSocket socket = null;
    private ArrayAdapter<String> BTArrayAdapter;
    private ConnectedThread thread;

    private ImageButton power=null;
    private LinearLayout linearLayout=null;
    private ImageButton timer=null;
    private ImageButton fade=null;
    private RelativeLayout list=null;
    private ListView listView=null;
    private ImageButton add=null;

    private DeletableAdapter adapter=null;
    private ArrayList<String> text;

    private RelativeLayout relativeLayout3=null;
    private Picker timePicker=null;
    private Date date=new Date();
    private Button choseTime=null;
    private TextView choseTimeText=null;
    private SwitchView openOrClose=null;
    private SwitchView xiaoyedeng_flag=null;
    private SwitchView liangduzidong_flag=null;
    private SwitchView yansejianbian_flag=null;

    private volatile boolean fade_flag=true;

    SharedPreferences.Editor editor=null;
    SharedPreferences pref=null;

    ActionBar actionBar;

    private int red;
    private int green;
    private int blue;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);

        editor=getSharedPreferences("data",MODE_PRIVATE).edit();
        //saveInit();

        DisplayMetrics metrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final int widthPixels=metrics.widthPixels;
        final int heightPixels=metrics.heightPixels;

        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        power=(ImageButton)findViewById(R.id.power);
        timer=(ImageButton)findViewById(R.id.timer);
        fade=(ImageButton)findViewById(R.id.lightfade);
        linearLayout=(LinearLayout)findViewById(R.id.linearlayout1);
        list=(RelativeLayout)findViewById(R.id.List);
        list.setVisibility(View.GONE);
        listView=(ListView)findViewById(R.id.listView);
        add=(ImageButton)findViewById(R.id.add);
        relativeLayout3=(RelativeLayout)findViewById(R.id.relativeLayout3);
        relativeLayout3.setVisibility(View.GONE);
        timePicker=(Picker)findViewById(R.id.amPicker);
        choseTime=(Button)findViewById(R.id.choseTime);
        choseTimeText=(TextView)findViewById(R.id.timeText);

        openOrClose=(SwitchView)findViewById(R.id.openorclose);
        xiaoyedeng_flag=(SwitchView)findViewById(R.id.xiaoyedeng_isopened);
        liangduzidong_flag=(SwitchView)findViewById(R.id.liangduzidong_isopened);
        yansejianbian_flag=(SwitchView)findViewById(R.id.yansezidong_isopened);
        xiaoyedeng_flag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(xiaoyedeng_flag.isOpened()){
                    editor.putBoolean("xiaoyedeng_flag",true);
                    try {
                        if(socket!=null){
                            OutputStream outStream = socket.getOutputStream();
                            outStream.write(0x02);
                            outStream.write(1);
                            outStream.write(0x0a);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    editor.putBoolean("xiaoyedeng_flag",false);
                    try {
                        if(socket!=null){
                            OutputStream outStream = socket.getOutputStream();
                            outStream.write(0x02);
                            outStream.write(0);
                            outStream.write(0x0a);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                editor.commit();
            }
        });

        liangduzidong_flag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(liangduzidong_flag.isOpened()){
                    editor.putBoolean("liangduzidong_flag",true);
                    try {
                        if(socket!=null){
                            OutputStream outStream = socket.getOutputStream();
                            outStream.write(0x03);
                            outStream.write(1);
                            outStream.write(0x0a);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    editor.putBoolean("liangduzidong_flag",false);
                    try {
                        if(socket!=null){
                            OutputStream outStream = socket.getOutputStream();
                            outStream.write(0x03);
                            outStream.write(0);
                            outStream.write(0x0a);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                editor.commit();
            }
        });

        yansejianbian_flag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(yansejianbian_flag.isOpened()){
                    editor.putBoolean("yansejianbian_flag",true);
                    try {
                        if(socket!=null){
                            OutputStream outStream = socket.getOutputStream();
                            outStream.write(0x04);
                            outStream.write(1);
                            outStream.write(0x0a);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    editor.putBoolean("yansejianbian_flag",false);
                    try {
                        if(socket!=null){
                            OutputStream outStream = socket.getOutputStream();
                            outStream.write(0x04);
                            outStream.write(0);
                            outStream.write(0x0a);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                editor.commit();
            }
        });

        timePicker.setClockColor(Color.CYAN);
        //Set text color
        timePicker.setTextColor(Color.BLACK);
        //Enable 24 or 12 hour clock
        timePicker.setHourFormat(true);
        //Set dial's size
        timePicker.setTrackSize(15);
        //Set adjuster's size
        timePicker.setDialRadiusDP(50);
        //Initialize picker's time
        timePicker.setTime(9,0);
        timePicker.setEnabled(true);

        timePicker.setTimeChangedListener(new TimeChangedListener() {
            @Override
            public void timeChanged(Date date) {
                String minute=Integer.toString(timePicker.getCurrentMin());
                if(timePicker.getCurrentMin()<10){
                    minute="0"+minute;
                }
                if(openOrClose.isOpened()){
                    choseTimeText.setText(timePicker.getCurrentHour()+":"+minute+" | "+"开启");
                }else{
                    choseTimeText.setText(timePicker.getCurrentHour()+":"+minute+" | "+"关闭");
                }

            }
        });

        openOrClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String minute=Integer.toString(timePicker.getCurrentMin());
                if(timePicker.getCurrentMin()<10){
                    minute="0"+minute;
                }
                if(openOrClose.isOpened()){
                    choseTimeText.setText(timePicker.getCurrentHour()+":"+minute+" | "+"开启");
                }else{
                    choseTimeText.setText(timePicker.getCurrentHour()+":"+minute+" | "+"关闭");
                }
            }
        });

        int[] location=new int[2];
        power.getLocationOnScreen(location);

        ColorPicker picker = (ColorPicker) findViewById(R.id.picker);
        SVBar svBar = (SVBar) findViewById(R.id.svbar);
        OpacityBar opacityBar = (OpacityBar) findViewById(R.id.opacitybar);//饱和度
        SaturationBar saturationBar = (SaturationBar) findViewById(R.id.saturationbar);
        ValueBar valueBar = (ValueBar) findViewById(R.id.valuebar);

        picker.addSVBar(svBar);
        picker.addOpacityBar(opacityBar);
        picker.addSaturationBar(saturationBar);
        picker.addValueBar(valueBar);


        picker.getColor();

        //To set the old selected color u can do it like this
        picker.setOldCenterColor(picker.getColor());
        // adds listener to the colorpicker which is implemented
        //in the activity
        picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                float HSV[]=new float[3];
                Color.colorToHSV(color,HSV);
                RGBTypeColor RGB=HSVtoRGB(HSV[0],HSV[1],HSV[2]);
                int red=(int)(RGB.getR()*255);
                int green=(int)(RGB.getG()*255);
                int blue=(int)(RGB.getB()*255);
                if(BT_CONNECTED==1){
                    chatOUT(1,red,green,blue);
                }

            }
        });

        //to turn of showing the old color
        picker.setShowOldCenterColor(false);

        //adding onChangeListeners to bars
        opacityBar.setOnOpacityChangedListener(new OpacityBar.OnOpacityChangedListener() {
            @Override
            public void onOpacityChanged(int opacity) {

            }
        });
        valueBar.setOnValueChangedListener(new ValueBar.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {

            }
        });
        saturationBar.setOnSaturationChangedListener(new SaturationBar.OnSaturationChangedListener() {
            @Override
            public void onSaturationChanged(int saturation) {

            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        power.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                powerUpFlag=!powerUpFlag;
                if(powerUpFlag){
                    if(BT_CONNECTED==1){
                        chatOUT(0,red,green,blue);
                    }

                    linearLayout.setVisibility(View.GONE);//power按钮往上升的动画
                    timer.setVisibility(View.GONE);
                    fade.setVisibility(View.GONE);
                    AnimationSet animationSetAlpha=new AnimationSet(true);
                    AlphaAnimation alphaAnimation=new AlphaAnimation(1,0);
                    alphaAnimation.setDuration(800);
                    animationSetAlpha.addAnimation(alphaAnimation);
                    linearLayout.startAnimation(alphaAnimation);
                    timer.startAnimation(alphaAnimation);
                    fade.startAnimation(alphaAnimation);

                    power.setVisibility(View.GONE);
                    power.setY((heightPixels/2)-200);
                    AnimationSet animationSet=new AnimationSet(false);
                    TranslateAnimation translateAnimation=new TranslateAnimation(0f,0f,(heightPixels/2)-250,0f);
                    translateAnimation.setDuration(1000);
                    translateAnimation.setFillBefore(false);
                    translateAnimation.setFillAfter(true);
                    animationSet.addAnimation(translateAnimation);
                    power.startAnimation(animationSet);
                    power.setVisibility(View.VISIBLE);

                }
                else{
                    if(BT_CONNECTED==1){
                        chatOUT(1,red,green,blue);
                    }
                    //Toast.makeText(MainActivity.this, "holo", Toast.LENGTH_SHORT).show();
                    int[] location=new int[2];
                    power.getLocationOnScreen(location);

                    AnimationSet animationSetAlpha=new AnimationSet(true);
                    AlphaAnimation alphaAnimation=new AlphaAnimation(0,1);
                    alphaAnimation.setDuration(800);
                    animationSetAlpha.addAnimation(alphaAnimation);
                    linearLayout.startAnimation(alphaAnimation);
                    timer.startAnimation(alphaAnimation);
                    fade.startAnimation(alphaAnimation);
                    linearLayout.setVisibility(View.VISIBLE);
                    timer.setVisibility(View.VISIBLE);
                    fade.setVisibility(View.VISIBLE);

                    power.setVisibility(View.GONE);
                    power.setY(heightPixels-450);
                    AnimationSet animationSet=new AnimationSet(false);
                    TranslateAnimation translateAnimation=new TranslateAnimation(0f,0f,-(heightPixels/2)+450,0f);
                    translateAnimation.setDuration(1000);
                    translateAnimation.setFillBefore(false);
                    translateAnimation.setFillAfter(true);
                    animationSet.addAnimation(translateAnimation);
                    power.startAnimation(animationSet);
                    power.setVisibility(View.VISIBLE);

                }
            }
        });

        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.GONE);
                timer.setVisibility(View.GONE);
                fade.setVisibility(View.GONE);
                power.setVisibility(View.GONE);
                actionBar.setDisplayHomeAsUpEnabled(true);
                text = new ArrayList<String>();
                pref=getSharedPreferences("data",MODE_PRIVATE);
                if(pref.getInt("number_of_timeSet",0)!=0){
                    for(int i=1;i<=pref.getInt("number_of_timeSet",0);i++){
                        text.add(pref.getString(Integer.toString(i),""));
                    }
                }
                xiaoyedeng_flag.setOpened(pref.getBoolean("xiaoyedeng_flag",false));
                liangduzidong_flag.setOpened(pref.getBoolean("liangduzidong_flag",false));
                yansejianbian_flag.setOpened(pref.getBoolean("yansejianbian_flag",false));
                adapter = new DeletableAdapter(MainActivity.this, text,editor,socket);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String aa=text.get(position);
                        Toast.makeText(MainActivity.this,aa,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        fade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this,"add",Toast.LENGTH_SHORT).show();
                //text.add("1000");
                //adapter.notifyDataSetChanged();
                list.setVisibility(View.GONE);
                relativeLayout3.setVisibility(View.VISIBLE);
            }
        });
        choseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* String minute=Integer.toString(timePicker.getCurrentMin());
                if(timePicker.getCurrentMin()<10){
                    minute="0"+minute;
                }
                text.add(timePicker.getCurrentHour()+":"+minute);*/
                text.add(choseTimeText.getText()+"");
                adapter.notifyDataSetChanged();
                editor=getSharedPreferences("data",MODE_PRIVATE).edit();
                editor.putInt("number_of_timeSet",text.size());
                editor.putString(Integer.toString(text.size()),choseTimeText.getText()+"");
                editor.commit();
                if(openOrClose.isOpened()){
                    try {
                        OutputStream outStream = socket.getOutputStream();
                        outStream.write(0xaa);
                        outStream.write(timePicker.getCurrentHour());
                        outStream.write(timePicker.getCurrentMin());
                        outStream.write(0x0a);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        OutputStream outStream = socket.getOutputStream();
                        outStream.write(0xcc);
                        outStream.write(timePicker.getCurrentHour());
                        outStream.write(timePicker.getCurrentMin());
                        outStream.write(0x0a);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                relativeLayout3.setVisibility(View.GONE);
                list.setVisibility(View.VISIBLE);
            }
        });

    }


    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BTArrayAdapter.add(device.getName() + "|" + device.getAddress());
                BTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    public void on() {
        if (!myBluetoothAdapter.isEnabled()) {
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);
            Toast.makeText(this, "Bluetooth turned on", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Alreadly on", Toast.LENGTH_SHORT).show();
        }
    }

    public void off() {
        myBluetoothAdapter.disable();
        //text.setText("Status: Disconnected");
        Toast.makeText(this, "off", Toast.LENGTH_SHORT).show();
    }
    public void goBack(){
        list.setVisibility(View.GONE);
        linearLayout.setVisibility(View.VISIBLE);
        timer.setVisibility(View.VISIBLE);
        fade.setVisibility(View.VISIBLE);
        power.setVisibility(View.VISIBLE);
        actionBar.setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void connectPairedDevices() {
        pairedDevices = myBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            //BTArrayAdapter.add(device.getName() + "|" + device.getAddress());
            if (device.getName().equals("heyheyhey")) {
                BluetoothDevice mmdevice = myBluetoothAdapter.getRemoteDevice(device.getAddress());
                Method m;
                try {
                    m = mmdevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                    socket = (BluetoothSocket) m.invoke(device, Integer.valueOf(1));
                } catch (SecurityException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (NoSuchMethodException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    myBluetoothAdapter.cancelDiscovery();
                    socket.connect();
                    BT_CONNECTED=1;
                    Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    setTitle("连接失败");//目前连接若失败会导致程序出现ANR
                    Toast.makeText(MainActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
                }
                thread = new ConnectedThread(socket);  //开启通信的线程
                thread.start();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.openBluetooth:
                on();
                break;
            case R.id.closeBluetooth:
                off();
                break;
            case R.id.connectPairedDevices:
                connectPairedDevices();
                break;
            case R.id.exit:
                finish();
                break;
            case android.R.id.home:
                goBack();
                break;
        }
        return true;

    }

    @Override
    public void onDestroy(){
        this.unregisterReceiver(bReceiver);
        thread.destroy();
        super.onDestroy();
    }
    @Override
    public void finish(){
        super.finish();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.huangshaojie.holo/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.huangshaojie.holo/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    private class ConnectedThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //构造函数
        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream(); //获取输入流
                tmpOut = socket.getOutputStream();  //获取输出流
            } catch (IOException e) {

            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        public void run() {
            while(true){
                try {
                    //mmOutStream.write(getHexBytes(25+""));
                    int receive_data = mmInStream.read();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }
    public void chatOUT(int powerflag,int R_color,int G_color,int B_color) {
        Log.e("TAG", "chatOUT...");
        try {
            OutputStream outStream = socket.getOutputStream();
            outStream.write(0x0d);
            outStream.write(R_color);
            outStream.write(G_color);
            outStream.write(B_color);
            outStream.write(powerflag);
            outStream.write(0x0a);
            Log.d("DATA", "output: " + R_color+G_color+B_color);
        } catch (IOException e) {
            Log.e("TAG", "fail send: " + e.toString());
        }
    }
    private byte[] getHexBytes(String message) {
        int len = message.length() / 2;
        char[] chars = message.toCharArray();
        String[] hexStr = new String[len];
        byte[] bytes = new byte[len];
        for (int i = 0, j = 0; j < len; i += 2, j++) {
            hexStr[j] = "" + chars[i] + chars[i + 1];
            bytes[j] = (byte) Integer.parseInt(hexStr[j], 16);

        }
        return bytes;
    }

    RGBTypeColor HSVtoRGB(float h /* 0~360 degrees */, float s /* 0 ~ 1.0 */, float v /* 0 ~ 1.0 */) {
        float f, p, q, t;
        if (s == 0) { // achromatic (grey)
            return new RGBTypeColor(v, v, v);
        }

        h /= 60;      // sector 0 to 5
        int i = (int) Math.floor(h);
        f = h - i;      // factorial part of h
        p = v * (1 - s);
        q = v * (1 - s * f);
        t = v * (1 - s * (1 - f));
        switch (i) {
            case 0:
                return new RGBTypeColor(v, t, p);//v,t,p
            case 1:
                return new RGBTypeColor(q, v, p);//Q,V,P
            case 2:
                return new RGBTypeColor(p, v, t);
            case 3:
                return new RGBTypeColor(p, q, v);
            case 4:
                return new RGBTypeColor(t, p, v);
            default:    // case 5:
                return new RGBTypeColor(v, p, q);
        }
    }

    public void saveInit(){
        editor=getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putInt("number_of_timeSet",0);
        editor.commit();
    }

}
