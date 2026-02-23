package com.ecommerce.inventory.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class InventoryHandlerFactory {

    private final Map<String, InventoryHandler> handlers;

    @Autowired
    public InventoryHandlerFactory(Map<String, InventoryHandler> handlers) {
        this.handlers = handlers;
    }

    // Return handler by type/name; fallback to "defaultInventoryHandler"
    public InventoryHandler getHandler(String type) {
        if (type != null && handlers.containsKey(type)) return handlers.get(type);
        if (handlers.containsKey("defaultInventoryHandler")) return handlers.get("defaultInventoryHandler");
        // last resort, return any handler
        return handlers.values().stream().findFirst().orElseThrow(() -> new IllegalStateException("No InventoryHandler beans available"));
    }
}
