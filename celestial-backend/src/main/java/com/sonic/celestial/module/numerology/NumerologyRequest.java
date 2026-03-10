package com.sonic.celestial.module.numerology;

import jakarta.validation.constraints.NotBlank;

public class NumerologyRequest {

    @NotBlank
    private String birthDate; // format: dd/MM/yyyy

    private String fullName; // optional

    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
}