package com.aplazo.dmc.AplazoBnpl.controller.dto.response;

import lombok.Data;

@Data
public class ErrorResponse {

    private String code;
    private String error;
    private long timestamp;
    private String message;
    private String path;

}
