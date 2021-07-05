package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/** Represents a Typeracer game. Serves as an interface for e.g. the UI. */
public class Typeracer {

  private final GameState state;
  private final PropertyChangeSupport support = new PropertyChangeSupport(this);

  Typeracer(final String text) {
    state = new GameState(text);
  }

  public void addPropertyChangeListener(final PropertyChangeListener changeListener) {
    support.addPropertyChangeListener(changeListener);
  }

  public static Typeracer create() {
    String randomText = "new WordDatabase().getWord()";
    return new Typeracer(randomText);
  }

  private void notifyListeners() {
    support.firePropertyChange("GameState", null, this.getState());
  }

  public GameState getState() {
    return state;
  }

  /**
   * Check the given character. This method can only be called on an active game. Otherwise, an
   * IllegalStateException is thrown.
   *
   * @param guessedCharacter character to guess
   * @return true if the check was successful. false otherwise.
   * @throws IllegalStateException – if the current Typeracer game is not running
   */
  public CorrectionStates check(char guessedCharacter) {
    if (state.getCurrentGamePhase() != GamePhase.RUNNING) {
      throw new IllegalStateException("Game not running.");
    }

    CorrectionStates isCharCorrect = state.getTypeChar().checkChar(guessedCharacter);
    if (state.getTypeChar().checkFinish()) {
      state.endGame();
    }

    notifyListeners();
    return isCharCorrect;
  }

  /**
   * Forfeit the current game. This method can only be called on an active game. Otherwise, an
   * IllegalStateException is thrown.
   *
   * @throws IllegalStateException – if the current Typeracer game is not running
   */
  public void forfeit() {
    state.endGame();
    notifyListeners();
  }
}
