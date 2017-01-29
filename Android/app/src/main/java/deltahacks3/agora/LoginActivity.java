package deltahacks3.agora;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.AccessTokenTracker;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class LoginActivity extends AppCompatActivity {

    public static final String USERNAME_KEY = "fb_username";

    private String username;
    private Uri profileUri;

    LoginButton loginButton;
//    FrameLayout fbFragContainer;

    AccessToken accessToken;
    private CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    ProfileTracker profileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);*/
        setContentView(R.layout.activity_login);

        /*accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                accessToken = currentAccessToken;
            }
        };*/

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                username = currentProfile.getFirstName();
//              TODO:  profileUri = currentProfile.getProfilePictureUri(..);

                SharedPreferences cache = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                cache.edit().putString(LoginActivity.USERNAME_KEY, username).commit();
            }
        };

        if (this.callbackManager == null) {
            this.callbackManager = CallbackManager.Factory.create();
        }

        loginButton = (LoginButton) findViewById(R.id.fb_login_button);
        loginButton.setReadPermissions("public_profile");
        loginButton.registerCallback(this.callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                accessToken = loginResult.getAccessToken();
                Toast.makeText(LoginActivity.this, "FB Login success", Toast.LENGTH_SHORT).show();

                // Start the MapsActivity
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(LoginActivity.this, "FB Login cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(LoginActivity.this, "FB Login error", Toast.LENGTH_SHORT).show();
            }
        });

        // accessToken = AccessToken.getCurrentAccessToken();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        // accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }
}
