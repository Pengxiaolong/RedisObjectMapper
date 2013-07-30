package org.opensource.redis.objectmapper;

/**
 *
 * @author xlpeng
 * @param <F>  type of the first element
 * @param <S>  type of the second element
 * @param <T>  type of the third element
 */
public class Tris<F, S, T> {
  F first;
  S second;
  T third;

  /**
   *
   * @param first   the first element
   * @param second  the second element
   * @param third   the third element
   */
  public Tris(F first, S second, T third) {
    this.first = first;
    this.second = second;
    this.third = third;
  }

  public F getFirst() {
    return first;
  }

  public void setFirst(F first) {
    this.first = first;
  }

  public S getSecond() {
    return second;
  }

  public void setSecond(S second) {
    this.second = second;
  }

  public T getThird() {
    return third;
  }

  public void setThird(T third) {
    this.third = third;
  }
}
