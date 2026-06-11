package com.ojasvi.ecommerce.Security;

import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Util.RoleConstants;

public class AccessValidator {

    private AccessValidator() {
    }

    public static boolean isAdmin(User user) {

    	return user != null
    		       && user.getRole() != null
    		       && RoleConstants.ADMIN_ID.equals(user.getRole().getId());
    }

    public static boolean isCustomer(User user) {

    	return user != null
    		       && user.getRole() != null
    		       && RoleConstants.CUSTOMER_ID.equals(user.getRole().getId());
    }
}