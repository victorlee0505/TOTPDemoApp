package com.example.security.totpdemoapp.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.example.security.totpdemoapp.auth.TotpAuthentication;
import com.example.security.totpdemoapp.database.UserDataDAO;
import com.example.security.totpdemoapp.database.entity.UserData;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Value("${totp.enable: false}")
    private Boolean toptEnable;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserDataDAO userDataDao;

    @Autowired
    private TotpAuthentication otpAuth;

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {

        logger.info("LoginController.login ---->");

        Map<String, Object> response = new HashMap<String, Object>();

        if (body.containsKey("userId") && body.containsKey("userPassword")) {
            String userId = body.get("userId");

            UserData userData = userDataDao.findTopByUserId(userId);

            if (userData != null) {
                if (StringUtils.equals(body.get("userPassword").trim(), userData.getUserPassword())) {
                    response.put("Status", "Success");
                    response.put("message", "Logon");

                    // OTP process
                    if (toptEnable) {
                        if (StringUtils.isNotBlank(userData.getUserEncryptedSecret())) {
                            // Passcode
                            response.put("otp", "passcode");
                        } else {
                            // register
                            response.put("otp", "qrcode");
                        }
                    }

                } else {
                    response.put("Status", "Failed");
                    response.put("message", "incorrect password");
                }
            } else {
                response.put("Status", "Failed");
                response.put("message", "no user found");
            }

        } else {
            response.put("Status", "Failed");
            response.put("message", "invalid input");
        }

        ResponseEntity<Map<String, Object>> resEnt = ResponseEntity.status(HttpStatus.OK)
                .body(Collections.unmodifiableMap(response));

        logger.info("response: [{}]", response);
        logger.info("<---- LoginController.login");

        return resEnt;
    }

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> body) {

        logger.info("LoginController.register ---->");

        Map<String, Object> response = new HashMap<String, Object>();

        if (body.containsKey("userId") && body.containsKey("userPassword")) {
            String userId = body.get("userId");
            String userPassword = body.get("userPassword");

            UserData userData = userDataDao.findTopByUserId(userId);

            if (userData != null) {
                logger.warn("User ID: [{}] already exist", userData.getUserId());
                response.put("Status", "Failed");
                response.put("message", "User ID :" + userData.getUserId() + " already exists");
            } else {
                userData = new UserData(userId, userPassword);
                userDataDao.saveAndFlush(userData);

                userData = userDataDao.findTopByUserId(userId);

                if (userData != null) {
                    if (StringUtils.equals(body.get("userId"), userData.getUserId())) {
                        logger.info("User ID: [{}] registered.", userData.getUserId());
                        response.put("Status", "Success");
                        response.put("message", "Registered, Hello, " + userData.getUserId() + "!");
                    } else {
                        response.put("Status", "Failed");
                        response.put("message", "Registration failed, something went wrong.... T_T");
                    }
                }
            }
        } else {
            response.put("Status", "Failed");
            response.put("message", "invalid input");
        }

        ResponseEntity<Map<String, Object>> resEnt = ResponseEntity.status(HttpStatus.OK)
                .body(Collections.unmodifiableMap(response));

        logger.info("response: [{}]", response);
        logger.info("<---- LoginController.register");

        return resEnt;
    }

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserData> users() {
        logger.info("LoginController.users ---->");
        List<UserData> userData = userDataDao.findAll();
        logger.info("response: [{}]", userData);
        logger.info("<---- LoginController.users");
        return userData;
    }

    @PostMapping(value = "/passcode", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> passcode(@RequestBody Map<String, String> body) {
        logger.info("LoginController.passcode ---->");

        Map<String, Object> response = new HashMap<String, Object>();

        if (body.containsKey("userId") && body.containsKey("passcode")) {
            String userId = body.get("userId");

            UserData userData = userDataDao.findTopByUserId(userId);

            String passcode = body.get("passcode");

            if(StringUtils.isNumeric(passcode)){
                boolean isValid = otpAuth.isPasscodeValid(userData.getUserEncryptedSecret(), passcode);

                if(isValid){
                    response.put("Status", "Success");
                    response.put("message", "Passcode is Valid.");
                } else {
                    response.put("Status", "Failed");
                    response.put("message", "Passcode invalid, please try again");
                }
            } else {
                response.put("Status", "Failed");
                response.put("message", "Passcode must be Numeric, please try again");
            }
        }

        ResponseEntity<Map<String, Object>> resEnt = ResponseEntity.status(HttpStatus.OK)
                .body(Collections.unmodifiableMap(response));

        logger.info("<---- LoginController.passcode");
        return resEnt;
    }

    @PostMapping(value = "/qrcode", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> qrcode(@RequestBody Map<String, String> body) {
        logger.info("LoginController.qrcode ---->");

        Map<String, Object> response = new HashMap<String, Object>();

        if (body.containsKey("userId") && body.containsKey("userPassword")) {
            String userId = body.get("userId");

            UserData userData = userDataDao.findTopByUserId(userId);

            BufferedImage imgQR = otpAuth.otpRegister(userData);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ImageIO.write(imgQR, "png", baos);
                baos.flush();
                byte[] imageInByteArray = baos.toByteArray();
                baos.close();
                String b64 = javax.xml.bind.DatatypeConverter.printBase64Binary(imageInByteArray);
                response.put("image", b64);
            } catch (IOException e) {
                logger.error("IOException", e);
            }

        }

        ResponseEntity<Map<String, Object>> resEnt = ResponseEntity.status(HttpStatus.OK)
                .body(Collections.unmodifiableMap(response));

        logger.info("<---- LoginController.qrcode");
        return resEnt;
    }

}
