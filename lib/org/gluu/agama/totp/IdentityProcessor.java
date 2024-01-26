package org.gluu.agama.totp;

import io.jans.as.common.model.common.User;
import io.jans.as.common.service.common.EncryptionService;
import io.jans.as.common.service.common.UserService;
import io.jans.orm.exception.operation.EntryNotFoundException;
import io.jans.service.cdi.util.CdiUtil;
import io.jans.util.StringHelper;

import java.io.IOException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.jans.inbound.Attrs.*;

public class IdentityProcessor {

    private static final Logger logger = LoggerFactory.getLogger(IdentityProcessor.class);

    private static final String UID = "uid";
    private static final String EXT_ATTR = "jansExtUid";
    private static final String EXT_UID_PREFIX = "totp:";
   
    public static String externalIdOf(String id) {
        return id;
    }
    
    public static String link(String uid, String totpSecretKey)
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

    public static String getUserTOTPSecretKey(String uid)
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
}
