package highfive.unibus.service;

import highfive.unibus.common.PublicApi;
import highfive.unibus.domain.StationPassengerInfo;
import highfive.unibus.domain.StationPassengerInfoId;
import highfive.unibus.dto.passenger.*;
import highfive.unibus.repository.StationPassengerInfoRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class PassengerService {

    private final StationPassengerInfoRepository stationPassengerInfoRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public ArrayList<AvailableBusDto> getAvailableBusListByStationNums(String departureStationNum, String destinationStationNum) {

        try {
            ArrayList<BusDto> departureBusDtos;
            ArrayList<BusDto> destinationBusDtos;

            departureBusDtos = getBusListByStationNum(departureStationNum);
            destinationBusDtos = getBusListByStationNum(destinationStationNum);

            return getOverlapBusList(departureBusDtos, destinationBusDtos);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<StationDto> getStationsByName(String stationName) {

        try {
            String url = PublicApi.initBaseUrl("stationinfo/getLowStationByName");
            url = PublicApi.addParamToUrl(url, "stSrch", stationName);
            JSONArray itemList = PublicApi.call(url);

            ArrayList<StationDto> stations = new ArrayList<>();
            for (Object station : itemList) {
                JSONObject jsonStation = (JSONObject) station;
                String name = (String) jsonStation.get("stNm");
                String num = (String) jsonStation.get("arsId");
                if (!num.equals("0")) {
                    stations.add(new StationDto(name, num));
                }
            }

            return stations;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<BusDto> getBusListByStationNum(String stationNum) throws IOException, ParseException {
        String url = PublicApi.initBaseUrl("stationinfo/getLowStationByUid");
        url = PublicApi.addParamToUrl(url, "arsId", stationNum);
        JSONArray itemList = PublicApi.call(url);

        ArrayList<BusDto> busList = new ArrayList<>();
        for (Object bus : itemList) {
            JSONObject jsonBus = (JSONObject) bus;
            busList.add(BusDto.builder()
                    .busId((String) jsonBus.get("vehId1"))
                    .busNum((String) jsonBus.get("rtNm"))
                    .arrivalTime((String) jsonBus.get("arrmsg1"))
                    .busType((String) jsonBus.get("busType1"))
                    .orderInRoute((String) jsonBus.get("staOrd"))
                    .build());
        }

        return busList;
    }

    public ArrayList<AvailableBusDto> getOverlapBusList(ArrayList<BusDto> departureBusList, ArrayList<BusDto> DestinationBusList) {
        HashMap<String , OffsetAndBusDto> map = new HashMap<>();

        for (BusDto departureBus : departureBusList) {
            String departureBusNum = departureBus.getBusNum();
            int departureOrder = Integer.parseInt(departureBus.getOrderInRoute());

            for (BusDto destinationBus : DestinationBusList) {
                String destinationBusNum = destinationBus.getBusNum();
                int destinationOrder = Integer.parseInt(destinationBus.getOrderInRoute());

                if ((departureBusNum.equals(destinationBusNum)) && departureOrder < destinationOrder) {

                    OffsetAndBusDto offsetAndBusDto = OffsetAndBusDto.builder()
                            .offset(destinationOrder - departureOrder)
                            .availableBusDto(new AvailableBusDto(departureBus, destinationBus.getOrderInRoute()))
                            .build();

                    if (map.containsKey(departureBusNum)) {
                        if (map.get(departureBusNum).getOffset() > (destinationOrder - departureOrder)) {
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
            overlapBusList.add(map.get(key).getAvailableBusDto());
        }

        return overlapBusList;
    }

    public void reserveBus(BusReservationDto busReservationDto) {
        int busId = Integer.parseInt(busReservationDto.getBusId());
        int departureStationNum = Integer.parseInt(busReservationDto.getDepartureStationNum());
        int destinationStationNum = Integer.parseInt(busReservationDto.getDestinationStationNum());

        StationPassengerInfo departureInfo = makeStationPassengerInfo(busId, departureStationNum);
        StationPassengerInfo destinationInfo = makeStationPassengerInfo(busId, destinationStationNum);;

        if (busReservationDto.getDisabilityType().equals("visual")) { // 시각 장애
            departureInfo.setVisualDisabilityNum(departureInfo.getVisualDisabilityNum() + 1);
        } else { // 신체 장애
            departureInfo.setPhysicalDisabilityNum(departureInfo.getPhysicalDisabilityNum() + 1);
        }

        destinationInfo.setGetOffNum(destinationInfo.getGetOffNum() + 1);
    }

    private StationPassengerInfo makeStationPassengerInfo(int busId, int stationNum) {
        StationPassengerInfoId stationPassengerInfoId = new StationPassengerInfoId(busId, stationNum);
        StationPassengerInfo stationPassengerInfo;

        if (stationPassengerInfoRepository.findById(stationPassengerInfoId).isPresent()) {
            stationPassengerInfo = stationPassengerInfoRepository.findById(stationPassengerInfoId).get();
        } else {
            stationPassengerInfo = StationPassengerInfo.builder()
                    .stationPassengerInfoId(stationPassengerInfoId)
                    .visualDisabilityNum(0)
                    .physicalDisabilityNum(0)
                    .getOffNum(0)
                    .build();

            stationPassengerInfoRepository.save(stationPassengerInfo);
        }

        return stationPassengerInfo;
    }

    // 버스 아이디로 버스 위치 조회 (정류소 순번) - 출발지 정류소 순번이랑 비교
    private boolean notifyDepartureStation(String busId, String stationNum) {

        try {
            String url = PublicApi.initBaseUrl("buspos/getBusPosByVehId");
            url = PublicApi.addParamToUrl(url, "vehId", busId);
            JSONObject object = PublicApi.callAndGetFisrt(url);
            if (object.get("stOrd").equals(stationNum)) {
                PassengerNotificationDto msg = PassengerNotificationDto.builder()
                        .stationName((String) object.get("congetion"))
                        .vehicleNum((String) object.get("plainNo"))
                        .build();
                simpMessagingTemplate.convertAndSend("/topic/" + "clientid", msg);
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return  false;

    }

}

@Getter
@Builder
class OffsetAndBusDto {

    int offset;
    AvailableBusDto availableBusDto;

}