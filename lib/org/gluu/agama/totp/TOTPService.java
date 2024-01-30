package org.gluu.agama.totp;

import org.gluu.agama.totp.jans.service.JansTOTPService;
import org.gluu.agama.totp.jans.model.ContextData;

public abstract class TOTPService {

    public abstract String generateSecretKey(int keyLength);
    public abstract String generateTotpSecretKeyUri(String totpSecretKey, String issuer, String userDisplayName);
    public boolean validateTOTP(String clientTOTP, String totpSecretKey, String alg);
    public String linkUser(String uid, String totpSecretKey);
    public String getUserTOTPSecretKey(String uid);

    public static TOTPService getInstance(){
        return  JansTOTPService.getInstance();
    }
}
