package br.com.customer.controller;

import br.com.customer.dto.request.LogSerieRequest;
import br.com.customer.dto.response.ExercisePerformedGetResponse;
import br.com.customer.dto.response.WorkoutPerformedDetailResponse;
import br.com.customer.dto.response.WorkoutPerformedGetResponse;
import br.com.customer.service.WorkoutSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/performed")
@Slf4j
public class PerformedController {

    private final WorkoutSessionService workoutSessionService;

    /**
     * Sessions of the logged user, either for one day (calendar day screen) or
     * for a whole month (dots on the home calendar). Defaults to today.
     */
    @GetMapping
    public ResponseEntity<List<WorkoutPerformedGetResponse>> list(
            @RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "month", required = false) Integer month){
        log.debug("[start] PerformedController - list");

        List<WorkoutPerformedGetResponse> response;
        if (year != null && month != null) {
            YearMonth yearMonth = YearMonth.of(year, month);
            response = workoutSessionService.listByMonth(yearMonth.getYear(), yearMonth.getMonthValue());
        } else {
            response = workoutSessionService.listByDay(date != null ? date : LocalDate.now());
        }

        log.debug("[finish] PerformedController - list");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{performedId}")
    public ResponseEntity<WorkoutPerformedDetailResponse> detail(@PathVariable(name = "performedId") UUID performedId){
        log.debug("[start] PerformedController - detail");
        var response = workoutSessionService.detail(performedId);
        log.debug("[finish] PerformedController - detail");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{performedId}/finish")
    public ResponseEntity<WorkoutPerformedGetResponse> finish(@PathVariable(name = "performedId") UUID performedId){
        log.debug("[start] PerformedController - finish");
        var response = workoutSessionService.finish(performedId);
        log.debug("[finish] PerformedController - finish");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{performedId}/exercise/{exerciseId}/serie/{serie}")
    public ResponseEntity<ExercisePerformedGetResponse> logSerie(
            @PathVariable(name = "performedId") UUID performedId,
            @PathVariable(name = "exerciseId") UUID exerciseId,
            @PathVariable(name = "serie") Short serie,
            @RequestBody LogSerieRequest logSerieRequest){
        log.debug("[start] PerformedController - logSerie");
        var response = workoutSessionService.logSerie(performedId, exerciseId, serie, logSerieRequest);
        log.debug("[finish] PerformedController - logSerie");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
