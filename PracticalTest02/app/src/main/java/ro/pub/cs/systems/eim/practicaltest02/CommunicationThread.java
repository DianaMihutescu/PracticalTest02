package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class CommunicationThread extends Thread {
    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    public static BufferedReader getReader(Socket socket) throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public static PrintWriter getWriter(Socket socket) throws IOException {
        return new PrintWriter(socket.getOutputStream(), true);
    }


    @Override
    public void run() {
        if (socket == null) {
            Log.e("abc", "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        Log.d("abc", "Started Communication Thread");
        try {
            BufferedReader bufferedReader = getReader(socket);
            PrintWriter printWriter = getWriter(socket);

            if (bufferedReader == null || printWriter == null) {
                Log.e("abc", "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i("abc", "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type!");


            // We read the first query sent in the ClientThread
            String query1 = bufferedReader.readLine();
            // We read the second query sent in the ClientThread
            String informationType = "name";

            if (query1 == null || query1.isEmpty() || informationType == null || informationType.isEmpty()) {
                Log.e("abc", "[COMMUNICATION THREAD] Error receiving parameters from client (query1 / information type!");
                return;
            }

            HashMap<String, AutocompleteInfo> dataServer = serverThread.getData();
            AutocompleteInfo responseData;
            String result;

            if (dataServer.containsKey(query1)) {
                Log.i("abc", "[COMMUNICATION THREAD] Getting the information from the cache...");
                responseData = dataServer.get(query1);

            } else {
                Log.i("abc", "[COMMUNICATION THREAD] Getting the information from the webservice...");
                HttpClient httpClient = new DefaultHttpClient();
                // In case of POST change to HttpPost and remover the arghuments from the urkl
                HttpGet httpPost = new HttpGet("http://autocomplete.wunderground.com/aq?query=" + query1);
                List<NameValuePair> params = new ArrayList<>();

                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String pageSourceCode = httpClient.execute(httpPost, responseHandler);
                if (pageSourceCode == null) {
                    Log.e("abc", "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                    return;
                }
                String auto = "";
                int indexName = pageSourceCode.indexOf("name");
                for(int i = indexName + 8; i < pageSourceCode.length(); i ++)
                {
                    if (pageSourceCode.charAt(i) != '\"')
                        auto += pageSourceCode.charAt(i);
                    else
                        break;
                }
                Log.d("abc", auto);

                /*Document document = Jsoup.parse(pageSourceCode);
                Element element = document.child(0);

                Elements elements = element.getElementsByTag("body");
                Log.d("abc", elements.text());
                JSONObject jsonData = new JSONObject(elements.text());
                JSONObject querryData = jsonData.getJSONObject("main");
                Log.d("abc", querryData.getString("name"));*/


                responseData = new AutocompleteInfo(auto);
                Log.d("abc", query1);

                serverThread.setData(query1, responseData);


            }
            result = responseData.queryResponse1;

            // Send the data to the client
            printWriter.println(result);
            printWriter.flush();

            socket.close();
        }catch (Exception e){
            Log.d("abc", "Exceptie: + " + e);
        }
    }
}

