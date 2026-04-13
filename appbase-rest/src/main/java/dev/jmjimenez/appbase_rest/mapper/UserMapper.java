package dev.jmjimenez.appbase_rest.mapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import dev.jmjimenez.appbase_rest.dto.LoggedUserDTO;
import dev.jmjimenez.appbase_rest.dto.UserDTO;
import dev.jmjimenez.appbase_rest.dto.request.user.UpdateUserRequestDTO;
import dev.jmjimenez.appbase_rest.entity.Role;
import dev.jmjimenez.appbase_rest.entity.User;
import dev.jmjimenez.appbase_rest.entity.enums.RoleName;

@Mapper(componentModel = "spring")
public interface UserMapper {

	@Mapping(target = "roles", source = "roles", qualifiedByName = "mapRoles")
	LoggedUserDTO toLoggedUserDto(User user);

	@Mapping(target = "roles", source = "roles", qualifiedByName = "mapRoles")
	UserDTO toUserDTO(User user);

	@Mapping(target = "roles", source = "roles", qualifiedByName = "mapRolesToEntity")
	@Mapping(target = "password", ignore = true)
	User toUserEntity(UserDTO userDTO);

	@Named("mapRoles")
	default List<String> mapRoles(Set<Role> roles) {
		return roles.stream().map(role -> role.getName().name()).collect(Collectors.toList());
	}

	@Named("mapRolesToEntity")
	default Set<Role> mapRolesToEntity(List<String> roles) {
		if (roles == null)
			return new HashSet<>();

		return roles.stream().map(name -> Role.builder().name(RoleName.valueOf(name)).build())
				.collect(Collectors.toSet());
	}

	default void updateUserFromDto(UpdateUserRequestDTO dto, User user) {
		if (dto.getName() != null)
			user.setName(dto.getName());
		if (dto.getSurnames() != null)
			user.setSurnames(dto.getSurnames());
		if (dto.getPhone() != null)
			user.setPhone(dto.getPhone());
	}
}
