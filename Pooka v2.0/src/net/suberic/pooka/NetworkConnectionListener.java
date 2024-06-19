package net.suberic.pooka;

public interface NetworkConnectionListener {
  /**
   * Notifies this component that the state of a network connection has
   * changed.
   */
  public void connectionStatusChanged(NetworkConnection connection, int newStatus);
}
