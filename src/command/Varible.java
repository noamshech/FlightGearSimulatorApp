
package command;

import model.SimulatorConnection;

public class Varible {

	private double value;
	private String bind;
	

	public Varible(double value) {
		this.value = value;
		bind = null;
	}
	

	public String getBind() {return bind;}
	public double getValue() {return value;}
	

	public void setBind(String bind) {this.bind = bind;}
	public void setValueWithoutBind(double value){
		this.value = value;
	}
	public void setValue(double value) {
		this.value = value;
		if(bind != null) 
			try{
				String[] outToServer ={
						"set "+bind+" "+Double.toString(value)
				};
				SimulatorConnection aConnection = SimulatorConnection.getConnection();
				if(aConnection != null)	aConnection.sendData(outToServer);
			}catch (Exception e) {}
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return Double.toString(this.value);
	}
	
}
