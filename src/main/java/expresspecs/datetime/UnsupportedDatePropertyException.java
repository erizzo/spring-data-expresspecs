package expresspecs.datetime;

import expresspecs.PropertyPath;
import jakarta.persistence.criteria.Root;
import lombok.Getter;

public class UnsupportedDatePropertyException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	@Getter
	private Class<?> type;


	public UnsupportedDatePropertyException(PropertyPath path, Class<?> type, Root<?> root) {
		super("Property %s.%s is type %s which is not supported"
				.formatted(root.getJavaType().getSimpleName(),
							path,
							type == null ? "(unknown)" : type.getName()
						));
		this.type = type;
	}

}
