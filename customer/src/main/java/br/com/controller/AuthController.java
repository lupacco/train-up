package br.com.controller;

import br.com.core.request.AuthPostRequest;
import br.com.core.request.RegisterPostRequest;
import br.com.model.Customer;
import br.com.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Log4j2
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Customer register(@RequestBody RegisterPostRequest request){
        log.debug("[start] AuthController - register");
        var response = authService.register(request);
        log.debug("[finish] AuthController - register");
        return response;
    }

    @PostMapping("/auth")
    @ResponseStatus(HttpStatus.OK)
    public Customer authenticate(@RequestBody AuthPostRequest request){
        log.debug("[start] AuthController - authenticate");
        var response = authService.authenticate(request);
        log.debug("[finish] AuthController - authenticate");
        return response;
    }

//    @PostMapping
//    public ResponseEntity<BusinessAreaGetResponse> create(
//            @RequestHeader(value = "Authorization") String authorization,
//            @RequestBody @Valid BusinessAreaPostRequest request){
//        var response = businessAreaService.create(authorization, request);
//        return ResponseEntity.ok().body(response);
//    }

//    @PostMapping
//    @PreAuthorize("hasAuthority('CREATE_AREA')")
//    @ResponseStatus(HttpStatus.CREATED)
//    public AreaGetResponse create(@RequestBody @Valid AreaPostRequest request){
//        log.debug("[start] AreaController - create");
//        var response = areaService.create(request);
//        log.debug("[finish] AreaController - create");
//        return response;
//    }
}
