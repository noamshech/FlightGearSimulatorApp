package viewModel;

import command.Interpreter;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import model.MainModel;
import model.Point;
import view.MainWindowController;

import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class ViewModel extends Observable implements Observer {
    final MainWindowController view;
    final MainModel mainModel;
    public DoubleProperty aileron, elevator, rudder, throttle;

   
    public ViewModel(MainWindowController view, MainModel mainModel) {
        this.view = view;
        this.mainModel = mainModel;
        aileron = new SimpleDoubleProperty();
        elevator = new SimpleDoubleProperty();
        rudder = new SimpleDoubleProperty();
        throttle = new SimpleDoubleProperty();
    }

    
    public void connectToSimulator(String ip, String port) {
        this.mainModel.connectToASimulator(ip, Integer.parseInt(port));
    }

    
    public void getPlainLocation() {
        this.mainModel.getPlainLocation();
    }

    
    public void movePlain() {
        mainModel.movePlain(aileron.getValue(), elevator.getValue());
    }

    public void setThrottle() {
        mainModel.setThrottle(throttle.getValue());
    }

    public void setRudder() {
        mainModel.setRudder(rudder.getValue());
    }

    
    public void connectToPathFinder(String ip, String port) {
        this.mainModel.connectToAPathFinder(ip, Integer.parseInt(port));
    }

   
    public void findPath(Point startPos, Point endPos, int[][] map) {
        new Thread(() -> {
            try {
                this.mainModel.findPath(startPos, endPos, map);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

  
    public void interpret(File aFile){
        this.mainModel.interpret(aFile);
    }
    public void stopInterpret(){
        this.mainModel.stopInterpret();
    }

   

    @Override
    public void update(Observable object, Object arg) {
        if (object != this.mainModel) return;

        String[] input = (String[]) arg;
        setChanged();
        notifyObservers(input);
    }



}
