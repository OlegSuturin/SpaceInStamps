package com.oliverst.russianstamps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class AboutActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    public void onClickToEmail(View view) {

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.email)});
        intent.setData(Uri.parse("mailto:"));
        startActivity(intent);
    }

    public void onClickToPayPal(View view) {
        Uri address = Uri.parse("https://www.paypal.com/paypalme/OlegSuturin");
        Intent intent = new Intent(Intent.ACTION_VIEW, address);
        startActivity(intent);
    }

    public void onClickToYandexMoney(View view) {
        Uri address = Uri.parse("https://yoomoney.ru/to/410011876295126");
        Intent intent = new Intent(Intent.ACTION_VIEW, address);
        startActivity(intent);
    }
}