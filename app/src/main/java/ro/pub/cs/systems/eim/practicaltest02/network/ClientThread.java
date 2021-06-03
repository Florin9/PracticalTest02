package ro.pub.cs.systems.eim.practicaltest02.network;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;

public class ClientThread extends Thread {

    private String address;
    private int port;
    private String hour;
    private String minute;
    private TextView weatherForecastTextView;
    private String reqType;

    private Socket socket;

    public ClientThread(String address, int port, String hour, String minute, TextView weatherForecastTextView, String reqType) {
        this.address = address;
        this.port = port;
        this.hour = hour;
        this.minute = minute;
        this.weatherForecastTextView = weatherForecastTextView;
        this.reqType = reqType;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            if (socket == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
                return;
            }
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            String req = null;
            switch (reqType) {
                case "Set":
                    req = "set," + hour + "," + minute;
                    break;
                case "Reset":
                    req = "reset";
                    break;
                case "Poll":
                    req = "poll";
                    break;
            }
            printWriter.println(req);
            printWriter.flush();
            if (reqType.equals("Poll")) {
                String responsePoll;
                while ((responsePoll = bufferedReader.readLine()) != null) {
                    final String finalizedWeateherInformation = responsePoll;
                    weatherForecastTextView.post(new Runnable() {
                        @Override
                        public void run() {
                            weatherForecastTextView.setText(finalizedWeateherInformation);
                        }
                    });
                }
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

}
