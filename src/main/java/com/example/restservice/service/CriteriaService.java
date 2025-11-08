package com.example.restservice.service;

import com.example.restservice.dto.CreateCriteriaDTO;
import com.example.restservice.entity.Criteria;
import com.example.restservice.mapper.CriteriaMapper;
import com.example.restservice.repository.CriteriaRepository;
import com.example.restservice.repository.UserRepository;
import com.example.restservice.response.CriteriaDetailResponse;
import com.example.restservice.response.CriteriaResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CriteriaService {
    private final UserRepository userRepository;
    private final CriteriaRepository criteriaRepository;

    @Autowired
    private CriteriaMapper criteriaMapper;

    public CriteriaService(UserRepository userRepository, CriteriaRepository criteriaRepository) {
        this.userRepository = userRepository;
        this.criteriaRepository = criteriaRepository;
    }

    public List<CriteriaResponseDTO> search(String searchText) {
        List<Criteria> criteriaList = criteriaRepository.findByNameContainingIgnoreCase(searchText);
        return criteriaMapper.toDTOs(criteriaList);
    }    

    public CriteriaResponseDTO create(CreateCriteriaDTO req) {
        Criteria criteria = new Criteria();
        criteria.setName(req.getName());
        criteria.setDescription(req.getDescription());
        criteria.setWeight(req.getWeight());
        criteria.setCategory(req.getCategory());
        Criteria saved = criteriaRepository.save(criteria);
        return criteriaMapper.toDTO(saved);
    }

    public CriteriaDetailResponse update(Long id, CreateCriteriaDTO req) {
        Criteria criteria = criteriaRepository.findById(id)
                .orElseThrow(java.util.NoSuchElementException::new);
        criteria.setName(req.getName());
        criteria.setDescription(req.getDescription());
        criteria.setWeight(req.getWeight());
        criteria.setCategory(req.getCategory());
        Criteria saved = criteriaRepository.save(criteria);
        return criteriaMapper.toDetailDTO(saved);
    }

    public void delete(Long id) {
        if (!criteriaRepository.existsById(id)) {
            throw new java.util.NoSuchElementException();
        }
        criteriaRepository.deleteById(id);
    }
}
