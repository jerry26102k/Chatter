package com.vickyjha.chatter.fragments;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import  java.io.Serializable;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.strictmode.WebViewMethodCalledOnWrongThreadViolation;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vickyjha.chatter.Adapter.ContactListAdapter;
import com.vickyjha.chatter.CreateGroup;
import com.vickyjha.chatter.Data.ItemModelData;
import com.vickyjha.chatter.HomepageActivity;
import com.vickyjha.chatter.LoginActivity;
import com.vickyjha.chatter.R;
import com.vickyjha.chatter.utility.CountryToPhonePrefix;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;


public class ContactsFragment extends Fragment {


    private RecyclerView mUserList;
    private RecyclerView.Adapter mUserListAdapter;
    private RecyclerView.LayoutManager mUserListLayoutManager;
    private ImageView createGroup;

    ArrayList<ItemModelData> userList, contactList;
    Context context;
    private int checkVariable;
    private ImageView done,logout;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts,container,false);
        contactList= new ArrayList<>();
        userList= new ArrayList<>();
        context = HomepageActivity.context;
        createGroup = view.findViewById(R.id.createGroup);
        done = view.findViewById(R.id.done);
        checkVariable = 0;

        logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(view.getContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                ((Activity)view.getContext()).finish();
                return;
            }
        });

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkVariable = 1;
                mUserListAdapter.notifyDataSetChanged();
                done.setVisibility(View.VISIBLE);
                createGroup.setVisibility(View.GONE);
                initializeRecyclerView(view);
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CreateGroup.class);
                intent.putExtra("userList",userList);
                context.startActivity(intent);

            }
        });

        initializeRecyclerView(view);
        getContactList(view);

        return view;
    }



   /*

    }*/

    private void getContactList(View view){

        String ISOPrefix = "+91";

        Cursor phones = view.getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while(phones.moveToNext()){
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            phone = phone.replace(" ", "");
            phone = phone.replace("-", "");
            phone = phone.replace("(", "");
            phone = phone.replace(")", "");

            if(!String.valueOf(phone.charAt(0)).equals("+"))
                phone = ISOPrefix + phone;

            ItemModelData mContact = new ItemModelData("","", name, phone,false);
            boolean check = false;
            for(ItemModelData m : contactList){
                if(m.getPhoneNo().equals(mContact.getPhoneNo())){

                    check = true;
                    break;
                }
            }
            if(!check){
                contactList.add(mContact);
                getUserDetails(mContact);

            }





        }
    }

    private void getUserDetails(ItemModelData mContact) {
        DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("user");
        Query query = mUserDB.orderByChild("phone").equalTo(mContact.getPhoneNo());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String  phone = "",
                            name = "",
                            image = "";
                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                        if(!childSnapshot.getKey().equals(FirebaseAuth.getInstance().getUid())) {
                            if (childSnapshot.child("phone").getValue() != null)
                                phone = childSnapshot.child("phone").getValue().toString();
                            if (childSnapshot.child("name").getValue() != null)
                                name = childSnapshot.child("name").getValue().toString();
                            if (childSnapshot.child("image").getValue() != null)
                                image = childSnapshot.child("image").getValue().toString();


                            ItemModelData mUser = new ItemModelData(image, childSnapshot.getKey(), name, phone,false);


                            userList.add(mUser);
                            mUserListAdapter.notifyDataSetChanged();
                        }


                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



   /* private String getCountryISO(Context context){
        String iso = null;

        TelephonyManager telephonyManager = (TelephonyManager) context.getApplicationContext().getSystemService(context.getApplicationContext().TELEPHONY_SERVICE);
        if(telephonyManager.getNetworkCountryIso()!=null)
            if (!telephonyManager.getNetworkCountryIso().toString().equals(""))
                iso = telephonyManager.getNetworkCountryIso().toString();

        return CountryToPhonePrefix.getPhone(iso);
    }*/

    private void initializeRecyclerView(View view) {
        mUserList= view.findViewById(R.id.contactRecView);
        mUserList.setNestedScrollingEnabled(false);
        mUserList.setHasFixedSize(false);
        mUserListLayoutManager = new LinearLayoutManager(view.getContext().getApplicationContext(), RecyclerView.VERTICAL, false);
        mUserList.setLayoutManager(mUserListLayoutManager);
        mUserListAdapter = new ContactListAdapter(userList,context,checkVariable);
        mUserList.setAdapter(mUserListAdapter);
    }
}
