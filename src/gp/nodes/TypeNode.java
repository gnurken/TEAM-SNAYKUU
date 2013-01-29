package gp.nodes;

import java.util.Random;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.ERC;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import gp.data.TypeData;

public class TypeNode extends ERC
{
	
	private TypeData.TileType type;

	@Override
	public String toString()
	{
		if (type != null)
		{
			String str = type.toString();
			str = str.charAt(0) + str.substring(1).toLowerCase();
			return str;
		}
		
		return "Type";
	}
	
	@Override
	public void resetNode(EvolutionState state, int thread)
	{
		int n = state.random[thread].nextInt(TypeData.TileType.values().length);
		
		type = TypeData.TileType.values()[n];
	}

	@Override
	public boolean nodeEquals(GPNode node)
	{
		return (node instanceof TypeNode && ((TypeNode)node).type == type);
	}

	@Override
	public String encode()
	{
		return toString();
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem)
	{
		((TypeData)input).type = type;
	}

}
