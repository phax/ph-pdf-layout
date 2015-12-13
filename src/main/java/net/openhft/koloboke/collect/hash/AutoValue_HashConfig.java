package net.openhft.koloboke.collect.hash;

import net.openhft.koloboke.function.Predicate;

@javax.annotation.Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_HashConfig extends HashConfig {
  private final double getMinLoad;
  private final double getTargetLoad;
  private final double getMaxLoad;
  private final double getGrowFactor;
  private final Predicate<HashContainer> getShrinkCondition;

  AutoValue_HashConfig(
      double getMinLoad,
      double getTargetLoad,
      double getMaxLoad,
      double getGrowFactor,
      Predicate<HashContainer> getShrinkCondition) {
    this.getMinLoad = getMinLoad;
    this.getTargetLoad = getTargetLoad;
    this.getMaxLoad = getMaxLoad;
    this.getGrowFactor = getGrowFactor;
    this.getShrinkCondition = getShrinkCondition;
  }

  @Override
  public double getMinLoad() {
    return getMinLoad;
  }

  @Override
  public double getTargetLoad() {
    return getTargetLoad;
  }

  @Override
  public double getMaxLoad() {
    return getMaxLoad;
  }

  @Override
  public double getGrowFactor() {
    return getGrowFactor;
  }

  @Override
  public Predicate<HashContainer> getShrinkCondition() {
    return getShrinkCondition;
  }

  @Override
  public String toString() {
    return "HashConfig{"
        + "getMinLoad=" + getMinLoad
        + ", getTargetLoad=" + getTargetLoad
        + ", getMaxLoad=" + getMaxLoad
        + ", getGrowFactor=" + getGrowFactor
        + ", getShrinkCondition=" + getShrinkCondition
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof HashConfig) {
      HashConfig that = (HashConfig) o;
      return (Double.doubleToLongBits(this.getMinLoad) == Double.doubleToLongBits(that.getMinLoad()))
          && (Double.doubleToLongBits(this.getTargetLoad) == Double.doubleToLongBits(that.getTargetLoad()))
          && (Double.doubleToLongBits(this.getMaxLoad) == Double.doubleToLongBits(that.getMaxLoad()))
          && (Double.doubleToLongBits(this.getGrowFactor) == Double.doubleToLongBits(that.getGrowFactor()))
          && ((this.getShrinkCondition == null) ? (that.getShrinkCondition() == null) : this.getShrinkCondition.equals(that.getShrinkCondition()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= (Double.doubleToLongBits(getMinLoad) >>> 32) ^ Double.doubleToLongBits(getMinLoad);
    h *= 1000003;
    h ^= (Double.doubleToLongBits(getTargetLoad) >>> 32) ^ Double.doubleToLongBits(getTargetLoad);
    h *= 1000003;
    h ^= (Double.doubleToLongBits(getMaxLoad) >>> 32) ^ Double.doubleToLongBits(getMaxLoad);
    h *= 1000003;
    h ^= (Double.doubleToLongBits(getGrowFactor) >>> 32) ^ Double.doubleToLongBits(getGrowFactor);
    h *= 1000003;
    h ^= (getShrinkCondition == null) ? 0 : getShrinkCondition.hashCode();
    return h;
  }
}
