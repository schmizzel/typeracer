package client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import protocol.ProgressSnapshot;
import protocol.Request;
import protocol.RequestFactory;

/** Client provides all communication with the server. */
public class ClientImpl implements Closeable, Client {

  private final Socket socket;
  private final PrintWriter writer;
  private final Gson gson;
  private final ResponseHandler responseHandler;

  /**
   * Tries to connect to the server and starts listening for responses when successful. Subscribe
   * observer to receive responses.
   *
   * @param serverAddress address to server
   * @param port port the server listens to
   * @throws IOException if connection attempt fails
   */
  public ClientImpl(InetAddress serverAddress, int port) throws IOException {
    this.socket = new Socket(serverAddress, port);
    this.writer = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
    GsonBuilder builder = new GsonBuilder();
    this.gson = builder.create();
    this.responseHandler = new ResponseHandler(socket, gson);
  }

  @Override
  public void close() throws IOException {
    writer.close();
    socket.close();
  }

  @Override
  public void registerUser(String userName) {
    sendRequest(RequestFactory.makeRegisterRequest(userName));
  }

  @Override
  public void newLobby(String userId, String lobbyName) {
    sendRequest(RequestFactory.makeNewLobbyRequest(userId, lobbyName));
  }

  @Override
  public void joinLobby(String userId, String gameId) {
    sendRequest(RequestFactory.makeJoinLobbyRequest(userId, gameId));
  }

  @Override
  public void startRace() {
    sendRequest(RequestFactory.makeStartRaceRequest());
  }

  @Override
  public void leaveLobby() {
    sendRequest(RequestFactory.makeLeaveLobbyRequest());
  }

  @Override
  public void requestLobbies() {
    sendRequest(RequestFactory.makeGetOpenLobbiesRequest());
  }

  @Override
  public void setIsReady(boolean isReady) {
    sendRequest(RequestFactory.makeIsReadyRequest(isReady));
  }

  @Override
  public void sendProgressUpdate(ProgressSnapshot snapshot) {
    sendRequest(RequestFactory.makeProgressUpdateRequest(snapshot));
  }

  @Override
  public void subscribe(ClientObserver observer) {
    responseHandler.subscribe(observer);
  }

  @Override
  public void unsubscribe(ClientObserver observer) {
    responseHandler.unsubscribe(observer);
  }

  private void sendRequest(Request request) {
    this.writer.println(gson.toJson(request));
  }
}
