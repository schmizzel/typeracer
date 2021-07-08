package app.controller;

import app.IconManager;
import app.elements.RaceTrack;
import app.model.GameFinishedModel;
import app.model.MultiplayerModel;
import app.model.MultiplayerModelObserver;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import protocol.PlayerUpdate;
import protocol.RaceResult;
import protocol.UserData;
import protocol.UserResult;
import typeracer.CheckResult;
import typeracer.GamePhase;

/** Handles all gui functionality associated with gameplay. */
class MultiplayerController extends Controller implements MultiplayerModelObserver {

  private static final String FXMLPATH = "view/multiplayer.fxml";

  // TODO: Combine in auxiliary class
  HashMap<String, Label> wpmLabels = new HashMap<>();
  HashMap<String, RaceTrack> userProgress = new HashMap<>();
  List<Label> textLabels = new ArrayList<>();

  private final MultiplayerModel model;
  private int colorAlternateCounter = 0;

  @FXML TextFlow textToType;

  @FXML TextFlow enteredText;

  @FXML VBox userList;

  @FXML Label timerLabel;

  @FXML Label countdownLabel;

  /**
   * Controller for Multiplayer game screen.
   *
   * @param stage where controller is hosted in
   */
  public MultiplayerController(Stage stage, MultiplayerModel model) throws IOException {
    super(stage, FXMLPATH);
    this.model = model;
    model.setObserver(this);
    setupText(model.getRaceData().textToType);
    setupKeyHandler();
    setupTracks(model.getRaceData().players);
    model.initRaceStart();
  }

  @Override
  public void raceStarted() {
    countdownLabel.setVisible(false);
  }

  @Override
  public void updatedRaceState() {
    List<PlayerUpdate> updates = model.getRaceUpdate();
    countdownLabel.setText(model.getHurryUp());
    for (PlayerUpdate update : updates) {
      trackUpdate(update);
      wpmUpdate(update);
    }
  }

  @Override
  public void receivedRaceResult(RaceResult result) {
    openGameOverScreen(result);
    System.out.println("Duration: " + result.duration);
    for (UserResult res : result.classification) {
      System.out.println(res.userData.name);
      System.out.println(res.wpm);
    }
  }

  @Override
  public void checkeredFlag(long raceEndTimestamp) {
    countdownLabel.setVisible(true);
  }

  @Override
  public void updatedTimer(long time) {
    Platform.runLater(() -> timerLabel.setText("Time: " + time + "s"));
  }

  @Override
  public void updatedCountDown(long time) {
    countdownLabel.setText(Long.toString(time));
  }

  private void openGameOverScreen(RaceResult result) {
    model.leftScreen();
    try {
      new GameFinishedController(stage, new GameFinishedModel(result)).show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Keylistener for user input. Checks input, colors text green if correct, red if not in method
   * charLabel.
   */
  private void setupKeyHandler() {
    stage
        .getScene()
        .addEventHandler(
            KeyEvent.KEY_TYPED,
            event -> {
              String typed = event.getCharacter();
              CheckResult check = model.typed(typed);
              if (check != null) {
                showTextProgess(check);
              }
            });
  }

  /*
   * Adds the user list along with progress bars and wpm to game screen.
   */
  private void setupTracks(List<UserData> players) {
    for (UserData player : players) {

      VBox userVbox = new VBox();
      wpmCreator(player.userId);
      userVbox.getChildren().add(userLabelCreator(player.name));
      userVbox.getChildren().add(wpmLabels.get(player.userId));
      wpmLabels
          .get(player.userId)
          .setStyle("-fx-font-size: 20px; -fx-text-fill: #62fbf7; -fx-min-width: 40px;");

      HBox userHbox = new HBox();
      userHbox.setSpacing(20);
      RaceTrack track = trackCreator(player);

      userHbox.getChildren().add(userVbox);
      userHbox.getChildren().add(track);

      userList.getChildren().add(userHbox);
      userProgress.put(player.userId, track);
    }
  }

  private void setupText(String t) {
    for (int i = 0; i<t.length(); i++) {
      Label character = new Label(Character.toString(t.charAt(i)));
      character.setStyle("-fx-text-fill: #ffffff");
      textLabels.add(character);
      textToType.getChildren().addAll(character);
    }
  }

  /** Creates labels for user input which will be added to hbox enteredText. */
  private Label showTextProgess(CheckResult check) {
    Label label = new Label();
    switch (check.state) {
      case CORRECT:
        textLabels.get(model.getPosition()-1).setStyle("-fx-text-fill: #62fbf7;");
        break;
      case INCORRECT:
        textLabels.get(model.getPosition()).setStyle("-fx-text-fill: #fe55f7;");
        break;
      case AUTOCORRECTED:
        textLabels.get(model.getPosition()-1).setStyle("-fx-text-fill: #d789f7;");
        break;
      default:
    }
    return label;
  }

  private Label userLabelCreator(String user) {
    Label label = new Label(user);
    label.setTextFill(Color.WHITE);
    label.setStyle(
        "-fx-font-size: 20px; -fx-background-color: #ffffff; "
            + "-fx-text-fill: #000000; -fx-min-width: 150px;");
    return label;
  }

  private RaceTrack trackCreator(UserData userData) {
    try {
      colorAlternateCounter++;
      if (colorAlternateCounter % 2 == 0) {
        return new RaceTrack(IconManager.iconForId(userData.iconId), 550, 20, Color.web("#fe55f7"));
      } else {
        return new RaceTrack(IconManager.iconForId(userData.iconId), 550, 20, Color.web("#62fbf7"));
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  private void wpmCreator(String userId) {
    Label label = new Label();
    label.setText("WPM: 0");
    label.setStyle("-fx-text-fill:#ffffff;");
    wpmLabels.put(userId, label);
  }

  private void wpmUpdate(PlayerUpdate update) {
    wpmLabels.get(update.userId).setText("WPM: " + update.wpm);
  }

  private void trackUpdate(PlayerUpdate update) {
    userProgress.get(update.userId).updateProgress(update.percentProgress);
  }

  @Override
  public void receivedError(String message) {
    displayError(message);
  }
}
