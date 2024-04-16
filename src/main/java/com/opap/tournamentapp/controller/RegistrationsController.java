package com.opap.tournamentapp.controller;

import com.opap.tournamentapp.dto.CheckRegistrationsTimeDTO;
import com.opap.tournamentapp.model.User;
import com.opap.tournamentapp.service.AuthService;
import com.opap.tournamentapp.service.RegistrationsTimeService;
import com.opap.tournamentapp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Map;

@RestController
public class RegistrationsController {

    private final UserService userService;

    private final RegistrationsTimeService registrationsTimeService;

    public RegistrationsController(UserService userService, RegistrationsTimeService registrationsTimeService){
        this.userService=userService;
        this.registrationsTimeService=registrationsTimeService;
    }

    /**
     * <h2> Number of Registered Users </h2>
     * <p>
     * Check the number of registered users and return it.
     *
     * @return A new ResponseEntity with a 200 (OK) status code and response body with the Integer number of registered users
     */
    @GetMapping("/check/total-registered")
    public ResponseEntity<Integer> checkTotalRegister() {
        return ResponseEntity.ok(userService.totalRegistered());
    }

    /**
     * <h2> Time when Registrations Closing </h2>
     * <p>
     * Check the date and time registrations are closing and return it.
     * Also does proper round handling.
     *
     * @return A new ResponseEntity with a 200 (OK) status code and response body with the LocalDateTime when registrations are closing
     */
    @GetMapping("/public/check/endtime")
    public ResponseEntity<CheckRegistrationsTimeDTO> checkRegisterTime() {
        if(registrationsTimeService.isRegistrationsOpen()) {
            CheckRegistrationsTimeDTO checkRegistrationsTimeDTO = new CheckRegistrationsTimeDTO(true, registrationsTimeService.getRegistrationsEndTime(), registrationsTimeService.getRegistrationRounds());
            return ResponseEntity.ok(checkRegistrationsTimeDTO);
        } else {
            CheckRegistrationsTimeDTO checkRegistrationsTimeDTO = new CheckRegistrationsTimeDTO(false, null, registrationsTimeService.getRegistrationRounds());
            return ResponseEntity.ok(checkRegistrationsTimeDTO);
        }
    }

    /**
     * <h2> Change Registrations End Time </h2>
     * <p>
     * Call setEndTime method and change the endTime variable.
     *
     * @param payload The new end time we want to set.
     * @return A new ResponseEntity with a 200 (OK) status code and response body with the new end time.
     */

    @PostMapping("/admin/end-date")
    public ResponseEntity<String> setEndDate(@RequestBody Map<String, String> payload) {
        String newEndDate = payload.get("newEndDate");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZoneId zoneId = ZoneId.of("Europe/Athens");

        try {
            // First, parse the string to a LocalDateTime
            LocalDateTime parsedEndDate = LocalDateTime.parse(newEndDate, formatter);

            // Then, convert it to a ZonedDateTime using the specified zone
            ZonedDateTime zonedDateTime = ZonedDateTime.of(parsedEndDate, zoneId);

            // Now, pass the ZonedDateTime to your service method
            registrationsTimeService.setRegistrationsEndTime(zonedDateTime);

            // You may want to return the formatted ZonedDateTime string including the time zone
            String responseDate = zonedDateTime.format(formatter); // Note: This does not include time zone information in the string
            return ResponseEntity.ok(responseDate);
        } catch (DateTimeParseException error) {
            return ResponseEntity.badRequest().body("Invalid date format, with error: " + error);
        }
    }
}