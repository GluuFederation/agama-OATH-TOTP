package org.gluu.agama.totp;

import org.gluu.agama.totp.jans.JansTOTPService;

public abstract class TOTPService {

    public abstract String generateSecretKey(int keyLength);
    public abstract String generateTotpSecretKeyUri(String totpSecretKey, String issuer, String userDisplayName);
    public abstract boolean validateTOTP(String clientTOTP, String totpSecretKey, String alg);
    public abstract String linkUser(String uid, String totpSecretKey, String nickName);
    public abstract String getUserTOTPSecretKey(String uid);

    public static TOTPService getInstance(){
        return  JansTOTPService.getInstance();
    }
}
