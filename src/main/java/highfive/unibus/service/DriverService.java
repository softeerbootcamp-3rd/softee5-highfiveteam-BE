package highfive.unibus.service;

import highfive.unibus.domain.StationPassengerInfo;
import highfive.unibus.domain.StationPassengerInfoId;
import highfive.unibus.dto.driver.BusInfoDto;
import highfive.unibus.dto.driver.DriverNotificationDto;
import highfive.unibus.repository.StationPassengerInfoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final StationPassengerInfoRepository stationPassengerInfoRepository;

    @Transactional
    public DriverNotificationDto getNextStationInfo(BusInfoDto dto) {

        try {
            String busId = dto.getBusId();
            String prevStationOrd = dto.getPrevStationOrd();

            JSONObject location = getLocationInfo(busId);
            String stationId = (String) location.get("stId");
            String stationOrd = (String) location.get("stOrd");
            String arsId = getStationNumber(stationId);
            String stationName = getStationName(arsId);

//            System.out.println("busId = " + busId);
//            System.out.println("stationId = " + stationId);
//            System.out.println("stationOrd = " + stationOrd);
//            System.out.println("arsId = " + arsId);
//            System.out.println("stationName = " + stationName);

            StationPassengerInfoId id = new StationPassengerInfoId(Integer.parseInt(busId), Integer.parseInt(arsId));

            if (isStationOrdChange(prevStationOrd, stationOrd)) {
                if (stationPassengerInfoRepository.findById(id).isPresent()) {
                    StationPassengerInfo result = stationPassengerInfoRepository.findById(id).get();
                    return new DriverNotificationDto(result);
                } else {
                    System.out.println("no reservation");
                    return new DriverNotificationDto(stationName);
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return null;
    }

    private String getStationName(String arsId) throws IOException, ParseException {
        String apiUrl = "http://ws.bus.go.kr/api/rest/stationinfo/getLowStationByUid";
        String resultType = "json";

        StringBuilder urlBuilder = new StringBuilder(apiUrl);
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=0OhBU7ZCGIobDVKDeBJDpmDRqK3IRNF6jlf%2FJB2diFAf%2FfR2czYO9A4UTGcsOwppV6W2HVUeho%2FFPwXoL6DwqA%3D%3D");
        urlBuilder.append("&" + URLEncoder.encode("arsId","UTF-8") + "=" + URLEncoder.encode(arsId, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("resultType","UTF-8") + "=" + URLEncoder.encode(resultType, "UTF-8"));
        URL url = new URL(urlBuilder.toString());

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
        rd.close();
        conn.disconnect();

//        System.out.println(result);
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(result);
        JSONObject msgBody = (JSONObject) jsonObject.get("msgBody");
        return (String) ((JSONObject) ((JSONArray) msgBody.get("itemList")).get(0)).get("stnNm");
    }

    public JSONObject getLocationInfo(String busId) throws IOException, ParseException {
        String apiUrl = "http://ws.bus.go.kr/api/rest/buspos/getBusPosByVehId";
        String resultType = "json";

        StringBuilder urlBuilder = new StringBuilder(apiUrl);
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=0OhBU7ZCGIobDVKDeBJDpmDRqK3IRNF6jlf%2FJB2diFAf%2FfR2czYO9A4UTGcsOwppV6W2HVUeho%2FFPwXoL6DwqA%3D%3D");
        urlBuilder.append("&" + URLEncoder.encode("vehId","UTF-8") + "=" + URLEncoder.encode(busId, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("resultType","UTF-8") + "=" + URLEncoder.encode(resultType, "UTF-8"));
        URL url = new URL(urlBuilder.toString());

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

//        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        String result = rd.readLine();
        rd.close();
        conn.disconnect();

//        System.out.println(result);
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(result);
        JSONObject msgBody = (JSONObject) jsonObject.get("msgBody");
        return (JSONObject) ((JSONArray) msgBody.get("itemList")).get(0);
    }

    public String getStationNumber(String stationId) throws IOException, ParseException {
        String apiUrl = "http://ws.bus.go.kr/api/rest/arrive/getLowArrInfoByStId";
        String resultType = "json";

        StringBuilder urlBuilder = new StringBuilder(apiUrl);
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=0OhBU7ZCGIobDVKDeBJDpmDRqK3IRNF6jlf%2FJB2diFAf%2FfR2czYO9A4UTGcsOwppV6W2HVUeho%2FFPwXoL6DwqA%3D%3D");
        urlBuilder.append("&" + URLEncoder.encode("stId","UTF-8") + "=" + URLEncoder.encode(stationId, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("resultType","UTF-8") + "=" + URLEncoder.encode(resultType, "UTF-8"));
        URL url = new URL(urlBuilder.toString());

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

//        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        String result = rd.readLine();
        rd.close();
        conn.disconnect();

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(result);
        JSONObject msgBody = (JSONObject) jsonObject.get("msgBody");
        JSONObject stationInfo = (JSONObject) ((JSONArray) msgBody.get("itemList")).get(0);
        return (String) stationInfo.get("arsId");
    }

    private boolean isStationOrdChange(String origStationOrd, String stationOrd) {
        return !origStationOrd.equals(stationOrd);
    }

}