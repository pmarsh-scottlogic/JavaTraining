package com.example.demo.security.userInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.AUTO;

@Entity // in JPA, an entity is any POJO representing data that can be persisted to a database
// An entity class represents a table, and each instance of the class is a new row in the table
@Data // lombok annotation that shortcuts @ToString @EqualsAndHashCode @Getter @Setter @RequiredArgsConstructor
@NoArgsConstructor @AllArgsConstructor // lombok stuff for boilerplate code
public class Role {
    @Id // defines the primary key for the table
    @GeneratedValue(strategy = AUTO)
    private Long id;
    private String name;
}
