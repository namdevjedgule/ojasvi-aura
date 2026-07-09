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
	private ShippingConfigRepository shippingConfigRepository;

	@Autowired
	private ShippingZoneRepository shippingZoneRepository;

	public ShippingConfig getConfig() {

		return shippingConfigRepository.findAll().stream().findFirst().orElseGet(() -> {

			ShippingConfig config = new ShippingConfig();

			return shippingConfigRepository.save(config);
		});
	}

	public ShippingConfig saveConfig(ShippingConfig config) {

		return shippingConfigRepository.save(config);
	}

	public List<ShippingZone> getAllZones() {

		return shippingZoneRepository.findAll();
	}

	public ShippingZone saveZone(ShippingZone zone) {

		return shippingZoneRepository.save(zone);
	}

	public ShippingZone getZone(Long id) {

		return shippingZoneRepository.findById(id).orElse(null);
	}

	public void deleteZone(Long id) {

		shippingZoneRepository.deleteById(id);
	}

	public Double calculateShipping(
	        String city,
	        String state,
	        String country,
	        String shippingMethod,
	        Double orderAmount) {

	    ShippingConfig config = getConfig();

	    if ("standard".equals(shippingMethod)) {

	        if (!config.getStandardEnabled()) {
	            return null;
	        }

	        if (orderAmount >= config.getStandardFreeAbove()) {
	            return 0.0;
	        }
	    }

	    if ("express".equals(shippingMethod)) {

	        if (!config.getExpressEnabled()) {
	            return null;
	        }

	        if (orderAmount >= config.getExpressFreeAbove()) {
	            return 0.0;
	        }
	    }

	    ShippingZone zone = null;

	    if (city != null &&
	        city.equalsIgnoreCase("Pune")) {

	        zone = shippingZoneRepository
	                .findByZoneNameIgnoreCase("Pune")
	                .orElse(null);
	    }
	    else if (state != null &&
	             state.equalsIgnoreCase("Maharashtra")) {

	        zone = shippingZoneRepository
	                .findByZoneNameIgnoreCase("Maharashtra")
	                .orElse(null);
	    }
	    else if (country != null &&
	             country.equalsIgnoreCase("India")) {

	        zone = shippingZoneRepository
	                .findByZoneNameIgnoreCase("Rest of India")
	                .orElse(null);
	    }
	    else {

	        zone = shippingZoneRepository
	                .findByZoneNameIgnoreCase("International")
	                .orElse(null);
	    }

	    if (zone == null) {

	        if ("express".equals(shippingMethod)) {
	            return config.getExpressCharge();
	        }

	        return config.getStandardCharge();
	    }

	    if ("express".equals(shippingMethod)) {
	        return zone.getExpressCharge();
	    }

	    return zone.getStandardCharge();
	}
}
