package com.Luxa.inventory.service;

import com.Luxa.inventory.model.Product;
import org.springframework.stereotype.Service;

@Service
public class RestockAdvisorService {

    public String getAdvice(Product p) {
        Double velocity = p.getSalesVelocity();
        if (velocity == null) velocity = 0.0;
        
        if (p.getQuantity() <= p.getMinThreshold()) {
            return "Critical: Restock immediately. Sales velocity is " + velocity;
        }
        return "Stock levels are currently stable.";
    }
}
