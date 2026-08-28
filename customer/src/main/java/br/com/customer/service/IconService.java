package br.com.customer.service;

import br.com.customer.dto.response.IconGetResponse;
import br.com.customer.model.Icon;
import br.com.customer.repository.jpa.JpaIconRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class IconService {

    private final JpaIconRepository jpaIconRepository;

    public List<IconGetResponse> listAll(){
        log.debug("[start] IconService - listAll");
        List<IconGetResponse> result = jpaIconRepository.findAll(Sort.by("name")).stream()
                .map(Icon::toGetResponse)
                .toList();
        log.debug("[finish] IconService - listAll");
        return result;
    }
}
