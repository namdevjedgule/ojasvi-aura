package com.ojasvi.ecommerce.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ojasvi.ecommerce.Entity.Address;
import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Security.AccessValidator;
import com.ojasvi.ecommerce.Security.SessionUtil;
import com.ojasvi.ecommerce.Service.AddressService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/customer/addresses")
public class CustomerAddressController {

	@Autowired
	private AddressService addressService;

	// ✅ 1. LOAD PAGE + ADDRESSES
	@GetMapping
	public String customerAddresses(HttpSession session, Model model) {

		User user = (User) session.getAttribute("user");

		if (user == null)
			return "redirect:/login";

		if (!AccessValidator.isCustomer(user)) {
			return "redirect:/admin-dashboard";
		}

		model.addAttribute("user", user);
		model.addAttribute("addresses", addressService.getUserAddresses(user.getId()));

		return "customer/customer-addresses";
	}

	// ✅ 2. SAVE ADDRESS
	@PostMapping("/save")
	@ResponseBody
	public Map<String, Object> saveAddress(@RequestBody Address address, HttpSession session) {

		Map<String, Object> res = new HashMap<>();

		User user = SessionUtil.getLoggedInUser(session);

		if (user == null) {
			res.put("success", false);
			res.put("message", "Please login");
			return res;
		}

		try {
			addressService.saveAddress(address, user);

			res.put("success", true);
			res.put("message", "Address saved successfully");

		} catch (Exception e) {
			res.put("success", false);
			res.put("message", e.getMessage());
		}

		return res;
	}

	// ✅ 3. UPDATE ADDRESS
	@PostMapping("/update")
	@ResponseBody
	public Map<String, Object> updateAddress(@RequestBody Address address, HttpSession session) {

		Map<String, Object> res = new HashMap<>();

		try {

			User user = SessionUtil.getLoggedInUser(session);

			if (user == null) {
				res.put("success", false);
				res.put("message", "Please login");
				return res;
			}

			Address updated = addressService.update(address, user);

			res.put("success", true);
			res.put("message", "Address updated successfully");
			res.put("data", updated);

		} catch (Exception e) {
			res.put("success", false);
			res.put("message", e.getMessage());
		}

		return res;
	}

	@PostMapping("/set-default/{id}")
	@ResponseBody
	public Map<String, Object> setDefault(@PathVariable Long id, HttpSession session) {

		Map<String, Object> res = new HashMap<>();

		try {
			User user = SessionUtil.getLoggedInUser(session);

			if (user == null) {
				res.put("success", false);
				res.put("message", "Please login");
				return res;
			}

			addressService.setDefaultAddress(id, user);

			res.put("success", true);
			res.put("message", "Default address updated");

		} catch (Exception e) {
			res.put("success", false);
			res.put("message", e.getMessage());
		}

		return res;
	}

	@PostMapping("/delete/{id}")
	@ResponseBody
	public Map<String, Object> delete(@PathVariable Long id, HttpSession session) {
		Map<String, Object> response = new HashMap<>();

		try {
			User user = SessionUtil.getLoggedInUser(session);
			if (user == null) {
				response.put("success", false);
				response.put("message", "Please login first");
				return response;
			}

			addressService.deleteAddress(id, user);
			response.put("success", true);
			response.put("message", "Address removed successfully");

		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("message", e.getMessage());

		} catch (Exception e) {

			e.printStackTrace();
			response.put("success", false);
			response.put("message", "Something went wrong");

		}
		return response;
	}
}
