package com.example.yinyoga.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.widget.Toast;

public class NetworkUtil {
    public static void monitorNetwork(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkListener listener = (NetworkListener) context;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    super.onAvailable(network);
                    listener.onAvailable();
                }

                @Override
                public void onLost(Network network) {
                    super.onLost(network);
                    listener.onLost();
                }
            });
        } else {
            Toast.makeText(context, "Your device version is not supported.", Toast.LENGTH_SHORT).show();
        }
    }

    public interface NetworkListener {
        void onAvailable();
        void onLost();
    }
}
