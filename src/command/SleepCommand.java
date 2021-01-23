package command;


public class SleepCommand implements Command {

	@Override
	public int execute(Interpreter i, int index) {
		try {
			Thread.sleep(Integer.parseInt(i.code.get(index + 1)));
		}
		catch (NumberFormatException e) {} 
		catch (InterruptedException e) {}
		return 1;
	}

}
