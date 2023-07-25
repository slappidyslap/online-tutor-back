package kg.musabaev.onlinetutorback.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "base_classes")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "class_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"specialistId", "title"})
public class BaseClass {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	@Column(nullable = false)
	String title;
	@Column(nullable = false, length = 500)
	@Builder.Default
	String description = "";
	Long specialistId; // FIXME
	@Builder.Default
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "classes_categories",
			joinColumns = @JoinColumn(name = "class_id"),
			inverseJoinColumns = @JoinColumn(name = "category_id"))
	List<Category> categories = new ArrayList<>();
	@Column(nullable = false)
	Integer price;
	@Column(nullable = false, updatable = false)
	@Builder.Default
	LocalDate createdDate = LocalDate.now();
	@Column(nullable = false)
	String coverUrl;
}
