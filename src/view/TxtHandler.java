package view;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TxtHandler {

    public static File LoadFile(Window window) {

        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open txt file");
        fileChooser.setInitialDirectory(new File(".\\resources"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.
                ExtensionFilter("TEXT Files", "*.txt"));

        
        return fileChooser.showOpenDialog(window);
    }

    public static String[] getTxtData(File txtFile) throws FileNotFoundException {
        if (txtFile == null) throw new FileNotFoundException();

       
        String fileName = txtFile.getName();
        int fileNameLength = fileName.length();
        if (!fileName.substring(fileNameLength - 3, fileNameLength).equals("txt")) throw new IllegalArgumentException();

        

        Scanner scanner = new Scanner(txtFile);
        List<String> output = new ArrayList<>();
        while (scanner.hasNext())
            output.add(scanner.nextLine());
        scanner.close();

        
        return output.toArray(new String[0]);
    }

   
}
