// ***** This file is automatically generated from SeqComparison.java.jpp

package daikon.inv.binary.twoSequence;

import daikon.*;
import daikon.Quantify.QuantFlags;
import daikon.derive.binary.*;
import daikon.inv.*;
import daikon.suppress.*;
import java.util.*;
import java.util.logging.Logger;
import org.checkerframework.checker.interning.qual.Interned;
import org.checkerframework.checker.lock.qual.GuardSatisfied;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Pure;
import org.checkerframework.dataflow.qual.SideEffectFree;
import org.plumelib.util.ArraysPlume;
import org.plumelib.util.Intern;
import typequals.prototype.qual.NonPrototype;
import typequals.prototype.qual.Prototype;

/**
 * Represents invariants between two sequences of long values. If order matters for each
 * variable (which it does by default), then the sequences are compared lexically. Prints as
 * {@code x[] == y[] lexically}.
 *

 * <p>If order doesn't matter for each variable, then the sequences are compared to see if they are
 * set equivalent. Prints as {@code x[] == y[]}.
 *

 * <p>If the auxiliary information (e.g., order matters) doesn't match between two variables, then
 * this invariant cannot apply to those variables.
 */
public class SeqSeqIntEqual extends TwoSequence

  implements EqualityComparison

{
  // We are Serializable, so we specify a version to allow changes to
  // method signatures without breaking serialization.  If you add or
  // remove fields, you should change this number to the current date.
  static final long serialVersionUID = 20030822L;

  // Variables starting with dkconfig_ should only be set via the
  // daikon.config.Configuration interface.
  /** Boolean. True iff SeqSeqIntEqual invariants should be considered. */
  public static boolean dkconfig_enabled = Invariant.invariantEnabledDefault;

  /** Debugging logger. */
  static final Logger debug = Logger.getLogger("daikon.inv.binary.twoSequence.SeqSeqIntEqual");

  static Comparator<long[]> comparator = new ArraysPlume.LongArrayComparatorLexical();

  boolean orderMatters;

  protected SeqSeqIntEqual(PptSlice ppt, boolean order) {
    super(ppt);
    orderMatters = order;
  }

  protected @Prototype SeqSeqIntEqual(boolean order) {
    super();
    orderMatters = order;
  }

  private static @Prototype SeqSeqIntEqual proto = new @Prototype SeqSeqIntEqual(true);

  /** Returns the prototype invariant for SeqSeqIntEqual */
  public static @Prototype SeqSeqIntEqual get_proto() {
    return proto;
  }

  /** Returns whether or not this invariant is enabled. */
  @Override
  public boolean enabled() {
    return dkconfig_enabled;
  }

  /** Non-Equal SeqComparison is only valid on integral types. */
  @Override
  public boolean instantiate_ok(VarInfo[] vis) {

    if (!valid_types(vis)) {
      return false;
    }

    return true;
  }

  /** Instantiates the invariant on the specified slice. */
  @Override
  protected SeqSeqIntEqual instantiate_dyn(@Prototype SeqSeqIntEqual this, PptSlice slice) {
    boolean has_order = slice.var_infos[0].aux.hasOrder() && slice.var_infos[1].aux.hasOrder();
    return new SeqSeqIntEqual(slice, has_order);
  }

  @Override
  protected Invariant resurrect_done_swapped() {

    return this;
  }

  @Override
  public String repr(@GuardSatisfied SeqSeqIntEqual this) {
    return "SeqSeqIntEqual" + varNames() + ": ,orderMatters=" + orderMatters
      + ",enoughSamples=" + enoughSamples()
      ;
  }

  @SideEffectFree
  @Override
  public String format_using(@GuardSatisfied SeqSeqIntEqual this, OutputFormat format) {
    // System.out.println("Calling SeqSeqIntEqual.format for: " + repr());

    if (format == OutputFormat.SIMPLIFY) {
      return format_simplify();
    }

    if (format == OutputFormat.DAIKON) {
      String name1 = var1().name_using(format);
      String name2 = var2().name_using(format);

      return name1 + " == " + name2;
    }

    if (format == OutputFormat.CSHARPCONTRACT) {

      String[] split1 = var1().csharp_array_split();
      String[] split2 = var2().csharp_array_split();
      // Pairwise equal.
      return "Contract.ForAll(0, " + split1[0] + ".Count(), i => " + split1[0] + "[i]" + split1[1] + ".Equals(" + split2[0] + "[i]" + split2[1] + "))";
    }

    if (format.isJavaFamily()) {
      String name1 = var1().name_using(format);
      String name2 = var2().name_using(format);

      return "daikon.Quant.pairwiseEqual(" + name1 + ", " + name2 + ")";
    }

    return format_unimplemented(format);
  }

  public String format_simplify(@GuardSatisfied SeqSeqIntEqual this) {
    if (Invariant.dkconfig_simplify_define_predicates) {
      return format_simplify_defined();
    } else {
      return format_simplify_explicit();
    }
  }

  private String format_simplify_defined(@GuardSatisfied SeqSeqIntEqual this) {
    String[] var1_name = var1().simplifyNameAndBounds();
    String[] var2_name = var2().simplifyNameAndBounds();
    if (var1_name == null || var2_name == null) {
      return String.format("%s.format_simplify_defined(%s): var1_name=%s, var2_name=%s, for %s",
                           getClass().getSimpleName(), this,
                           Arrays.toString(var1_name), Arrays.toString(var2_name), format());
    }
    return "(|lexical-==| "
      + var1_name[0] + " " + var1_name[1] + " " + var1_name[2] + " "
      + var2_name[0] + " " + var2_name[1] + " " + var2_name[2] + ")";
  }

  private String format_simplify_explicit(@GuardSatisfied SeqSeqIntEqual this) {

      // A simple case: if two sequences are lexically equal iff they
      // are elementwise equal.
      String[] form = VarInfo.simplify_quantify(QuantFlags.element_wise(),
                                                 var1(), var2());
      return form[0]
        + "(EQ " + form[1] + " " + form[2] + ")" + form[3];

  }

  @Override
  public InvariantStatus check_modified(
      long @Interned [] v1, long @Interned [] v2, int count) {
    /// This does not do the right thing; I really want to avoid comparisons
    /// if one is missing, but not if one is zero-length.
    // // Don't make comparisons with empty arrays.
    // if ((v1.length == 0) || (v2.length == 0)) {
    //   return;
    // }

    int comparison = 0;
    if (orderMatters) {
      // Standard element wise comparison
       comparison = comparator.compare(v1, v2);
    } else {
      // Do a double subset comparison
      comparison = (ArraysPlume.isSubset(v1, v2) && ArraysPlume.isSubset( v2, v1)) ? 0 : -1;
    }

    if (!(comparison == 0) ) {
      return InvariantStatus.FALSIFIED;
    }
    return InvariantStatus.NO_CHANGE;
  }

  @Override
  public InvariantStatus add_modified(long @Interned [] v1, long @Interned [] v2, int count) {
    if (logDetail()) {
      log("add_modified (%s, %s)", Arrays.toString(v1), Arrays.toString(v2));
    }
        return check_modified(v1, v2, count);
  }

  @Override
  protected double computeConfidence() {

    // It's an equality invariant
    if (ppt.num_samples() == 0) {
      return Invariant.CONFIDENCE_UNJUSTIFIED;
    } else {
      return Invariant.CONFIDENCE_JUSTIFIED;
    }

  }

  // For Comparison interface
  @Override
  public double eq_confidence() {
      return getConfidence();
  }

  @Pure
  @Override
  public boolean isSameFormula(Invariant o) {
    return true;
  }

  @Pure
  @Override
  public boolean isExclusiveFormula(Invariant o) {
    return false;
  }

  /**
   * Since this invariant can be a postProcessed equality, we have to handle isObvious especially to
   * avoid circular isObvious relations.
   */
  @Pure
  @Override
  public @Nullable DiscardInfo isObviousStatically_SomeInEquality() {
    if (var1().equalitySet == var2().equalitySet) {
      return isObviousStatically(this.ppt.var_infos);
    } else {
      return super.isObviousStatically_SomeInEquality();
    }
  }

  /**
   * Since this invariant can be a postProcessed equality, we have to handle isObvious especially to
   * avoid circular isObvious relations.
   */
  @Pure
  @Override
  public @Nullable DiscardInfo isObviousDynamically_SomeInEquality() {
    if (logOn()) {
      log("Considering dynamically_someInEquality");
    }
    if (var1().equalitySet == var2().equalitySet) {
      return isObviousDynamically(this.ppt.var_infos);
    } else {
      return super.isObviousDynamically_SomeInEquality();
    }
  }

  @Pure
  @Override
  public @Nullable DiscardInfo isObviousStatically(VarInfo[] vis) {

      VarInfo var1 = vis[0];
      VarInfo var2 = vis[1];
      DiscardInfo di;
      di = SubSequence.isObviousSubSequence(this, var1, var2);
      if (di == null) {
        di = SubSequence.isObviousSubSequence(this, var2, var1);
      }
      if (di != null) {
        return di;
      }

    return super.isObviousStatically(vis);
  }

  @Pure
  @Override
  public @Nullable DiscardInfo isObviousDynamically(VarInfo[] vis) {
    DiscardInfo super_result = super.isObviousDynamically(vis);
    if (super_result != null) {
      return super_result;
    }
    assert ppt != null;

      Debug debug = new Debug(getClass(), ppt, vis);

      if (logOn()) {
        debug.log("Checking IsObviousDynamically");
      }

      // Check to see if the same Pairwise invariant exists
      DiscardInfo di = new DiscardInfo(this, DiscardCode.obvious, "");
      if (ppt.parent.check_implied(di, vis[0], vis[1], PairwiseIntEqual.get_proto())) {
        di.add_implied_vis(vis);
        return di;
      }

      // If either variable is a subsequence and the original arrays
      // are related elementwise this isn't interesting
      VarInfo v1 = vis[0];
      VarInfo v2 = vis[1];
      VarInfo arr1 = v1;
      VarInfo arr2 = v2;
      if (v1.derived instanceof SequenceScalarSubsequence) {
        arr1 = ((SequenceScalarSubsequence) v1.derived).seqvar();
      }
      if (v2.derived instanceof SequenceScalarSubsequence) {
        arr2 = ((SequenceScalarSubsequence) v2.derived).seqvar();
      }
      if (!isEqual() && ((arr1 != v1) || (arr2 != v2))) {
        VarInfo[] avis = new VarInfo [] {arr1, arr2};
        PptSlice slice = this.ppt.parent.findSlice_unordered(avis);
        if (slice != null) {
          PairwiseIntEqual picEQ = PairwiseIntEqual.find(slice);
          if (picEQ != null) {
            return new DiscardInfo(this, DiscardCode.obvious, "Implied by " + picEQ.format());
          }
          PairwiseIntLessThan picLT = PairwiseIntLessThan.find(slice);
          if (picLT != null) {
            return new DiscardInfo(this, DiscardCode.obvious, "Implied by " + picLT.format());
          }
          PairwiseIntGreaterThan picGT = PairwiseIntGreaterThan.find(slice);
          if (picGT != null) {
            return new DiscardInfo(this, DiscardCode.obvious, "Implied by " + picGT.format());
          }
          PairwiseIntLessEqual picLE = PairwiseIntLessEqual.find(slice);
          if (picLE != null) {
            return new DiscardInfo(this, DiscardCode.obvious, "Implied by " + picLE.format());
          }
          PairwiseIntGreaterEqual picGE = PairwiseIntGreaterEqual.find(slice);
          if (picGE != null) {
            return new DiscardInfo(this, DiscardCode.obvious, "Implied by " + picGE.format());
          }
        }
      }

      // Similarly, if either variable is a subsequence and the original
      // arrays are related lexically this isn't interesting
      if ((arr1 != v1) || (arr2 != v2)) {
        if (arr1 == arr2) {
          debug.log("Obvious Dynamic- subsequence from same array");
          return new DiscardInfo(this, DiscardCode.obvious, "Supersequences are related lexically");
        }
        VarInfo[] avis = {arr1, arr2};
        debug.log("looking for " + avis[0].name() + " " + avis[1].name());
        PptSlice slice = this.ppt.parent.findSlice_unordered(avis);
        debug.log("Found ppt " + slice);
        if (slice != null) {
          for (Invariant inv : slice.invs) {
            debug.log("-- invariant " + inv.format());
          }
          Invariant inv;
          inv = SeqSeqIntEqual.find(slice);
          if (inv != null) {
            if (logOn()) {
              debug.log("Obvious Dynamic from " + inv.format() + "(" + inv.getClass() + ")");
            }
            return new DiscardInfo(this, DiscardCode.obvious, "Implied by " + inv.format());
          }
          inv = SeqSeqIntLessThan.find(slice);
          if (inv != null) {
            if (logOn()) {
              debug.log("Obvious Dynamic from " + inv.format() + "(" + inv.getClass() + ")");
            }
            return new DiscardInfo(this, DiscardCode.obvious, "Implied by " + inv.format());
          }
          inv = SeqSeqIntGreaterThan.find(slice);
          if (inv != null) {
            if (logOn()) {
              debug.log("Obvious Dynamic from " + inv.format() + "(" + inv.getClass() + ")");
            }
            return new DiscardInfo(this, DiscardCode.obvious, "Implied by " + inv.format());
          }
          inv = SeqSeqIntLessEqual.find(slice);
          if (inv != null) {
            if (logOn()) {
              debug.log("Obvious Dynamic from " + inv.format() + "(" + inv.getClass() + ")");
            }
            return new DiscardInfo(this, DiscardCode.obvious, "Implied by " + inv.format());
          }
          inv = SeqSeqIntGreaterEqual.find(slice);
          if (inv != null) {
            if (logOn()) {
              debug.log("Obvious Dynamic from " + inv.format() + "(" + inv.getClass() + ")");
            }
            return new DiscardInfo(this, DiscardCode.obvious, "Implied by " + inv.format());
          }
        }
      }

      // Check to see if these variables are obviously related
      if (v1.isDerived() || v2.isDerived()) {
        if (SubSequence.isObviousSubSequenceDynamically(this, v1, v2)
          || SubSequence.isObviousSubSequenceDynamically(this, v2, v1)) {
          if (logOn()) {
            debug.log("Obvious SubSequence Dynamically");
          }
          assert ppt != null;
          return new DiscardInfo(
              this,
              DiscardCode.obvious,
              "Both vars are derived and one is a subsequence of the other");
        }
      }

    return null;
  }

  @Override
  public void repCheck() {
    super.repCheck();
    /*
      This code is no longer needed now that the can_be_x's are gone
    if (!(this.can_be_eq || this.can_be_lt || this.can_be_gt)
        && ppt.num_samples() != 0) {
      System.err.println(this.repr());
      System.err.println(this.ppt.num_samples());
      throw new Error();
    }
    */
  }

  @Pure
  public boolean isEqual() {

    return true;
  }

  // Look up a previously instantiated invariant.
  public static @Nullable SeqSeqIntEqual find(PptSlice ppt) {
    assert ppt.arity() == 2;
    for (Invariant inv : ppt.invs) {
      if (inv instanceof SeqSeqIntEqual) {
        return (SeqSeqIntEqual) inv;
      }
    }
    return null;
  }

  /** Returns a list of non-instantiating suppressions for this invariant. */
  @Pure
  @Override
  public @Nullable NISuppressionSet get_ni_suppressions() {
    return suppressions;
  }

    /** Definition of this invariant (the suppressee) */
    private static NISuppressee suppressee = new NISuppressee(SeqSeqIntEqual.class, 2);

    // Suppressor definitions (used in suppressions below)
    private static NISuppressor v1_pw_v2 = new NISuppressor(0, 1, PairwiseIntEqual.class);

    private static NISuppressionSet suppressions =
      new NISuppressionSet(
          new NISuppression[] {
            // pairwise => lexical
            new NISuppression(v1_pw_v2, suppressee),
          });

}
