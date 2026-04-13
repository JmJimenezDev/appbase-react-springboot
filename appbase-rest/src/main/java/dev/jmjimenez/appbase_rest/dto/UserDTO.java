package dev.jmjimenez.appbase_rest.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String name;
    private String surnames;
    private String email;
    private String phone;
    private boolean enabled;
    private boolean emailVerified;
    private List<String> roles;
    private LocalDateTime createdAt;
}
