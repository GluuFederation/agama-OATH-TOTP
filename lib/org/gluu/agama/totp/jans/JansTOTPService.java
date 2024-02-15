package org.gluu.agama.totp.jans;

import org.gluu.agama.totp.TOTPService;
import java.security.SecureRandom;
import com.lochbridge.oath.otp.*;
import com.lochbridge.oath.otp.keyprovisioning.*;
import com.lochbridge.oath.otp.TOTP;
import com.lochbridge.oath.otp.keyprovisioning.OTPAuthURIBuilder;
import com.lochbridge.oath.otp.keyprovisioning.OTPKey;
import com.lochbridge.oath.otp.keyprovisioning.OTPKey.OTPType;
import com.lochbridge.oath.otp.HmacShaAlgorithm;
import java.util.concurrent.TimeUnit;
import com.google.common.io.BaseEncoding;
import javax.crypto.KeyGenerator;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.jans.as.common.model.common.User;
import io.jans.as.common.service.common.EncryptionService;
import io.jans.as.common.service.common.UserService;
import io.jans.orm.exception.operation.EntryNotFoundException;
import io.jans.service.cdi.util.CdiUtil;
import io.jans.util.StringHelper;
import java.io.IOException;
import java.util.*;

public class JansTOTPService extends TOTPService {

    private static final Logger logger = LoggerFactory.getLogger(JansTOTPService.class);
    private static final int DIGITS = 6;
    private static final int TIME_STEP = 30;

    private static final String UID = "uid";
    private static final String EXT_ATTR = "jansExtUid";
    private static final String EXT_UID_PREFIX = "totp:";

    private static JansTOTPService INSTANCE = null;
    private JansTOTPService() {}
    public static synchronized JansTOTPService getInstance()
    {
        if (INSTANCE == null)
            INSTANCE = new JansTOTPService();

        return INSTANCE;
    }

    public String generateSecretKey(int keyLen) throws NoSuchAlgorithmException {
        byte[] randomBytes = new byte[keyLen];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(randomBytes);

        StringBuilder result = new StringBuilder();
        for (byte b : randomBytes) {
            result.append(String.format("%02X", b));
        }
        return result.toString();
    }

    public String generateTotpSecretKeyUri(String secretKey, String issuer, String userDisplayName) {
        String secretKeyBase32 = base32Encode(secretKey);
        OTPKey key = new OTPKey(secretKeyBase32, OTPType.TOTP);
        String label = issuer + " " + userDisplayName;

        OTPAuthURI uri = OTPAuthURIBuilder.fromKey(key).label(label).issuer(issuer).digits(DIGITS)
                .timeStep(TimeUnit.SECONDS.toMillis(TIME_STEP)).build();
        return uri.toUriString();
    }
  
    public boolean validateTOTP(String clientTOTP, String secretKey, String alg) {
        byte[] key = secretKey.getBytes();
        HmacShaAlgorithm algorithm = HmacShaAlgorithm.from("Hmac" + alg.toUpperCase());

        logger.debug("validateTOTP. algorithm ", algorithm);
        TOTP totp = TOTP.key(key).timeStep(TimeUnit.SECONDS.toMillis(TIME_STEP)).digits(DIGITS).hmacSha(algorithm).build();
        logger.debug("validateTOTP. client OTP ", clientTOTP);
        logger.debug("validateTOTP. totp OTP ", totp.value());
        
        if (totp.value().equals(clientTOTP)) {
            return true
        } else {
            return false
        }
    }

    public String linkUser(String uid, String totpSecretKey)
        throws Exception {
        User user = getUser(UID, uid);

        if (user == null) {
            logger.error("User identified with {} not found!", uid);
            throw new IOException("Target user for account linking does not exist");
        }
        String extUidPrefixTotpSecretKey = externalIdOf(totpSecretKey)
        String jansExtUidFieldValue = getSingleValuedAttr(user, EXT_ATTR);
        logger.debug("User ext uid ", jansExtUidFieldValue);
        if (jansExtUidFieldValue == null) {
            logger.debug("User ext uid not found");
            user.setAttribute(EXT_ATTR, extUidPrefixTotpSecretKey, true);
            UserService userService = CdiUtil.bean(UserService.class);
            userService.updateUser(user);
            return extUidPrefixTotpSecretKey
        }
        return jansExtUidFieldValue
    }

    public String getUserTOTPSecretKey(String uid)
        throws Exception {
        User user = getUser(UID, uid);

        if (user == null) {
            logger.error("User identified with {} not found!", uid);
            throw new IOException("Target user for account linking does not exist");
        }
        String jansExtUidFieldValue = getSingleValuedAttr(user, EXT_ATTR);
        logger.debug("User ext uid getUserTOTPSecretKey ", jansExtUidFieldValue);
        return jansExtUidFieldValue
    }

    private static String base32Encode(String input) {
        byte[] bytesToEncode = input.getBytes();
        return BaseEncoding.base32().omitPadding().encode(bytesToEncode);
    }

    private static User getUser(String attributeName, String value) {
        UserService userService = CdiUtil.bean(UserService.class);
        return userService.getUserByAttribute(attributeName, value, true);
    }

    private static String getSingleValuedAttr(User user, String attribute) {
        Object value = null;
        if (attribute.equals(UID)) {
            value = user.getUserId();
        } else {
            value = user.getAttribute(attribute, true, false); 
        }
        return value == null ? null : value.toString();
    }

    private static String externalIdOf(String id) {
        return id;
    }    
}