package me.gogradually.toycommerce.common.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public abstract class ToyCommerceException extends RuntimeException {

    private final String debugMessage;
    private final Map<String, String> debugContext;

    protected ToyCommerceException(String debugMessage) {
        this(debugMessage, Map.of());
    }

    protected ToyCommerceException(String debugMessage, Map<String, String> debugContext) {
        super(debugMessage);
        this.debugMessage = debugMessage == null ? "" : debugMessage;
        this.debugContext = debugContext == null ? Map.of() : Map.copyOf(debugContext);
    }

    @Override
    public String getMessage() {
        return debugMessage;
    }

    public boolean hasDebugContext() {
        return !debugContext.isEmpty();
    }
}
