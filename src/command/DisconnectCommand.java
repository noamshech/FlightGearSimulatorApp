package command;


public class DisconnectCommand implements Command {

	@Override
	public int execute(Interpreter i, int index) {
		try {
			ConnectCommand.sendToServer("bye");//Disconnect connection to the server
			OpenDataServerCommand.stop();//Stop listening as a server
		} catch (Exception e) {}
		return 0;
	}

}
