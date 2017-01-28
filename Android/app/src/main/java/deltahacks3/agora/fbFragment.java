package deltahacks3.agora;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

/**
 * Created by Vaibhav Ariyur on 2017-01-28.
 */

public class fbFragment extends Fragment {
    CallbackManager callbackManager;
    LoginButton fb_log_in;
    AccessTokenTracker accessTokenTracker;
    AccessToken accessToken;
    ProfileTracker profileTracker;
    Profile profile;
    java.util.Arrays Arrays;

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fb_fragment, container, false);

        fb_log_in = (LoginButton) view.findViewById(R.id.fb_login_button);
        fb_log_in.setReadPermissions("email");
        // If using in a fragment
        fb_log_in.setFragment(this);
        // Other app specific specialization

        // Callback registration
        fb_log_in.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
            }
        };
        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(
                    Profile oldProfile,
                    Profile currentProfile) {
                // App code
            }
        };

        // If the access token is available already assign it.
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
//        getActionBar().hide(); // getActionBar() returns null

        this.fbLoginBtn = (LoginButton) findViewById(R.id.fb_login_button);
        fbLoginBtn.setReadPermissions("email");
        this.fbLoginBtn.setClickable(true);
        fbLoginBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Toast.makeText(getApplicationContext(), "Clicked FB button", Toast.LENGTH_SHORT).show();

                // Start the MapsActivity
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);

            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
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
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }
}
}
