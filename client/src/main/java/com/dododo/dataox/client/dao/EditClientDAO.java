package com.dododo.dataox.client.dao;

import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class EditClientDAO {

    @Nullable
    private String name;

    @Nullable
    private String email;

    @Nullable
    private String password;

    @Nullable
    private String address;

}
