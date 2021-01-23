package command;

import Interpreter.InterpreterAlgorithem;

public abstract class ConditionCommand implements Command {
	
	protected Interpreter interpeter;
	protected int counter1,index ;
	
	
	@Override
	public int execute(Interpreter i, int index) {
		this.interpeter = i;
		this.index = index;
		int flag=0;
		
		StringBuilder pExpression1 = new StringBuilder();
		String operator = null;
		StringBuilder pExpression2 = new StringBuilder();
		
		for(counter1 = 1; counter1 + index < i.code.size(); counter1++) {
			String s = i.code.get(index+counter1);
			if(s.equals("{")) 
				break; 
			if(s.equals(">") || s.equals("<") || s.equals("<=") || s.equals(">=")|| s.equals("==") || s.equals("!=")) {
				operator = s; 
				flag = 1;
			}else if(flag == 0){
				pExpression1.append(s).append(" ");
			}else pExpression2.append(s).append(" ");
		}
		String[] predicat = { pExpression1.toString() , operator , pExpression2.toString() };
		
		return this.conditionParser(predicat);
	}

	
	
	protected abstract int conditionParser(String[] predicat);
	
	
	protected boolean predictCheck(String[] predicat) {
		boolean cond = false;
		StringBuilder expression1 = new StringBuilder();
		for(String s : predicat[0].split(" ")) {//Replacing varabiles with their values
			if(interpeter.varTable.containsKey(s))
				expression1.append(interpeter.varTable.get(s).getValue());
			else expression1.append(s);
		}
		StringBuilder expression2 = new StringBuilder();
		for(String s : predicat[2].split(" ")) {//Replacing varabiles with their values
			if(interpeter.varTable.containsKey(s))
				expression2.append(interpeter.varTable.get(s).getValue());
			else expression2.append(s);
		}
		
		double result1 = InterpreterAlgorithem.calc(expression1.toString());//Calculate left side value
		double result2 = InterpreterAlgorithem.calc(expression2.toString());//Calculate right side value
		
		switch (predicat[1]) {//operator
			case "<":
				cond =  result1 < result2;
				break;
			case ">":
				cond = result1 > result2;
				break;
			case "<=":
				cond = result1 <= result2;
				break;
			case ">=":
				cond = result1 >= result2;
				break;
			case "==":
				cond = result1 == result2;
				break;
			case "!=":
				cond = result1 != result2;
				break;
		}
		
		return cond;
	}

}
