package protocol;

import java.util.List;

/**
 * Factory used for creating {@link Response} instances.
 */
public class ResponseFactory {

  /**
   * Make Error response. Used for any errors encountered.
   *
   * @param message error message
   * @return {@link Response}
   */
  public static Response makeErrorResponse(String message) {
    Response response = new Response(Response.Types.ERROR);
    response.message = message;
    return response;
  }

  /**
   * Make Lobby response. Used for any lobby updates.
   *
   * @param lobby {@link LobbyModel} of the updated lobby
   * @return {@link Response}
   */
  public static Response makeLobbyUpdateResponse(LobbyModel lobby) {
    Response response = new Response(Response.Types.LOBBY_UPDATE);
    response.lobby = lobby;
    return response;
  }

  /**
   * Make race starting response. Used to notify users when a race is about to start.
   *
   * @param textToType text of the race
   * @return {@link Response}
   */
  public static Response makeRaceStartingResponse(String textToType) {
    Response response = new Response(Response.Types.GAME_STARTING);
    response.race = new RaceModel(textToType);
    return response;
  }

  /**
   * Make registered response. Used to send userId to newly registered players
   *
   * @param userId id of user
   * @return {@link Response}
   */
  public static Response makeRegisteredResponse(String userId) {
    Response response = new Response(Response.Types.REGISTERED);
    response.userId = userId;
    return response;
  }

  /**
   * Make Lobbies response. Used to send all open lobbies to clients.
   *
   * @param lobbies list of {@link LobbyModel}
   * @return {@link Response}
   */
  public static Response makeLobbiesResponse(List<LobbyModel> lobbies) {
    Response response = new Response(Response.Types.OPEN_LOBBIES);
    response.lobbies = lobbies;
    return response;
  }

}