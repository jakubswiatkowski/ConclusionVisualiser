package DataClasses;
import java.util.ArrayList;

public class Data {
	private Literal toBeProved;
	private ArrayList<Formula> formulas; // list of formulas
	private ArrayList<Literal> literals; //list of proved literals
	
	public Data(Literal argToBeProved){
		toBeProved = argToBeProved;
		formulas = new ArrayList<Formula>();
		literals = new  ArrayList<Literal>();
	}
	
	public Data(Literal argToBeProved, ArrayList<Formula> argFormulas, ArrayList<Literal> argLiterals )
	{
		toBeProved = argToBeProved;
		formulas = argFormulas;
		literals = argLiterals;
	}
	
	public Literal getToBeProved() { return toBeProved;}
	public ArrayList<Formula> getFormulas() { return formulas;}
	public ArrayList<Literal> getLiterals() { return literals; } 
	public void addFormula( Formula argFormula){
		formulas.add(argFormula);
	}
	public void addLiteral( Literal argLiteral){
		literals.add(argLiteral);
	}
}
