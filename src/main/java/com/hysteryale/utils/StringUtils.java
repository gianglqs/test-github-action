package com.hysteryale.utils;

public class StringUtils {

    /**
     * To check if a password is strong enough to use
     * @param password
     * @return true if password is strong
     */
    public static boolean checkPasswordStreng(String password){
        boolean containLowerChar= false, containUpperChar = false;
        boolean containDigit = false, containSpecialChar = false,    minLength = false;

        String special_chars = "!(){}[]:;<>?,@#$%^&*+=_-~`|./'";
        String strength;

        for(char ch : password.toCharArray()){
            if(Character.isLowerCase(ch)){
                containLowerChar = true;
            }
            if(Character.isUpperCase(ch)){
                containUpperChar = true;
            }
            if(Character.isDigit(ch)){
                containDigit = true;
            }
            if(special_chars.contains(String.valueOf(ch))){
                containSpecialChar = true;
            }
        }
        if (password.length() >= 8){
            minLength = true;
        }

        // if all the conditions passed then password is strong
        if(minLength && containDigit && containUpperChar && containSpecialChar && containLowerChar){
            return true;
        }else{
            return false;
        }
    }

}
