package com.ojasvi.ecommerce.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

	@GetMapping
    public String customerAddresses(HttpSession session,
                                 Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        if (!AccessValidator.isCustomer(user)) {
            return "redirect:/admin-dashboard";
        }

        model.addAttribute("user", user);
        
        model.addAttribute(
                "addresses",
                addressService.getUserAddresses(user.getId()));

        model.addAttribute("address", new Address());

        return "customer/customer-addresses";
    }
	
	@PostMapping("/save")
	public String saveAddress(
	        @ModelAttribute Address address,
	        HttpSession session,
	        RedirectAttributes ra) {

	    User user = SessionUtil.getLoggedInUser(session);

	    if (user == null) {
	        return "redirect:/login";
	    }

	    address.setUser(user);

	    addressService.save(address);

	    ra.addFlashAttribute(
	            "success",
	            "Address saved successfully");

	    return "redirect:/customer/addresses";
	}
}
