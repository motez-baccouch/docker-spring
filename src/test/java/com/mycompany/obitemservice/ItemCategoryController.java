package com.mycompany.obitemservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.obitemservice.model.ItemCategoryModel;
import com.mycompany.obitemservice.repository.ItemCategoryRepository;
import com.mycompany.obitemservice.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.net.URI;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ItemCategoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ItemCategoryRepository itemCategoryRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemCategoryController itemCategoryController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(itemCategoryController).build();
    }

    @Test
    public void testGetAllItemCategories() throws Exception {
        when(itemCategoryRepository.findAll()).thenReturn(Arrays.asList(new ItemCategoryModel(), new ItemCategoryModel()));

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(itemCategoryRepository, times(1)).findAll();
        verifyNoMoreInteractions(itemCategoryRepository);
    }

    @Test
    public void testGetItemCategory() throws Exception {
        String categoryId = "123";
        ItemCategoryModel itemCategory = new ItemCategoryModel();
        itemCategory.setId(categoryId);

        when(itemCategoryRepository.findById(categoryId)).thenReturn(Optional.of(itemCategory));

        mockMvc.perform(get("/api/v1/categories/{id}", categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryId));

        verify(itemCategoryRepository, times(1)).findById(categoryId);
        verifyNoMoreInteractions(itemCategoryRepository);
    }

    @Test
    public void testSaveItemCategory() throws Exception {
        ItemCategoryModel itemCategory = new ItemCategoryModel();
        itemCategory.setId("123");

        when(itemCategoryRepository.insert(any(ItemCategoryModel.class))).thenReturn(itemCategory);

        mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(itemCategory)))
                .andExpect(status().isCreated());

        verify(itemCategoryRepository, times(1)).insert(any(ItemCategoryModel.class));
        verifyNoMoreInteractions(itemCategoryRepository);
    }

    @Test
    public void testUpdateItemCategory() throws Exception {
        String categoryId = "123";
        ItemCategoryModel itemCategory = new ItemCategoryModel();
        itemCategory.setId(categoryId);

        when(itemCategoryRepository.findById(categoryId)).thenReturn(Optional.of(itemCategory));
        when(itemCategoryRepository.save(any(ItemCategoryModel.class))).thenReturn(itemCategory);

        mockMvc.perform(put("/api/v1/categories/{id}", categoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(itemCategory)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryId));

        verify(itemCategoryRepository, times(1)).findById(categoryId);
        verify(itemCategoryRepository, times(1)).save(any(ItemCategoryModel.class));
        verifyNoMoreInteractions(itemCategoryRepository);
    }

    @Test
    public void testDeleteItemCategory() throws Exception {
        String categoryId = "123";

        mockMvc.perform(delete("/api/v1/categories/{id}", categoryId))
                .andExpect(status().isOk())
                .andExpect(content().string(categoryId));

        verify(itemRepository, times(1)).deleteAllByCategoryId(categoryId);
        verify(itemCategoryRepository, times(1)).deleteById(categoryId);
        verifyNoMoreInteractions(itemRepository, itemCategoryRepository);
    }
}
