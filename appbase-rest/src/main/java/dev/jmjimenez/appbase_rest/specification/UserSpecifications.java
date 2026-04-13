package dev.jmjimenez.appbase_rest.specification;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.data.jpa.domain.Specification;

import dev.jmjimenez.appbase_rest.entity.User;

public class UserSpecifications {

	private UserSpecifications() {
		/* This utility class should not be instantiated */
	}

	public static Specification<User> hasEmail(String email) {
		return (root, query, cb) -> email == null ? null
				: cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
	}

	public static Specification<User> hasName(String name) {
		return (root, query, cb) -> name == null ? null
				: cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
	}

	public static Specification<User> hasSurname(String surname) {
		return (root, query, cb) -> surname == null ? null
				: cb.like(cb.lower(root.get("surnames")), "%" + surname.toLowerCase() + "%");
	}

	public static Specification<User> hasPhone(String phone) {
		return (root, query, cb) -> phone == null ? null
				: cb.like(cb.lower(root.get("phone")), "%" + phone.toLowerCase() + "%");
	}

	public static Specification<User> hasRole(String roleName) {
		return (root, query, cb) -> {
			if (roleName == null)
				return null;
			query.distinct(true); // evitar duplicados al hacer join
			return cb.equal(root.join("roles").get("name"), roleName);
		};
	}

	public static Specification<User> isEmailVerified(Boolean verified) {
		return (root, query, cb) -> {
			if (verified == null)
				return null;
			return cb.equal(root.get("emailVerified"), verified);
		};
	}

	public static Specification<User> isEnabled(Boolean enabled) {
		return (root, query, cb) -> {
			if (enabled == null)
				return null;
			return cb.equal(root.get("enabled"), enabled);
		};
	}

	public static Specification<User> globalSearch(String keyword) {
		return (root, query, cb) -> {
			if (keyword == null || keyword.isBlank())
				return null;
			String pattern = "%" + keyword.toLowerCase() + "%";
			return cb.or(cb.like(cb.lower(root.get("name")), pattern), cb.like(cb.lower(root.get("surnames")), pattern),
					cb.like(cb.lower(root.get("email")), pattern));
		};
	}

	public static Specification<User> hasStartDate(Date startDate) {
		return (root, query, cb) -> {
			if (startDate == null)
				return null;
			LocalDateTime startOfDay = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
					.atStartOfDay();
			return cb.greaterThanOrEqualTo(root.get("createdAt"), startOfDay);
		};
	}

	public static Specification<User> hasEndDate(Date endDate) {
		return (root, query, cb) -> {
			if (endDate == null)
				return null;
			LocalDateTime endOfDay = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atTime(23, 59,
					59);
			return cb.lessThanOrEqualTo(root.get("createdAt"), endOfDay);
		};
	}
}
