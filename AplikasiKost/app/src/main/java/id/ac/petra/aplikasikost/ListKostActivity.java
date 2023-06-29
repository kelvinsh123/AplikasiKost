package id.ac.petra.aplikasikost;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ListKostActivity extends AppCompatActivity {

    private ListView listViewKost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_kost);
        listViewKost = findViewById(R.id.listViewKost);

        new FetchKostDataTask().execute();
    }

    private class FetchKostDataTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String result;
            try {
                URL url = new URL("http://192.168.1.14:7000/listkost");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    result = response.toString();
                } else {
                    result = "Error: " + responseCode;
                }
                connection.disconnect();
            } catch (Exception e) {
                result = "Error: " + e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            List<String> kostList = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject kostObject = jsonArray.getJSONObject(i);
                    String kostName = kostObject.getString("namakost");
                    String kostAddress = kostObject.getString("alamat");
                    String kostFacilities = kostObject.getString("fasilitas");
                    String kostPrice = kostObject.getString("harga");
                    String kostStock = kostObject.getString("kamarkosong");

                    String kostDetails = "Nama Kost: " + kostName + "\n" + "Alamat: " + kostAddress + "\n"
                            + "Fasilitas: " + kostFacilities + "\n"
                            + "Harga: " + kostPrice + "\n"
                            + "Kamar Tersedia: " + kostStock;

                    kostList.add(kostDetails);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(ListKostActivity.this,
                        android.R.layout.simple_list_item_1, kostList);
                listViewKost.setAdapter(adapter);

            } catch (JSONException e) {
                Toast.makeText(ListKostActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}