package deltahacks3.agora;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    public static final String USERNAME_KEY = "fb_username";

    AppCompatButton fbLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        this.fbLoginBtn = (AppCompatButton) findViewById(R.id.fb_login_btn);
        this.fbLoginBtn.setClickable(true);
        this.fbLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Clicked FB button", Toast.LENGTH_SHORT).show();

                SharedPreferences cache = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                cache.edit().putString(LoginActivity.USERNAME_KEY, "USERNAME HERE").commit();

                // Start the MapsActivity
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
            }
        });
    }
}
