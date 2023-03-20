package it.finanze.sanita.fse2.ms.edssrvdataprocessor.config;

import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.context.annotation.Configuration;

@Configuration
public class FhirAdvicesCFG {
	
	public static final Pattern HAPI_CODE_EXTRACTOR = Pattern.compile("HAPI-\\d+");
	
	private static final String PROPS_FILENAME = "fhir-advices";

	private final ResourceBundle source;
	
	public FhirAdvicesCFG() {
		this.source = ResourceBundle.getBundle(PROPS_FILENAME);
	}
	
	public Optional<String> get(String key) {
		return source.containsKey(key) ? Optional.of(source.getString(key)) : Optional.empty();
	}
	
	public Optional<String> exists(String message) {
		Optional<String> value = Optional.empty();
		Matcher matcher = HAPI_CODE_EXTRACTOR.matcher(message);
		if(matcher.find()) {
			value = Optional.of(matcher.group());
		}
		return value;
	}
	
	public Function<Exception, String> map() {
		return e -> {
			String extra = null;
			Optional<String> code = exists(e.getMessage());
			if(code.isPresent()) {
				Optional<String> advice = get(code.get());
				if(advice.isPresent()) {
					extra = advice.get();
				}
			}
			return extra;
		};
	}
}
