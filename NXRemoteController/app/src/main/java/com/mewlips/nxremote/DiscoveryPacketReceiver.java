package com.mewlips.nxremote;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class DiscoveryPacketReceiver extends Thread {

    private static final int DISCOVERY_UDP_PORT = 5681;
    private static final int DISCOVERY_PACKET_SIZE = 32;

    public interface DiscoveryListener {
        void onFound(String version, String model, String ipAddress);
    }

    private static final String TAG = "DiscoveryPacketReceiver";

    private DiscoveryListener mDiscoveryListener;
    private DatagramSocket mSocket;

    public DiscoveryPacketReceiver(DiscoveryListener listener) {
        mDiscoveryListener = listener;
    }

    @Override
    public void run() {
        byte[] buf = new byte[DISCOVERY_PACKET_SIZE];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        try {
            mSocket = new DatagramSocket(DISCOVERY_UDP_PORT, InetAddress.getByName("0.0.0.0"));
            mSocket.setBroadcast(true);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            while (mSocket != null) {
                mSocket.receive(packet);
                byte[] data = packet.getData();
                String discoveryMessage = new String(data);
                String[] cameraInfos = discoveryMessage.split("\\|");
                if (cameraInfos.length == 5) {
                    String header = cameraInfos[0];
                    String version = cameraInfos[1];
                    String model = cameraInfos[2];
                    //String fwVersion = cameraInfos[3];
                    // cameraInfos[4] is garbage
                    String ipAddress = packet.getAddress().getHostAddress();

                    if (header.equals("NX_REMOTE")) {
                        Log.d(TAG, "discovery packet received from " + ipAddress +
                                ". [NX_REMOTE v" + version + " (" + model + ")]");
                        if (mDiscoveryListener != null) {
                            mDiscoveryListener.onFound(version , model, ipAddress);
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "DiscoveryPacketReceiver finished.");
    }

    protected void closeSocket() {
        if (mSocket != null) {
            mSocket.close();
            mSocket = null;
        }
    }
}
