package Core;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import DataClasses.Formula;
import DataClasses.Literal;
import DataClasses.Data;


public class CalculationEngine
{

	public ArrayList<Formula> doBackwardChaining(String dataFilePath)
	{
		// For testing literal to prove should be -2
		return backwardChaining(readDataFromFile(dataFilePath));
	}

	public ArrayList<Formula> doForwardChaining(String dataFilePath)
	{
		return forwardChaining(readDataFromFile(dataFilePath));	
	}


	// Reads data from file specified in argument into lists of formulas and proved literals
	public Data readDataFromFile(String dataFileName){
		Data data = null;
		try{
			// Open the file given as the parameter 
			FileInputStream fstream = new FileInputStream(dataFileName);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			
			//Read literal to be proved
			strLine = br.readLine();
			String[] temp = strLine.split(":");
				
			Literal toBeParsed = parseLiteral(temp[1]);
			
			data = new Data(toBeParsed);
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				loadDataFromClause(strLine, data);
			}
			//Close the input stream
			in.close();		
			
		}
		catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		return data;
	}
	
	// Parses literal from string
	private Literal parseLiteral(String str){
		boolean value;
		// Delete white spaces and parse into int
		int number = Integer.parseInt(str.replaceAll("[\\s;]", ""));
		if(number > 0) value = true; 
		else if(number < 0) value = false;
		else
			throw new IllegalArgumentException("Error: Literal cannot be named 0.");
		// Absolute value of the number is the name of literal and the sign of number implicates true or false value of literal
		return new Literal(Math.abs(number),value);
	}
	
	
	// Parses clause from input string
	private void loadDataFromClause(String strLine, Data data){
		ArrayList<String> notParsedLiterals = new ArrayList<String>(Arrays.asList(strLine.split("[vV]")));
		ArrayList<Literal> parsedLiterals = new ArrayList<Literal>();
		
		for(String currentLiteralToParse : notParsedLiterals){
			parsedLiterals.add(parseLiteral(currentLiteralToParse));
		}
		// If there is only one literal in clause add it to list of proved literals and exit
		if(parsedLiterals.size() == 1){
			data.addLiteral(parsedLiterals.get(0));
			//add it also to the list of formulas (required for the backwardAlgorithm)
			ArrayList<Literal> tmp = new ArrayList<Literal>();
			Formula newFormula = new Formula(parsedLiterals.get(0), tmp,strLine);
			newFormula.used(); //mark it as used so that forwardChaining function does not use it (it cannot, only backwardChaining can use it)
			data.addFormula(newFormula);
			return;
		}
		// Else add new formulas made from parsed clause
		else{
			Iterator<Literal> it = parsedLiterals.iterator();
			while(it.hasNext()){
				ArrayList<Literal> newFormulaPremises = new ArrayList<Literal>();
				// Add all literals beside pointed by iterator to premises
				Literal currConclusionLiteral = it.next();
				for(Literal currLiteral : parsedLiterals){
					if(currLiteral != currConclusionLiteral)
					{
						// add negated premises
						Literal premis = new Literal(currLiteral.getNumber(),currLiteral.getValue()? false : true);
						newFormulaPremises.add(premis);
					}
				}
				Formula newFormula = new Formula(new Literal(currConclusionLiteral), newFormulaPremises,strLine);
				data.addFormula(newFormula);
			}
		}
	}
	
	private ArrayList<Formula> backwardChaining(Data data)
	{
		Literal toBeProved = data.getToBeProved();
		ArrayList<Formula> formulas = data.getFormulas(); // list of formulas
		ArrayList<Literal> literals = data.getLiterals(); //list of proved literals
		
		ArrayList<Formula> K = new ArrayList<Formula>();
		//check whether the literal is on the literals list
		//if it is - nothing to prove, just return
		Iterator<Literal> il = literals.iterator();
		while(il.hasNext())
		{
			Literal l = (Literal) il.next();
			if (l.equals(toBeProved))
				return K;
		}
		
		ArrayList<Literal> h0 = new ArrayList<Literal>();
		ArrayList<Literal> h1 = new ArrayList<Literal>();
		ArrayList<Literal> tmp = new ArrayList<Literal>(); //contains literals added to h0 in 4b, used in 4c
		boolean skip=false;
		boolean deadEnd = false;
		boolean enableDeadEnd = true; 	//if it is false, it is not possible to enter
										//deadEnd section even if deadEnd is true 
										//(used to avoid entering deadEnd section twice in a row)
		h0.add(toBeProved);
		
		//stop condition
		int difference = -1; //explained in the forwardChaining function
		int s = formulas.size();		
		if (s==0)
			return K;
		
		Iterator<Formula> i = formulas.iterator();
		while (!h0.isEmpty())
		{
			//4a
			//find a formula with B as a conclusion
			while (true)
			{
				if (!i.hasNext())
					i = formulas.iterator();//come back to the beginning of the formulas list	
				++difference;
				Formula k = (Formula) i.next();				
				if (difference >= s)
				{
					deadEnd = true; //go to deadEnd section
					break;
				}
				

				//if the formula has not been used - OK
				//if it has been - continue
				if (K.contains(k))
					continue;
				
				//if the premises are not in h1 - OK
				//if any of them is - continue
				ArrayList<Literal> premises = k.getPremises();
				Iterator<Literal> ip = premises.iterator();
				skip=false;
				while(ip.hasNext())
				{
					Literal l = (Literal) ip.next();
					if (h1.contains(l))
					{
						skip=true;
						continue;
					}
				}
				
				if (skip)
					continue;
				
				//if the conclusion literal is in h0 - OK
				//if not - continue
				Literal B = k.getConclusion();
				if (!h0.contains(B))
					continue;
				
				//4b
				//deadEnd=false;
				enableDeadEnd = true; //some formula has been found - unlock the deadEnd section
				difference=0;
				h0.remove(B);
				h1.add(B);
				K.add(k);
				premises = k.getPremises();
				ip = premises.iterator();
				while (ip.hasNext())
				{
					Literal l = (Literal) ip.next();
					if (!h0.contains(l))
					{
						h0.add(l);
						tmp.add(l);//need to prepare tmp, can be used later on in 4c
					}
				}
				break;				
			}
					
			//4c
			//if nothing can be found - deadEnd
			if (enableDeadEnd && deadEnd)
			{
				difference=0;
				deadEnd=false;
				enableDeadEnd = false; //to avoid entering deadEnd section twice in a row
	
				//if nothing is in K - stop the function
				if (K.isEmpty())
					break;
				
				//remove the last formula
				int t = K.size();
				Formula f = K.get(t-1);
				K.remove(t-1);
				
				//remove premises
				Iterator<Literal> it = tmp.iterator();
				while (it.hasNext())
				{
					Literal l = (Literal) it.next();
					h0.remove(l);
				}
				
				//move B
				h0.add(f.getConclusion());
				h1.remove(f.getConclusion());
				
				//clear
				tmp.clear();
			}
			
			//stop condition (will only be activated if enableDeadLock is false and diff>=s)
			if (difference >= s)
				break;
		}
		
		//5
		//send to graph
		//K - formulas used to prove A
		
		return K;
	}
	
	private ArrayList<Formula> forwardChaining(Data data)
	{
		Literal toBeProved = data.getToBeProved();
		ArrayList<Formula> formulas = data.getFormulas(); // list of formulas
		ArrayList<Literal> literals = data.getLiterals(); //list of proved literals
	
		ArrayList<Formula> fPath = new ArrayList<Formula>(); //path of formulas

		//check whether toBeProved is on the literal list
		Iterator<Literal> il = literals.iterator();
		while(il.hasNext())
		{
			Literal l = (Literal) il.next();
			
			if (l == toBeProved)
				return fPath; //return empty path
		}
		
		boolean skip = false;
		int s = formulas.size();
		//stop condition:
		//if the number of iterations between the current iteration and the iteration in which 
		//the last literal has been proved is higher than the number of formulas - nothing more can be found
		int difference = -1;
		Iterator<Formula> i = formulas.iterator();
		while (true)
		{
			if (!i.hasNext())
				i = formulas.iterator();//come back to the beginning
			++difference;
			if (difference >= s)
				return fPath; //return path (not whole, just the part that is proved)
			Formula f = (Formula) i.next();
			if (f.isUsed())
				continue;			
			ArrayList<Literal> premises = f.getPremises();
			Iterator<Literal> ip = premises.iterator();
			while (ip.hasNext())
			{
				Literal l = (Literal) ip.next();
				//if the premise literal is not in literals L - skip the formula
				if (!literals.contains(l))
				{
					skip=true;
					break;	
				}
			}
			
			if (skip)
			{
				skip=false;
				continue;
			}
			
			//if all premises are in literals, add conclusion
			if (!literals.contains(f.getConclusion()))
			{
				literals.add(f.getConclusion());
				difference = 0;
				f.used();//mark the formula as used
				
				//update the path
				fPath.add(f);
			}
			
			if ((f.getConclusion()).equals(toBeProved))
				return fPath;
		}
	}
}