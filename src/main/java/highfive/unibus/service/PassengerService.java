package highfive.unibus.service;

import highfive.unibus.dto.passenger.AvailableBusDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

@Service
public class PassengerService {

    @Value("${api.secret-key}")
    private String secretKey;
    private final JSONParser parser = new JSONParser();

    public ArrayList<AvailableBusDto> getBusListByStationNames(String departure, String destination) {
        ArrayList<String> departureBusNums;
        ArrayList<String> destinationBusNums;
        ArrayList<AvailableBusDto> departureBusDtos = new ArrayList<>();
        ArrayList<AvailableBusDto> destinationBusDtos = new ArrayList<>();

        try {
            departureBusNums = getStationNumByName(departure);
            destinationBusNums = getStationNumByName(destination);

            for (String departureBusNum : departureBusNums) {
                System.out.println(departureBusNum);
                departureBusDtos.addAll(getBusListByStationNum(departureBusNum));
            }
            for (String destinationBusNum : destinationBusNums) {
                destinationBusDtos.addAll(getBusListByStationNum(destinationBusNum));
            }

            return getOverlapBusList(departureBusDtos, destinationBusDtos);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<String> getStationNumByName(String stationName) throws IOException, ParseException {
        StringBuilder urlBuilder = new StringBuilder("http://ws.bus.go.kr/api/rest/stationinfo/getLowStationByName"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + secretKey); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("stSrch","UTF-8") + "=" + URLEncoder.encode(stationName, "UTF-8")); /*정류소명 검색어*/
        urlBuilder.append("&" + URLEncoder.encode("resultType","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*json 으로*/

        String result = callApi(urlBuilder.toString());

        JSONObject jsonObject = (JSONObject) parser.parse(result);
        JSONObject msgBody = (JSONObject) jsonObject.get("msgBody");
        JSONArray itemList = (JSONArray) msgBody.get("itemList");

        ArrayList<String> stationNums = new ArrayList<>();
        for (Object station : itemList) {
            JSONObject jsonStation = (JSONObject) station;
            if (jsonStation.get("stNm").equals(stationName)) {
                stationNums.add((String) jsonStation.get("arsId"));
            }
        }

        return stationNums;
    }

    public ArrayList<AvailableBusDto> getBusListByStationNum(String stationNum) throws IOException, ParseException {
        StringBuilder urlBuilder = new StringBuilder("http://ws.bus.go.kr/api/rest/stationinfo/getLowStationByUid"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + secretKey); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("arsId","UTF-8") + "=" + URLEncoder.encode(stationNum, "UTF-8")); /*정류소 번호 검색어*/
        urlBuilder.append("&" + URLEncoder.encode("resultType","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*json 으로*/

        String result = callApi(urlBuilder.toString());

        JSONObject jsonObject = (JSONObject) parser.parse(result);
        JSONObject msgBody = (JSONObject) jsonObject.get("msgBody");
        JSONArray itemList = (JSONArray) msgBody.get("itemList");

        ArrayList<AvailableBusDto> busList = new ArrayList<>();
        for (Object bus : itemList) {
            JSONObject jsonBus = (JSONObject) bus;
            busList.add(AvailableBusDto.builder()
                    .busId(Integer.parseInt((String) jsonBus.get("vehId1")))
                    .busNum((String) jsonBus.get("rtNm"))
                    .arrivalTime((String) jsonBus.get("arrmsg1"))
                    .busType((String) jsonBus.get("busType1"))
                    .orderInRoute((String) jsonBus.get("staOrd"))
                    .build());
        }

        return busList;
    }

    public ArrayList<AvailableBusDto> getOverlapBusList(ArrayList<AvailableBusDto> departureBusList, ArrayList<AvailableBusDto> DestinationBusList) {
        HashMap<String , OffsetAndBusDto> map = new HashMap<>();

        for (AvailableBusDto departureBus : departureBusList) {
            String departureBusNum = departureBus.getBusNum();
            int departureOrder = Integer.parseInt(departureBus.getOrderInRoute());

            for (AvailableBusDto destinationBus : DestinationBusList) {
                String destinationBusNum = destinationBus.getBusNum();
                int destinationOrder = Integer.parseInt(destinationBus.getOrderInRoute());

                if ((departureBusNum.equals(destinationBusNum)) && departureOrder < destinationOrder) {
                    OffsetAndBusDto offsetAndBusDto = new OffsetAndBusDto(departureOrder - destinationOrder, departureBus);
                    if (map.containsKey(departureBusNum)) {
                        if (offsetAndBusDto.getOffset() > (destinationOrder - departureOrder)) {
                            map.replace(departureBusNum, offsetAndBusDto);
                        }
                    } else {
                        map.put(departureBusNum, offsetAndBusDto);
                    }
                }
            }
        }

        ArrayList<AvailableBusDto> overlapBusList = new ArrayList<>();
        for (String key : map.keySet()) {
            overlapBusList.add(map.get(key).getBusDto());
        }

        return overlapBusList;
    }

    public String callApi(String urlStr) throws IOException {
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
        rd.close();
        conn.disconnect();

        return result;
    }

}

@Getter
@AllArgsConstructor
class OffsetAndBusDto {

    int offset;
    AvailableBusDto busDto;

}