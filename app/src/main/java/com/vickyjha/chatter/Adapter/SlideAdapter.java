package com.vickyjha.chatter.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.vickyjha.chatter.R;

public class SlideAdapter extends PagerAdapter {

    Context context;
    LayoutInflater inflater;

    public SlideAdapter(Context context){
        this.context = context;
    }
    public int[] slideImages = {
            R.drawable.group,
            R.drawable.security,
            R.drawable.smooth

    };

    public String[] slideHeadings = {
            "Group Chat",
            "Secure Messaging",
            "Smooth Experience"

    };
    public String[] slideDescriptions = {
            "Stay connected with group conversations",
            "Your data is yours to see",
            "smooth and fast experience"


    };
    @Override
    public int getCount() {
        return slideHeadings.length;
    }

    @Override
    public boolean isViewFromObject(View view,  Object object) {
        return view == (LinearLayout)object;
    }

    @Override
    public Object instantiateItem( ViewGroup container, final int position) {
        inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.slide_layout,container,false);
        ImageView slideImage = (ImageView)view.findViewById(R.id.image1);
        TextView slideHeading = (TextView)view.findViewById(R.id.heading1);
        TextView slideDesc = (TextView)view.findViewById(R.id.description1);

       slideImage.setImageResource(slideImages[position]);
        slideHeading.setText(slideHeadings[position]);
        slideDesc.setText(slideDescriptions[position]);

        container.addView(view);
        return view;


    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((LinearLayout)object);

    }
}
