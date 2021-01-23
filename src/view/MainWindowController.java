package view;

import com.sun.javafx.css.StyleCache;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Rotate;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import model.Point;
import viewModel.ViewModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

public class MainWindowController extends Observable implements Initializable, Observer {

   
    private final ToggleGroup radioButtons = new ToggleGroup();
    @FXML
    private HeightMapDisplayer heightMap;
    @FXML
    private Canvas iconLayer, lineLayer;
    @FXML
    private RadioButton manual, autoPilot;
    @FXML
    private Slider throttleSlider, rudderSlider;
    @FXML
    private Circle border, joystick;
    @FXML
    private TextArea txtArea;
   
    @FXML
    private TextField ipInput, portInput;
   
    public DoubleProperty aileron, elevator;
   
    private boolean isConnected, pathFound, interpretIsOn;
    private File currentFile;
    private static String currIp, currPort, pathFinderIP, pathFinderPort;
    private final Color LineColor = Color.BLACK.darker();
    private final Image AirplaneImage = new Image(new FileInputStream("./resources/airplane.png"));
    private final Image TargetImage = new Image(new FileInputStream("./resources/target.png"));
    private final int cube_length = HeightMapDisplayer.CubeLength;
    private ViewModel viewModel;
    private int[][] _mapData;
    private String[] pathInstructions;
    private Point airplane_pos, target_pos, startPos, endPos;
    private Point originalScene, originalTranslate;
    private double offset, angle;


   

    public MainWindowController() throws FileNotFoundException {
        interpretIsOn = false;
        pathFound = false;
        isConnected = false;
        airplane_pos = new Point();
        aileron = new SimpleDoubleProperty();
        elevator = new SimpleDoubleProperty();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        
        try {
            if (heightMap != null) {
                Pair<double[], int[][]> result = CSVHandler.GetCsvData(new File("./resources/honolulu_map.csv"));
                startPos = new Point(result.getKey()[0], result.getKey()[1]);
                offset = result.getKey()[2];
                this._mapData = result.getValue();
                heightMap.set_heightMatrix(this._mapData);
            }

            if (iconLayer != null && lineLayer != null) {
                iconLayer.setOnMouseClicked(MapPressedHandler);
                iconLayer.setWidth(this.heightMap.get_colAmount() * cube_length);
                iconLayer.setHeight(this.heightMap.get_rowAmount() * cube_length);
                lineLayer.setWidth(this.heightMap.get_colAmount() * cube_length);
                lineLayer.setHeight(this.heightMap.get_rowAmount() * cube_length);
                drawIcons();
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not exist");
        } catch (IllegalStateException e) {
            System.out.println("Wrong file exception");
        } catch (Exception e) {
            e.printStackTrace();
        }

        
        if (manual != null && autoPilot != null) {
            manual.setToggleGroup(radioButtons);
            autoPilot.setToggleGroup(radioButtons);
        }

       
        if (joystick != null) {
            joystick.setOnMousePressed(JoyStickPressed);
            joystick.setOnMouseDragged(JoyStickDragged);
            joystick.setOnMouseReleased(joystickRelease);
        }

       
        if (throttleSlider != null && rudderSlider != null) {
            throttleSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (manual.isSelected())
                    viewModel.setThrottle();
            });

            rudderSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (manual.isSelected())
                    viewModel.setRudder();
            });
        }
    }

   
    public void setViewModel(ViewModel viewModel) {
        this.viewModel = viewModel;
        aileron.bindBidirectional(this.viewModel.aileron);
        elevator.bindBidirectional(this.viewModel.elevator);
        rudderSlider.valueProperty().bindBidirectional(this.viewModel.rudder);
        throttleSlider.valueProperty().bindBidirectional(this.viewModel.throttle);
    }

    

    
    public void loadFile() {
        File csvFile = CSVHandler.LoadFile(heightMap.getScene().getWindow());
        try {
            Pair<double[], int[][]> result = CSVHandler.GetCsvData(csvFile);
            startPos = new Point(result.getKey()[0], result.getKey()[1]);
            offset = result.getKey()[2];
            this._mapData = result.getValue();
            heightMap.set_heightMatrix(this._mapData);
        } catch (FileNotFoundException e) {
            System.out.println("File not exist");
        } catch (IllegalStateException e) {
            System.out.println("Wrong file exception");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public void connect() {
        this.showPopup();
        this.viewModel.connectToSimulator(currIp, currPort);
        this.viewModel.getPlainLocation();
        isConnected = true;
    }

    
    public void calculatePath() {
        if (target_pos == null) {
            new Alert(Alert.AlertType.ERROR, "Please select target before calculating a path").showAndWait();
            return;
        }
        this.showPopup();
        pathFinderIP = currIp;
        pathFinderPort = currPort;
        this.viewModel.connectToPathFinder(pathFinderIP, pathFinderPort);
        this.viewModel.findPath(
                airplane_pos,
                target_pos,
                this._mapData
        );
    }

    
    public void loadAutoPilot() {
        if (interpretIsOn) {
            this.viewModel.stopInterpret();
            interpretIsOn = false;
        }

        currentFile = TxtHandler.LoadFile(heightMap.getScene().getWindow());
        if(currentFile == null) return;
        try {
            txtArea.clear();
            String[] txtData = TxtHandler.getTxtData(currentFile);
            for (String s : txtData) {
                txtArea.appendText(s);
                txtArea.appendText("\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (autoPilot.isSelected() && currentFile != null) {
            this.viewModel.interpret(currentFile);
            interpretIsOn = true;
        }
    }

    
    public void radioButtons1Pressed() {
        if (!isConnected) {
            ConnectionRequiredAlert();
            return;
        }
        if (currentFile != null) {
            this.viewModel.interpret(currentFile);
            interpretIsOn = true;
        }
    }

    
    public void radioButtons2Pressed() {
        if (interpretIsOn) {
            this.viewModel.stopInterpret();
            interpretIsOn = false;
        }
        else if(!isConnected) ConnectionRequiredAlert();
    }

    
    public EventHandler<MouseEvent> MapPressedHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent e) {
            target_pos = new Point(e.getX() / cube_length, e.getY() / cube_length);
            drawIcons();
            if(pathFound) {
                viewModel.connectToPathFinder(pathFinderIP, pathFinderPort);
                viewModel.findPath(
                        airplane_pos,
                        target_pos,
                        _mapData
                );
            }
        }
    };

    
    public EventHandler<MouseEvent> JoyStickPressed = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            originalScene = new Point(event.getSceneX(), event.getSceneY());
            originalTranslate = new Point(
                    ((Circle) event.getSource()).getTranslateX(),
                    ((Circle) event.getSource()).getTranslateY());
        }
    };

   
    public EventHandler<MouseEvent> JoyStickDragged = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            Point currOffset = new Point(
                    event.getSceneX() - originalScene.getX(),
                    event.getSceneY() - originalScene.getY());
            Point currTranslate = new Point(
                    originalTranslate.getX() + currOffset.getX(),
                    originalTranslate.getY() + currOffset.getY());

            if (isInside(currTranslate)) {
                ((Circle) (event.getSource())).setTranslateX(currTranslate.getX());
                ((Circle) (event.getSource())).setTranslateY(currTranslate.getY());
                if (manual.isSelected()) {
                    Point simulatorSafe = translateToSimulatorValues(currTranslate);
                    System.out.println(simulatorSafe.getX() + "     " + simulatorSafe.getY());
                    aileron.setValue(simulatorSafe.getX());
                    elevator.setValue(simulatorSafe.getY());
                    viewModel.movePlain();
                }
            }
        }
    };

  
    EventHandler<MouseEvent> joystickRelease = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {
            ((Circle) (t.getSource())).setTranslateX(originalTranslate.getX());
            ((Circle) (t.getSource())).setTranslateY(originalTranslate.getY());
        }
    };

    
    private Point translateToSimulatorValues(Point position) {
        double maxX = (border.getRadius() - joystick.getRadius()) + border.getCenterX();
        double minX = border.getCenterX() - (border.getRadius() - joystick.getRadius());
        double minY = (border.getRadius() - joystick.getRadius()) + border.getCenterY();
        double maxY = border.getCenterY() - (border.getRadius() - joystick.getRadius());
        return new Point(
                (position.getX() - minX) / (maxX - minX) * 2 - 1,
                (position.getY() - minY) / (maxY - minY) * 2 - 1);
    }

    
    private boolean isInside(Point position) {
        return (
                Math.pow((position.getX() - border.getCenterX()), 2) + Math.pow((position.getY() - border.getCenterY()), 2)
                        <= Math.pow((border.getRadius() - joystick.getRadius()), 2));
    }

    private void ConnectionRequiredAlert() {
        new Alert(Alert.AlertType.ERROR, "Please Set a connection first").showAndWait();
        manual.setSelected(false);
        autoPilot.setSelected(false);
    }

   
    private void drawIcons() {
        GraphicsContext graphicsContext = iconLayer.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, iconLayer.getWidth(), iconLayer.getHeight());
        drawAirplane(graphicsContext);
        if (target_pos != null)
            graphicsContext.drawImage(TargetImage, target_pos.getX() * cube_length, target_pos.getY() * cube_length);
    }

    private void drawAirplane(GraphicsContext graphicsContext){
        graphicsContext.save();
        Rotate r = new Rotate(angle, airplane_pos.getX() * cube_length, airplane_pos.getY() * cube_length);
        graphicsContext.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
        graphicsContext.drawImage(AirplaneImage, airplane_pos.getX() * cube_length, airplane_pos.getY() * cube_length);
        graphicsContext.restore();
    }

   
    private void drawLine(String[] line) {
        if (line == null) return;

        double x = airplane_pos.getX() * cube_length + (AirplaneImage.getWidth() / 2), y = airplane_pos.getY() * cube_length + (AirplaneImage.getHeight() / 2);
        int length = line.length;
        GraphicsContext graphicsContext = lineLayer.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, lineLayer.getWidth(), lineLayer.getHeight());
        graphicsContext.setStroke(LineColor);

        for (int i = 0; i < length; i++) {
            switch (line[i]) {
                case "Right":
                    graphicsContext.strokeLine(x, y, x + cube_length, y);
                    x += cube_length;
                    break;
                case "Left":
                    graphicsContext.strokeLine(x, y, x - cube_length, y);
                    x -= cube_length;
                    break;
                case "Up":
                    graphicsContext.strokeLine(x, y, x, y - cube_length);
                    y -= cube_length;
                    break;
                case "Down":
                    graphicsContext.strokeLine(x, y, x, y + cube_length);
                    y += cube_length;
                    break;
            }
        }
    }

    
    private void showPopup() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Popup.fxml"));
            final Stage popup = new Stage();
            Parent root = fxmlLoader.load();
            popup.initStyle(StageStyle.UNDECORATED);
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setScene(new Scene(root));
            popup.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public void popupSubmit() {
        try {
            if (ipInput != null && ipInput.getText().equals(""))
                new Alert(Alert.AlertType.ERROR, "Please insert a valid IP before continuing").showAndWait();
            else if (portInput != null && portInput.getText().equals(""))
                new Alert(Alert.AlertType.ERROR, "Please insert a valid Port before continuing").showAndWait();
            else {
                MainWindowController.currIp = ipInput.getText();
                MainWindowController.currPort = portInput.getText();
                ((Stage) ipInput.getScene().getWindow()).close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    @Override
    public void update(Observable object, Object arg) {
        if (object != this.viewModel) return;

        String[] input = (String[]) arg;
        if (input[0].equals("Down") || input[0].equals("Up") || input[0].equals("Right") || input[0].equals("Left")) {
            pathFound = true;
            pathInstructions = input;
            drawLine(pathInstructions);
        } else {
            double x = Double.parseDouble(input[0]);
            double y = Double.parseDouble(input[1]);
            angle = Double.parseDouble(input[2]);
            x = (x - startPos.getX() + offset) / offset;
            y = Math.abs((y - startPos.getY() + offset) / offset);
            airplane_pos.setX(x);
            airplane_pos.setY(y);
            drawIcons();
            if(pathFound) {
                this.viewModel.connectToPathFinder(pathFinderIP, pathFinderPort);
                this.viewModel.findPath(
                        airplane_pos,
                        target_pos,
                        this._mapData
                );
            }
        }

    }
}
