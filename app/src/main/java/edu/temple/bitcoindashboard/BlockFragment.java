package edu.temple.bitcoindashboard;



import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;



/**
 * A simple {@link Fragment} subclass.
 */
public class BlockFragment extends Fragment {
    String blockNo = "";
    String url1 = "";
    URL url;
    TextView v1,v2,v3;
    public BlockFragment() {
        // Required empty public constructor
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
        View v = inflater.inflate(R.layout.fragment_block, container, false);
        v1 = (TextView) v.findViewById(R.id.blockView1);
        v2 = (TextView) v.findViewById(R.id.blockView2);
        v3 = (TextView) v.findViewById(R.id.blockView3);
        return v;
    }
    //Setting up the toolbar for menu actions
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
                blockNo = query;
                blockData(blockNo);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


    }
    // Navigation of blocks to get information of a particular block
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.menu_next) {
            try {
                int next = Integer.valueOf(blockNo);
                next++;
                blockNo = Integer.toString(next);
                blockData(blockNo);
            } catch(Exception e){
                Toast.makeText(getActivity(),"Block number not provided. Cannot find next Block", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if(id == R.id.menu_previous){
            try {
                int previous = Integer.valueOf(blockNo);
                previous--;
                blockNo = Integer.toString(previous);
                blockData(blockNo);
            } catch(Exception e){
                Toast.makeText(getActivity(),"Block number not provided. Cannot find previous Block", Toast.LENGTH_SHORT).show();
            }

            return true;
        } else if (id == R.id.qrScan){
            Toast.makeText(getActivity(),"Block Scanning not Allowed", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
    // Gets the price of given block number.
    public void blockData(String block){
        url1 = "http://btc.blockr.io/api/v1/block/info/" + block;

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
                    blockHandler.sendMessage(msg);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

        };
        t.start();

    }
    // Handler for the thread that gets the block information.
    Handler blockHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                JSONObject responseObject1 = ((JSONObject) msg.obj);
                int number = responseObject1.getJSONObject("data")
                        .getInt("nb");

                v1.setText(Integer.toString(number));

                JSONObject responseObject2 = ((JSONObject) msg.obj);
                String hash = responseObject2.getJSONObject("data")
                        .getString("hash");

                v2.setText(hash);

                JSONObject responseObject3 = ((JSONObject) msg.obj);
                String size = responseObject3.getJSONObject("data")
                        .getString("size");

                v3.setText(size);


            } catch (Exception e) {
                e.printStackTrace();
            }


            return false;
        }
    });


}
