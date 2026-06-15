package expresspecs.datetime;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Entity
@Table(name = "socialites")
@Getter
@Setter
@ToString
@FieldNameConstants
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
/**
 * Socialite is an entity that has a lot if dates. Get it?
 */
public class Socialite {

	@Id
	@GeneratedValue
	@EqualsAndHashCode.Include
	private Long id;

	private LocalDate localDate;

	private LocalDateTime localDateTime;

	@SuppressWarnings("deprecation")
	@Temporal(TemporalType.TIMESTAMP)
	private Date javaUtilDateAsTimestamp;

	@SuppressWarnings("deprecation")
	@Temporal(TemporalType.DATE)
	private Date javaUtilDateAsDate;

	@SuppressWarnings("deprecation")
	@Temporal(TemporalType.DATE)
	private java.sql.Date sqlDate;

	private Timestamp sqlTimestamp;

	private Calendar javaUtilCalendar;

	private ZonedDateTime zonedDateTime;
}
