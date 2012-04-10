package gp;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import bot.Cancer;
import bot.CarefulBot;
import bot.FruitEaterBot;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.gp.GPProblem;
import ec.gp.koza.KozaFitness;
import ec.util.Parameter;
import gameLogic.Brain;
import gameLogic.GameObjectType;
import gameLogic.Metadata;
import gameLogic.Session;
import gameLogic.Snake;
import gameLogic.Team;

public class SnaykuuProblem extends GPProblem
{
	
	public DirectionData input;
	
	public Object clone()
	{
		SnaykuuProblem newProblem = (SnaykuuProblem)super.clone();
		newProblem.input = input;
		return newProblem;
	}
	
	public void setup(final EvolutionState state, final Parameter base)
	{
		super.setup(state, base);
		
		input = (DirectionData) state.parameters.getInstanceForParameterEq(
								base.push(P_DATA), null, DirectionData.class);
		input.setup(state, base.push(P_DATA));
		
	}

	@Override
	public void evaluate(EvolutionState state, Individual ind,
						 int subpopulation, int threadnum)
	{
		
		if (!ind.evaluated)
		{
			GameObjectType objectType = new GameObjectType("Snake", true);
			
			Metadata metadata = null;
			Session session = new Session(metadata);
			
			Team contestants = new Team("Contestants", 1);
			session.addTeam(contestants);
			
			GPIndividual individual = (GPIndividual)ind;
			int snakesPerTeam = individual.trees.length;
			for (int i = 0; i < snakesPerTeam; ++i)
			{
				GPNode root = individual.trees[i].child;
				
				GPBrain brain = new GPBrain();
				brain.setup(root, state, threadnum, stack, individual, this, input);
				
				Snake snake = new Snake(objectType, "Contestant" + i, brain, Color.BLUE);
				session.addSnake(snake, contestants);
			}
			
			// Create Opponent team
			Team opponents = new Team("Opponents", 2);
			session.addTeam(opponents);
			
			for (int i = 0; i < snakesPerTeam; ++i)
			{
				Snake snake = new Snake(objectType, "Opponent" + i, new FruitEaterBot(), Color.RED);
				session.addSnake(snake, opponents);
			}
			
			session.prepareForStart();
			
			while (!session.hasEnded())
				session.tick();
			
			List<Team> result = session.getGameResult().getTeams();
			
			// TODO: evaluate fitness
			((KozaFitness)ind.fitness).setStandardizedFitness(state, 1.0f);
			
			ind.evaluated = true;
		}
	}

}
