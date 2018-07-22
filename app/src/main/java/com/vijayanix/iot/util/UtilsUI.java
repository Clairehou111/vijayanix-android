package com.vijayanix.iot.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.vijayanix.iot.IOTApplication;
import com.vijayanix.iot.ui.about.AboutActivity;
import com.vijayanix.iot.ui.settings.SettingsActivity;
import com.vijayanix.iot.R;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.apache.log4j.Logger;

import java.util.Calendar;

public class UtilsUI {

    private static final Logger log = Logger.getLogger(UtilsUI.class);


    public static int darker (int color, double factor) {
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        return Color.argb(a, Math.max((int) (r * factor), 0), Math.max((int) (g * factor), 0), Math.max((int) (b * factor), 0));
    }

    public static Drawer setNavigationDrawer (Activity activity, final Context context, Toolbar toolbar) {

        final RecyclerView recyclerView = new RecyclerView(context);
        final String loadingLabel = "...";
        int header;
        AppPreferences appPreferences = IOTApplication.getAppPreferences();
        String apps, systemApps, favoriteApps, hiddenApps;


        if (getDayOrNight() == 1) {
            header = R.drawable.header_day;
        } else {
            header = R.drawable.header_night;
        }

      /*  if (appAdapter != null) {
            apps = Integer.toString(appAdapter.getItemCount());
        } else {
            apps = loadingLabel;
        }
        if (appSystemAdapter != null) {
            systemApps = Integer.toString(appSystemAdapter.getItemCount());
        } else {
            systemApps = loadingLabel;
        }
        if (appFavoriteAdapter != null) {
            favoriteApps = Integer.toString(appFavoriteAdapter.getItemCount());
        } else {
            favoriteApps = loadingLabel;
        }
        if (appHiddenAdapter != null) {
            hiddenApps = Integer.toString(appHiddenAdapter.getItemCount());
        } else {
            hiddenApps = loadingLabel;
        }*/

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(header)
                .build();

        Integer badgeColor = ContextCompat.getColor(context, R.color.divider);
        BadgeStyle badgeStyle = new BadgeStyle(badgeColor, badgeColor).withTextColor(Color.GRAY);

        DrawerBuilder drawerBuilder = new DrawerBuilder();
        drawerBuilder.withActivity(activity);
        drawerBuilder.withToolbar(toolbar);
        drawerBuilder.withAccountHeader(headerResult);
        drawerBuilder.withStatusBarColor(UtilsUI.darker(appPreferences.getPrimaryColorPref(), 0.8));

        final int BEDROOM_IDENTIFIER =1;
        final int KITCHEN_IDENTIFIER =2;
        final int SETTINGS_IDENTIFIER =3;
        final int ABOUT_IDENTIFIER =4;
	    drawerBuilder.addDrawerItems(
                    new PrimaryDrawerItem().withName(context.getResources().getString(R.string.action_bedroom)).withIcon(GoogleMaterial.Icon.gmd_phone_android).withBadge("0").withBadgeStyle(badgeStyle).withIdentifier(BEDROOM_IDENTIFIER),
                    new PrimaryDrawerItem().withName(context.getResources().getString(R.string.action_kitchen)).withIcon(GoogleMaterial.Icon.gmd_android).withBadge("0").withBadgeStyle(badgeStyle).withIdentifier(KITCHEN_IDENTIFIER),
                    new DividerDrawerItem(),

                    new SecondaryDrawerItem().withName(context.getResources().getString(R.string.action_settings)).withIcon(GoogleMaterial.Icon.gmd_settings).withSelectable(false).withIdentifier(SETTINGS_IDENTIFIER),
                    new SecondaryDrawerItem().withName(context.getResources().getString(R.string.action_about)).withIcon(GoogleMaterial.Icon.gmd_info).withSelectable(false).withIdentifier(ABOUT_IDENTIFIER));



	    drawerBuilder.withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {

            @Override
            public boolean onItemClick(View view, int position, IDrawerItem iDrawerItem) {
                switch (iDrawerItem.getIdentifier()) {
                    case BEDROOM_IDENTIFIER:
                       // recyclerView.setAdapter(appAdapter);
                        break;
                    case KITCHEN_IDENTIFIER:
                        //recyclerView.setAdapter(appSystemAdapter);
                        break;
                    case SETTINGS_IDENTIFIER:
                        context.startActivity(new Intent(context, SettingsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        break;
                    case ABOUT_IDENTIFIER:
                        context.startActivity(new Intent(context, AboutActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        break;

                    default:
                        break;
                }

                return false;
            }
        });

        return drawerBuilder.build();
    }

    public static int getDayOrNight() {
        int actualHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        if (actualHour >= 8 && actualHour < 19) {
            return 1;
        } else {
            return 0;
        }
    }

}
