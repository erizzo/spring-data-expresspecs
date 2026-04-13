package rizzoweb.spring.jpa.test;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Embeddable
@Getter
@Setter
@ToString
@FieldNameConstants
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PhoneNumber {

	private int areaCode;
	private int number;
	private Integer extension;
}
