package ro.pub.cs.systems.eim.practicaltest02.network;

import android.util.Log;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;
import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;
import ro.pub.cs.systems.eim.practicaltest02.model.AlarmInformation;
import org.apache.commons.net.time.TimeTCPClient;

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client hour/minute and reqType!");
            String request = bufferedReader.readLine();
            if (request == null || request.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client!");
                return;
            }
            AlarmInformation alarmInformation = null;

            switch(request){
                case "reset":
                    alarmInformation = null;
                    serverThread.setData(alarmInformation);
                    break;
                case "poll":
                    alarmInformation = serverThread.getData();
                    break;
                default:
                    String hour = request.split(",")[1];
                    String minute = request.split(",")[2];
                    alarmInformation = new AlarmInformation(Integer.parseInt(hour), Integer.parseInt(minute));
                    serverThread.setData(alarmInformation);
            }



            String result = "something";
            Log.d(Constants.TAG, "[COMMUNICATION THREAD] Debug request is: " + request);
            if(request.equals("poll")) {
                if(alarmInformation == null){
                    result = "none";
                } else {
                    String TIME_SERVER = "utcnist.colorado.edu";
                    NTPUDPClient timeClient = new NTPUDPClient();
                    InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
                    TimeInfo timeInfo = timeClient.getTime(inetAddress);
                    //long returnTime = timeInfo.getReturnTime();   //local device time
                    long returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();   //server time

                    Date time = new Date(returnTime);
                    Log.e("NTP time", "Time from " + TIME_SERVER + ": " + time);
                    int hour = time.getHours();
                    int minute = time.getMinutes();
                    Log.e("NTP time", "hour " + hour + ": " + minute);

                    if(hour < alarmInformation.getHour()){
                        result = "inactive";
                    } else if(hour > alarmInformation.getHour()){
                        result = "active";
                    } else if(minute < alarmInformation.getMinute()){
                        result = "inactive";
                    } else {
                        result = "active";
                    }

                }
                Log.d(Constants.TAG, "[COMMUNICATION THREAD]request and result: " + request + " " + result);
                printWriter.println(result);
                printWriter.flush();
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

}
