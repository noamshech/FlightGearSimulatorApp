package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class PathFinderConnection {

    private Socket socket;
    private PrintWriter outToSocket;
    private BufferedReader inFromSocket;

    public PathFinderConnection(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            outToSocket = new PrintWriter(socket.getOutputStream());
            inFromSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Path Finder connection established");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public void sendProblem(Point startPos, Point endPos, int[][] map) {
        System.out.println("Sending a problem to the PathFinder server");

      
        int rowAmount = map.length, colAmount = map[0].length;
        for (int row = 0; row < rowAmount ; row++)
            for(int col = 0;col< colAmount ; col++)
                if(col == colAmount - 1)
                    outToSocket.println(map[row][col]);
                else outToSocket.print(map[row][col] + ",");

        outToSocket.println("end");

        // Send starting point
        outToSocket.println(startPos.getIntY() + "," + startPos.getIntX());

        // Send ending point
        outToSocket.println(endPos.getIntY() + "," + endPos.getIntX());

        outToSocket.flush();
    }

   
    public String[] receiveSolution() throws IOException {
        String output = inFromSocket.readLine();
        System.out.println("Solution received -> " + output);
        outToSocket.close();
        inFromSocket.close();
        socket.close();
        return output.split(",");
    }
}
