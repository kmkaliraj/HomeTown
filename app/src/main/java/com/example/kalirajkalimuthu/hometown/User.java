package com.example.kalirajkalimuthu.hometown;

/**
 * Created by kalirajkalimuthu on 3/16/17.
 */

public class User{



    private Integer userId;
    private String nickname;
    private String country;
    private String state;
    private String city;
    private String year;
    private Double latitude,longitude;

    public User(){

    }

    public User(Integer userId,String name, String country, String state, String city, String year, Double latitude, Double longitude){
        this.userId = userId;
        this.nickname = name;
        this.country = country;
        this.state = state;
        this.city = city;
        this.year = year;
        this.latitude= latitude;
        this.longitude = longitude;
    }

    public User(Integer userId, String name, String country, String state, String city, String year){
        this.userId = userId;
        this.nickname = name;
        this.country = country;
        this.state = state;
        this.city = city;
        this.year = year;

    }


    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }


}
