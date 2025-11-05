package com.example.restservice.controller;

import com.example.restservice.dto.CreateCriteriaDTO;
import com.example.restservice.response.ApiResponse;
import com.example.restservice.response.CriteriaResponseDTO;
import com.example.restservice.service.CriteriaService;
import com.example.restservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import java.util.NoSuchElementException;
import io.swagger.v3.oas.annotations.Operation;

@Tag(name = "Criteria", description = "Endpoints for managing criteria")
@RestController
@RequestMapping("/criteria")
public class CriteriaController {
    private final CriteriaService criteriaService;
    private final UserService userService;

    public CriteriaController(CriteriaService criteriaService, UserService userService) {
        this.criteriaService = criteriaService;
        this.userService = userService;
    }

    // search criteria with search text
    @Operation(summary = "Search Criteria", description = "Search criteria based on the provided search text")
    @GetMapping("")
    public ResponseEntity<?> search(@RequestParam(defaultValue = "") String searchText) {
        try {
            var results = criteriaService.search(searchText);
            return ResponseEntity.ok(ApiResponse.success(results));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage(), 400));
        }
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody CreateCriteriaDTO req) {
        try {
            CriteriaResponseDTO res = criteriaService.create(req);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Refresh failed: " + e.getMessage());
        }
    }

    @PatchMapping({"/{criteriaId}"})
    public ResponseEntity<?> updateCriteria(
            @PathVariable("criteriaId") Long criteriaId,
            @RequestBody CreateCriteriaDTO req) {
        try {
            var updated = criteriaService.update(criteriaId, req);
            return ResponseEntity.ok(ApiResponse.success(updated));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder().message("Not Found").status(404).data(null).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder().message(e.getMessage()).status(400).data(null).build());
        }
    }

    @DeleteMapping({"/{criteriaId}"})
    public ResponseEntity<?> deleteCriteria(@PathVariable("criteriaId") Long criteriaId) {
        try {
            criteriaService.delete(criteriaId);
            return ResponseEntity.ok(ApiResponse.builder().message("Deleted").status(200).data(null).build());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder().message("Not Found").status(404).data(null).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder().message(e.getMessage()).status(400).data(null).build());
        }
    }
}
