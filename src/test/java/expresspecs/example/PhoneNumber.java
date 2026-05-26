package expresspecs.example;

import jakarta.persistence.Column;
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

	@Column(name = "phone_area_code")
	private String areaCode;

	@Column(name = "phone_number")
	private String number;

	@Column(name = "phone_extension")
	private String extension;
}
