package com.ojasvi.ecommerce.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ojasvi.ecommerce.Entity.StoreSettings;
import com.ojasvi.ecommerce.Repository.StoreSettingsRepository;

@Service
public class StoreSettingsService {

    @Autowired
    private StoreSettingsRepository repository;

    public StoreSettings getSettings() {

        Optional<StoreSettings> settings = repository.findById(1L);

        if (settings.isPresent()) {
            return settings.get();
        }

        StoreSettings defaultSettings = new StoreSettings();
        return repository.save(defaultSettings);
    }

    public StoreSettings save(StoreSettings settings) {
        settings.setId(1L);
        return repository.save(settings);
    }
}
