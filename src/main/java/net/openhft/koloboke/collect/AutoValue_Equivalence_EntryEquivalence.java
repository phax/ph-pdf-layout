package net.openhft.koloboke.collect;


@javax.annotation.Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_Equivalence_EntryEquivalence<K extends java.lang.Object, V extends java.lang.Object> extends Equivalence.EntryEquivalence<K, V> {
  private final Equivalence<K> keyEquivalence;
  private final Equivalence<V> valueEquivalence;

  AutoValue_Equivalence_EntryEquivalence(
      Equivalence<K> keyEquivalence,
      Equivalence<V> valueEquivalence) {
    if (keyEquivalence == null) {
      throw new NullPointerException("Null keyEquivalence");
    }
    this.keyEquivalence = keyEquivalence;
    if (valueEquivalence == null) {
      throw new NullPointerException("Null valueEquivalence");
    }
    this.valueEquivalence = valueEquivalence;
  }

  @Override
  Equivalence<K> keyEquivalence() {
    return keyEquivalence;
  }

  @Override
  Equivalence<V> valueEquivalence() {
    return valueEquivalence;
  }

  @Override
  public String toString() {
    return "EntryEquivalence{"
        + "keyEquivalence=" + keyEquivalence
        + ", valueEquivalence=" + valueEquivalence
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof Equivalence.EntryEquivalence) {
      Equivalence.EntryEquivalence<?, ?> that = (Equivalence.EntryEquivalence<?, ?>) o;
      return (this.keyEquivalence.equals(that.keyEquivalence()))
          && (this.valueEquivalence.equals(that.valueEquivalence()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= keyEquivalence.hashCode();
    h *= 1000003;
    h ^= valueEquivalence.hashCode();
    return h;
  }
}
