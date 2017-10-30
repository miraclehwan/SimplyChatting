package daehwankim.com.con_c_server;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    EditText ServerIP;
    EditText ServerPort;
    EditText SendMessage;
    Button SendButton;
    Button ConnectButton;
    TextView getMessage;
    int read;
    char[] buffer;
    TCPClient tcpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ServerIP = (EditText) findViewById(R.id.EditText_ServerIP);
        ServerPort = (EditText) findViewById(R.id.EditText_ServerPort);
        SendMessage = (EditText) findViewById(R.id.EditText_Message);
        SendButton = (Button) findViewById(R.id.Button_SendMessage);
        ConnectButton = (Button) findViewById(R.id.Button_Connect);
        getMessage = (TextView) findViewById(R.id.TextView_getText);

        ConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpClient = new TCPClient();
                Thread thread = new Thread(tcpClient);
                thread.start();
            }
        });

        SendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tcpClient != null){
                    tcpClient.SendMessage(SendMessage.getText().toString());
                    SendMessage.setText("");
                }else{
                    Toast.makeText(MainActivity.this, "서버에 접속해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private class TCPClient implements Runnable{

        private Socket inetSocket = null;
        private String msg;
        private String return_msg;

        PrintWriter out;
        BufferedReader in;

        public TCPClient(){

        }

        @Override
        public void run() {
            try {
                while(true){
                    if(inetSocket == null)
                        inetSocket = new Socket(ServerIP.getText().toString(), Integer.parseInt(ServerPort.getText().toString()));

                    if(out == null)
                        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(inetSocket.getOutputStream())), true);



                    if(in == null) {
                        in = new BufferedReader(new InputStreamReader(inetSocket.getInputStream(), "UTF-8"));
                    }

//                    buffer = new char[512];
//
//
//                    if((in.read(buffer, 0, 512)) != -1){
//                        return_msg = in.readLine();
//                        Handler handler = new Handler(getMainLooper());
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                Log.e("return_msg", return_msg);
//                                Log.e("return_msg", String.valueOf(buffer).replace("\0", ""));
//                                getMessage.setText(String.valueOf(buffer).replace("\0", ""));
//                            }
//                        });
//                    }

                    if((return_msg = in.readLine()) != null){
                        Handler handler = new Handler(getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("return_msg", return_msg);
                                getMessage.setText(return_msg);
                            }
                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inetSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void SendMessage(String msg){
            out.println(msg);
            Log.e("MessageLog", msg);
        }

    }

}
