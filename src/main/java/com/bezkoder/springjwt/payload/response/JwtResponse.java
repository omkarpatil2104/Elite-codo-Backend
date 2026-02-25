package com.bezkoder.springjwt.payload.response;

import java.util.List;

public class JwtResponse {
  private String token;
  private String type = "Bearer";
  private Long id;
  private String username;
  private String email;
  private List<String> roles;

  private String firstName;
  private String lastName;

  private String profilePicture;

  private String logoImage;
  private String colorTheme;
  private String watermarkImage;

  private String className;

  public JwtResponse(String accessToken, Long id, String username, String email, String firstName, String lastName, String profilePicture, String watermarkImage, String logoImage, String colorTheme, List<String> roles, String className) {
    this.token = accessToken;
    this.id = id;
    this.username = username;
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.profilePicture = profilePicture;
    this.watermarkImage = watermarkImage;
    this.logoImage = logoImage;
    this.colorTheme=colorTheme;
    this.roles = roles;
    this.className = className;
  }

  public String getAccessToken() {
    return token;
  }

  public void setAccessToken(String accessToken) {
    this.token = accessToken;
  }

  public String getTokenType() {
    return type;
  }

  public void setTokenType(String tokenType) {
    this.type = tokenType;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public List<String> getRoles() {
    return roles;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getProfilePicture() {
    return profilePicture;
  }

  public void setProfilePicture(String profilePicture) {
    this.profilePicture = profilePicture;
  }

  public String getLogoImage() {
    return logoImage;
  }

  public void setLogoImage(String logoImage) {
    this.logoImage = logoImage;
  }

  public String getWatermarkImage() {
    return watermarkImage;
  }

  public String getColorTheme() {
    return colorTheme;
  }

  public void setColorTheme(String colorTheme) {
    this.colorTheme = colorTheme;
  }

  public void setWatermarkImage(String watermarkImage) {
    this.watermarkImage = watermarkImage;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }
}
