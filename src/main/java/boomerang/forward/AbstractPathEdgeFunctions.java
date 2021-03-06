package boomerang.forward;

import soot.Local;
import soot.Scene;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import boomerang.BoomerangContext;
import boomerang.accessgraph.AccessGraph;
import boomerang.accessgraph.WrappedSootField;
import boomerang.ifdssolver.DefaultPathEdgeFunctions;
import boomerang.ifdssolver.FlowFunctions;
import boomerang.ifdssolver.IPathEdge;
import boomerang.ifdssolver.DefaultIFDSTabulationProblem.Direction;

public abstract class AbstractPathEdgeFunctions extends
    DefaultPathEdgeFunctions<Unit, AccessGraph, SootMethod> {
  protected BoomerangContext context;

  public AbstractPathEdgeFunctions(FlowFunctions<Unit, AccessGraph, SootMethod> flowFunctions,
      BoomerangContext c, Direction dir) {
    super(flowFunctions, c.debugger, dir);
    context = c;
  }

  @Override
  protected void sanitize(IPathEdge<Unit, AccessGraph> edge) {
    assertAccessPath(edge.factAtTarget());
    Unit target = edge.getTarget();
    AccessGraph ap = edge.factAtTarget();
    assert edge.factAtSource().isStatic() || edge.factAtSource().getBase() !=null;
    SootMethod method = context.icfg.getMethodOf(target);
    if (ap.isStatic() || method.isStatic())
      return;
    
    assert ap.isStatic() || method.getActiveBody().getLocals().contains(ap.getBase());
  }

  private void assertAccessPath(AccessGraph a) {
	  assert a.isStatic() || a.getBase() != null;
	  assert !(a.getBase() == null && a.hasSetBasedFieldGraph());
  }

  @Override
  public boolean isValid(AccessGraph d2) {
    return context.isValidAccessPath(d2);
  }

  public void cleanup() {
    this.context = null;
  }

}
