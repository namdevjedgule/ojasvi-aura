package com.ojasvi.ecommerce.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ojasvi.ecommerce.Entity.Address;
import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Repository.AddressRepository;

@Service
public class AddressService {

	@Autowired
	private AddressRepository addressRepository;

	public List<Address> getUserAddresses(Long userId) {
		return addressRepository.findByUserIdOrderByDefaultAddressDescCreatedAtDesc(userId);
	}

	private void clearDefault(User user) {

		List<Address> addresses = addressRepository.findByUserId(user.getId());

		for (Address addr : addresses) {
			addr.setDefaultAddress(false);
		}

		addressRepository.saveAll(addresses);
	}

	@Transactional
	public Address saveAddress(Address address, User user) {

		address.setUser(user);

		if (Boolean.TRUE.equals(address.getDefaultAddress())) {
			clearDefault(user);
		}

		List<Address> list = addressRepository.findByUserId(user.getId());

		if (list.size() >= 5) {

			throw new RuntimeException("Maximum 5 addresses allowed");
		}

		if (list.isEmpty()) {
			address.setDefaultAddress(true);
		}

		if (Boolean.TRUE.equals(address.getDefaultAddress())) {

			list.forEach(a -> a.setDefaultAddress(false));

			addressRepository.saveAll(list);
		}

		return addressRepository.save(address);
	}
	
	@Transactional
	public Address saveAddressForOrder(Address address, User user){

	    address.setUser(user);
	    address.setDefaultAddress(false);

	    return addressRepository.save(address);

	}

	@Transactional
	public Address update(Address address, User user) {

		Address existing = addressRepository.findById(address.getId())
				.orElseThrow(() -> new RuntimeException("Address not found"));

		if (!existing.getUser().getId().equals(user.getId())) {
			throw new RuntimeException("Unauthorized");
		}

		// STEP 1: If this is being set as default
		if (Boolean.TRUE.equals(address.getDefaultAddress())) {

			List<Address> addresses = addressRepository.findByUserId(user.getId());

			for (Address addr : addresses) {
				addr.setDefaultAddress(false);
			}

			addressRepository.saveAll(addresses);
		}

		// STEP 2: update fields
		existing.setAddressLine1(address.getAddressLine1());
		existing.setAddressLine2(address.getAddressLine2());
		existing.setCity(address.getCity());
		existing.setState(address.getState());
		existing.setCountry(address.getCountry());
		existing.setPincode(address.getPincode());
		existing.setLandmark(address.getLandmark());
		existing.setAddressType(address.getAddressType());
		existing.setDefaultAddress(address.getDefaultAddress());

		return addressRepository.save(existing);
	}

	@Transactional
	public void setDefaultAddress(Long id, User user) {

		List<Address> list = addressRepository.findByUserId(user.getId());

		for (Address a : list) {
			a.setDefaultAddress(false);
		}

		addressRepository.saveAll(list);

		Address selected = addressRepository.findById(id).orElseThrow(() -> new RuntimeException("Address not found"));

		if (!selected.getUser().getId().equals(user.getId())) {
			throw new RuntimeException("Unauthorized");
		}

		selected.setDefaultAddress(true);

		addressRepository.save(selected);
	}

	public Address getById(Long id) {
		return addressRepository.findById(id).orElseThrow(() -> new RuntimeException("Address not found"));
	}

	@Transactional
	public void deleteAddress(Long addressId, User user) {

		Address address = addressRepository.findById(addressId)
				.orElseThrow(() -> new RuntimeException("Address not found"));

		if (!address.getUser().getId().equals(user.getId())) {

			throw new RuntimeException("Unauthorized");
		}

		List<Address> addresses = addressRepository.findByUserId(user.getId());

		if (addresses.size() == 1) {

			throw new RuntimeException("You cannot delete your only address");
		}

		boolean wasDefault = Boolean.TRUE.equals(address.getDefaultAddress());

		addressRepository.delete(address);

		if (wasDefault) {

			Address newDefault = addresses.stream().filter(a -> !a.getId().equals(addressId)).findFirst().orElse(null);

			if (newDefault != null) {

				newDefault.setDefaultAddress(true);

				addressRepository.save(newDefault);
			}
		}
	}
}
