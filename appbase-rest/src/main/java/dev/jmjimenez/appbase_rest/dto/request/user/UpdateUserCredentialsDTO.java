package dev.jmjimenez.appbase_rest.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserCredentialsDTO {
	
    private String email;
    private String oldPassword;
    private String newPassword;
}
