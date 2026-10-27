package com.buggygames.hellorgb;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final int[] RAINBOW = {
        0xFFFF0040, 0xFFFF8000, 0xFFFFFF00, 0xFF00FF40,
        0xFF00C0FF, 0xFF4040FF, 0xFFC000FF, 0xFFFF0040
    };
    private ValueAnimator animator;
    private TextView hello;
    private Button toggle;
    private float shift = 0f;
    private long duration = 2000;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        float dp = getResources().getDisplayMetrics().density;

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setBackgroundColor(0xFF0B0B10);
        int pad = (int) (24 * dp);
        root.setPadding(pad, pad, pad, pad);
        root.setOnApplyWindowInsetsListener((v, insets) -> {
            if (android.os.Build.VERSION.SDK_INT >= 30) {
                android.graphics.Insets bars = insets.getInsets(WindowInsets.Type.systemBars());
                v.setPadding(pad + bars.left, pad + bars.top, pad + bars.right, pad + bars.bottom);
            }
            return insets;
        });

        hello = new TextView(this);
        hello.setText("Hello World");
        hello.setTextSize(52);
        hello.setTypeface(Typeface.DEFAULT_BOLD);
        hello.setGravity(Gravity.CENTER);
        hello.setShadowLayer(18 * dp, 0, 0, 0x88FFFFFF);
        hello.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        hello.addOnLayoutChangeListener((v, l, t, r, b, ol, ot, or, ob) -> applyGradient());
        root.addView(hello, new LinearLayout.LayoutParams(-1, -2));

        TextView sub = new TextView(this);
        sub.setText("by Buggy Games YT");
        sub.setTextColor(0xFF9A9AB0);
        sub.setTextSize(14);
        sub.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(-1, -2);
        subLp.topMargin = (int) (8 * dp);
        root.addView(sub, subLp);

        TextView speedLabel = new TextView(this);
        speedLabel.setText("Speed");
        speedLabel.setTextColor(Color.WHITE);
        speedLabel.setTextSize(16);
        LinearLayout.LayoutParams lblLp = new LinearLayout.LayoutParams(-1, -2);
        lblLp.topMargin = (int) (48 * dp);
        root.addView(speedLabel, lblLp);

        SeekBar speed = new SeekBar(this);
        speed.setMax(100);
        speed.setProgress(50);
        speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar s, int p, boolean user) {
                duration = 4000 - p * 36L; // 4.0s slow .. 0.4s fast
                if (animator != null) {
                    float frac = animator.getAnimatedFraction();
                    animator.setDuration(duration);
                    animator.setCurrentFraction(frac);
                }
            }
            public void onStartTrackingTouch(SeekBar s) {}
            public void onStopTrackingTouch(SeekBar s) {}
        });
        root.addView(speed, new LinearLayout.LayoutParams(-1, -2));

        toggle = new Button(this);
        toggle.setText("Pause");
        toggle.setTextColor(Color.WHITE);
        toggle.setAllCaps(false);
        toggle.setTextSize(18);
        GradientDrawable bg = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
            new int[]{0xFFFF0040, 0xFF4040FF});
        bg.setCornerRadius(28 * dp);
        toggle.setBackground(bg);
        toggle.setOnClickListener(v -> {
            if (animator.isPaused()) { animator.resume(); toggle.setText("Pause"); }
            else { animator.pause(); toggle.setText("Play"); }
        });
        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams((int) (200 * dp), (int) (56 * dp));
        btnLp.topMargin = (int) (32 * dp);
        btnLp.gravity = Gravity.CENTER_HORIZONTAL;
        root.addView(toggle, btnLp);

        setContentView(root);

        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(duration);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(a -> {
            shift = (float) a.getAnimatedValue();
            applyGradient();
        });
        animator.start();
    }

    private void applyGradient() {
        int w = hello.getWidth();
        if (w == 0) return;
        Shader s = hello.getPaint().getShader();
        if (!(s instanceof LinearGradient)) {
            s = new LinearGradient(0, 0, w, 0, RAINBOW, null, Shader.TileMode.MIRROR);
            hello.getPaint().setShader(s);
        }
        Matrix m = new Matrix();
        m.setTranslate(shift * w * 2, 0);
        s.setLocalMatrix(m);
        hello.invalidate();
    }

    @Override protected void onPause() { super.onPause(); if (animator != null && !animator.isPaused()) animator.pause(); }
    @Override protected void onResume() { super.onResume(); if (animator != null && "Pause".contentEquals(toggle.getText())) animator.resume(); }
    @Override protected void onDestroy() { super.onDestroy(); if (animator != null) animator.cancel(); }
}
