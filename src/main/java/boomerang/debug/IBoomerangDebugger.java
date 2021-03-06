package boomerang.debug;

import boomerang.AliasResults;
import boomerang.BoomerangContext;
import boomerang.Query;
import boomerang.accessgraph.AccessGraph;
import boomerang.ifdssolver.IFDSDebugger;
import boomerang.ifdssolver.IPathEdge;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.AssignStmt;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;

public interface IBoomerangDebugger extends
    IFDSDebugger<Unit, AccessGraph, SootMethod, BiDiInterproceduralCFG<Unit, SootMethod>> {
  public void finishedQuery(Query q, AliasResults res);

  public void startQuery(Query q);

public void onCurrentlyProcessingRecursiveQuery(Query q);

public void onLoadingQueryFromCache(Query q, AliasResults aliasResults);

public void onAllocationSiteReached(Unit as, AccessGraph factAtTarget);

public void onAliasQueryFinished(Query q, AliasResults res);

public void onAliasTimeout(Query q);

public void indirectFlowEdgeAtRead(AccessGraph source, Unit curr, AccessGraph ap, Unit succ);

public void indirectFlowEdgeAtWrite(AccessGraph source, Unit target, AccessGraph ag, Unit curr);

public void indirectFlowEdgeAtReturn(AccessGraph source, Unit callSite, AccessGraph alias, Unit returnSite);
public void indirectFlowEdgeAtCall(AccessGraph source, Unit callSite, AccessGraph alias, Unit returnSite);

public void setContext(BoomerangContext boomerangContext);
}
