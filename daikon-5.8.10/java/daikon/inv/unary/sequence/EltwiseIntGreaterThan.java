// ***** This file is automatically generated from EltwiseIntComparisons.java.jpp

package daikon.inv.unary.sequence;

import daikon.*;
import daikon.Quantify.QuantFlags;
import daikon.derive.*;
import daikon.derive.binary.*;
import daikon.inv.*;
import java.util.*;
import org.checkerframework.checker.interning.qual.Interned;
import org.checkerframework.checker.lock.qual.GuardSatisfied;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Pure;
import org.checkerframework.dataflow.qual.SideEffectFree;
import org.plumelib.util.Intern;
import typequals.prototype.qual.NonPrototype;
import typequals.prototype.qual.Prototype;

  /**
   * Represents the invariant &gt; between adjacent elements
   * (x[i], x[i+1]) of a long sequence. Prints as
   * {@code x[] sorted by >}.
   */

public class EltwiseIntGreaterThan extends EltwiseIntComparison {
  // We are Serializable, so we specify a version to allow changes to
  // method signatures without breaking serialization.  If you add or
  // remove fields, you should change this number to the current date.
  static final long serialVersionUID = 20030822L;

  // Variables starting with dkconfig_ should only be set via the
  // daikon.config.Configuration interface.
  /** Boolean. True iff EltwiseIntComparison invariants should be considered. */
  public static boolean dkconfig_enabled = Invariant.invariantEnabledDefault;

  static final boolean debugEltwiseIntComparison = false;

  protected EltwiseIntGreaterThan(PptSlice ppt) {
    super(ppt);
  }

  protected @Prototype EltwiseIntGreaterThan() {
    super();
  }

  private static @Prototype EltwiseIntGreaterThan proto = new @Prototype EltwiseIntGreaterThan();

  /** Returns the prototype invariant for EltwiseIntGreaterThan */
  public static @Prototype EltwiseIntGreaterThan get_proto() {
    return proto;
  }

  /** returns whether or not this invariant is enabled */
  @Override
  public boolean enabled() {
    return dkconfig_enabled;
  }

  /** Non-equality EltwiseIntGreaterThan invariants are only valid on integral types. */
  @Override
  public boolean instantiate_ok(VarInfo[] vis) {

    if (!valid_types(vis)) {
      return false;
    }

      if (!vis[0].type.baseIsIntegral()) {
        return false;
      }

    return true;
  }

  /** Instantiate the invariant on the specified slice. */
  @Override
  protected EltwiseIntGreaterThan instantiate_dyn(@Prototype EltwiseIntGreaterThan this, PptSlice slice) {
    return new EltwiseIntGreaterThan(slice);
  }

  @SideEffectFree
  @Override
  public EltwiseIntGreaterThan clone(@GuardSatisfied EltwiseIntGreaterThan this) {
    EltwiseIntGreaterThan result = (EltwiseIntGreaterThan) super.clone();
    return result;
  }

  @Override
  public String repr(@GuardSatisfied EltwiseIntGreaterThan this) {
    return "EltwiseIntGreaterThan" + varNames() + ": falsified=" + falsified;
  }

  @SideEffectFree
  @Override
  public String format_using(@GuardSatisfied EltwiseIntGreaterThan this, OutputFormat format) {
    if (format.isJavaFamily()) {
      return format_java_family(format);
    }

    if (format == OutputFormat.DAIKON) {
      return format_daikon();
    }
    if (format == OutputFormat.ESCJAVA) {
      return format_esc();
    }
    if (format == OutputFormat.CSHARPCONTRACT) {
      return format_csharp_contract();
    }
    if (format == OutputFormat.SIMPLIFY) {
      return format_simplify();
    }

    return format_unimplemented(format);
  }

  public String format_daikon(@GuardSatisfied EltwiseIntGreaterThan this) {
    if (debugEltwiseIntComparison) {
      System.out.println(repr());
    }

    return (var().name() + " sorted by >");
  }

  public String format_esc(@GuardSatisfied EltwiseIntGreaterThan this) {
    String[] form = VarInfo.esc_quantify(false, var(), var());

      return form[0] + "((i+1 == j) ==> (" + form[1] + " > " + form[2] + "))" + form[3];
  }

  public String format_java_family(@GuardSatisfied EltwiseIntGreaterThan this, OutputFormat format) {
    return "daikon.Quant.eltwiseGT(" + var().name_using(format) + ")";
  }

  public String format_csharp_contract(@GuardSatisfied EltwiseIntGreaterThan this) {
    String[] split = var().csharp_array_split();
    return "Contract.ForAll(0, " + split[0] + ".Count()-1, i => " + split[0] + "[i]" + split[1] + " > " + split[0] + "[i+1]" + split[1] + ")";
  }

  public String format_simplify(@GuardSatisfied EltwiseIntGreaterThan this) {
    String[] form = VarInfo.simplify_quantify(QuantFlags.adjacent(),
                                               var(), var());

    String comparator = ">";

    return form[0] + "(" + comparator + " " + form[1] + " " + form[2] + ")"
      + form[3];
  }

  @Override
  @SuppressWarnings("UnnecessaryParentheses")  // generated code, parentheses are sometimes needed
  public InvariantStatus check_modified(long @Interned [] a, int count) {
    for (int i = 1; i < a.length; i++) {
      if (!((a[i - 1]) > ( a[i]))) {
        return InvariantStatus.FALSIFIED;
      }
    }
    return InvariantStatus.NO_CHANGE;
  }

  @Override
  public InvariantStatus add_modified(long @Interned [] a, int count) {
    return check_modified(a, count);
  }

  // Perhaps check whether all the arrays of interest have length 0 or 1.

  @Override
  protected double computeConfidence() {

    return 1 - Math.pow(.5, ppt.num_samples());
  }

  @Pure
  @Override
  public boolean isExact() {

    return false;
  }

  @Pure
  @Override
  public boolean isSameFormula(Invariant other) {
    return (other instanceof EltwiseIntGreaterThan);
  }

  // Not pretty... is there another way?
  // Also, reasonably complicated, need to ensure exact correctness, not sure if the
  // regression tests test this functionality

  @Pure
  @Override
  public boolean isExclusiveFormula(Invariant other) {
    // This whole approach is wrong in the case when the sequence can
    // ever consist of only one element.  For now, just forget
    // it. -SMcC
    if (true) {
      return false;
    }

    if (other instanceof EltwiseIntComparison) {

      return !((other instanceof EltwiseIntGreaterThan) || (other instanceof EltwiseFloatGreaterThan)
               || (other instanceof EltwiseIntGreaterEqual) || (other instanceof EltwiseFloatGreaterEqual));

    }
    return false;
  }

  // Look up a previously instantiated invariant.
  public static @Nullable EltwiseIntGreaterThan find(PptSlice ppt) {
    assert ppt.arity() == 1;
    for (Invariant inv : ppt.invs) {
      if (inv instanceof EltwiseIntGreaterThan) {
        return (EltwiseIntGreaterThan) inv;
      }
    }
    return null;
  }

  // Copied from IntComparison.
  // public boolean isExclusiveFormula(Invariant other)
  // {
  //   if (other instanceof IntComparison) {
  //     return core.isExclusiveFormula(((IntComparison) other).core);
  //   }
  //   if (other instanceof IntNonEqual) {
  //     return isExact();
  //   }
  //   return false;
  // }

  /**
   * This function returns whether a sample has been seen by this Invariant that includes two or
   * more entries in an array. For a 0 or 1 element array a, a[] sorted by any binary operation is
   * "vacuously true" because no check is ever made since the binary operation requires two
   * operands. Thus although invariants of this type are true regarding 0 or 1 length arrays, they
   * are meaningless. This function is meant to be used in isObviousImplied() to prevent such
   * meaningless invariants from being printed.
   */
  @Override
  public boolean hasSeenNonSingletonSample() {
    ValueSet.ValueSetScalarArray vs = (ValueSet.ValueSetScalarArray) ppt.var_infos[0].get_value_set();
    return (vs.nonsingleton_arr_cnt() > 0);
  }

  @Pure
  @Override
  public @Nullable DiscardInfo isObviousDynamically(VarInfo[] vis) {
    DiscardInfo super_result = super.isObviousDynamically(vis);
    if (super_result != null) {
      return super_result;
    }

    if (!hasSeenNonSingletonSample()) {
      return new DiscardInfo(this, DiscardCode.obvious,
                             "No samples sequences of size >=2 were seen. Vacuously true.");
    }

    EltOneOf eoo = EltOneOf.find(ppt);
    if ((eoo != null) && eoo.enoughSamples() && (eoo.num_elts() == 1)) {
      return new DiscardInfo(this, DiscardCode.obvious, "The sequence contains all equal values.");
    }

    // sorted by (any operation) for an entire sequence -> sorted by that same
    // operation for a subsequence

    // also, sorted by < for entire -> sorted by <= for subsequence
    //       sorted by > for entire -> sorted by >= for subsequence

    Derivation deriv = vis[0].derived;

    if ((deriv instanceof SequenceScalarSubsequence) || (deriv instanceof SequenceFloatSubsequence)) {
      // Find the slice with the full sequence, check for an invariant of this type
      PptSlice sliceToCheck;

      if (deriv instanceof SequenceScalarSubsequence) {
        sliceToCheck = ppt.parent.findSlice(((SequenceScalarSubsequence)deriv).seqvar());
      } else {
        sliceToCheck = ppt.parent.findSlice(((SequenceFloatSubsequence)deriv).seqvar());
      }

      if (sliceToCheck != null) {
        for (Invariant inv : sliceToCheck.invs) {

          if (inv.getClass().equals(getClass())) {
            String discardString = "This is a subsequence of a sequence for which the same invariant holds.";
            return new DiscardInfo(this, DiscardCode.obvious, discardString);
          }
        }
      }
    }

    return null;
  }
}