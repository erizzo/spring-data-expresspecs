package expresspecs.datetime;

import lombok.Getter;

public class UnsupportedDatePropertyException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	@Getter
	private Class<?> type;


	public UnsupportedDatePropertyException(Class<?> type, String message) {
		super(message);
		this.type = type;
	}

}
