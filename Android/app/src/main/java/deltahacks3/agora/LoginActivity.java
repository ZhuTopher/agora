package deltahacks3.agora;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.AccessTokenTracker;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    public static final String USERNAME_KEY = "fb_username";
    public static final String PROFILE_PIC_KEY = "fb_profile_pic";

    private String username;
    private Uri profileUri;

    LoginButton loginButton;

    AccessToken accessToken;
    private CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    ProfileTracker profileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        // Search logs to view app key hash for FB
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("deltahacks3.agora", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.v("KEY_HASH", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("KEY_HASH", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("KEY_HASH", e.toString());
        } catch (Exception e) {
            Log.e("KEY_HASH", e.toString());
        }*/

        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        LoginManager.getInstance().logOut();
        // accessToken = AccessToken.getCurrentAccessToken();

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
                if (currentProfile != null) {
                    username = currentProfile.getFirstName();
                    profileUri = currentProfile.getProfilePictureUri(100, 100);

                    SharedPreferences cache = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    cache.edit().putString(LoginActivity.USERNAME_KEY, username).commit();
                    // cache.edit().putString(LoginActivity.PROFILE_PIC_KEY, profileUri.toString()).commit();
                }
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
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
