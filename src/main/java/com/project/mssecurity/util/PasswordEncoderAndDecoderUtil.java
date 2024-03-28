package com.project.mssecurity.util;

import lombok.extern.log4j.Log4j2;

import java.util.Arrays;
import java.util.Base64;
@Log4j2
public class PasswordEncoderAndDecoderUtil {

        public static String typeAction(String password,String action)
        {try {
            String newPassword ="";
            if (action.equalsIgnoreCase("encode"))
            {
                newPassword= Base64.getEncoder().encodeToString(password.getBytes());
            }else if (action.equalsIgnoreCase("decode"))
            {
                byte[] decodedBytes = Base64.getDecoder().decode(password);
                String decodedString = new String(decodedBytes);
                newPassword=decodedString;
            }else {
                log.info("Select encoder or decoder");
            }
            return newPassword;
        }catch (Exception exception)
        {
            log.error(exception.getMessage());
            return exception.getMessage();
        }
        }
}
