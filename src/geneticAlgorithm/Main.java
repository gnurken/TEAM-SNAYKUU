package geneticAlgorithm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import ec.Evolve;

public class Main {
	
	public static void main(String[] args)
	{
		String evolveArguments[] = {};
		
		int vision = GASnaykuuProblem.vision;
		
		String resultsFilePath = "evolution_results_" + vision + ".txt";
		File resultsFile = new File(resultsFilePath);
		long resultsFileModified = resultsFile.exists() ? resultsFile.lastModified() : Long.MIN_VALUE;
		
		String path = ".";
		File folder = new File(path);
		File[] files = folder.listFiles();
		
		long lastCheckPointModified = Long.MIN_VALUE;
		int lastCheckPointNr = 0;
		
		for (File file : files)
		{
			String filePath = file.getName();
			if (filePath.contains("GA_SnaykuuCP.") && filePath.contains(".gz"))
			{
				long modified = file.lastModified();
				if (modified > lastCheckPointModified)
				{
					lastCheckPointNr = Integer.parseInt(filePath.substring(filePath.indexOf(".") + 1, filePath.lastIndexOf(".")));
					lastCheckPointModified = file.lastModified();
				}
			}
		}
		
		if (lastCheckPointModified > resultsFileModified)
		{
			System.out.println("Restarting from checkpoint.");
			String arguments[] = {"-checkpoint", "GA_SnaykuuCP." + lastCheckPointNr + ".gz"};
			evolveArguments = arguments;
		}
		else
		{
			int nrOfRuns = GASnaykuuProblem.runsPerSetting;
			
			// Create the results file if it does not exist.
			// Otherwise count number of previous runs, and subtract them from the number to be run.
			if (!resultsFile.exists())
			{
				try
				{
					FileWriter fstream = new FileWriter(resultsFilePath);
					BufferedWriter out = new BufferedWriter(fstream);
					out.close();
				} 
				catch (IOException e)
				{
					System.out.println("Failed to create the genome results file.");
					e.printStackTrace();
					System.exit(0);
				}
			}
			else
			{
				try
				{
					FileReader fstream = new FileReader(resultsFilePath);
					BufferedReader reader = new BufferedReader(fstream);
					
					String line;
					while ((line = reader.readLine()) != null)
					{
						if (line.contains(GASnaykuuProblem.teamResultStart))
							--nrOfRuns;
					}
					
					reader.close();
				}
				catch (IOException e)
				{
					System.out.println("Error while reading results file.");
					e.printStackTrace();
					System.exit(0);
				}
			}
			
			System.out.println("Running " + nrOfRuns + " jobs.");
			String arguments[] = {"-file", "params/ga_snaykuu.params", "-p", "jobs=" + nrOfRuns, "stat.gather-full=true"};
			evolveArguments = arguments;
		}
		
		Evolve.main(evolveArguments);
	}
	
}
