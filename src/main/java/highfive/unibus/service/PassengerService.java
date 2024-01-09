package highfive.unibus.service;

import highfive.unibus.common.PublicApi;
import highfive.unibus.domain.StationPassengerInfo;
import highfive.unibus.domain.StationPassengerInfoId;
import highfive.unibus.dto.passenger.AvailableBusDto;
import highfive.unibus.dto.passenger.BusReservationDto;
import highfive.unibus.dto.passenger.StationDto;
import highfive.unibus.repository.StationPassengerInfoRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class PassengerService {

    private final StationPassengerInfoRepository stationPassengerInfoRepository;

    public ArrayList<AvailableBusDto> getAvailableBusListByStationNums(String departureStationNum, String destinationStationNum) {

        try {
            ArrayList<AvailableBusDto> departureBusDtos;
            ArrayList<AvailableBusDto> destinationBusDtos;

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

    public ArrayList<AvailableBusDto> getBusListByStationNum(String stationNum) throws IOException, ParseException {
        String url = PublicApi.initBaseUrl("stationinfo/getLowStationByUid");
        url = PublicApi.addParamToUrl(url, "arsId", stationNum);
        JSONArray itemList = PublicApi.call(url);

        ArrayList<AvailableBusDto> busList = new ArrayList<>();
        for (Object bus : itemList) {
            JSONObject jsonBus = (JSONObject) bus;
            busList.add(AvailableBusDto.builder()
                    .busId((String) jsonBus.get("vehId1"))
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

}

@Getter
@AllArgsConstructor
class OffsetAndBusDto {

    int offset;
    AvailableBusDto busDto;

}