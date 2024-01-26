![logo](./logo.png)

[![Contributors][contributors-shield]](contributors-url)
[![Forks][forks-shield]](forks-url)
[![Stargazers][stars-shield]](stars-url)
[![Issues][issues-shield]](issues-url)
[![Apache License][license-shield]](license-url)

# Gluu Agama TOTP Project

Welcome to the [https://github.com/kdhttps/agama-otp](https://github.com/kdhttps/agama-otp) project. This project is governed by Gluu and published under an Apache 2.0 license.

Use this project to add user authentication with OTOP(Time-based One-time Passwords) 2-factor authentication.

## How it works at a glance

When then main flow of this project is launched (namely, `org.gluu.agama.totp.main`) it shows login page. User enters username and password. After user authn, OTP enrollmen page open for new user and if user is already enrolled then it will directly ask for OTP.

```mermaid
sequenceDiagram

title Agama TOTP Project Flow

participant browser as Browser
participant rp as RP
participant jans as Jans Authz Server

autonumber
browser->>rp: Request page
rp->>jans: Invoke /authorize endpoint
loop n times - (multistep authentication)
jans->>browser: Present Login screen
browser->>jans: Present Login credentials
end
jans->>jans: Authenticate user
opt if new user
jans->>browser: Present OTP enrollment page with QR-Code
browser->>browser: Scan QR-Code in OTP Auth App
browser->>jans: Enter OTP
jans->>jans: Validate OTP and save secrey key to user
end
opt if enrolled user
jans->>browser: Present OTP page to enter OTP
browser->>jans: enter OTP
jans->>jans: Validate OTP
end
jans->>jans: Create internal Jans session
jans->>rp: Redirect with Success response
rp->>rp: Validate response
rp->>browser: Page is accessed
```

# Core Developers

<table>
 <tr>
  <td align="center" style="word-wrap: break-word; width: 150.0; height: 150.0">
    <a href=https://github.com/kdhttps>
        <img src="https://avatars.githubusercontent.com/u/39133739?v=4" width="100;"  style="border-radius:50%;align-items:center;justify-content:center;overflow:hidden;padding-top:10px" alt="Kiran Mali">
        <br />
        <sub style="font-size:14px"><b>Kiran Mali</b></sub>
    </a>
  </td>
 </tr>
</table>

# License

This project is licensed under the [Apache 2.0](https://github.com/kdhttps/agama-otp/blob/main/LICENSE)

<!-- This are stats url reference for this repository -->

[contributors-shield]: https://img.shields.io/github/contributors/kdhttps/agama-otp.svg?style=for-the-badge
[contributors-url]: https://github.com/kdhttps/agama-otp/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/kdhttps/agama-otp.svg?style=for-the-badge
[forks-url]: https://github.com/kdhttps/agama-otp/network/members
[stars-shield]: https://img.shields.io/github/stars/kdhttps/agama-otp?style=for-the-badge
[stars-url]: https://github.com/kdhttps/agama-otp/stargazers
[issues-shield]: https://img.shields.io/github/issues/kdhttps/agama-otp.svg?style=for-the-badge
[issues-url]: https://github.com/kdhttps/agama-otp/issues
[license-shield]: https://img.shields.io/github/license/kdhttps/agama-otp.svg?style=for-the-badge
[license-url]: https://github.com/kdhttps/agama-otp/blob/main/LICENSE
