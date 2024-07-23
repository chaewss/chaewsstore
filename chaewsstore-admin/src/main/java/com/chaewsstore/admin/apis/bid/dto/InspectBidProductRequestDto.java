package com.chaewsstore.admin.apis.bid.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PositiveOrZero;

public record InspectBidProductRequestDto(
    @PositiveOrZero
    @Max(value = 100)
    Integer score
) {

}
