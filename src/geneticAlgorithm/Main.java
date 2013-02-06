package geneticAlgorithm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import ec.Evolve;

public class Main {
	
	public static void main(String[] args)
	{
		// Create genome result file
		try
		{
			FileWriter fstream = new FileWriter("evolution_results_" + GASnaykuuProblem.vision + ".txt");
			BufferedWriter out = new BufferedWriter(fstream);
			out.close();
			System.out.println("Successfully created the genome results file.");
		} 
		catch (IOException e)
		{
			System.out.println("Failed to create the genome results file.");
			e.printStackTrace();
			System.exit(0);
		}
		
		int nrOfRuns = 2;
		
		String evolveArgs[] = {"-file", "params/ga_snaykuu.params", "-p", "jobs=" + nrOfRuns, "stat.gather-full=true"};
		
		Evolve.main(evolveArgs);
	}
	
}
