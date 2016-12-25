package edu.temple.bitcoindashboard;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;



/**
 * A simple {@link Fragment} subclass.
 */
public class ValueDashboard extends Fragment {

    TextView bitcoinPrice;

    public ValueDashboard() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_price_dashboard, container, false);
        bitcoinPrice = (TextView) v.findViewById(R.id.Current_Price);
        getPriceThread();

        return v;
    }
//Gets the current price of bitcoins
    private void getPriceThread() {
        Thread t1 = new Thread() {
            @Override
            public void run() {

                URL bitCoinUrl;

                try {

                        bitCoinUrl = new URL("http://btc.blockr.io/api/v1/coin/info");

                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(
                                        bitCoinUrl.openStream()));

                        String response = "", tmpResponse;

                        tmpResponse = reader.readLine();
                        while (tmpResponse != null) {
                            response = response + tmpResponse;
                            tmpResponse = reader.readLine();
                        }

                        JSONObject bitcoinObject = new JSONObject(response);
                        Message msg = Message.obtain();
                        msg.obj = bitcoinObject;
                        bitcoinPriceHandler.sendMessage(msg);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        };
        t1.start();
    }

    Handler bitcoinPriceHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            try {
                JSONObject responseObject = ((JSONObject) msg.obj);
                double value = responseObject.getJSONObject("data")
                        .getJSONObject("markets")
                        .getJSONObject("btce")
                        .getDouble("value");

                bitcoinPrice.setText(String.format("%1$.3f", value));

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }
    });


}



