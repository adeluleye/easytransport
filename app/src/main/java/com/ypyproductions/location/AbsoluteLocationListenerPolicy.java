package com.ypyproductions.location;

public class AbsoluteLocationListenerPolicy implements LocationListenerPolicy {

  private final long interval;

  /**
   * Constructor.
   * 
   * @param interval the interval to request for gps signal
   */
  public AbsoluteLocationListenerPolicy(long interval) {
    this.interval = interval;
  }

  @Override
  public long getDesiredPollingInterval() {
    return interval;
  }

  @Override
  public int getMinDistance() {
    return 0;
  }

  @Override
  public void updateIdleTime(long idleTime) {
    // Ignore
  }
}
