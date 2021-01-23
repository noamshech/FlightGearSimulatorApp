package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class HeightMapDisplayer extends Canvas {

    public static int CubeLength = 2; 
    private final int MaxHeight = 800; 
    private final int MinHeight = 0; 
    private final GraphicsContext graphicsContext = super.getGraphicsContext2D(); //Drawer

    private int[][] _heightMatrix;
    private int _rowAmount, _colAmount;

   
    public HeightMapDisplayer() {
        _heightMatrix = null;
        _rowAmount = -1;
        _colAmount = -1;
    }

    
    public int get_rowAmount() {
        return _rowAmount;
    }

    public int get_colAmount() {
        return _colAmount;
    }


  
    public void set_heightMatrix(int[][] heightMatrix) {

       
        _heightMatrix = heightMatrix;
        _rowAmount = heightMatrix.length;
        _colAmount = heightMatrix[0].length;

       
        super.setWidth(_colAmount * CubeLength);
        super.setHeight(_rowAmount * CubeLength);

       
        this.drawMap();
    }

    
    private void drawMap() {
        if (_heightMatrix == null) return;

        graphicsContext.clearRect(0, 0, super.getWidth(), super.getHeight());

        for (int row = 0; row < _rowAmount; row++)
            for (int col = 0; col < _colAmount; col++) {

                int value = _heightMatrix[row][col];
                value = Math.max(MinHeight, Math.min(MaxHeight, value));

               
                int red = (MaxHeight - value) > 200 ? 255 : 70, blue = 0,
                        green = (int) ((double) value / ((double) MaxHeight / 255.0));
                graphicsContext.setFill(Color.rgb(red, green, blue));

                
                int x = col * CubeLength, y = row * CubeLength;
                graphicsContext.fillRect(x, y, CubeLength, CubeLength);

                
            }
    }
}
