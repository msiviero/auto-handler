package org.siviero.auto.handler.testing;

import java.net.InetAddress;
import java.net.ServerSocket;
import javax.net.ServerSocketFactory;

public class HttpTestUtils {

  private static final int PORT_RANGE_MIN = 16384;
  private static final int PORT_RANGE_MAX = 32768;

  public static boolean isPortFree(int port) {
    try {
      ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(
        port, 1, InetAddress.getByName("localhost"));
      serverSocket.close();
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  public static int findAvailablePort() {
    int portRange = PORT_RANGE_MAX - PORT_RANGE_MIN;
    int candidatePort;
    int searchCounter = 0;
    do {
      if (searchCounter > portRange) {
        throw new IllegalStateException(String.format(
          "Could not find an available port in the range [%d, %d] after %d attempts",
          PORT_RANGE_MIN, PORT_RANGE_MAX, searchCounter));
      }
      candidatePort = PORT_RANGE_MIN + portRange + 1;
      searchCounter++;
    }
    while (!isPortFree(candidatePort));

    return candidatePort;
  }
}
