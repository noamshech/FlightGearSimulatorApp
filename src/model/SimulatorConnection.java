package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;


public class SimulatorConnection {

    private static SimulatorConnection singleConnection = null;
    private final String ip;
    private final int port;
    private Socket socket;
    private PrintWriter outToSocket;
    private BufferedReader inFromSocket;

   
    private SimulatorConnection(String ip, int port) {
        this.ip = ip;
        this.port = port;
        try {
            socket = new Socket(ip, port);
            outToSocket = new PrintWriter(socket.getOutputStream());
            inFromSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Simulator connection established");
    }

 
    public static SimulatorConnection getConnection(String ip, int port) {
        if (singleConnection == null)
            singleConnection = new SimulatorConnection(ip, port);

        return singleConnection;
    }

    public static SimulatorConnection getConnection() {
        return singleConnection;
    }

   
    public String[] getPlainLocation() {

        String line;
        ArrayList<String> location = new ArrayList<>();

        try {
            Socket soc = new Socket(ip,port);
            PrintWriter out = new PrintWriter(soc.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));

            
            out.println("dump /position");
            out.flush();

          
            while (!(line = in.readLine()).equals("</PropertyList>")) {
                if (!line.equals(""))
                    location.add(line);
            }

            String longitude = location.get(2);
            String latitude = location.get(3);
            String[] x = longitude.split("[<>]");
            String[] y = latitude.split("[<>]");
            in.readLine();

            out.println("get /instrumentation/heading-indicator/indicated-heading-deg");
            out.flush();
            String angle = in.readLine();
            angle = angle.substring(
                    angle.indexOf("'") + 1,angle.lastIndexOf("'")
            );
            System.out.println(angle);

            out.close();
            in.close();
            soc.close();

            return new String[]{x[2], y[2], angle};

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

   
    public void sendData(String[] dataToSend) {
        for (String line : dataToSend) {
            outToSocket.println(line);
            outToSocket.flush();
        }
    }

   
    public void stopAndDeleteConnection() {
        try {
            if (this.outToSocket != null)
                this.outToSocket.close();
            if (this.inFromSocket != null)
                this.inFromSocket.close();
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        singleConnection = null;
    }
}
