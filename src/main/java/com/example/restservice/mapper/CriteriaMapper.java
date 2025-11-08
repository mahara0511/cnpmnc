package com.example.restservice.mapper;

import com.example.restservice.entity.Criteria;
import com.example.restservice.response.CriteriaResponseDTO;
import com.example.restservice.response.CriteriaDetailResponse;
import org.springframework.stereotype.Component;
import java.util.List;
@Component
public class CriteriaMapper {
    public CriteriaResponseDTO toDTO(Criteria entity) {
        return new CriteriaResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getWeight(),
                entity.getCategory()
        );
    }

    public CriteriaDetailResponse toDetailDTO(Criteria entity) {
        return new CriteriaDetailResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getWeight(),
                entity.getCategory()
        );
    }

    public List<CriteriaResponseDTO> toDTOs(List<Criteria> entities) {
        return entities.stream().map(this::toDTO).toList();
    }
}
