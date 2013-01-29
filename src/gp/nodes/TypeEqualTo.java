package gp.nodes;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import gp.data.BoolData;
import gp.data.TypeData;

public class TypeEqualTo extends GPNode
{

	@Override
	public String toString()
	{
		return "=";
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem)
	{
		TypeData data = new TypeData();
		children[0].eval(state, thread, data, stack, individual, problem);
		TypeData.TileType left = data.type;
		children[1].eval(state, thread, data, stack, individual, problem);
		TypeData.TileType right = data.type;
		((BoolData)input).bool = left == right;
	}

}
