package com.mycompany.obitemservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.obitemservice.model.ItemModel;
import com.mycompany.obitemservice.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ItemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemController itemController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
    }

    @Test
    public void testGetAllItems() throws Exception {
        when(itemRepository.findAll()).thenReturn(Arrays.asList(new ItemModel(), new ItemModel()));

        mockMvc.perform(get("/api/v1/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(itemRepository, times(1)).findAll();
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void testGetItem() throws Exception {
        String itemId = "123";
        ItemModel item = new ItemModel();
        item.setId(itemId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        mockMvc.perform(get("/api/v1/items/{id}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId));

        verify(itemRepository, times(1)).findById(itemId);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void testSaveItem() throws Exception {
        ItemModel item = new ItemModel();
        item.setId("123");

        when(itemRepository.insert(any(ItemModel.class))).thenReturn(item);

        mockMvc.perform(post("/api/v1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(item)))
                .andExpect(status().isCreated());

        verify(itemRepository, times(1)).insert(any(ItemModel.class));
        verifyNoMoreInteractions(itemRepository);
    }

    // Similarly, you can write tests for updateItem, updateItemPrice, and deleteItem methods
}
