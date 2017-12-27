package pg.exception;

import java.util.HashMap;
import java.util.Map;

/**Created by Gawa 2017-10-07*/
public class ProgramException extends RuntimeException {
    private final UIError uiError;
    private Map<UIError, String> arguments = new HashMap<>();

    public ProgramException(UIError uiError) {
        super();
        this.uiError = uiError;
    }

    public ProgramException(UIError uiError, Exception exception) {
        super(exception);
        this.uiError = uiError;
    }

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

        if (!arguments.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            for(UIError error : arguments.keySet()) {
                stringBuilder.append(error).append(" - ").append(arguments.get(error)).append(", ");
            }
            String arg = stringBuilder.substring(0, stringBuilder.lastIndexOf(", "));
            return String.format("%s %s %s", uiError.msg(), arg, super.getMessage());
        }
        return String.format("%s %s", uiError.msg(), super.getMessage());
    }
}
