package mh.hedi.attendancemanager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        LottieAnimationView logoAnimation = findViewById(R.id.logoAnimation);
        LottieAnimationView loadingAnimation = findViewById(R.id.loadingAnimation);

        logoAnimation.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
            }
        });

        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LottieAnimationView lottieAnimationView = findViewById(R.id.logoAnimation);
        LottieAnimationView loadingAnimation = findViewById(R.id.loadingAnimation);
        if (lottieAnimationView != null) {
            lottieAnimationView.cancelAnimation();
        }
        if (loadingAnimation != null) {
            loadingAnimation.cancelAnimation();
        }
    }
}