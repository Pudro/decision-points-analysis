// ***** This file is automatically generated from Numeric.java.jpp

package daikon.inv.binary.twoSequence;

import org.checkerframework.checker.signature.qual.ClassGetName;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.dataflow.qual.SideEffectFree;
import org.checkerframework.checker.lock.qual.GuardSatisfied;
import org.checkerframework.dataflow.qual.Pure;
import org.checkerframework.checker.nullness.qual.Nullable;
import static daikon.inv.Invariant.asInvClass;

import daikon.*;
import daikon.Quantify.QuantFlags;
import daikon.derive.binary.*;
import daikon.inv.*;
import daikon.inv.binary.twoScalar.*;
import daikon.inv.binary.twoString.*;
import daikon.inv.unary.scalar.*;
import daikon.inv.unary.sequence.*;
import daikon.suppress.*;
import java.util.*;
import org.plumelib.util.UtilPlume;
import typequals.prototype.qual.NonPrototype;
import typequals.prototype.qual.Prototype;

/**
 * Baseclass for binary numeric invariants.
 *
 * Each specific invariant is implemented in a subclass (typically, in this file). The subclass must
 * provide the methods instantiate(), check(), and format(). Symmetric functions should define
 * is_symmetric() to return true.
 */
public abstract class PairwiseNumericInt extends TwoSequence {

  // We are Serializable, so we specify a version to allow changes to
  // method signatures without breaking serialization.  If you add or
  // remove fields, you should change this number to the current date.
  static final long serialVersionUID = 20060609L;

  protected PairwiseNumericInt(PptSlice ppt, boolean swap) {
    super(ppt);
    this.swap = swap;
  }

  protected PairwiseNumericInt(boolean swap) {
    super();
    this.swap = swap;
  }

  /** Returns true if it is ok to instantiate a numeric invariant over the specified slice. */
  @Override
  public boolean instantiate_ok(VarInfo[] vis) {

    ProglangType type1 = vis[0].file_rep_type;
    ProglangType type2 = vis[1].file_rep_type;
    if (!type1.baseIsIntegral() || !type2.baseIsIntegral()) {
      return false;
    }

    return true;
  }

  @Pure
  @Override
  public boolean isExact() {
    return true;
  }

  @Override
  public String repr(@GuardSatisfied PairwiseNumericInt this) {
    return getClass().getSimpleName() + ": " + format()
      + (swap ? " [swapped]" : " [unswapped]");
  }

  /**
   * Returns a string in the specified format that describes the invariant.
   *
   * The generic format string is obtained from the subclass specific get_format_str(). Instances of
   * %varN% are replaced by the variable name in the specified format.
   */
  @SideEffectFree
  @Override
  public String format_using(@GuardSatisfied PairwiseNumericInt this, OutputFormat format) {

    if (ppt == null) {
      return (String.format("proto ppt [class %s] format %s", getClass(),
                             get_format_str(format)));
    }
    String fmt_str = get_format_str(format);

    String v1;
    String v2;
        if (format.isJavaFamily()) {

            v1 = var1().name_using(format);
            v2 = var2().name_using(format);
            if (this instanceof Divides) {
              return "daikon.Quant.pairwiseDivides(" + v1 + ", " + v2 + ")";
            } else if (this instanceof Square) {
              return "daikon.Quant.pairwiseSquare(" + v1 + ", " + v2 + ")";
            } else if (this instanceof BitwiseComplement) {
              return "daikon.Quant.pairwiseBitwiseComplement(" +v1+", "+v2+ ")";
            } else if (this instanceof BitwiseSubset) {
              return "daikon.Quant.pairwiseBitwiseSubset(" +v1+ ", " + v2 + ")";
            } else {
              return format_unimplemented(format);
            }

        } else if (format == OutputFormat.CSHARPCONTRACT) {

            v1 = var1().csharp_name();
            v2 = var2().csharp_name();
            String[] split1 = var1().csharp_array_split();
            String[] split2 = var2().csharp_array_split();
            if (this instanceof Divides) {
              return "Contract.ForAll(0, " + split1[0] + ".Count(), i => " + split1[0] + " [i]" + split1[1] + " % " + split2[0] + "[i]" + split2[1] + " == 0)";
            } else if (this instanceof Square) {
              return "Contract.ForAll(0, " + split1[0] + ".Count(), i => " + split1[0] + " [i]" + split1[1] + " == " + split2[0] + "[i]" + split2[1] + "*" + split2[0] + "[i]" + split2[1] + ")";
            } else if (this instanceof BitwiseComplement) {
              return "Contract.ForAll(0, " + split1[0] + ".Count(), i => " + split1[0] + " [i]" + split1[1] + " == ~" + split2[0] + "[i]" + split2[1] + ")";
            } else if (this instanceof BitwiseSubset) {
              return "Contract.ForAll(0, " + split1[0] + ".Count(), i => " + split1[0] + " [i]" + split1[1] + " == " + split1[0] + "[i]" + split1[1] + " | " + split2[0] + "[i]" + split2[1] + ")";
            } else {
              return format_unimplemented(format);
            }

        }

      if (format == OutputFormat.ESCJAVA) {
        String[] form = VarInfo.esc_quantify(var1(), var2());
        fmt_str = form[0] + "(" + fmt_str + ")" + form[3];
        v1 = form[1];
        v2 = form[2];
      } else if (format == OutputFormat.SIMPLIFY) {
        String[] form = VarInfo.simplify_quantify(QuantFlags.element_wise(),
                                                   var1(), var2());
        fmt_str = form[0] + " " + fmt_str + " " + form[3];
        v1 = form[1];
        v2 = form[2];
      } else {
        v1 = var1().name_using(format);
        v2 = var2().name_using(format);
        if (format == OutputFormat.DAIKON) {
          fmt_str += " (elementwise)";
        }
      }

    // Note that we do not use String.replaceAll here, because that's
    // inseparable from the regex library, and we don't want to have to
    // escape v1 with something like
    // v1.replaceAll("([\\$\\\\])", "\\\\$1")
    fmt_str = fmt_str.replace("%var1%", v1);
    fmt_str = fmt_str.replace("%var2%", v2);

    // if (false && (format == OutputFormat.DAIKON)) {
    //   fmt_str = "[" + getClass() + "]" + fmt_str + " ("
    //          + var1().get_value_info() + ", " + var2().get_value_info() +  ")";
    // }
    return fmt_str;
  }

  /** Calls the function specific equal check and returns the correct status. */

  @Override
  public InvariantStatus check_modified(long[] x, long[] y,
                                        int count) {
    if (x.length != y.length) {
      if (Debug.logOn()) {
        log("Falsified - x length = %s y length = %s", x.length, y.length);
      }
      return InvariantStatus.FALSIFIED;
    }

    if (Debug.logDetail()) {
      log("testing values %s, %s", Arrays.toString (x),
           Arrays.toString(y));
    }

    try {
      for (int i = 0; i < x.length; i++) {
        if (!eq_check(x[i], y[i])) {
          if (Debug.logOn()) {
            log("Falsified - x[%s]=%s y[%s]=%s", i, x[i], i, y[i]);
          }
          return InvariantStatus.FALSIFIED;
        }
      }
      return InvariantStatus.NO_CHANGE;
    } catch (Exception e) {
      if (Debug.logOn()) {
        log("Falsified - exception %s", e);
      }
      return InvariantStatus.FALSIFIED;
    }
  }

  /**
   * Checks to see if this invariant is over subsequences and if the same relationship holds over
   * the full sequence. This is obvious if it does. For example 'x[foo..] op y[bar..]' would be
   * obvious if 'x[] op y[]' This can't fully be handled as a suppression since a suppression needs
   * to insure that foo == bar as well. But that is not a requirement here (the fact that 'x[] op
   * y[]' implies that foo == bar when x[] and y[] are not missing).
   */
  public @Nullable DiscardInfo is_subsequence(VarInfo[] vis) {

    VarInfo v1 = var1(vis);
    VarInfo v2 = var2(vis);

    // Make sure each var is a sequence subsequence
    if (!v1.isDerived() || !(v1.derived instanceof SequenceScalarSubsequence)) {
      return null;
    }
    if (!v2.isDerived() || !(v2.derived instanceof SequenceScalarSubsequence)) {
      return null;
    }

    @NonNull SequenceScalarSubsequence der1 = (SequenceScalarSubsequence) v1.derived;
    @NonNull SequenceScalarSubsequence der2 = (SequenceScalarSubsequence) v2.derived;

    // Both of the indices must be either from the start or up to the end.
    // It is not necessary to check that they match in any other way since
    // if the supersequence holds, that implies that the sequences are
    // of the same length.  Thus any subsequence that starts from the
    // beginning or finishes at the end must end or start at the same
    // spot (or it would have been falsified when it didn't)
    if (der1.from_start != der2.from_start) {
      return null;
    }

    // Look up this class over the sequence variables
    Invariant inv = find(getClass(), der1.seqvar(), der2.seqvar());
    if (inv == null) {
      return null;
    }
    return new DiscardInfo(this, DiscardCode.obvious, "Implied by "
                           + inv.format());
  }

  @Pure
  @Override
  public @Nullable DiscardInfo isObviousDynamically(VarInfo[] vis) {

    DiscardInfo super_result = super.isObviousDynamically(vis);
    if (super_result != null) {
      return super_result;
    }

      // any elementwise relation across subsequences is made obvious by
      // the same relation across the original sequence
      DiscardInfo result = is_subsequence(vis);
      if (result != null) {
        return result;
      }

    // Check for invariant specific obvious checks.  The obvious_checks
    // method returns an array of arrays of antecedents.  If all of the
    // antecedents in an array are true, then the invariant is obvoius.
    InvDef[][] obvious_arr = obvious_checks(vis);
    obvious_loop:
    for (int i = 0; i < obvious_arr.length; i++) {
      InvDef[] antecedents = obvious_arr[i];
      StringBuilder why = null;
      for (int j = 0; j < antecedents.length; j++) {
        Invariant inv = antecedents[j].find();
        if (inv == null) {
          continue obvious_loop;
        }
        if (why == null) {
          why = new StringBuilder(inv.format());
        } else {
          why.append(" and ");
          why.append(inv.format());
        }
      }
      return new DiscardInfo(this, DiscardCode.obvious, "Implied by " + why);
    }

    return null;
  }

  /**
   * Returns an invariant that is true when the size(v1) == size(v2). There are a number of
   * possible cases for an array:
   *
   * <pre>
   *    x[]         - entire array, size usually available as size(x[])
   *    x[..(n-1)]  - size is n
   *    x[..n]      - size is n+1
   *    x[n..]      - size is size(x[]) - n
   *    x[(n+1)..]  - size is size(x[]) - (n+1)
   * </pre>
   *
   * Each combination of the above must be considered in creating the equality invariant. Not all
   * possibilities can be handled. Null is returned in that case. In the following table, s stands
   * for the size
   *
   * <pre>
   *                    x[]     x[..(n-1)]  x[..n]  x[n..]    x[(n+1)..]
   *                  --------- ----------  ------  ------    ----------
   *    y[]           s(y)=s(x)   s(y)=n
   *    y[..(m-1)]        x         m=n
   *    y[..m]            x         x         m=n
   *    y[m..]            x         x          x     m=n &and;
   *                                                s(y)=s(x)
   *    y[(m+1)..]        x         x          x        x       m=n &and;
   *                                                           s(y)=s(x)
   * </pre>
   * NOTE: this is not currently used. Many (if not all) of the missing table cells above could be
   * filled in with linear binary invariants (eg, m = n + 1).
   */
  public @Nullable InvDef array_sizes_eq(VarInfo v1, VarInfo v2) {

    VarInfo v1_size = get_array_size(v1);
    VarInfo v2_size = get_array_size(v2);

    // If we can find a size variable for each side build the invariant
    if ((v1_size != null) && (v2_size != null)) {
      return (new InvDef(v1_size, v2_size, IntEqual.class));
    }

    // If either variable is not derived, there is no possible invariant
    // (since we covered all of the direct size comparisons above)
    if ((v1.derived == null) || (v2.derived == null)) {
      return null;
    }

    // Get the sequence subsequence derivations
    SequenceScalarSubsequence v1_ss = (SequenceScalarSubsequence) v1.derived;
    SequenceScalarSubsequence v2_ss = (SequenceScalarSubsequence) v2.derived;

    // If both are from_start and have the same index_shift, just compare
    // the variables
    if (v1_ss.from_start && v2_ss.from_start
        && (v1_ss.index_shift == v2_ss.index_shift)) {
      return (new InvDef(v1_ss.sclvar(), v2_ss.sclvar(), IntEqual.class));
    }

    return null;
  }

  /**
   * Returns a variable that corresponds to the size of v. Returns null if no such variable exists.
   *
   * There are two cases that are not handled: x[..n] with an index shift and x[n..].
   */
  public @Nullable VarInfo get_array_size(VarInfo v) {

    assert v.rep_type.isArray();

    if (v.derived == null) {
      return (v.sequenceSize());
    } else if (v.derived instanceof SequenceScalarSubsequence) {
      SequenceScalarSubsequence ss = (SequenceScalarSubsequence) v.derived;
      if (ss.from_start && (ss.index_shift == -1)) {
        return (ss.sclvar());
      }
    }

    return null;
  }

  /**
   * Return a format string for the specified output format. Each instance of %varN% will be
   * replaced by the correct name for varN.
   */
  public abstract String get_format_str(@GuardSatisfied PairwiseNumericInt this, OutputFormat format);

  /** Returns true if x and y don't invalidate the invariant. */
  public abstract boolean eq_check(long x, long y);

  /**
   * Returns an array of arrays of antecedents. If all of the antecedents in any array are true,
   * then the invariant is obvious.
   */
  public InvDef[][] obvious_checks(VarInfo[] vis) {
    return (new InvDef[][] {});
  }

  public static List<@Prototype Invariant> get_proto_all() {

    List<@Prototype Invariant> result = new ArrayList<>();

      result.add(Divides.get_proto(false));
      result.add(Divides.get_proto(true));
      result.add(Square.get_proto(false));
      result.add(Square.get_proto(true));

      result.add(BitwiseComplement.get_proto());
      result.add(BitwiseSubset.get_proto(false));
      result.add(BitwiseSubset.get_proto(true));

    // System.out.printf("%s get proto: %s%n", PairwiseNumericInt.class, result);
    return result;
  }

  // suppressor definitions, used by many of the classes below
  protected static NISuppressor

      var1_eq_0       = new NISuppressor(0, EltRangeInt.EqualZero.class),
      var2_eq_0       = new NISuppressor(1, EltRangeInt.EqualZero.class),
      var1_ge_0       = new NISuppressor(0, EltRangeInt.GreaterEqualZero.class),
      var2_ge_0       = new NISuppressor(1, EltRangeInt.GreaterEqualZero.class),
      var1_eq_1       = new NISuppressor(0, EltRangeInt.EqualOne.class),
      var2_eq_1       = new NISuppressor(1, EltRangeInt.EqualOne.class),
      var1_eq_minus_1 = new NISuppressor(0, EltRangeInt.EqualMinusOne.class),
      var2_eq_minus_1 = new NISuppressor(1, EltRangeInt.EqualMinusOne.class),
      var1_ne_0       = new NISuppressor(0, EltNonZero.class),
      var2_ne_0       = new NISuppressor(1, EltNonZero.class),
      var1_le_var2    = new NISuppressor(0, 1, PairwiseIntLessEqual.class),

    var1_eq_var2    = new NISuppressor(0, 1, PairwiseIntEqual.class),
    var2_eq_var1    = new NISuppressor(0, 1, PairwiseIntEqual.class);

    protected static NISuppressor var2_valid_shift =
      new NISuppressor(1, EltRangeInt.Bound0_63.class);

  //
  // Int and Float Numeric Invariants
  //

  /**
   * Represents the divides without remainder invariant between corresponding elements of two sequences of long.
   * Prints as {@code x[] % y[] == 0}.
   */
  public static class Divides extends PairwiseNumericInt {
    // We are Serializable, so we specify a version to allow changes to
    // method signatures without breaking serialization.  If you add or
    // remove fields, you should change this number to the current date.
    static final long serialVersionUID = 20040113L;

    protected Divides(PptSlice ppt, boolean swap) {
      super(ppt, swap);
    }

    protected Divides(boolean swap) {
      super(swap);
    }

    private static @Prototype Divides proto = new @Prototype Divides(false);
    private static @Prototype Divides proto_swap = new @Prototype Divides(true);

    /** Returns the prototype invariant. */
    public static @Prototype PairwiseNumericInt get_proto(boolean swap) {
      if (swap) {
        return proto_swap;
      } else {
        return proto;
      }
    }

    // Variables starting with dkconfig_ should only be set via the
    // daikon.config.Configuration interface.
    /** Boolean. True iff divides invariants should be considered. */
    public static boolean dkconfig_enabled = Invariant.invariantEnabledDefault;

    /** Returns whether or not this invariant is enabled. */
    @Override
    public boolean enabled() {
      return dkconfig_enabled;
    }

    @Override
    protected Divides instantiate_dyn(@Prototype Divides this, PptSlice slice) {
      return new Divides(slice, swap);
    }

    @Override
    public String get_format_str(@GuardSatisfied Divides this, OutputFormat format) {
      if (format == OutputFormat.SIMPLIFY) {
        return "(EQ 0 (MOD %var1% %var2%))";
      } else if (format == OutputFormat.CSHARPCONTRACT) {
        return "%var1% % %var2% == 0";
      } else {
        return "%var1% % %var2% == 0";
      }
    }

    @Override
    public boolean eq_check(long x, long y) {
      return (0 == (x % y));
    }

      /**
       * This needs to be an obvious check and not a suppression for sequences because there is no
       * consistent way to check that var1 and var2 have the same length (for derivations).
       */
      @Override
      public InvDef[][] obvious_checks(VarInfo[] vis) {

        return new InvDef[][] {
          new InvDef[] {
            new InvDef(var2(vis), EltOneOf.class, InvDef.elts_minus_one_and_plus_one)
          },
          new InvDef[] {
            new InvDef(var1(), EltOneOf.class, InvDef.elts_zero)
          }
        };
      }

    /** Returns a list of non-instantiating suppressions for this invariant. */
    @Pure
    @Override
    public @NonNull NISuppressionSet get_ni_suppressions() {
      if (swap) {
        return suppressions_swap;
      } else {
        return suppressions;
      }
    }

    /** definition of this invariant (the suppressee) (unswapped) */
    private static NISuppressee suppressee = new NISuppressee(Divides.class, false);

    private static NISuppressionSet suppressions =
      new NISuppressionSet(
          new NISuppression[] {

            // (var1 == var2) ^ (var2 != 0) ==> var1 % var2 == 0
            new NISuppression(var1_eq_var2, var2_ne_0, suppressee),

            // (var2 == var1) ^ (var1 != 0) ==> var2 % var1 == 0
            new NISuppression(var2_eq_var1, var1_ne_0, suppressee),

          });
    private static NISuppressionSet suppressions_swap = suppressions.swap();

    /**
     * Returns non-null if this invariant is obvious from an existing, non-falsified linear binary
     * invariant in the same slice as this invariant. This invariant of the form "x % y == 0" is
     * falsified if a linear binary invariant is found of the form "a*y - 1*x + 0 == 0"
     *
     * @return non-null value iff this invariant is obvious from other invariants in the same slice
     */
    @Pure
    @Override
    public @Nullable DiscardInfo isObviousDynamically(VarInfo[] vis) {
      // First call super type's method, and if it returns non-null, then
      // this invariant is already known to be obvious, so just return
      // whatever reason the super type returned.
      DiscardInfo di = super.isObviousDynamically(vis);
      if (di != null) {
        return di;
      }

      VarInfo var1 = vis[0];
      VarInfo var2 = vis[1];

      // ensure that var1.varinfo_index <= var2.varinfo_index
      if (var1.varinfo_index > var2.varinfo_index) {
        var1 = vis[1];
        var2 = vis[0];
      }

      // Find slice corresponding to these two variables.
      // Ideally, this should always just be ppt if all
      // falsified invariants have been removed.
      PptSlice2 ppt2 = ppt.parent.findSlice(var1,var2);

      // If no slice is found , no invariants exist to make this one obvious.
      if (ppt2 == null) {
        return null;
      }

      // For each invariant, check to see if it's a linear binary
      // invariant of the form "a*y - 1*x + 0 == 0" and if so,
      // you know this invariant of the form "x % y == 0" is obvious.
      for(Invariant inv : ppt2.invs) {

        if (inv instanceof LinearBinary) {
          LinearBinary linv = (LinearBinary) inv;

          // General form for linear binary: a*x + b*y + c == 0,
          // but a and b can be switched with respect to vis, and either
          // one may be negative, so instead check:
          //  - c == 0
          //  - a*b < 0   (a and b have different signs)
          //  - |a| == 1 or |b| == 1, so one will divide the other
          //     While this means that both x % y == 0 and y % x == 0,
          //     only one of these invariants will still be true at this
          //     time, and only that one will be falsified by this test.
          if (!linv.is_false()
              && Global.fuzzy.eq(linv.core.c, 0)
              && linv.core.b * linv.core.a < 0
              && (Global.fuzzy.eq(linv.core.a * linv.core.a, 1)
                  || Global.fuzzy.eq(linv.core.b * linv.core.b, 1))) {
            return new DiscardInfo(this, DiscardCode.obvious,
                                   "Linear binary invariant implies divides");
          }
        }
      }

      return null;
    }

  }

  /**
   * Represents the square invariant between corresponding elements of two sequences of long.
   * Prints as {@code x[] = y[]**2}.
   */
  public static class Square extends PairwiseNumericInt {
    // We are Serializable, so we specify a version to allow changes to
    // method signatures without breaking serialization.  If you add or
    // remove fields, you should change this number to the current date.
    static final long serialVersionUID = 20040113L;

    protected Square(PptSlice ppt, boolean swap) {
      super(ppt, swap);
    }

    protected Square(boolean swap) {
      super(swap);
    }

    private static @Prototype Square proto = new @Prototype Square(false);
    private static @Prototype Square proto_swap = new @Prototype Square(true);

    /** Returns the prototype invariant. */
    public static @Prototype Square get_proto(boolean swap) {
      if (swap) {
        return proto_swap;
      } else {
        return proto;
      }
    }

    // Variables starting with dkconfig_ should only be set via the
    // daikon.config.Configuration interface.
    /** Boolean. True iff square invariants should be considered. */
    public static boolean dkconfig_enabled = Invariant.invariantEnabledDefault;

    /** Returns whether or not this invariant is enabled. */
    @Override
    public boolean enabled() {
      return dkconfig_enabled;
    }
    @Override
    protected Square instantiate_dyn(@Prototype Square this, PptSlice slice) {
      return new Square(slice, swap);
    }

    @Override
    public String get_format_str(@GuardSatisfied Square this, OutputFormat format) {
      if (format == OutputFormat.SIMPLIFY) {
        return "(EQ %var1% (* %var2% %var2))";
      } else if (format == OutputFormat.CSHARPCONTRACT) {
        return "%var1% == %var2%*%var2%";
      } else if (format.isJavaFamily()) {

        return "%var1% == %var2%*%var2%";
      } else {
        return "%var1% == %var2%**2";
      }
    }

    /** Check to see if x == y squared. */
    @Override
    public boolean eq_check(long x, long y) {
      return (x == y*y);
    }

    // Note there are no NI Suppressions for Square.  Two obvious
    // suppressions are:
    //
    //      (var2 == 1) ^ (var1 == 1)  ==> var1 = var2*var2
    //      (var2 == 0) ^ (var1 == 0)  ==> var1 = var2*var2
    //
    // But all of the antecedents would be constants, so we would
    // never need to create this slice, so there is no reason to create
    // these.

  }

  /**
   * Represents the zero tracks invariant between
   * corresponding elements of two sequences of long; that is, when {@code x[]} is zero,
   * {@code y[]} is also zero.
   * Prints as {@code x[] = 0 => y[] = 0}.
   */
  public static class ZeroTrack extends PairwiseNumericInt {
    // We are Serializable, so we specify a version to allow changes to
    // method signatures without breaking serialization.  If you add or
    // remove fields, you should change this number to the current date.
    static final long serialVersionUID = 20040313L;

    protected ZeroTrack(PptSlice ppt, boolean swap) {
      super(ppt, swap);
    }

    protected @Prototype ZeroTrack(boolean swap) {
      super(swap);
    }

    private static @Prototype ZeroTrack proto = new @Prototype ZeroTrack(false);
    private static @Prototype ZeroTrack proto_swap = new @Prototype ZeroTrack(true);

    /** Returns the prototype invariant. */
    public static @Prototype ZeroTrack get_proto(boolean swap) {
      if (swap) {
        return proto_swap;
      } else {
        return proto;
      }
    }

    // Variables starting with dkconfig_ should only be set via the
    // daikon.config.Configuration interface.
    /** Boolean. True iff zero-track invariants should be considered. */
    public static boolean dkconfig_enabled = false;

    /** Returns whether or not this invariant is enabled. */
    @Override
    public boolean enabled() {
      return dkconfig_enabled;
    }

    @Override
    protected ZeroTrack instantiate_dyn(@Prototype ZeroTrack this, PptSlice slice) {
      return new ZeroTrack(slice, swap);
    }

    @Override
    public String get_format_str(@GuardSatisfied ZeroTrack this, OutputFormat format) {
      if (format == OutputFormat.SIMPLIFY) {
        return "(IMPLIES (EQ %var1% 0) (EQ %var2% 0))";
      } else if (format.isJavaFamily() || format == OutputFormat.CSHARPCONTRACT) {
        return "(!(%var1% == 0)) || (%var2% == 0)";
      } else {
        return "(%var1% == 0) ==> (%var2% == 0)";
      }
    }

    @Override
    public boolean eq_check(long x, long y) {
      if (x == 0) {
        return (y == 0);
      } else {
        return true;
      }
    }

    /** Returns a list of non-instantiating suppressions for this invariant. */
    @Pure
    @Override
    public @NonNull NISuppressionSet get_ni_suppressions() {
      if (swap) {
        return suppressions_swap;
      } else {
        return suppressions;
      }
    }

    /** definition of this invariant (the suppressee) (unswapped) */
    private static NISuppressee suppressee = new NISuppressee(ZeroTrack.class, false);

    private static NISuppressionSet suppressions =
      new NISuppressionSet(
          new NISuppression[] {
            // (var1 == var2) ==> (var1=0 ==> var2=0)
            new NISuppression(var1_eq_var2, suppressee),
            // (var1 != 0)    ==> (var1=0 ==> var2=0)
            new NISuppression(var1_ne_0, suppressee),
            // (var2 == 0) ==> (var1=0 ==> var2=0)
            new NISuppression(var2_eq_0, suppressee),
          });
    private static NISuppressionSet suppressions_swap = suppressions.swap();

  }

  /**
   * Represents the bitwise complement invariant between corresponding elements of two sequences of long.
   * Prints as {@code x[] = ~y[]}.
   */
  public static class BitwiseComplement extends PairwiseNumericInt {
    // We are Serializable, so we specify a version to allow changes to
    // method signatures without breaking serialization.  If you add or
    // remove fields, you should change this number to the current date.
    static final long serialVersionUID = 20040113L;

    protected BitwiseComplement(PptSlice ppt) {
      super(ppt, false);
    }

    protected @Prototype BitwiseComplement() {
      super(false);
    }

    private static @Prototype BitwiseComplement proto = new @Prototype BitwiseComplement();

    /** Returns the prototype invariant. */
    public static @Prototype BitwiseComplement get_proto() {
      return proto;
    }

    // Variables starting with dkconfig_ should only be set via the
    // daikon.config.Configuration interface.
    /** Boolean. True iff bitwise complement invariants should be considered. */
    public static boolean dkconfig_enabled = false;

    /** Returns whether or not this invariant is enabled. */
    @Override
    public boolean enabled() {
      return dkconfig_enabled;
    }

    @Override
    protected BitwiseComplement instantiate_dyn(@Prototype BitwiseComplement this, PptSlice slice) {
      return new BitwiseComplement(slice);
    }

    @Pure
    @Override
    public boolean is_symmetric() {
      return true;
    }

    @Override
    public String get_format_str(@GuardSatisfied BitwiseComplement this, OutputFormat format) {
      if (format == OutputFormat.SIMPLIFY) {
        return "(EQ %var1% (~ %var2%))";
      } else if (format == OutputFormat.CSHARPCONTRACT) {
        return "%var1% == ~%var2%";
      } else {
        return "%var1% == ~%var2%";
      }
    }

    /** Check to see if x == ~y . */
    @Override
    public boolean eq_check(long x, long y) {
      return ((x == ~y));
    }
  }

  /**
   * Represents the bitwise subset invariant between corresponding elements of two sequences of long; that is, the bits of
   * {@code y[]} are a subset of the bits of {@code x[]}.
   * Prints as {@code x[] = y[] | x[]}.
   */
  public static class BitwiseSubset extends PairwiseNumericInt {
    // We are Serializable, so we specify a version to allow changes to
    // method signatures without breaking serialization.  If you add or
    // remove fields, you should change this number to the current date.
    static final long serialVersionUID = 20040113L;

    protected BitwiseSubset(PptSlice ppt, boolean swap) {
      super(ppt, swap);
    }

    protected BitwiseSubset(boolean swap) {
      super(swap);
    }

    private static @Prototype BitwiseSubset proto = new @Prototype BitwiseSubset(false);
    private static @Prototype BitwiseSubset proto_swap = new @Prototype BitwiseSubset(true);

    /** Returns the prototype invariant. */
    public static @Prototype BitwiseSubset get_proto(boolean swap) {
      if (swap) {
        return proto_swap;
      } else {
        return proto;
      }
    }

    // Variables starting with dkconfig_ should only be set via the
    // daikon.config.Configuration interface.
    /** Boolean. True iff bitwise subset invariants should be considered. */
    public static boolean dkconfig_enabled = false;

    /** Returns whether or not this invariant is enabled. */
    @Override
    public boolean enabled() {
      return dkconfig_enabled;
    }

    @Override
    public BitwiseSubset instantiate_dyn(@Prototype BitwiseSubset this, PptSlice slice) {
      return new BitwiseSubset(slice, swap);
    }

    @Override
    public String get_format_str(@GuardSatisfied BitwiseSubset this, OutputFormat format) {
      if (format == OutputFormat.SIMPLIFY) {
        return "(EQ %var1% (|java-bitwise-or| %var2% %var1%))";
      } else if (format == OutputFormat.DAIKON) {
        return "%var2% is a bitwise subset of %var1%";
      } else if (format == OutputFormat.CSHARPCONTRACT) {
        return "%var1% == (%var2% | %var1%)";
      } else {
        return "%var1% == (%var2% | %var1%)";
      }
    }

    @Override
    public boolean eq_check(long x, long y) {
      return ((x == (y | x)));
    }

      /**
       * This needs to be an obvious check and not a suppression for sequences because there is no
       * consistent way to check that var1 and var2 have the same length (for derivations).
       */
      @Override
      public InvDef[][] obvious_checks(VarInfo[] vis) {

        return new InvDef[][] {
          // suppress if var2 == 0
          new InvDef[] {new InvDef(var2(), EltOneOf.class, InvDef.elts_zero)},
          // suppress if var1 == -1 (all of its bits are on)
          new InvDef[] {new InvDef(var1(), EltOneOf.class, InvDef.elts_minus_one)}
        };
      }

    /** Returns a list of non-instantiating suppressions for this invariant. */
    @Pure
    @Override
    public @NonNull NISuppressionSet get_ni_suppressions() {
      if (swap) {
        return suppressions_swap;
      } else {
        return suppressions;
      }
    }

    /** definition of this invariant (the suppressee) (unswapped) */
    private static NISuppressee suppressee = new NISuppressee(BitwiseSubset.class, false);

    private static NISuppressionSet suppressions =
      new NISuppressionSet(
          new NISuppression[] {

              // (var1 == var2) ==> var1 = (var2 | var1)
              new NISuppression(var1_eq_var2, suppressee),

      });
    private static NISuppressionSet suppressions_swap = suppressions.swap();
  }

  /**
   * Represents the BitwiseAnd == 0 invariant between corresponding elements of two sequences of long; that is, {@code x[]} and
   * {@code y[]} have no bits in common.
   * Prints as {@code x[] & y[] == 0}.
   */
  public static class BitwiseAndZero extends PairwiseNumericInt {
    // We are Serializable, so we specify a version to allow changes to
    // method signatures without breaking serialization.  If you add or
    // remove fields, you should change this number to the current date.
    static final long serialVersionUID = 20040313L;

    protected BitwiseAndZero(PptSlice ppt) {
      super(ppt, false);
    }

    protected @Prototype BitwiseAndZero() {
      super(false);
    }

    private static @Prototype BitwiseAndZero proto = new @Prototype BitwiseAndZero();

    /** Returns the prototype invariant. */
    public static @Prototype BitwiseAndZero get_proto() {
      return proto;
    }

    // Variables starting with dkconfig_ should only be set via the
    // daikon.config.Configuration interface.
    /** Boolean. True iff BitwiseAndZero invariants should be considered. */
    public static boolean dkconfig_enabled = false;

    /** Returns whether or not this invariant is enabled. */
    @Override
    public boolean enabled() {
      return dkconfig_enabled;
    }

    @Override
    public BitwiseAndZero instantiate_dyn(@Prototype BitwiseAndZero this, PptSlice slice) {
      return new BitwiseAndZero(slice);
    }

    @Override
    public String get_format_str(@GuardSatisfied BitwiseAndZero this, OutputFormat format) {
      if (format == OutputFormat.SIMPLIFY) {
        return "(EQ (|java-&| %var1% %var2%) 0)";
      } else if (format == OutputFormat.CSHARPCONTRACT) {
        return "(%var1% & %var2%) == 0";
      } else {
        return "(%var1% & %var2%) == 0";
      }
    }

    @Pure
    @Override
    public boolean is_symmetric() {
      return true;
    }

    @Override
    public boolean eq_check(long x, long y) {
      return ((x & y) == 0);
    }

    /** Returns a list of non-instantiating suppressions for this invariant. */
    @Pure
    @Override
    public @Nullable NISuppressionSet get_ni_suppressions() {
      return suppressions;
    }

    private static @Nullable NISuppressionSet suppressions = null;

  }

  /**
   * Represents the ShiftZero invariant between corresponding elements of two sequences of long; that is, {@code x[]}
   * right-shifted by {@code y[]} is always zero.
   * Prints as {@code x[] >> y[] = 0}.
   */
  public static class ShiftZero  extends PairwiseNumericInt {
    // We are Serializable, so we specify a version to allow changes to
    // method signatures without breaking serialization.  If you add or
    // remove fields, you should change this number to the current date.
    static final long serialVersionUID = 20040313L;

    protected ShiftZero(PptSlice ppt, boolean swap) {
      super(ppt, swap);
    }

    protected ShiftZero(boolean swap) {
      super(swap);
    }

    private static @Prototype ShiftZero proto = new @Prototype ShiftZero(false);
    private static @Prototype ShiftZero proto_swap = new @Prototype ShiftZero(true);

    /** Returns the prototype invariant. */
    public static @Prototype ShiftZero get_proto(boolean swap) {
      if (swap) {
        return proto_swap;
      } else {
        return proto;
      }
    }

    // Variables starting with dkconfig_ should only be set via the
    // daikon.config.Configuration interface.
    /** Boolean. True iff ShiftZero invariants should be considered. */
    public static boolean dkconfig_enabled = false;

    /** Returns whether or not this invariant is enabled. */
    @Override
    public boolean enabled() {
      return dkconfig_enabled;
    }

    @Override
    protected ShiftZero instantiate_dyn(@Prototype ShiftZero this, PptSlice slice) {
      return new ShiftZero(slice, swap);
    }

    @Override
    public String get_format_str(@GuardSatisfied ShiftZero this, OutputFormat format) {
      if (format == OutputFormat.SIMPLIFY) {
        return "(EQ (|java->>| %var1% %var2%) 0)";
      } else if (format == OutputFormat.CSHARPCONTRACT) {
        return "(%var1% >> %var2% == 0)";
      } else {
        return "(%var1% >> %var2% == 0)";
      }
    }

    @Override
    public boolean eq_check(long x, long y) {
      if ((y < 0) || (y > 63)) {
        throw new ArithmeticException("shift op (" + y + ") is out of range");
      }
      return ((x >> y) == 0);
    }

    /** Returns a list of non-instantiating suppressions for this invariant. */
    @Pure
    @Override
    public @NonNull NISuppressionSet get_ni_suppressions() {
      if (swap) {
        return suppressions_swap;
      } else {
        return suppressions;
      }
    }

    /** definition of this invariant (the suppressee) (unswapped) */
    private static NISuppressee suppressee = new NISuppressee(ShiftZero.class, false);

    private static NISuppressionSet suppressions =
      new NISuppressionSet(
          new NISuppression[] {
              // (var1>=0) ^ (var1<=var2) ^ (0<=var2<=63) ==> (var1 >> var2) == 0
              new NISuppression(var1_ge_0, var1_le_var2, var2_valid_shift,
                                suppressee),
          });
    private static NISuppressionSet suppressions_swap = suppressions.swap();
  }

//
// Standard String invariants
//

}
