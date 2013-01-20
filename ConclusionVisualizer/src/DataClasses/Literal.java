package DataClasses;

public class Literal {
	private int number;
	private boolean value;
	
	public Literal(int inNumber,boolean inValue) {
		value = inValue; // Boolean value of literal
		number = inNumber; // Name of literal
	}
	public Literal(Literal argLiteral){
		number = argLiteral.getNumber();
		value = argLiteral.getValue();
	}
	
	public boolean getValue(){ return value;}
	public int getNumber(){ return number;}
	
    public boolean equals (Object o) 
    {
        Literal x = (Literal) o;
        if (x.number == number && x.value == value ) 
        	return true;
        
        return false;
    }
    
    public String toString()
    {
    	String string = new String();
    	if(!value)
    		string = "~";
    	string += "L" + number; 
    	return string;
    }
	
}