package highfive.unibus.service;

import highfive.unibus.common.PublicApi;
import highfive.unibus.domain.Driver;
import highfive.unibus.domain.StationPassengerInfo;
import highfive.unibus.domain.StationPassengerInfoId;
import highfive.unibus.dto.driver.DriverNotificationDto;
import highfive.unibus.dto.passenger.StationDto;
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

    public void send() {
        DriverNotificationDto s = DriverNotificationDto.builder()
                .stationName("다음 역")
                .visualDisabilityNum(3)
                .physicalDisabilityNum(2)
                .getOffNum(1)
                .build();
        simpMessagingTemplate.convertAndSend("/sub/" + "1", s);
    }

    @Transactional
    public void getNextStationInfo(Driver driver) {

        String busId = driver.getBusId();
        String prevStationOrd = driver.getPrevStationOrd();

        try {
            JSONObject location = getLocationInfo(busId);
            String stationId = (String) location.get("stId"); // 정류소 고유 id
            String stationOrd = (String) location.get("stOrd"); // 정류소의 해당 노선에서의 순번
            String arsId = getStationNumber(stationId);
            String stationName = getStationName(arsId);

            DriverNotificationDto msg;
            StationPassengerInfoId id = new StationPassengerInfoId(busId, arsId);

            if (isStationOrdChange(prevStationOrd, stationOrd)) {
                driver.updateStationOrd(stationOrd); // 버스의 정류소 순번 업데이트
                if (stationPassengerInfoRepository.findById(id).isPresent()) { // 버스 탑승/하차 인원을 db에서 조회
                    System.out.println("*************** 디비에서 조회 성공");
                    StationPassengerInfo result = stationPassengerInfoRepository.findById(id).get();
                    System.out.println(result);
                    msg = new DriverNotificationDto(result, stationName);
                } else {
                    msg = new DriverNotificationDto(stationName);
                }
                simpMessagingTemplate.convertAndSend("/sub/" + busId, msg);
                System.out.println("버스 기사에게 알림" + msg.toString());
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }

    private String getStationName(String arsId) throws IOException, ParseException {
        String url = PublicApi.initBaseUrl("stationinfo/getLowStationByUid");
        url = PublicApi.addParamToUrl(url, "arsId", arsId);

        String name = (String) PublicApi.callAndGetFisrt(url).get("stnNm");
        System.out.println("***************** 정류소 번호로 정류소 이름 조회" + name);
        return name;
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
        System.out.println("***************** 정류소 아이디로 번호 조회" + stationInfo.get("arsId"));
        return (String) stationInfo.get("arsId");
    }

    private boolean isStationOrdChange(String origStationOrd, String stationOrd) {
        return !origStationOrd.equals(stationOrd);
    }

}