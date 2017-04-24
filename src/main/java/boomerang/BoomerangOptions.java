package boomerang;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

import boomerang.accessgraph.AccessGraph;
import boomerang.debug.DefaultBoomerangDebugger;
import boomerang.debug.IBoomerangDebugger;
import boomerang.ifdssolver.IPathEdge;
import boomerang.ifdssolver.IPropagationController;
import boomerang.pointsofindirection.Alloc;
import boomerang.pointsofindirection.AllocationSiteHandler;
import boomerang.pointsofindirection.AllocationSiteHandlers;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.NullConstant;
import soot.jimple.StringConstant;
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG;

public abstract class BoomerangOptions {

	public IBoomerangDebugger getDebugger() {
		return new DefaultBoomerangDebugger();
	}

	public long getTimeBudget() {
		return TimeUnit.SECONDS.toMillis(100);
	}

	public boolean getTrackStaticFields() {
		return true;
	}

	public boolean getTrackStatementsInFields() {
		return false;
	}

	public boolean stronglyUpdateFields() {
		return true;
	}

	public abstract IInfoflowCFG icfg();

	public String toString() {
		String str = "====== Boomerang Options ======";
		str += "\nDebugger:\t\t" + getDebugger();
		str += "\nAnalysisBudget(ms):\t" + getTimeBudget();
		str += "\nAllocationSiteHandler:\t" + allocationSiteHandlers();
		str += "\n====================";
		return str;
	}

	public ContextScheduler getScheduler() {
		return new ContextScheduler();
	}

	public IPropagationController<Unit, AccessGraph> propagationController() {
		return new IPropagationController<Unit, AccessGraph>() {
			@Override
			public boolean continuePropagate(IPathEdge<Unit, AccessGraph> edge) {
				return true;
			}
		};
	}

	public AllocationSiteHandlers allocationSiteHandlers() {
		return new AllocationSiteHandlers() {

			private boolean isAllocationValue(Value val) {
				return (val instanceof NewExpr || val instanceof NewArrayExpr
						|| val instanceof NewMultiArrayExpr || val instanceof NullConstant);
			}

			@Override
			public Optional<AllocationSiteHandler> assignStatement(final AssignStmt stmt,final Value rightOp,
					final AccessGraph source) {
				if (!isAllocationValue(rightOp))
					return Optional.absent();
				return Optional.<AllocationSiteHandler>of(new AllocationSiteHandler() {
					@Override
					public Optional<Alloc> sendForwards() {
						if (source.getFieldCount() > 0 && !source.firstFieldMustMatch(AliasFinder.ARRAY_FIELD)) {
							return Optional.absent();
						}
						if (source.getFieldCount() > 1 && source.firstFieldMustMatch(AliasFinder.ARRAY_FIELD))
							return Optional.absent();
						return Optional.of(new Alloc(source,stmt, rightOp instanceof NullConstant));
					}
				});
			}

			@Override
			public Optional<AllocationSiteHandler> arrayStoreStatement(final AssignStmt stmt, Value rightOp,
					final AccessGraph source) {
				if (!isAllocationValue(rightOp))
					return Optional.absent();
				return Optional.<AllocationSiteHandler>of(new AllocationSiteHandler() {
					@Override
					public Optional<Alloc> sendForwards() {
						return Optional.of(new Alloc(source,stmt ,false));
					}
				});
			}

			@Override
			public Optional<AllocationSiteHandler> returnStmtViaCall(final AssignStmt assignedCallSite, final AccessGraph source,
					Value retOp) {
				if (!(retOp instanceof NullConstant))
					return Optional.absent();
				return Optional.<AllocationSiteHandler>of(new AllocationSiteHandler() {
					@Override
					public Optional<Alloc> sendForwards() {
						return Optional.of(new Alloc(source,assignedCallSite, true));
					}
				});
			}

			@Override
			public Optional<AllocationSiteHandler> fieldStoreStatement(final AssignStmt stmt, InstanceFieldRef fieldRef,
					Value rightOp, final AccessGraph source) {
				if (!(rightOp instanceof NullConstant))
					return Optional.absent();
				if(source.getFieldCount() != 1){
					return Optional.absent();
				}
				return Optional.<AllocationSiteHandler>of(new AllocationSiteHandler() {
					@Override
					public Optional<Alloc> sendForwards() {
						return Optional.of(new Alloc(source,stmt, true));
					}
				});
			}
		};
	}
}
