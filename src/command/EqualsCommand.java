package command;

import Interpreter.InterpreterAlgorithem;


public class EqualsCommand implements Command {

	@Override
	public int execute(Interpreter i, int index) {
		String varName = i.code.get(index - 1);
		int counter = 1;
		if(i.code.get(index + 1).equals("bind")) {
			String bindTo = i.code.get(index + 2);
			if(!i.varTable.containsKey(bindTo)) {
				Varible v = i.varTable.get(varName);
				v.setBind(bindTo);
				i.varTable.put(bindTo, v);
			}else {
				Varible v = i.varTable.get(bindTo);
				v.setBind(bindTo);
				i.varTable.put(varName, v);
			}
			counter = 2;
		}else {
			StringBuilder expression = new StringBuilder();
			for(counter = 1; counter + index < i.code.size(); counter++) {
				String s = i.code.get(index+counter);
				if((Interpreter.cMap.containsKey(s)) ||(index+counter+1 < i.code.size() && i.code.get(index+counter+1).equals("=")) ||( s.equals("}") )) {// If we reached the next command
					counter--;
					break;  // 2 + 4 + 3 - 2  
				}
				if(i.varTable.containsKey(s)) {
					s = i.varTable.get(s).toString();
				}
				expression.append(s).append(" ");
			}
			double result = InterpreterAlgorithem.calc(expression.toString());
			Varible v = i.varTable.get(varName.replaceAll("\\s+",""));
			v.setValue(result);
			if(v.getBind()!= null) i.varTable.get(v.getBind()).setValue(result);
		}
		return counter;
	}

}
