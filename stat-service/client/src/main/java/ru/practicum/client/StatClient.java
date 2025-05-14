package ru.practicum.client;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.HitCreateRequestDto;
import ru.practicum.dto.HitResponseDto;
import ru.practicum.dto.StatResponseDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatClient extends BaseClient {
    public StatClient(@Value("${stats.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public HitResponseDto saveEvent(HitCreateRequestDto hitCreateRequestDto) throws IOException {
        ResponseEntity<Object> response = post(hitCreateRequestDto);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue((JsonParser) response.getBody(), HitResponseDto.class);

    }


    public List<StatResponseDto> getStats(LocalDateTime start,
                                          LocalDateTime end,
                                          List<String> uris,
                                          Boolean unique) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("start", start);
        params.put("end", end);
        params.put("uris", uris);
        params.put("unique", unique);
        ResponseEntity<Object> response = get(params);
        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<List<StatResponseDto>> jacksonTypeReference = new TypeReference<>() {
        };
        return objectMapper.readValue((JsonParser) response.getBody(), jacksonTypeReference);
    }
}
