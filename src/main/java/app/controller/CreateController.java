package app.controller;

import client.Client;
import client.ClientImpl;
import client.ClientObserver;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import protocol.LobbyModel;
import protocol.RaceModel;

import java.util.List;

class CreateController extends Controller implements ClientObserver {

  private static final String FXMLPATH = "view/createscreen.fxml";
  private final Client client;

  CreateController(Stage stage, Client client) {
    super(stage, FXMLPATH);
    this.client = client;
    client.subscribe(this);
  }

  @FXML
  void switchToGameLobby() {
      new GameLobbyController(stage, client);
      client.newGame("Test");
  }

  @Override
  public void registered(String userId) {

  }

  @Override
  public void receivedError(String message) {

  }

  @Override
  public void gameStarting(RaceModel race) {

  }

  @Override
  public void receivedLobbyUpdate(LobbyModel lobby) {
  }

  @Override
  public void receivedOpenLobbies(List<LobbyModel> lobbies) {

  }
}
