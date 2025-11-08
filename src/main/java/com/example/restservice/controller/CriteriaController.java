package com.example.restservice.controller;

import com.example.restservice.dto.CreateCriteriaDTO;
import com.example.restservice.response.ApiResponse;
import com.example.restservice.response.CriteriaResponseDTO;
import com.example.restservice.service.CriteriaService;
import com.example.restservice.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.UUID;
import java.util.NoSuchElementException;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.access.prepost.PreAuthorize;

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

    // Search criteria with pagination
    @PreAuthorize("hasRole('SUPERVISOR')")
    @Operation(summary = "Search Criteria with Pagination", 
               description = "Search criteria based on name or description with pagination support")
    @GetMapping("")
    public ResponseEntity<?> search(
            @Parameter(description = "Search query for name or description")
            @RequestParam(defaultValue = "") String query,
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<CriteriaResponseDTO> results = criteriaService.searchWithPagination(query, pageable);
            return ResponseEntity.ok(ApiResponse.success(results));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage(), 400));
        }
    }

    @Operation(summary = "Create Criteria", description = "Create a new criteria with the provided details")
    @PreAuthorize("hasRole('SUPERVISOR')")
    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody CreateCriteriaDTO req) {
        try {
            CriteriaResponseDTO res = criteriaService.create(req);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Refresh failed: " + e.getMessage());
        }
    }


    @Operation(summary = "Update Criteria", description = "Update an existing criteria identified by criteriaId with the provided details")
    @PreAuthorize("hasRole('SUPERVISOR')")
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


    @Operation(summary = "Delete Criteria", description = "Delete an existing criteria identified by criteriaId")
    @PreAuthorize("hasRole('SUPERVISOR')")
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
