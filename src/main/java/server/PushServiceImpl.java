package server;

import java.sql.Connection;
import java.util.HashMap;
import protocol.Response;

class PushServiceImpl implements PushService {

  private final HashMap<String, Connection> connections;

  PushServiceImpl() {
    this.connections = new HashMap<>();
  }

  //  void addConnection(Connection connection) {
  //    connections.put(connection.getId(), connection);
  //  }
  //
  //  void removeConnection(Connection connection) {
  //    connections.remove(connection.getId());
  //  }

  @Override
  public void sendResponse(String connectionId, Response response) throws Exception {
    Connection connection = connections.get(connectionId);
    if (!connections.containsKey(connectionId)) {
      throw new Exception("connection with id " + connectionId + " does not exist");
    }
    // connection.sendResponse(response);
  }
}
