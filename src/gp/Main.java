package gp;

import ec.Evolve;

public class Main {
	
	public static void main(String[] args)
	{
		String evolveArgs[] = {"-file", "params/snaykuu.params", "-p", "stat.gather-full=true"};
		
		Evolve.main(evolveArgs);
	}
	
}
