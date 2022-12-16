package com.services;

import com.models.CartItem;
import com.models.Category;
import com.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public void saveCategory(Category category){
        categoryRepository.save(category);
    }
    public Optional<Category> getCategoryById(Long id){
        return categoryRepository.findById(id);
    }
    public void deleteCategory(Long id){
        categoryRepository.deleteById(id);
    }
    public List<Category> getAllCategories(){
        if(categoryRepository != null){
            return categoryRepository.findAll();
        }
        return null;
    }
    public Optional<Category> findById(Long id){
        return categoryRepository.findById(id);
    }
}
