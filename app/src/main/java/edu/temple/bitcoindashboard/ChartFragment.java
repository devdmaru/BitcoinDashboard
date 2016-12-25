package edu.temple.bitcoindashboard;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChartFragment extends Fragment {

    ImageView chartDisplay;
    Button b1, b2;
    String day = "";
    Thread t;
    public ChartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chart, container, false);
        chartDisplay = (ImageView) v.findViewById(R.id.imageView);
        b1 = (Button) v.findViewById(R.id.button1);
        b2 = (Button) v.findViewById(R.id.button2);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day ="1d";
                getChart();

            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day = "5d";
                getChart();
            }
        });
        return v;
    }

    //Gets the chart for the current value of bitcoins.
    private void getChart(){


      t = new Thread(){
            @Override
            public void run() {
                URL url;
                try {
                    while (!isInterrupted()) {

                        url = new URL("https://chart.yahoo.com/z?s=BTCUSD=X&t=" + day);
                        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                        Message msg = Message.obtain();
                        msg.obj = bmp;
                        chartHandler.sendMessage(msg);
                        Thread.sleep(15000);
                    }

                } catch(Exception e){
                    e.printStackTrace();
                }

            }
        };
        t.start();
    }

    Handler chartHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Bitmap bmp = (Bitmap) msg.obj;
            chartDisplay.setImageBitmap(bmp);
            return false;
        }
    });



}
