package com.skillswap;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;
    private String skillToTeach;
    private String skillToLearn;

    public User() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSkillToTeach() {
        return skillToTeach;
    }

    public void setSkillToTeach(String skillToTeach) {
        this.skillToTeach = skillToTeach;
    }

    public String getSkillToLearn() {
        return skillToLearn;
    }

    public void setSkillToLearn(String skillToLearn) {
        this.skillToLearn = skillToLearn;
    }
}