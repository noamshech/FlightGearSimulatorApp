package model;

import command.Interpreter;

import java.io.File;
import java.io.IOException;
import java.nio.channels.ConnectionPendingException;
import java.util.Observable;

public class MainModel extends Observable {

    private SimulatorConnection singleConnection;
    private PathFinderConnection pathFinderConnection;
    private final Interpreter interpreter = new Interpreter();
    private Thread thread;

    
    public void connectToASimulator(String ip, int port) {
        singleConnection = SimulatorConnection.getConnection(ip, port);
    }

    public void disconnectFromTheSimulator(){
        singleConnection.stopAndDeleteConnection();
    }

    
    public void movePlain(double aileron, double elevator) {
        String[] commands = {
                "set /controls/flight/aileron " + aileron,
                "set /controls/flight/elevator " + elevator,
        };
        singleConnection.sendData(commands);
    }

    public void setThrottle(double throttle) {
        String[] commands = {
                "set /controls/engines/current-engine/throttle " + throttle
        };
        singleConnection.sendData(commands);
    }

    public void setRudder(double rudder) {
        String[] commands = {
                "set /controls/flight/rudder " + rudder
        };
        singleConnection.sendData(commands);
    }

   
    public void getPlainLocation() {
        new Thread(()->{
            while(true) {
                setChanged();
                notifyObservers(singleConnection.getPlainLocation());
                try{
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    
    public void connectToAPathFinder(String ip, int port) {
        pathFinderConnection = new PathFinderConnection(ip, port);
    }

    
    public void findPath(Point startPos, Point endPos, int[][] map) throws IOException {
        if (pathFinderConnection == null) throw new ConnectionPendingException();
        pathFinderConnection.sendProblem(startPos, endPos, map);
        String solution[] = pathFinderConnection.receiveSolution();
        this.setChanged();
        this.notifyObservers(solution);
    }

   
    public void interpret(File aFile) {
        thread = new Thread(() -> interpreter.interpret(aFile.getName()));
        try{
            thread.start();
        }catch (Exception e) {}
    }

    public void stopInterpret() {
        thread.interrupt();
        interpreter.stop();
    }

}
