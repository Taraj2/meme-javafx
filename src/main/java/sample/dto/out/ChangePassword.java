package sample.dto.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChangePassword {
    private String oldPassword;
    private String newPassword;
}
