package com.example.huangshaojie.holo;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
//import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by huang shaojie on 2017/1/19.
 */
public class DeletableAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> text;
    private SharedPreferences.Editor editor;
    private BluetoothSocket socket = null;

    public DeletableAdapter(Context context, ArrayList<String> text,SharedPreferences.Editor editor,BluetoothSocket socket){
        this.context=context;
        this.text=text;
        this.editor=editor;
        this.socket=socket;
    }

    @Override
    public int getCount() {
        return text.size();
    }

    @Override
    public Object getItem(int position) {
        return text.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int index=position;
        View view=convertView;
        if(view==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.row_simple_list_item_2, null);
        }
        final TextView textView=(TextView)view.findViewById(R.id.simple_item_1);
        textView.setTextSize(18);
        textView.setText(text.get(position));
        final ImageView imageView=(ImageView)view.findViewById(R.id.simple_item_2);
        imageView.setBackgroundResource(android.R.drawable.ic_delete);
        imageView.setTag(position);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(socket!=null){
                    if(text.get(index).contains("开启")){
                        try {
                            OutputStream outStream = socket.getOutputStream();
                            outStream.write(0xaa);
                            outStream.write(0);
                            outStream.write(0);
                            outStream.write(0x0a);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else{
                        try {
                            OutputStream outStream = socket.getOutputStream();
                            outStream.write(0xcc);
                            outStream.write(0);
                            outStream.write(0);
                            outStream.write(0x0a);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                text.remove(index);
                notifyDataSetChanged();
                editor.putInt("number_of_timeSet",text.size());
                if(text.size()!=0){
                    for(int i=1;i<=text.size();i++){
                        editor.putString(Integer.toString(i),text.get(i-1));
                    }
                }
                editor.commit();
            }
        });
        return view;
    }
}
