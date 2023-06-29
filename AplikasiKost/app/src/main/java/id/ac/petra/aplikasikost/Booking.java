package id.ac.petra.aplikasikost;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Booking extends AppCompatActivity {
    private static final String TAG = Booking.class.getSimpleName();

    private EditText usernameEditText;
    private EditText teleponEditText;
    private EditText namakostEditText;
    private Button bookButton;
    private TextView bookingIdTextView;
    private TextView bookingDetailsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        usernameEditText = findViewById(R.id.usernameEditText);
        teleponEditText = findViewById(R.id.teleponEditText);
        namakostEditText = findViewById(R.id.namakostEditText);
        bookButton = findViewById(R.id.bookButton);
        bookingIdTextView = findViewById(R.id.bookingIdTextView);
        bookingDetailsTextView = findViewById(R.id.bookingDetailsTextView);

        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String telepon = teleponEditText.getText().toString();
                String namakost = namakostEditText.getText().toString();

                BookAsyncTask bookAsyncTask = new BookAsyncTask();
                bookAsyncTask.execute(username, telepon, namakost);
            }
        });
    }

    private class BookAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String telepon = params[1];
            String namakost = params[2];
            String response = "";

            try {
                // Create a JSON object with the booking data
                JSONObject bookingData = new JSONObject();
                bookingData.put("username", username);
                bookingData.put("telepon", telepon);
                bookingData.put("namakost", namakost);

                // Send the POST request to the server
                URL url = new URL("http://192.168.1.14:7000/booking");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(bookingData.toString().getBytes());
                outputStream.flush();
                outputStream.close();

                // Read the response from the server
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    StringBuilder stringBuilder = new StringBuilder();

                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    reader.close();
                    inputStream.close();

                    response = stringBuilder.toString();
                } else {
                    response = "Error: " + responseCode;
                }

                connection.disconnect();
            } catch (IOException | JSONException e) {
                Log.e(TAG, "Error: " + e.getMessage());
                response = "Error: " + e.getMessage();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            Log.d(TAG, "Response: " + response);
            try {
                JSONObject bookingInfo = new JSONObject(response);
                String bookingId = bookingInfo.getString("id");
                String nama = bookingInfo.getString("nama");
                String telepon = bookingInfo.getString("telepon");
                String namakost = bookingInfo.getString("namakost");

                String bookingDetails = "Booking ID: " + bookingId + "\n"
                        + "Nama: " + nama + "\n"
                        + "Telepon: " + telepon + "\n"
                        + "Nama Kost: " + namakost;

                bookingIdTextView.setText("Booking ID: " + bookingId);
                bookingDetailsTextView.setText(bookingDetails);
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                bookingIdTextView.setText("Kamar Penuh");
                bookingDetailsTextView.setText("");
            }
        }
    }
}
