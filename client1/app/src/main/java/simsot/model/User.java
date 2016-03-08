package simsot.model;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

    private static final String USERNAME = "pseudo";
    private static final String PASSWORD = "password";

    private String userLogin;

    private String userPassword;

    public User(String userLogin, String userPassword) {
        this.userLogin = userLogin;
        this.userPassword = userPassword;
    }

    public JSONObject ToJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(USERNAME, userLogin);
        jsonObject.put(PASSWORD, userPassword);

        return jsonObject;
    }
}
