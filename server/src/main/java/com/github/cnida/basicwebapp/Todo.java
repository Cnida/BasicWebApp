package com.github.cnida.basicwebapp;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Date;

@Entity
@Data
@NoArgsConstructor
public class Todo {

    @Id @GeneratedValue
    private Integer id;

    @NonNull
    private String title;

    private Boolean completed = false;

    private Date date;
}
