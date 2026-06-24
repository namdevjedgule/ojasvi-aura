package com.ojasvi.ecommerce.Util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Service.WishlistService;

@Component
public class WishlistHelper {

    @Autowired
    private WishlistService wishlistService;

    public List<Long> getWishlistIds(User user) {

        if (user == null) {
            return List.of();
        }

        return wishlistService.getWishlistProductIdsByUser(user.getId());
    }
}
