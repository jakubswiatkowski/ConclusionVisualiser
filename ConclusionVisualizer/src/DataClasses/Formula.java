package DataClasses;
import java.util.ArrayList;


public class Formula {
	private Literal conclusion; 
	private ArrayList<Literal> premises;
	private String originClause;
	boolean used; //if the formula has been used by the algorithm - true

	public Formula(Literal argConclusion, ArrayList<Literal> argPremises, String argOriginClause ){
		premises = argPremises; // Need to andd 'new' operator?
		conclusion = argConclusion;
		originClause = argOriginClause;
		used = false;
	}
	public ArrayList<Literal> getPremises() { return premises; }
	public Literal getConclusion() { return conclusion; }
	public String getOriginClause() { return originClause; }
	public boolean isUsed() { return used; }
	public void used() { used = true; }
	
    public boolean equals (Object o) 
    {
        Formula x = (Formula) o;        
        if (x.conclusion.getNumber() == conclusion.getNumber() && x.conclusion.getValue() == conclusion.getValue() && x.premises == premises)
        	return true;
        
        return false;
    }
    
    public String getPremisesAsString()
    {
    	if(premises.isEmpty())
    		return "";
    	ArrayList<Literal> premisesCopy = premises;
    	String string = premisesCopy.remove(0).toString();
    	while(!premisesCopy.isEmpty())
    	{
    		string += " ^ " + premisesCopy.remove(0).toString();
    	}
    	return string;
    }
}
