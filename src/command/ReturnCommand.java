package command;

import Interpreter.InterpreterAlgorithem;


public class ReturnCommand implements Command {

	@Override
	public int execute(Interpreter i, int index) {
		int counter;
		StringBuilder expression = new StringBuilder();
		for(counter = 1; index + counter < i.code.size(); counter++) {
			String s = i.code.get(index+counter);
			if((Interpreter.cMap.containsKey(s))) {
				counter--;
				break; 
			}
			String[] split = s.split(String.format("((?<=%1$s)|(?=%1$s))","[-=+/*]"));
			for(String s2 : split) {
				if(i.varTable.containsKey(s2))  
					expression.append(i.varTable.get(s2).toString());
				else expression.append(s2);
			}
		}
		double result = InterpreterAlgorithem.calc(expression.toString());
		i.setReturnValue((int)result);
	return counter;
	}

}
