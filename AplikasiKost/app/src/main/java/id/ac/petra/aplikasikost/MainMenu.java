package id.ac.petra.aplikasikost;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenu extends AppCompatActivity implements View.OnClickListener {

    private Button listKostButton;
    private Button bookingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        listKostButton = findViewById(R.id.listKostButton);
        bookingButton = findViewById(R.id.bookingButton);

        listKostButton.setOnClickListener(this);
        bookingButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.listKostButton:
                // Handle List Kendaraan button click
                openListKostActivity();
                break;
            case R.id.bookingButton:
                // Handle Booking button click
                openBookingActivity();
                break;
        }
    }

    private void openListKostActivity() {
        Intent intent = new Intent(this, ListKostActivity.class);
        startActivity(intent);
    }

    private void openBookingActivity() {
        Intent intent = new Intent(this, Booking.class);
        startActivity(intent);
    }
}
