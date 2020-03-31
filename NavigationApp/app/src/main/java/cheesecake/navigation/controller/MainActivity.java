package cheesecake.navigation.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Anton 22/03/20
 *  - Modified by Gio 22/03/20: cleaned code, added comments, added maps template activity for directions, added empty template activity for summary
 *
 * Main Activity class to control functionality of main menu
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "searchMain";
    /**
     * Create views and set listeners
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button dirBtn = findViewById(R.id.directionBtn);
        Button summaryBtn = findViewById(R.id.summaryBtn);

        summaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Summary button pressed", Toast.LENGTH_LONG).show(); //display the text of button1
                openActivity("sum");
            }
        });

        dirBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Direction button pressed", Toast.LENGTH_LONG).show(); //display the text of button1
                openActivity("dir");
            }
        });
    }

    /**
     * Open Activity based on button "clicked"
     * @param str - name of activity (page) to open
     */
    public void openActivity(String str) {
        if (str.equals("dir")) {
            Intent intent = new Intent(this, DirectionsActivity.class);
            startActivity(intent);

        }
        else if (str.equals("sum")) {
            Intent intent = new Intent(this, SummaryActivity.class);
            startActivity(intent);

        }
    }
}
