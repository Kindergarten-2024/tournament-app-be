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

    private final AuthService authService;

    private final RegistrationsTimeService registrationsTimeService;

    public RegistrationsController(UserService userService, AuthService authService, RegistrationsTimeService registrationsTimeService){
        this.userService=userService;
        this.authService=authService;
        this.registrationsTimeService=registrationsTimeService;

    }

    /**
     * <h2> Registration </h2>
     * <p>
     * Register current user by OAuth2 token to the tournament,
     * by changing his Registered column to true.
     *
     * @param token The OAuth2AuthenticationToken of current user
     * @return A new ResponseEntity with a 200 (OK) status code and response body with the String
     * "Error" if token not exist, "Registered" if user registered and "Registrations closed." if registrations have closed
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(OAuth2AuthenticationToken token) {
        User user = authService.getUserFromAuthenticationToken(token);

        if (user == null) {
            return ResponseEntity.ok("Error");
        } else if (registrationsTimeService.isRegistrationsOpen()) {
            Long user_id = user.getId();
            user.setRegistered(true);
            userService.updateUser(user_id, user);
            return ResponseEntity.ok("Registered");
        } else {
            return ResponseEntity.ok("Registrations closed.");
        }
    }

    /**
     * <h2> Check if user is Registered </h2>
     * <p>
     * Checks if current user by OAuth2 token is registered.
     *
     * @param token The OAuth2AuthenticationToken of current user
     * @return A new ResponseEntity with a 200 (OK) status code and response body with the String
     * "Registered" if user is registered or { "message": "Not registered." } if not
     */
    @GetMapping("/check/register")
    public ResponseEntity<?> checkRegister(OAuth2AuthenticationToken token) {

        User user = authService.getUserFromAuthenticationToken(token);

        if (user.getRegistered()) {
            return ResponseEntity.ok("Registered");

        } else {
            return ResponseEntity.ok(Collections.singletonMap("message", "Not registered."));
        }
    }

    /**
     * <h2> Unregister </h2>
     * <p>
     * Unregister current user by OAuth2 token from the tournament,
     * by changing his Registered column to false.
     *
     * @param token The OAuth2AuthenticationToken of current user
     * @return A new ResponseEntity with a 200 (OK) status code and response body with the String "Successful unregister"
     */
    @PostMapping("/unregister")
    public ResponseEntity<String> unRegister(OAuth2AuthenticationToken token) {
        User user = authService.getUserFromAuthenticationToken(token);
        Long user_id = user.getId();
        user.setRegistered(false);
        userService.updateUser(user_id, user);
        return ResponseEntity.ok("Successful unregister");
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
    @GetMapping("/admin/check/endtime")
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