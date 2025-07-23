package ru.yandex.intershop.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @Getter
    private Long id;
    @Getter
    private String username;
    @Getter
    private String password;
    private String roles;

    public String[] getRoles(){
        return roles.split(",");
    }
}
