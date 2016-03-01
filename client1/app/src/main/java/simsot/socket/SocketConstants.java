package simsot.socket;


public final class SocketConstants {

    public static final String CONNECTION_REQUEST = "connect_user";
    public static final String REGISTER_REQUEST = "subscribe";
    public static final String NEW_ROOM_REQUEST = "new_room";
    public static final String GET_LIST_ROOM = "get_list_room";

    public enum SocketRequestType {
        CONNECTION_REQUEST,
        REGISTER_REQUEST,
        NEW_ROOM_REQUEST,
        GET_LIST_ROOM;
    }
}
