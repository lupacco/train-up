package br.com.customer.controller;

import br.com.customer.dto.response.IconGetResponse;
import br.com.customer.service.IconService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/icon")
@Slf4j
public class IconController {

    private final IconService iconService;

    @GetMapping
    public ResponseEntity<List<IconGetResponse>> listAll(){
        log.debug("[start] IconController - listAll");
        var response = iconService.listAll();
        log.debug("[finish] IconController - listAll");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
