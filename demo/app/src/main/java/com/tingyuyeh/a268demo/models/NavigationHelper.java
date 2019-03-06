package com.tingyuyeh.a268demo.models;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tingyuyeh.a268demo.C0;
import com.tingyuyeh.a268demo.C1;
import com.tingyuyeh.a268demo.C2;
import com.tingyuyeh.a268demo.C3;
import com.tingyuyeh.a268demo.R;

public class NavigationHelper {


    public static void buildNavigation(DrawerLayout drawer,
            Context currentContext,
            NavigationView navigationView,
            ImageView header_profile_image,
            TextView header_email_text,
            Context applicationContext) {


        View headerLayout = navigationView.getHeaderView(0);

        header_profile_image = headerLayout.findViewById(R.id.header_profile_image);
        header_email_text = headerLayout.findViewById(R.id.header_email_text);


        User user = FirebaseHelper.getInstance().getUser();
        header_email_text.setText(FirebaseHelper.getInstance().getEmail());
        if (user._thumbnail != null && !user._thumbnail.equals("")) {
            header_profile_image.setImageBitmap(FirebaseHelper.decodeImage(user._thumbnail));
        }
        header_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                Intent intent = new Intent(applicationContext, C3.class);
                currentContext.startActivity(intent);
            }
        });
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight

                        menuItem.setChecked(true);

                        // close drawer when item is tapped

                        drawer.closeDrawers();

                        Intent intent = new Intent();
                        switch (menuItem.getTitle().toString()) {
                            case "Problems":
                                intent.setClass(applicationContext, C0.class);
                                break;
                            case "Favourite":
                                intent.setClass(applicationContext, C1.class);
                                break;
                            case "Report":
                                intent.setClass(applicationContext, C2.class);
                                intent.putExtra("message", "C0");
                                break;
                            default:
                                break;
                        }
                        currentContext.startActivity(intent);
                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });

    }
}
