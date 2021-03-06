package boomerang.backward;

import boomerang.AliasFinder;
import boomerang.BoomerangContext;
import boomerang.accessgraph.AccessGraph;
import boomerang.bidi.Incomings;
import boomerang.bidi.PathEdgeStore;
import boomerang.bidi.Summaries;
import boomerang.ifdssolver.DefaultIFDSTabulationProblem.Direction;
import boomerang.ifdssolver.IFDSSolver;
import boomerang.ifdssolver.IPathEdge;
import boomerang.ifdssolver.PathEdge;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;

public class BackwardSolver
		extends IFDSSolver<Unit, AccessGraph, SootMethod, BiDiInterproceduralCFG<Unit, SootMethod>> {

	private BoomerangContext context;

	public BackwardSolver(BackwardProblem tabulationProblem, BoomerangContext context) {
		super(tabulationProblem, context.debugger);
		this.context = context;
		this.pathEdges = new PathEdgeStore(context, Direction.BACKWARD);
		this.summaries = new Summaries(context);
		this.incomings = new Incomings();
	}

	public void startPropagation(AccessGraph d1, Unit stmt) {
		for (Unit s : icfg.getSuccsOf(stmt)) {
			PathEdge<Unit, AccessGraph> edge = new PathEdge<Unit, AccessGraph>(null, d1, s, d1);
			debugger.backwardStart(Direction.BACKWARD, stmt, d1, s);
			propagate(edge, PropagationType.Normal);
		}
	}

	@Override
	public void onRegister(IPathEdge<Unit, AccessGraph> edge) {
		context.sanityCheckEdge(edge);
		if (edge.getTarget() != null)
			AliasFinder.VISITED_METHODS.add(icfg.getMethodOf(edge.getTarget()));
	}

	public String toString() {
		return "BWSOLVER";
	}

}
