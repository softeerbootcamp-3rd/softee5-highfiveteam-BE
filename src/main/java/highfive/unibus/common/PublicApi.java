package highfive.unibus.common;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Component
public class PublicApi {

    private static String secretKey;

    @Value("${api.secret-key}")
    private void setSecretKey(String value) {
        secretKey = value;
    }

    public static JSONArray call(String urlStr) throws IOException, ParseException {
        urlStr = addParamToUrl(urlStr, "resultType", "json");
        System.out.println(urlStr);
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());

        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        String result = rd.readLine();
        System.out.println(result);
        rd.close();
        conn.disconnect();

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(result);
        JSONObject msgBody = (JSONObject) jsonObject.get("msgBody");
        JSONArray itemList = (JSONArray) msgBody.get("itemList");

        return itemList;
    }

    public static JSONObject callAndGetFisrt(String urlStr) throws IOException, ParseException {
        return (JSONObject) call(urlStr).get(0);
    }

    public static String initBaseUrl(String baseUrl) throws IOException {
        String urlStr = "http://ws.bus.go.kr/api/rest/" + baseUrl
                + "?" + URLEncoder.encode("serviceKey", "UTF-8") + secretKey;
        return urlStr;
    }

    public static String addParamToUrl(String urlStr, String key, String value) throws IOException {
        return urlStr + "&" + URLEncoder.encode(key,"UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
    }

}
