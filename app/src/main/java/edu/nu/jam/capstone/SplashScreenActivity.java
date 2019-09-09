package edu.nu.jam.capstone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

public class SplashScreenActivity extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);

		new Handler().postDelayed(new Runnable() {
			@Override public void run() {
				Intent i = new Intent(SplashScreenActivity.this, MainActivity.class); startActivity(i);
				finish(); } }, 3500);
		}
}
