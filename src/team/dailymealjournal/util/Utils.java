package team.dailymealjournal.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slim3.repackaged.org.json.JSONArray;
import org.slim3.repackaged.org.json.JSONException;
import org.slim3.repackaged.org.json.JSONObject;

/**
 * Utility used to perform certain functions that are not supplied by the framework.
 * @author Kim Agustin
 * @version 0.01
 * Version History
 * [08/31/2015] 0.01 – Kim Agustin – Initial codes.
 */
public class Utils {

    /**
     * Method used to convert a JSONObject into a HashMap.
     * Uses recursion implementation.
     * @param JSONObject json - JSON to be converted.
     * @return Map<String, Object> - the result Map.
     */
    public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if (json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        
        return retMap;
    }

    /**
     * Method used to push the object into the Map, if the object is a JSONObject.
     * @param JSONObject json - JSON to be converted.
     * @return Map<String, Object> - the result Map.
     */
    @SuppressWarnings("unchecked")
    protected static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            
            map.put(key, value);
        }
        
        return map;
    }

    /**
     * Method used to return the list of Objects, if the object is a JSONArray.
     * @param JSONArray array - JSON to be converted.
     * @return List<Object> - the result List.
     */
    protected static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
    
    public static JSONArray listToJson(List<String> list) {
        JSONArray array = new JSONArray();
        
        for (String string : list) {
            array.put(string);
        }
        
        return array;
    }

    /**
     * Shortcut method to write a JSONObject as the response of content type 'application/json'
     * @param response the response object where this JSONObject will be written to
     * @param object the JSONObject that will be written
     */
    public static void writeJsonResponse(HttpServletResponse response, JSONObject object) throws IOException {
        response.setContentType("application/json");
        response.getWriter().write(object.toString());
    }
    
    /**
     * Shortcut method to write a JSONArray as the response of content type 'application/json'
     * @param response the response object where this JSONArray will be written to
     * @param object the JSONArray that will be written
     */
    public static void writeJsonResponse(HttpServletResponse response, JSONArray array) throws IOException {
        response.setContentType("application/json");
        response.getWriter().write(array.toString());
    }

    public static void writeErrors(HttpServletResponse response, List<String> errorList) throws IOException, JSONException {
        JSONObject r = new JSONObject();
        JSONArray errors = listToJson(errorList);
        r.put("errors", errors);
        
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Input Validation Error");
        response.setContentType("application/json");
        response.getWriter().write(r.toString());
    }

}
