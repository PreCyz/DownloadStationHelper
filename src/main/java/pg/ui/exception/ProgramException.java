package pg.ui.exception;

import java.util.HashMap;
import java.util.Map;

/**Created by Gawa 2017-10-07*/
public class ProgramException extends RuntimeException {
    private final UIError uiError;
    private Map<UIError, String> arguments = new HashMap<>();

    public ProgramException(UIError uiError, String arguments, Exception exception) {
        super(exception);
        this.uiError = uiError;
        this.arguments.put(uiError, arguments);
    }

    public UIError getUiError() {
        return uiError;
    }

    @Override
    public String getMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        arguments.forEach((key, value) -> stringBuilder.append(key).append(" - ").append(value).append(", "));
        return stringBuilder.substring(0, stringBuilder.lastIndexOf(", "));
    }
}
