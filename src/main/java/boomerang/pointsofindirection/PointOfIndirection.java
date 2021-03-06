package boomerang.pointsofindirection;

import boomerang.BoomerangContext;
import boomerang.accessgraph.AccessGraph;
import boomerang.ifdssolver.IFDSSolver.PropagationType;
import boomerang.ifdssolver.PathEdge;
import heros.solver.Pair;
import soot.Unit;

public class PointOfIndirection {

	protected final AccessGraph sendBackward;
	protected final Unit fromStmt;
	protected final BoomerangContext context;
	public PointOfIndirection(AccessGraph sendBackward, Unit fromStmt, BoomerangContext context){
		assert !sendBackward.isStatic();
		assert sendBackward.getBase() != null;
		this.sendBackward = sendBackward;
		this.fromStmt = fromStmt;
		this.context = context;
	}
	
	public void sendBackward(){
		for(Unit u :context.icfg.getPredsOf(fromStmt)){
			PathEdge<Unit, AccessGraph> pe = new PathEdge<>(null, sendBackward, u, sendBackward);
			context.getBackwardSolver().inject(pe, PropagationType.Normal);
		}
	}
	public Pair<Unit,AccessGraph> getTarget(){
		return new Pair<Unit,AccessGraph>(fromStmt,sendBackward);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fromStmt == null) ? 0 : fromStmt.hashCode());
		result = prime * result + ((sendBackward == null) ? 0 : sendBackward.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PointOfIndirection other = (PointOfIndirection) obj;
		if (fromStmt == null) {
			if (other.fromStmt != null)
				return false;
		} else if (!fromStmt.equals(other.fromStmt))
			return false;
		if (sendBackward == null) {
			if (other.sendBackward != null)
				return false;
		} else if (!sendBackward.equals(other.sendBackward))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "POI " + sendBackward + " @ " + fromStmt;
	}
}
