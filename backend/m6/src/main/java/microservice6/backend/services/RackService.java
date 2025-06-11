package microservice6.backend.services;

import lombok.RequiredArgsConstructor;
import microservice6.backend.dto.*;
import microservice6.backend.repositories.ReserveClient;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class RackService {

    private final ReserveClient reserveClient;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RackService.class);

    public List<List<String>> getReserveByWeek(int year, int month, int day) {
        LocalDate date = LocalDate.of(year, month, day);
        LocalDate startDate = date.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));

        List<ReserveDTO> reserves = reserveClient.listReservesByWeek(new MADDTO(year, month, day)).getBody();

        logger.info("Reservas obtenidas: " + startDate + ": " + reserves);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");

        return IntStream.range(0, 7)
                .mapToObj(i -> startDate.plusDays(i))
                .map(d -> reserves.stream()
                        .filter(r -> r.getReserveday().equals(d))
                        .map(r -> r.getUsername() + " (" + r.getBegin().format(timeFormatter) + " - " + r.getFinish().format(timeFormatter) + ")")
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }
}