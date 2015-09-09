package team.dailymealjournal.utils;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slim3.repackaged.org.json.JSONArray;
import org.slim3.repackaged.org.json.JSONException;
import org.slim3.repackaged.org.json.JSONObject;

import team.dailymealjournal.dto.BaseDto;

public final class ResponseUtils {

    /**
     * Writes a json-encoded response body from the JSONObject given
     * @param response - the response object where this JSONObject will be written to
     * @param object - the JSONObject that will be written
     */
    public static void writeJsonResponse(HttpServletResponse response, JSONObject object) throws IOException {
        response.setContentType("application/json");
        response.getWriter().write(object.toString());
    }
    
    /**
     * Writes a json-encoded response body from the JSONArray given
     * @param response - the response object where the array will be written to
     * @param object - the JSONArray that will be written
     */
    public static void writeJsonResponse(HttpServletResponse response, JSONArray array) throws IOException {
        response.setContentType("application/json");
        response.getWriter().write(array.toString());
    }

    /**
     * Writes a json-encoded response body, with a status code of 403, 
     * that contains errors in the key 'errors'
     * @param response - the response object where the response will be written to
     * @param errorList - the list of errors that will be written
     * @param title - the http status text
     */
    public static void writeErrors(HttpServletResponse response, List<String> errorList, String title) throws IOException, JSONException {
        JSONObject r = new JSONObject();
        JSONArray errors = Utils.listToJson(errorList);
        r.put("errors", errors);
        
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, title);
        response.setContentType("application/json");
        response.getWriter().write(r.toString());
    }
    
    /**
     * Renders a response body of status code 403 if dto's errorList is not empty.
     * Otherwise, it does nothing.
     * @param response
     * @param dto
     */
    public static void handleDto(HttpServletResponse response, BaseDto dto) throws IOException {
        List<String> errorList = dto.getErrorList();
        if (errorList.size() > 0) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Input Validation Error");
            response.getWriter().write(Utils.join("\n", errorList));
        }
    }
    
}
