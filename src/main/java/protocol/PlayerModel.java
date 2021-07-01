package protocol;

/** Representing JSON Model of a Player. */
public class PlayerModel {

  public final String name;

  public final String userId;

  public PlayerModel(String userId, String name) {
    this.name = name;
    this.userId = userId;
  }
}
