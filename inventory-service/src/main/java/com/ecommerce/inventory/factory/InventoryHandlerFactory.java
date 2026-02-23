package com.ecommerce.inventory.factory;

import org.springframework.stereotype.Component;

@Component
public class InventoryHandlerFactory {

    // For now return default handler. In future can return different implementations based on strategy.
    public InventoryHandler getHandler(String type) {
        return new DefaultInventoryHandler();
    }
}

