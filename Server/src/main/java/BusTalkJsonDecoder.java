import org.json.JSONException;
import org.json.JSONObject;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

/**
 * Created by Kristoffer on 2015-09-30.
 */
public class BusTalkJsonDecoder implements Decoder.Text<UserMessage> {

    JSONObject jsonObject;
    @Override
    public UserMessage decode(String s) throws DecodeException {
        return new UserMessage(jsonObject);
    }

    @Override
    public boolean willDecode(String s) {
        try{
            jsonObject = new JSONObject(s);
            return true;

        }catch(JSONException e){
            return false;
        }
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }
}
