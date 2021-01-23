package view;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class CSVHandler {

    
    public static File LoadFile(Window window) {

        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open csv file");
        fileChooser.setInitialDirectory(new File(".\\resources"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.
                ExtensionFilter("CSV Files", "*.csv"));

        
        return fileChooser.showOpenDialog(window);
    }

   
    public static Pair<double[],int[][]> GetCsvData(File csvFile) throws FileNotFoundException,
            NoSuchElementException, IllegalStateException {

        if (csvFile == null) throw new FileNotFoundException();

        
        String fileName = csvFile.getName();
        int fileNameLength = fileName.length();
        if (!fileName.substring(fileNameLength - 3, fileNameLength).equals("csv")) throw new IllegalArgumentException();

       
        double[] location = new double[3];
        Scanner scanner = new Scanner(csvFile);
        String[] line = scanner.next().split(",");
        location[0] = Double.parseDouble(line[0]);
        location[1] = Double.parseDouble(line[1]);
        line = scanner.next().split(",");
        location[2] = Double.parseDouble(line[0]);
        List<int[]> output = new ArrayList<>();
        while (scanner.hasNext())
            output.add(
                    Arrays.stream(scanner.next().split(",")).mapToInt(Integer::parseInt).toArray());
        scanner.close();

        
        return new Pair<double[],int[][]>(location, output.toArray(new int[0][0]));
    }


    
}
