
public class Literal {
	private int number;
	private boolean value;
	
	public Literal(int inNumber,boolean inValue){
		number = inNumber; // Name of literal
		value = inValue; // Boolean value of literal
	}
	
	public boolean getValue(){ return value;}
	public int getNumber(){ return number;}
}
