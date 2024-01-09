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

        String busId = driver.getBusId();
        String prevStationOrd = driver.getPrevStationOrd();

        try {
            JSONObject location = getLocationInfo(busId);
            String stationId = (String) location.get("stId");
            String stationOrd = (String) location.get("stOrd");
            String arsId = getStationNumber(stationId);
            String stationName = getStationName(arsId);

            DriverNotificationDto dto = new DriverNotificationDto(stationName);

            StationPassengerInfoId id = new StationPassengerInfoId(Integer.parseInt(busId), Integer.parseInt(arsId));

            if (isStationOrdChange(prevStationOrd, stationOrd)) {
                driver.updateStationOrd(stationOrd); // 버스의 정류소 순번 업데이트
                if (stationPassengerInfoRepository.findById(id).isPresent()) { // 버스 탑승/하차 인원을 db에서 조회
                    StationPassengerInfo result = stationPassengerInfoRepository.findById(id).get();
                    dto = new DriverNotificationDto(result);
                }
                simpMessagingTemplate.convertAndSend("/topic/" + busId, dto);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }

    private String getStationName(String arsId) throws IOException, ParseException {
        String url = PublicApi.initBaseUrl("stationinfo/getLowStationByUid");
        url = PublicApi.addParamToUrl(url, "arsId", arsId);

        return (String) PublicApi.callAndGetFisrt(url).get("stnNm");
    }

    public JSONObject getLocationInfo(String busId) throws IOException, ParseException {
        String url = PublicApi.initBaseUrl("buspos/getBusPosByVehId");
        url = PublicApi.addParamToUrl(url, "vehId", busId);

        return PublicApi.callAndGetFisrt(url);
    }

    public String getStationNumber(String stationId) throws IOException, ParseException {
        String url = PublicApi.initBaseUrl("arrive/getLowArrInfoByStId");
        url = PublicApi.addParamToUrl(url, "stId", stationId);

        JSONObject stationInfo = PublicApi.callAndGetFisrt(url);
        return (String) stationInfo.get("arsId");
    }

    private boolean isStationOrdChange(String origStationOrd, String stationOrd) {
        return !origStationOrd.equals(stationOrd);
    }

}