import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CalculationEngine
{
	private List<List<Literal>> data;
	
	public CalculationEngine(){
		data = new ArrayList<List<Literal>>();
	}
	
	// Reads data from file into list of clauses
	public void readDataFromFile(String dataFileName){
		try{
			// Open the file given as the parameter 
			FileInputStream fstream = new FileInputStream(dataFileName);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				this.data.add(getParsedClause(strLine));
			}
			//Close the input stream
			in.close();			
		}
		catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	// Parses clause from input string
	private List<Literal> getParsedClause(String strLine){
		List<Literal> clause = new ArrayList<Literal>();
		ArrayList<String> literals = new ArrayList<String>(Arrays.asList(strLine.split("[vV]")));
		int number;
		boolean value;
		
		for(String s : literals){
			// Delete white spaces and parse into int
			number = Integer.parseInt(s.replaceAll("[\\s;]", ""));
			if(number > 0) value = true;
			else if(number < 0) value = false;
			else
				throw new IllegalArgumentException("Error: Literal cannot be named 0.");
			// Absolute value of the number is the name of literal and the sign of number implicates true or false value of literal
			Literal newLiteral = new Literal(Math.abs(number),value);
			clause.add(newLiteral);
		}
		return clause ;
	}

}
