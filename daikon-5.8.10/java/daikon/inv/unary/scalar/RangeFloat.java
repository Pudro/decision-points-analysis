// ***** This file is automatically generated from Range.java.jpp

package daikon.inv.unary.scalar;

import org.checkerframework.dataflow.qual.Pure;
import org.checkerframework.checker.lock.qual.GuardSatisfied;
import org.checkerframework.dataflow.qual.SideEffectFree;
import org.checkerframework.checker.interning.qual.Interned;
import org.checkerframework.checker.nullness.qual.Nullable;
import daikon.*;
import daikon.Quantify.QuantFlags;
import daikon.derive.unary.*;
import daikon.inv.*;
import daikon.inv.binary.sequenceScalar.*;
import daikon.inv.unary.sequence.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.plumelib.util.Intern;
import org.plumelib.util.UtilPlume;
import typequals.prototype.qual.NonPrototype;
import typequals.prototype.qual.Prototype;

/**
 * Baseclass for unary range based invariants. Each invariant is a special stateless version of
 * bound or oneof. For example EqualZero, BooleanVal, etc). These are never printed, but are used
 * internally as suppressors for ni-suppressions.
 *
 * Each specific invariant is implemented in a subclass (typically in this file).
 */

public abstract class RangeFloat extends SingleFloat {

  // We are Serializable, so we specify a version to allow changes to
  // method signatures without breaking serialization.  If you add or
  // remove fields, you should change this number to the current date.
  static final long serialVersionUID = 20040311L;

  protected RangeFloat(PptSlice ppt) {
    super(ppt);
  }

  protected @Prototype RangeFloat() {
    super();
  }

  /**
   * Check that instantiation is ok. The type must be integral
   * (not boolean or hash code).
   */
  @Override
  public boolean instantiate_ok(VarInfo[] vis) {

    if (!valid_types(vis)) {
      return false;
    }

    if (!vis[0].file_rep_type.baseIsFloat()) {
      return false;
    }

    return true;
  }

  /**
   * Returns a string in the specified format that describes the invariant.
   *
   * The generic format string is obtained from the subclass specific get_format_str(). Instances of
   * %var1% are replaced by the variable name in the specified format.
   */
  @SideEffectFree
  @Override
  public String format_using(@GuardSatisfied RangeFloat this, OutputFormat format) {

    String fmt_str = get_format_str(format);

    VarInfo var1 = ppt.var_infos[0];
    String v1 = null;

    if (v1 == null) {
      v1 = var1.name_using(format);
    }

    fmt_str = fmt_str.replace("%var1%", v1);
    return fmt_str;
  }

  @Override
  public InvariantStatus check_modified(double x, int count) {
    if (eq_check(x)) {
      return InvariantStatus.NO_CHANGE;
    } else {
      return InvariantStatus.FALSIFIED;
    }
  }

  @Override
  public InvariantStatus add_modified(double x, int count) {
    return check_modified(x, count);
  }

  @Override
  protected double computeConfidence() {
    return CONFIDENCE_JUSTIFIED;
  }

  @Pure
  @Override
  public boolean isSameFormula(Invariant other) {
    assert other.getClass() == getClass();
    return true;
  }
  @Pure
  @Override
  public boolean isExclusiveFormula(Invariant other) {
    return false;
  }

  /**
   * All range invariants except Even and PowerOfTwo are obvious since they represented by some
   * version of OneOf or Bound.
   */
  @Pure
  @Override
  public @Nullable DiscardInfo isObviousDynamically(VarInfo[] vis) {

    return new DiscardInfo(this, DiscardCode.obvious,
                            "Implied by Oneof or Bound");
  }

  /**
   * Looks for a OneOf invariant over vis. Used by Even and PowerOfTwo to dynamically suppress those
   * invariants if a OneOf exists.
   */
  protected @Nullable OneOfFloat find_oneof(VarInfo[] vis) {
    return (OneOfFloat) ppt.parent.find_inv_by_class(vis, OneOfFloat.class);
  }

  /**
   * Return a format string for the specified output format. Each instance of %varN% will be
   * replaced by the correct name for varN.
   */
  public abstract String get_format_str(@GuardSatisfied RangeFloat this, OutputFormat format);

  /** Returns true if x and y don't invalidate the invariant. */
  public abstract boolean eq_check(double x);

  /**
   * Returns a list of prototypes of all of the range
   * invariants.
   */
  public static List<@Prototype Invariant> get_proto_all() {

    List<@Prototype Invariant> result = new ArrayList<>();
    result.add(EqualZero.get_proto());
    result.add(EqualOne.get_proto());
    result.add(EqualMinusOne.get_proto());
    result.add(GreaterEqualZero.get_proto());
    result.add(GreaterEqual64.get_proto());

    return result;
  }

  /**
   * Internal invariant representing double scalars that are equal to zero. Used for
   * non-instantiating suppressions. Will never print since OneOf accomplishes the same thing.
   */
  public static class EqualZero extends RangeFloat {

    // We are Serializable, so we specify a version to allow changes to
    // method signatures without breaking serialization.  If you add or
    // remove fields, you should change this number to the current date.
    static final long serialVersionUID = 20040113L;

    protected EqualZero(PptSlice ppt) {
      super(ppt);
    }

    protected @Prototype EqualZero() {
      super();
    }

    private static @Prototype EqualZero proto = new @Prototype EqualZero();

    /** returns the prototype invariant */
    public static @Prototype EqualZero get_proto() {
      return proto;
    }

    /** Returns whether or not this invariant is enabled. */
    @Override
    public boolean enabled() {
      return OneOfFloat.dkconfig_enabled;
    }

    /** instantiates the invariant on the specified slice */
    @Override
    public EqualZero instantiate_dyn(@Prototype EqualZero this, PptSlice slice) {
      return new EqualZero(slice);
    }

    @Override
    public String get_format_str(@GuardSatisfied EqualZero this, OutputFormat format) {
      if (format == OutputFormat.SIMPLIFY) {
        return "(EQ 0 %var1%)";
      } else {
        return "%var1% == 0";
      }
    }

    @Override
    public boolean eq_check(double x) {
      return (x == 0);
    }
  }

  /**
   * Internal invariant representing double scalars that are equal to one. Used for
   * non-instantiating suppressions. Will never print since OneOf accomplishes the same thing.
   */
  public static class EqualOne extends RangeFloat {

    // We are Serializable, so we specify a version to allow changes to
    // method signatures without breaking serialization.  If you add or
    // remove fields, you should change this number to the current date.
    static final long serialVersionUID = 20040113L;

    protected EqualOne(PptSlice ppt) {
      super(ppt);
    }

    protected @Prototype EqualOne() {
      super();
    }

    private static @Prototype EqualOne proto = new @Prototype EqualOne();

    /** returns the prototype invariant */
    public static @Prototype EqualOne get_proto() {
      return proto;
    }

    /** Returns whether or not this invariant is enabled. */
    @Override
    public boolean enabled() {
      return OneOfFloat.dkconfig_enabled;
    }

    /** instantiates the invariant on the specified slice */
    @Override
    public EqualOne instantiate_dyn(@Prototype EqualOne this, PptSlice slice) {
      return new EqualOne(slice);
    }

    @Override
    public String get_format_str(@GuardSatisfied EqualOne this, OutputFormat format) {
      if (format == OutputFormat.SIMPLIFY) {
        return "(EQ 1 %var1%)";
      } else {
        return "%var1% == 1";
      }
    }

    @Override
    public boolean eq_check(double x) {
      return (x == 1);
    }
  }

  /**
   * Internal invariant representing double scalars that are equal to minus one. Used for
   * non-instantiating suppressions. Will never print since OneOf accomplishes the same thing.
   */
  public static class EqualMinusOne extends RangeFloat {

    // We are Serializable, so we specify a version to allow changes to
    // method signatures without breaking serialization.  If you add or
    // remove fields, you should change this number to the current date.
    static final long serialVersionUID = 20040824L;

    protected EqualMinusOne(PptSlice ppt) {
      super(ppt);
    }

    protected @Prototype EqualMinusOne() {
      super();
    }

    private static @Prototype EqualMinusOne proto = new @Prototype EqualMinusOne();

    /** returns the prototype invariant */
    public static @Prototype EqualMinusOne get_proto() {
      return proto;
    }

    /** Returns whether or not this invariant is enabled. */
    @Override
    public boolean enabled() {
      return OneOfFloat.dkconfig_enabled;
    }

    /** instantiates the invariant on the specified slice */
    @Override
    public EqualMinusOne instantiate_dyn(@Prototype EqualMinusOne this, PptSlice slice) {
      return new EqualMinusOne(slice);
    }

    @Override
    public String get_format_str(@GuardSatisfied EqualMinusOne this, OutputFormat format) {
      if (format == OutputFormat.SIMPLIFY) {
        return "(EQ -1 %var1%)";
      } else {
        return "%var1% == -1";
      }
    }

    @Override
    public boolean eq_check(double x) {
      return (x == -1);
    }
  }

  /**
   * Internal invariant representing double scalars that are greater than or equal to 0. Used
   * for non-instantiating suppressions. Will never print since Bound accomplishes the same thing.
   */
  public static class GreaterEqualZero extends RangeFloat {

    // We are Serializable, so we specify a version to allow changes to
    // method signatures without breaking serialization.  If you add or
    // remove fields, you should change this number to the current date.
    static final long serialVersionUID = 20040113L;

    protected GreaterEqualZero(PptSlice ppt) {
      super(ppt);
    }

    protected @Prototype GreaterEqualZero() {
      super();
    }

    private static @Prototype GreaterEqualZero proto = new @Prototype GreaterEqualZero();

    /** returns the prototype invariant */
    public static @Prototype GreaterEqualZero get_proto() {
      return proto;
    }

    /** Returns whether or not this invariant is enabled. */
    @Override
    public boolean enabled() {
      return LowerBoundFloat.dkconfig_enabled;
    }

    /** instantiates the invariant on the specified slice */
    @Override
    public GreaterEqualZero instantiate_dyn(@Prototype GreaterEqualZero this, PptSlice slice) {
      return new GreaterEqualZero(slice);
    }

    @Override
    public String get_format_str(@GuardSatisfied GreaterEqualZero this, OutputFormat format) {
      if (format == OutputFormat.SIMPLIFY) {
        return "(>= %var1% 0)";
      } else {
        return "%var1% >= 0";
      }
    }

    @Override
    public boolean eq_check(double x) {
      return (x >= 0);
    }
  }

  /**
   * Internal invariant representing double scalars that are greater than or equal to 64. Used
   * for non-instantiating suppressions. Will never print since Bound accomplishes the same thing.
   */
  public static class GreaterEqual64 extends RangeFloat {

    // We are Serializable, so we specify a version to allow changes to
    // method signatures without breaking serialization.  If you add or
    // remove fields, you should change this number to the current date.
    static final long serialVersionUID = 20040113L;

    protected GreaterEqual64(PptSlice ppt) {
      super(ppt);
    }

    protected @Prototype GreaterEqual64() {
      super();
    }

    private static @Prototype GreaterEqual64 proto = new @Prototype GreaterEqual64();

    /** returns the prototype invariant */
    public static @Prototype GreaterEqual64 get_proto() {
      return proto;
    }

    /** Returns whether or not this invariant is enabled. */
    @Override
    public boolean enabled() {
      return LowerBoundFloat.dkconfig_enabled;
    }

    /** instantiates the invariant on the specified slice */
    @Override
    public GreaterEqual64 instantiate_dyn(@Prototype GreaterEqual64 this, PptSlice slice) {
      return new GreaterEqual64(slice);
    }

    @Override
    public String get_format_str(@GuardSatisfied GreaterEqual64 this, OutputFormat format) {
      if (format == OutputFormat.SIMPLIFY) {
        return "(>= %var1% 64)";
      } else {
        return "%var1% >= 64";
      }
    }

    @Override
    public boolean eq_check(double x) {
      return (x >= 64);
    }
  }

}
