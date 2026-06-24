package com.ojasvi.ecommerce.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ojasvi.ecommerce.Entity.Address;
import com.ojasvi.ecommerce.Repository.AddressRepository;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    public List<Address> getUserAddresses(Long userId) {
        return addressRepository
                .findByUserIdOrderByDefaultAddressDescCreatedAtDesc(userId);
    }

    public Address save(Address address) {
        return addressRepository.save(address);
    }

    public Address getById(Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));
    }

    public void delete(Long id) {
        addressRepository.deleteById(id);
    }
}
