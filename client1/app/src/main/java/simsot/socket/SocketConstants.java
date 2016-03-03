package simsot.socket;


public final class SocketConstants {

    public static final String SERVER_URL = "https://simsot-server.herokuapp.com";

    public static final String LIST_PLAYER = "list_player";

    public static final String CONNECTION_REQUEST = "connect_user";
    public static final String REGISTER_REQUEST = "subscribe";
    public static final String NEW_ROOM_REQUEST = "new_room";
    public static final String GET_LIST_ROOM = "get_list_room";
    public static final String JOIN_ROOM = "join";
    public static final String KICKED_FROM_ROOM = "kick";
    public static final String LEAVE_ROOM = "leave";
    public static final String CHARACTER_CHOICE = "character_choice";


    public static final String CONNECTION_RESPONSE = "response_connect";
    public static final String REGISTRATION_RESPONSE = "response_subscribe";
    public static final String LIST_ROOM = "list_room";
    public static final String JOIN_RESPONSE = "response_join";
    public static final String CREATE_RESPONSE = "response_create";
    public static final String CHARACTER_CHOICE_RESPONSE = "character_choice_response";

    public enum SocketRequestType {
        CONNECTION_REQUEST,
        REGISTER_REQUEST,
        NEW_ROOM_REQUEST,
        GET_LIST_ROOM,
        JOIN_ROOM
    }
}
