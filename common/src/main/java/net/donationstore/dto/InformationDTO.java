package net.donationstore.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class InformationDTO implements WebstoreAPIResponseDTO {
    @JsonProperty("webstore")
    public Map<String, Object> webstore;

    @JsonProperty("server")
    public Map<String, Object> server;
}