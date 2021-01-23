package command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;


public class OpenDataServerCommand implements Command {
 
    private int port, latency;
    private volatile static boolean stop;
    Interpreter interpreter;

    @Override
    public int execute(Interpreter i, int index) {
        this.interpreter = i;
        this.port = Integer.parseInt(this.interpreter.code.get(index + 1));
        this.latency = Integer.parseInt(this.interpreter.code.get(index + 2));
        stop = false;
        try {
            if (port < 1000 || port > 9999) throw new Exception();
        } catch (Exception e) {
        } 
        new Thread(() -> this.runServer()).start();
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }
        return 2;
    }

    
    private void runServer() {
        try {
            ServerSocket server = new ServerSocket(port);
			System.out.println(port);
            server.setSoTimeout(3000);
            while (!this.stop) {
                try {
                    Socket aClient = server.accept();
                    try {
                        this.readLines(aClient.getInputStream());
                        aClient.close();
                    } catch (IOException e) {
                        System.out.println("exit(3) = can't read from client");
                    } catch (InterruptedException e) {
						e.printStackTrace();
					}
				} catch (SocketTimeoutException e) {;
                }
            }
            server.close();
        } catch (IOException e) { System.out.println("exit(1)");}
    }

    private void readLines(InputStream in) throws IOException, InterruptedException {
        String[] variablesNames = {"airspeed", "alt", "Pressure", "pitch", "roll", "Internal-Pitch",
                "Internal-Roll", "Encoder-Altitude", "Encoder-Pressure", "GPS-Altitude", "Ground-Speed",
                "Vertical-Speed", "heading", "Compass-Heading", "Slip", "Turn", "Fpm-Speed", "aileron",
                "elevator", "rudder", "Flaps", "throttle", "Rpm"
        };
        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(in));
        String line;

        while (!stop) { 
            line = inFromClient.readLine();
            Thread.sleep(1000 / this.latency);
            String[] variablesValues = line.split(",");
            for (int index = 0; index < variablesNames.length; index++) {
                String varName = variablesNames[index];
                double value = Double.parseDouble(variablesValues[index]);
                Varible var = this.interpreter.varTable.get(varName);
                if (var != null) 
                    var.setValueWithoutBind(value);
                else { 
                    Varible v = new Varible(value);
                    this.interpreter.varTable.put(varName, v);
                }
            }
        }
        inFromClient.close();
    }

    
    public static void stop() {
        stop = true;
    }
}

