package com.task.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.library.config.SecurityConfig;
import com.task.library.entity.Patron;
import com.task.library.service.PatronService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for PatronController
 */
@WebMvcTest(PatronController.class)
@ExtendWith(SpringExtension.class)
@Import(SecurityConfig.class) // Import security configuration
public class PatronControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatronService patronService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Test for GET /api/patrons
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetAllPatrons() throws Exception {
        Patron patron1 = new Patron(1L, "Patron One", "patron1@example.com", null);
        Patron patron2 = new Patron(2L, "Patron Two", "patron2@example.com", null);

        when(patronService.getAllPatrons()).thenReturn(Arrays.asList(patron1, patron2));

        mockMvc.perform(get("/api/patrons")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(patron1.getId()))
                .andExpect(jsonPath("$[0].name").value(patron1.getName()))
                .andExpect(jsonPath("$[1].id").value(patron2.getId()))
                .andExpect(jsonPath("$[1].name").value(patron2.getName()));

        verify(patronService, times(1)).getAllPatrons();
    }

    /**
     * Test for GET /api/patrons/{id}
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetPatronById() throws Exception {
        Long patronId = 1L;
        Patron patron = new Patron(patronId, "Patron One", "patron1@example.com", null);

        when(patronService.getPatronById(patronId)).thenReturn(patron);

        mockMvc.perform(get("/api/patrons/{id}", patronId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(patron.getId()))
                .andExpect(jsonPath("$.name").value(patron.getName()));

        verify(patronService, times(1)).getPatronById(patronId);
    }

    /**
     * Test for POST /api/patrons
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testCreatePatron() throws Exception {
        Patron patron = new Patron(null, "New Patron", "newpatron@example.com", null);
        Patron savedPatron = new Patron(1L, "New Patron", "newpatron@example.com", null);

        when(patronService.createPatron(any(Patron.class))).thenReturn(savedPatron);

        mockMvc.perform(post("/api/patrons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patron)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(savedPatron.getId()))
                .andExpect(jsonPath("$.name").value(savedPatron.getName()));

        verify(patronService, times(1)).createPatron(any(Patron.class));
    }

    /**
     * Test for PUT /api/patrons/{id}
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testUpdatePatron() throws Exception {
        Long patronId = 1L;
        Patron patronDetails = new Patron(null, "Updated Patron", "updated@example.com", null);
        Patron updatedPatron = new Patron(patronId, "Updated Patron", "updated@example.com", null);

        when(patronService.updatePatron(eq(patronId), any(Patron.class))).thenReturn(updatedPatron);

        mockMvc.perform(put("/api/patrons/{id}", patronId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patronDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedPatron.getId()))
                .andExpect(jsonPath("$.name").value(updatedPatron.getName()));

        verify(patronService, times(1)).updatePatron(eq(patronId), any(Patron.class));
    }

    /**
     * Test for DELETE /api/patrons/{id}
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testDeletePatron() throws Exception {
        Long patronId = 1L;

        doNothing().when(patronService).deletePatron(patronId);

        mockMvc.perform(delete("/api/patrons/{id}", patronId))
                .andExpect(status().isOk());

        verify(patronService, times(1)).deletePatron(patronId);
    }
}
