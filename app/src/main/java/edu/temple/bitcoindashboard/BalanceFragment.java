package edu.temple.bitcoindashboard;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class BalanceFragment extends Fragment implements Serializable {
    TextView v1 ,v2;
    String qraddress = "";
    String url1 = "";
    URL url;
    ArrayList<String> addresses = new ArrayList<>(Arrays.asList("198aMn6ZYAczwrE5NvNTUMyJ5qkfy4g3Hi",
            "1L8meqhMTRpxasdGt8DHSJfscxgHHzvPgk","1NiA6V8Ges2vEkSx11X5oo2aCyTsCv3XH3","1CHbYdgZm7LvkH1tD5ZoPQU9rBivTPKPBc"));
    int i = 0;

    public BalanceFragment() {
        // Required empty public constructor
    }


    @Override
    public void onPause() {
        saveArrayList(getActivity(),"bitcoin_addresses",addresses);
        super.onPause();
    }

    @Override
    public void onResume() {
        ArrayList<String> temp;
        temp =  ReadArrayList(getActivity(), "bitcoin_addresses");
        if(temp != null){
            addresses = temp;
        }
        i = addresses.size() - 1;
        getBalance(addresses.get(i));
        super.onResume();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_price, container, false);
        v1 = (TextView) v.findViewById(R.id.priceView2);
        v2 = (TextView) v.findViewById(R.id.priceView4);
        i = addresses.size() - 1;
        getBalance(addresses.get(i));

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem mSearchMenuItem = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) mSearchMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                addresses.add(query);
                i = addresses.size() - 1;
                getBalance(addresses.get(i));
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


    }
    //Navigation of addresses to see the balance of an address
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.menu_next) {
            try {
                i++;
                getBalance(addresses.get(i));

            } catch(Exception e){
                i--;
                Toast.makeText(getContext(),"End of addresses", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if(id == R.id.menu_previous){
            try {
                i--;

                getBalance(addresses.get(i));

            } catch(Exception e){
                i++;
                Toast.makeText(getContext(),"End of addresses", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if(id == R.id.qrScan){
            Toast.makeText(getContext(),"Do NOT rotate camera if in Portrait Mode", Toast.LENGTH_LONG).show();
            IntentIntegrator.forSupportFragment(this)
                            .initiateScan();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //Gets the current balance of a given address
    public void getBalance(String address){
        url1 = "http://btc.blockr.io/api/v1/address/info/" + address;

        Thread t = new Thread(){
            @Override
            public void run() {
                try {
                    url = new URL(url1);
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(
                                    url.openStream()));

                    String response = "", tmpResponse;

                    tmpResponse = reader.readLine();
                    while (tmpResponse != null) {
                        response = response + tmpResponse;
                        tmpResponse = reader.readLine();
                    }

                    JSONObject bitcoinObject = new JSONObject(response);
                    Message msg = Message.obtain();
                    msg.obj = bitcoinObject;
                    addressHandler.sendMessage(msg);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

        };
        t.start();

    }
    Handler addressHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                JSONObject responseObject1 = ((JSONObject) msg.obj);
                double balance = responseObject1.getJSONObject("data")
                        .getDouble("balance");

                v1.setText(String.format("%1$.8f", balance));
                v2.setText(addresses.get(i));
            } catch (Exception e) {
                e.printStackTrace();
            }


            return false;
        }
    });


    public static void saveArrayList(Context c, String filename, ArrayList<String> list){
        try {

            FileOutputStream fos = c.openFileOutput(filename + ".dat", c.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(list);
            oos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> ReadArrayList(Context c,String filename){
        ArrayList<String> savedArrayList = null;
        try {
            FileInputStream inputStream = c.openFileInput(filename + ".dat");
            ObjectInputStream in = new ObjectInputStream(inputStream);
            savedArrayList = (ArrayList<String>) in.readObject();
            in.close();
            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
        return savedArrayList;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result != null) {
            qraddress = result.getContents();
            qraddress = qraddress.replace("bitcoin:","");
            addresses.add(qraddress);
            //i = addresses.size() - 1;
            saveArrayList(getActivity(),"bitcoin_addresses",addresses);

        }
    }


}
