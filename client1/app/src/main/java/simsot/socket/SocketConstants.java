package simsot.socket;


public final class SocketConstants {

    public static final String SERVER_URL = "https://simsot-server.herokuapp.com";

    public static final String LIST_PLAYER = "list_player";

    public static final String CONNECTION_REQUEST = "connect_user";
    public static final String REGISTER_REQUEST = "subscribe";
    public static final String NEW_ROOM_REQUEST = "new_room";
    public static final String CREATE_SOLO_ROOM_REQUEST = "create_solo_room";
    public static final String GET_LIST_ROOM = "get_list_room";
    public static final String JOIN_ROOM = "join";
    public static final String KICKED_FROM_ROOM = "kick";
    public static final String LEAVE_ROOM = "leave";
    public static final String CHARACTER_CHOICE = "character_choice";
    public static final String GAME_START = "game_start";
    public static final String CHARACTER_POSITION = "character_position";
    public static final String CHARACTER_TIMEOUT_ENDED = "character_timeout_ended";

    public static final String CONNECTION_RESPONSE = "response_connect";
    public static final String REGISTRATION_RESPONSE = "response_subscribe";
    public static final String LIST_ROOM = "list_room";
    public static final String JOIN_RESPONSE = "response_join";
    public static final String CREATE_RESPONSE = "response_create";
    public static final String CREATE_SOLO_ROOM_RESPONSE = "create_solo_room_response";
    public static final String CHARACTER_CHOICE_RESPONSE = "character_choice_response";
    public static final String CHARACTER_POSITION_RESPONSE = "character_position_response";
    public static final String GAME_START_RESPONSE = "game_start_response";
    public static final String CHARACTER_TIMEOUT_ENDED_RESPONSE = "character_timeout_ended_response";


    public static final String ERROR_CODE = "error_code";
    public static final String PLAYER_NAME = "player_name";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String ROOM_NAME = "room_name";
    public static final String CHARACTER = "character";
    public static final String HOST = "host";
    public static final String SLOT_EMPTY = "slot_empty";
    public static final String ROOMS = "rooms";

    public enum SocketRequestType {
        CONNECTION_REQUEST,
        REGISTER_REQUEST,
        NEW_ROOM_REQUEST,
        GET_LIST_ROOM,
        JOIN_ROOM_REQUEST,
        CREATE_SOLO_ROOM
    }
}
