package highfive.unibus.repository;

import highfive.unibus.domain.StationPassengerInfo;
import highfive.unibus.domain.StationPassengerInfoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StationPassengerInfoRepository extends JpaRepository<StationPassengerInfo, StationPassengerInfoId> {
}