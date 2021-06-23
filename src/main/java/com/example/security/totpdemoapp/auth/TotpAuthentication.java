package com.example.security.totpdemoapp.auth;

import java.awt.image.BufferedImage;

import com.example.security.TotpCredential;
import com.example.security.TotpCredentialService;
import com.example.security.TotpPasscodeValidation;
import com.example.security.TotpQRCodeService;
import com.example.security.totpdemoapp.database.UserDataDAO;
import com.example.security.totpdemoapp.database.entity.UserData;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TotpAuthentication {
    
    private static final Logger logger = LoggerFactory.getLogger(TotpAuthentication.class);

    @Autowired
    private UserDataDAO userDataDao;

    @Value("${totp.encryptionkey}")
    private String otpEncryptionkey;

    public static final String provider = "demoapp";

    public boolean otpPasscodeValidate(UserData userData) {

        if (StringUtils.isNotBlank(userData.getUserEncryptedSecret())
                && StringUtils.isNotBlank(userData.getUserEncryptionKey())) {
            // Ask for Passcode

        } else {
            // re-initiate OTP registration

            TotpCredentialService tcs = TotpCredentialService.getInstance();
            TotpCredential cred = tcs.createCredential(userData.getUserId(), provider);

            userData.setUserEncryptedSecret(cred.getEncrytedSecret());
            userData.setUserEncryptionKey(cred.getEncryptionKey());

            userDataDao.saveAndFlush(userData);

            return false;

        }

        return false;
    }

    public BufferedImage otpRegister(UserData userData) {

        TotpCredentialService tcs = TotpCredentialService.getInstance();
        TotpCredential cred = tcs.createCredential(userData.getUserId(), provider, otpEncryptionkey);

        userData.setUserEncryptedSecret(cred.getEncrytedSecret());
        // userData.setUserEncryptionKey(cred.getEncryptionKey());

        userDataDao.saveAndFlush(userData);

        BufferedImage imgQR = otpQRCode(cred);

        return imgQR;

    }

    private BufferedImage otpQRCode(TotpCredential cred) {
        TotpQRCodeService qrService = new TotpQRCodeService();
        try {
            BufferedImage imgQR = qrService.generateQRCodeBufferedImage(cred);
            return imgQR;

        } catch (Exception e) {
            logger.error("QRCode generation error", e);
        }
        return null;
    }

    public boolean isPasscodeValid(String encrpytedSecret, String passcode){
        
        return TotpPasscodeValidation.isPasscodeValid(otpEncryptionkey, encrpytedSecret, passcode);
    }
}
