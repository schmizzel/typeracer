package server;

import backend.Api;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import requests.Request;
import requests.RequestTypes;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Representing a Connection to a Client.
 */
public class Connection implements Closeable {

  private final Socket socket;
  private final BufferedReader reader;
  private final Gson gson;
  private final Api api;

  /**
   * Constructor of RequestHandler.
   *
   * @param socket the Request handler is supposed to run on
   * @throws IOException if socket is not connected
   */
  public Connection(Socket socket, Api api) throws IOException {
    this.socket = socket;
    this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(),
            StandardCharsets.UTF_8));
    GsonBuilder builder = new GsonBuilder();
    this.gson = builder.create();
    this.api = api;
  }

  /**
   * Closes socket specified when creating RequestHandler.
   */
  @Override
  public void close() {
    try {
      socket.close();
      reader.close();
    } catch (IOException e) {
      Logger.log(Logger.LogLevel.ERROR, e.getMessage());
    }
  }

  /**
   * Listens on socket and forwards all received messages.
   */
  public void handleRequests() {
    try {
      String line;
      while ((line = this.reader.readLine()) != null) {
        // TODO: Handle invalid requests
        Request request = gson.fromJson(line, Request.class);
        receivedRequest(request);
      }
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

  private void receivedRequest(Request request) {
    switch (request.type) {
      case RequestTypes.REGISTER:
        api.registerPlayer(request.playerName);
        break;
      case RequestTypes.NEW_GAME:
        api.createNewGame(request.userId);
        break;
      case RequestTypes.JOIN_GAME:
        api.joinGame(request.userId, request.gameId);
        break;
      default:
        Logger.log(Logger.LogLevel.ERROR, "Unknown Request type: " + request.type);
    }
  }
}
