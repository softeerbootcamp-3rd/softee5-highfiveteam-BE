package highfive.unibus.service;

import highfive.unibus.common.PublicApi;
import highfive.unibus.domain.Driver;
import highfive.unibus.domain.StationPassengerInfo;
import highfive.unibus.domain.StationPassengerInfoId;
import highfive.unibus.dto.driver.DriverNotificationDto;
import highfive.unibus.repository.StationPassengerInfoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverService {

    @Value("${api.secret-key}")
    private String secretKey;
    private final StationPassengerInfoRepository stationPassengerInfoRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Transactional
    public void getNextStationInfo(Driver driver) {

        DriverNotificationDto dto = new DriverNotificationDto();

        String busId = driver.getBusId();
        String prevStationOrd = driver.getPrevStationOrd();

        try {
            JSONObject location = getLocationInfo(busId);
            String stationId = (String) location.get("stId");
            String stationOrd = (String) location.get("stOrd");
            String arsId = getStationNumber(stationId);
            String stationName = getStationName(arsId);

            StationPassengerInfoId id = new StationPassengerInfoId(Integer.parseInt(busId), Integer.parseInt(arsId));

            if (isStationOrdChange(prevStationOrd, stationOrd)) {
                driver.updateStationOrd(stationOrd); // 버스의 정류소 순번 업데이트
                if (stationPassengerInfoRepository.findById(id).isPresent()) { // 버스 탑승/하차 인원을 db에서 조회
                    StationPassengerInfo result = stationPassengerInfoRepository.findById(id).get();
                    dto = new DriverNotificationDto(result);
                }
            }
            dto.setStationName(stationName);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        simpMessagingTemplate.convertAndSend("/topic/" + busId, dto);

    }

    private String getStationName(String arsId) throws IOException, ParseException {
        StringBuilder urlBuilder = new StringBuilder("http://ws.bus.go.kr/api/rest/stationinfo/getLowStationByUid");
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + secretKey);
        urlBuilder.append("&" + URLEncoder.encode("arsId","UTF-8") + "=" + URLEncoder.encode(arsId, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("resultType","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8"));

        String result = PublicApi.call(urlBuilder.toString());

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(result);
        JSONObject msgBody = (JSONObject) jsonObject.get("msgBody");
        return (String) ((JSONObject) ((JSONArray) msgBody.get("itemList")).get(0)).get("stnNm");
    }

    public JSONObject getLocationInfo(String busId) throws IOException, ParseException {
        StringBuilder urlBuilder = new StringBuilder("http://ws.bus.go.kr/api/rest/buspos/getBusPosByVehId");
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + secretKey);
        urlBuilder.append("&" + URLEncoder.encode("vehId","UTF-8") + "=" + URLEncoder.encode(busId, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("resultType","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8"));

        String result = PublicApi.call(urlBuilder.toString());

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(result);
        JSONObject msgBody = (JSONObject) jsonObject.get("msgBody");
        return (JSONObject) ((JSONArray) msgBody.get("itemList")).get(0);
    }

    public String getStationNumber(String stationId) throws IOException, ParseException {
        StringBuilder urlBuilder = new StringBuilder("http://ws.bus.go.kr/api/rest/arrive/getLowArrInfoByStId");
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + secretKey);
        urlBuilder.append("&" + URLEncoder.encode("stId","UTF-8") + "=" + URLEncoder.encode(stationId, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("resultType","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8"));

        String result = PublicApi.call(urlBuilder.toString());

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