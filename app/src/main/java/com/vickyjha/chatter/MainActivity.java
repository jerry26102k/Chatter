package com.vickyjha.chatter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vickyjha.chatter.Adapter.SlideAdapter;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private LinearLayout linearLayout;
    private SlideAdapter slideAdapter;
    private TextView[] dots;

    TextView next, previous;
    private int currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference();
        scoresRef.keepSynced(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        linearLayout = findViewById(R.id.linearLayout);

        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);

        slideAdapter = new SlideAdapter(MainActivity.this);
        viewPager.setAdapter(slideAdapter);

        addDotsIndicator(0);
        viewPager.addOnPageChangeListener(viewListener);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentPage == dots.length - 1){
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }else viewPager.setCurrentItem(currentPage + 1);

            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               viewPager.setCurrentItem(currentPage - 1);

            }

        });



    }
    private void addDotsIndicator(int n){
        dots = new TextView[3];
        linearLayout.removeAllViews();
        for(int i = 0; i<dots.length; i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(this.getResources().getColor(R.color.TransparentWhite));

            linearLayout.addView(dots[i]);
        }
        dots[n].setTextColor(this.getResources().getColor(R.color.white));
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            currentPage = position;
            if(position == 0){
                previous.setVisibility(View.INVISIBLE);
                next.setText("Next");
                previous.setText("");
            }
            else if(position == dots.length -1){
                previous.setVisibility(View.VISIBLE);
                next.setText("Finish");
                previous.setText("Back");

            }
            else{
                previous.setVisibility(View.VISIBLE);
                next.setText("Next");
                previous.setText("Back");
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}