package dev.jmjimenez.appbase_rest.dto.request.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequestDTO {

	@Size(max = 50)
    private String name;
	
	@Size(max = 100)
    private String surnames;
	
	@Pattern(regexp = "^\\d{9,15}$")
    private String phone;
}
