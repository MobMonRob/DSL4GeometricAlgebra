package de.orat.math.graalvmlsp;

import java.util.concurrent.ExecutionException;
import org.graalvm.tools.lsp.exceptions.DiagnosticsNotification;

public class _Util {

	public static RuntimeException umwrapException(ExecutionException ex) {
		StringBuilder sb = new StringBuilder();
		var diag = ((DiagnosticsNotification) ex.getCause()).getDiagnosticParamsCollection();
		// Damit bekommt man die Message aus ValidationException und LanguageRuntimeException.
		diag.forEach(par -> par.getDiagnostics().forEach(d -> sb.append(d.getMessage())));
		String msg = sb.toString();
		return new RuntimeException(msg, ex);
	}

}
