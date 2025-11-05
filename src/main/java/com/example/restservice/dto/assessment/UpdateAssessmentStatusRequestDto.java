package com.example.restservice.dto.assessment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.restservice.common.enums.Status;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAssessmentStatusRequestDto {
    private String status;
}
