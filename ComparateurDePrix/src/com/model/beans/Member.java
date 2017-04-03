package com.model.beans;

import java.sql.Timestamp;

public class Member {

    private Long      memberId;
    private String    email;
    private String    password;
    private String    pseudonym;
    private Timestamp registrationDate;

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId( Long memberId ) {
        this.memberId = memberId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail( String email ) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword( String password ) {
        this.password = password;
    }

    public String getPseudonym() {
        return pseudonym;
    }

    public void setPseudonym( String pseudonym ) {
        this.pseudonym = pseudonym;
    }

    public Timestamp getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate( Timestamp registrationDate ) {
        this.registrationDate = registrationDate;
    }

}
