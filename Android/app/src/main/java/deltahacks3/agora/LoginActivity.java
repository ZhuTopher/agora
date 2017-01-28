package deltahacks3.agora;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    AppCompatButton fbLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.fbLoginBtn = (AppCompatButton) findViewById(R.id.fb_login_btn);
        this.fbLoginBtn.setClickable(true);
        this.fbLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Clicked FB button", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
