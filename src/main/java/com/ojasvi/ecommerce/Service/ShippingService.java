package com.ojasvi.ecommerce.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ojasvi.ecommerce.Entity.ShippingConfig;
import com.ojasvi.ecommerce.Entity.ShippingZone;
import com.ojasvi.ecommerce.Repository.ShippingConfigRepository;
import com.ojasvi.ecommerce.Repository.ShippingZoneRepository;

@Service
public class ShippingService {

    @Autowired
    private ShippingConfigRepository configRepo;

    @Autowired
    private ShippingZoneRepository zoneRepo;

    public ShippingConfig getConfig() {

        return configRepo.findAll()
                .stream()
                .findFirst()
                .orElseGet(() -> {

                    ShippingConfig config =
                            new ShippingConfig();

                    return configRepo.save(config);
                });
    }

    public ShippingConfig saveConfig(
            ShippingConfig config) {

        return configRepo.save(config);
    }

    public List<ShippingZone> getAllZones() {

        return zoneRepo.findAll();
    }

    public ShippingZone saveZone(
            ShippingZone zone) {

        return zoneRepo.save(zone);
    }

    public ShippingZone getZone(Long id) {

        return zoneRepo.findById(id)
                .orElse(null);
    }

    public void deleteZone(Long id) {

        zoneRepo.deleteById(id);
    }
}
