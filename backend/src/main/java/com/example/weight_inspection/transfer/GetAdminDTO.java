package com.example.weight_inspection.transfer;

import com.example.weight_inspection.models.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class GetAdminDTO {
    private Long id;

    private String firstName;

    private String lastName;

    private String username;

    private String password;

    private Email email;

    private Long emailId;
}
