package com.cg.frontend.service;

import com.cg.frontend.dto.EmployeeDto;
import com.cg.frontend.dto.JobDto;
import com.cg.frontend.dto.PageMetaDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${backend.base-url}")
    private String baseUrl;

    public EmployeeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public List<EmployeeDto> getAllEmployees(int page, int size) {
        try {
            String url = baseUrl + "/employees?page=" + page + "&size=" + size;
            ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
            JsonNode embedded = response.getBody().path("_embedded").path("employees");
            List<EmployeeDto> employees = new ArrayList<>();
            if (embedded.isArray()) {
                for (JsonNode node : embedded) {
                    employees.add(objectMapper.treeToValue(node, EmployeeDto.class));
                }
            }
            return employees;
        } catch (Exception e) {
            System.err.println("[EmployeeService] getAllEmployees failed: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public PageMetaDto getPageMeta(int page, int size) {
        try {
            String url = baseUrl + "/employees?page=" + page + "&size=" + size;
            ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
            JsonNode pageMeta = response.getBody().path("page");
            return objectMapper.treeToValue(pageMeta, PageMetaDto.class);
        } catch (Exception e) {
            System.err.println("[EmployeeService] getPageMeta failed: " + e.getMessage());
            return new PageMetaDto();
        }
    }

    public EmployeeDto getEmployeeById(String empId) {
        try {
            String url = baseUrl + "/employees/" + empId;
            ResponseEntity<EmployeeDto> response = restTemplate.getForEntity(url, EmployeeDto.class);
            return response.getBody();
        } catch (Exception e) {
            System.err.println("[EmployeeService] getEmployeeById failed for id=" + empId + ": " + e.getMessage());
            return null;
        }
    }

    public List<EmployeeDto> searchEmployees(String query) {
        try {
            String url = baseUrl + "/employees?page=0&size=200";
            ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
            JsonNode embedded = response.getBody().path("_embedded").path("employees");
            List<EmployeeDto> employees = new ArrayList<>();
            String q = query.toLowerCase();
            if (embedded.isArray()) {
                for (JsonNode node : embedded) {
                    EmployeeDto e = objectMapper.treeToValue(node, EmployeeDto.class);
                    if (matchesQuery(e, q)) employees.add(e);
                }
            }
            return employees;
        } catch (Exception e) {
            System.err.println("[EmployeeService] searchEmployees failed: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private boolean matchesQuery(EmployeeDto e, String q) {
        return (e.getFname() != null && e.getFname().toLowerCase().contains(q))
            || (e.getLname() != null && e.getLname().toLowerCase().contains(q))
            || (e.getEmpId() != null && e.getEmpId().toLowerCase().contains(q))
            || (e.getPubId() != null && e.getPubId().toLowerCase().contains(q));
    }

    public JobDto getJobById(Short jobId) {
        if (jobId == null) return null;
        try {
            String url = baseUrl + "/jobs/" + jobId;
            ResponseEntity<JobDto> response = restTemplate.getForEntity(url, JobDto.class);
            return response.getBody();
        } catch (Exception e) {
            System.err.println("[EmployeeService] getJobById failed for jobId=" + jobId + ": " + e.getMessage());
            return null;
        }
    }

    public List<EmployeeDto> getEmployeesByJobId(Short jobId) {
        try {
            String url = baseUrl + "/employees/search/findByJobId?jobId=" + jobId;
            ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
            JsonNode embedded = response.getBody().path("_embedded").path("employees");
            List<EmployeeDto> list = new ArrayList<>();
            if (embedded.isArray()) {
                for (JsonNode node : embedded) {
                    list.add(objectMapper.treeToValue(node, EmployeeDto.class));
                }
            }
            return list;
        } catch (Exception e) {
            System.err.println("[EmployeeService] getEmployeesByJobId failed: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Returns null on success, error message string on failure.
     */
    public String createEmployee(EmployeeDto employee) {
        try {
            String url = baseUrl + "/employees";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<EmployeeDto> request = new HttpEntity<>(employee, headers);
            System.out.println("[EmployeeService] POST " + url + " -> " + objectMapper.writeValueAsString(employee));
            ResponseEntity<EmployeeDto> response = restTemplate.postForEntity(url, request, EmployeeDto.class);
            System.out.println("[EmployeeService] createEmployee response status: " + response.getStatusCode());
            return null;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.err.println("[EmployeeService] createEmployee HTTP error: " + e.getStatusCode() + " body: " + e.getResponseBodyAsString());
            return "Backend error " + e.getStatusCode() + ": " + e.getResponseBodyAsString();
        } catch (Exception e) {
            System.err.println("[EmployeeService] createEmployee exception: " + e.getMessage());
            return "Connection error: " + e.getMessage();
        }
    }

    /**
     * Returns null on success, error message string on failure.
     */
    public String updateEmployee(String empId, EmployeeDto employee) {
        try {
            String url = baseUrl + "/employees/" + empId;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<EmployeeDto> request = new HttpEntity<>(employee, headers);
            System.out.println("[EmployeeService] PUT " + url + " -> " + objectMapper.writeValueAsString(employee));
            ResponseEntity<EmployeeDto> response = restTemplate.exchange(url, HttpMethod.PUT, request, EmployeeDto.class);
            System.out.println("[EmployeeService] updateEmployee response status: " + response.getStatusCode());
            return null;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.err.println("[EmployeeService] updateEmployee HTTP error: " + e.getStatusCode() + " body: " + e.getResponseBodyAsString());
            return "Backend error " + e.getStatusCode() + ": " + e.getResponseBodyAsString();
        } catch (Exception e) {
            System.err.println("[EmployeeService] updateEmployee exception: " + e.getMessage());
            return "Connection error: " + e.getMessage();
        }
    }
}
