package com.web.billim.order.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ReservationDateResponse {
    private  List<LocalDateTime> resDate;

}
