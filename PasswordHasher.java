package com.wolfsburgsolutions.myapplication;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Created by Debaditya on 12/14/2017.
 */

public class PasswordHasher {

    String passwordToBeHashed = "";
    int log_rounds;

    PasswordHasher(String password){
        passwordToBeHashed = password;
    }
    PasswordHasher(String password, int log_rounds){
        passwordToBeHashed = password;
        this.log_rounds = log_rounds;
    }
    protected String hashPassword(){
        String salt = BCrypt.gensalt(log_rounds);
        String hashed_pass = BCrypt.hashpw(passwordToBeHashed,salt);

        return hashed_pass;
    }
}
