![logo](./logo.png)

[![Contributors][contributors-shield]](contributors-url)
[![Forks][forks-shield]](forks-url)
[![Stargazers][stars-shield]](stars-url)
[![Issues][issues-shield]](issues-url)
[![Apache License][license-shield]](license-url)

# About Gluu Agama TOTP Project

This repo is home to the Gluu Agama-TOTP project. This Agama project provides 
authentication with TOTP(Time-based One-time Passwords) 2-factor authentication.

## Where To Deploy

The project can be deployed to any IAM server that runs an implementation of 
the [Agama Framework](https://docs.jans.io/head/agama/introduction/) like 
[Janssen Server](https://jans.io) and [Gluu Flex](https://gluu.org/flex/).

## How To Deploy

Different IAM servers may provide different methods and 
user interfaces from where an Agama project can be deployed on that server. 
The steps below show how the Agama-TOTP project can be deployed on the 
[Janssen Server](https://jans.io). 

Deployment of an Agama project involves three steps

- [Downloading the `.gama` package from project repository](#download-the-project)
- [Adding the `.gama` package to the IAM server](#add-the-project-to-the-server)
- [Configure the project](#configure-the-project)


The project is bundled as 
[.gama package](https://docs.jans.io/head/agama/gama-format/). 
Visit the `Assets` section of the 
[Releases](https://github.com/GluuFederation/agama-OATH-TOTP/releases) to download 
the `.gama` package.


### Add The Project To The Server

 The Janssen Server provides multiple ways an Agama project can be 
 deployed and configured. Either use the command-line tool, REST API, or a 
 TUI (text-based UI). Refer to 
 [Agama project configuration page](https://docs.jans.io/head/admin/config-guide/auth-server-config/agama-project-configuration/) 
 in the Janssen Server documentation for more details.


### Configure The Project

Agama project accepts configuration parameters in the JSON format. Every Agama 
project comes with a basic sample configuration file for reference.

Below is a typical configuration of the Agama-TOTP project. As show, it contains
configuration parameters for the [flows contained in it](#flows-in-the-project):
```
{
  "org.gluu.agama.totp.main": {
      "issuer": "your-host-or-title",
      "qrCodeLabel": "Gluu",
      "qrCodeAlg": "sha1",
      "qrCodeKeyLength": 20
  }
}
```

Check the flow detail section for details about configuration parameters.


| Name              | Description                                                           | Notes                                   |
| ----------------- | --------------------------------------------------------------------- | --------------------------------------- |
| `issuer`          | Issuer of the OTP service                                             | Keep it simple and little e.g. gluu.org |
| `qrCodeLabel`     | This config is used to add your brand name into the center of QR Code | Keep it simple and little               |
| `qrCodeKeyLength` | Key length to generate Secret Key                                     | Default is `20`                         |
| `qrCodeAlg`       | Algorithm used to validate TOTP                                       | Default is `sha1`                       |



### Test The Flow

Use any Relying party implementation (like [jans-tarp](https://github.com/JanssenProject/jans/tree/main/demos/jans-tarp)) 
to send authentication request that triggers the flow.

From the incoming authentication request, the Janssen Server reads the `ACR` 
parameter value to identify which authentication method should be used. 
To invoke the `org.gluu.agama.totp.main` flow contained in the  Agama-TOTP project, 
specify the ACR value as `agama_<qualified-name-of-the-top-level-flow>`, 
i.e  `agama_org.gluu.agama.totp.main`.



## Customize and Make It Your Own

Fork this repo to start customizing the Agama-TOTP project. It is possible to 
customize the user interface provided by the flow to suit your organization's 
branding 
guidelines. Or customize the overall flow behavior. Follow the best 
practices and steps listed 
[here](https://docs.jans.io/head/admin/developer/agama/agama-best-practices/#project-reuse-and-customizations) 
to achieve these customizations in the best possible way.
This  project can be re-used in other Agama projects to create more complex
 authentication journeys. To re-use, trigger the 
 [org.gluu.agama.totp.main](#flows-in-the-project) flow from other Agama projects.

To make it easier to visualize and customize the Agama Project, use 
[Agama Lab](https://cloud.gluu.org/agama-lab/login).

## Flows In The Project


The project consists of four flows that provide incremental functionality:

| Qualified Name             | Description                                                                                                                                                                                                                                                                                                                |
| -------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `org.gluu.agama.totp.main` | This is the main flow which you can directly launch from the browser. It first proceeds for user authn by triggering `org.gluu.agama.totp.pw` flow. Then helps to check user is already enrolled for TOTP 2FA or not. If a new user then trigger `org.gluu.agama.enroll` otherwise `org.gluu.agama.otp` and validate TOTP. |
| `org.gluu.agama.totp.pw`   | This flow is used for user authn. It first asks the user to enter a username, and password, and validate the user.                                                                                                                                                                                                         |
| `org.gluu.agama.enroll`    | This flow is used to enroll new users into TOTP 2FA. It provides an enrollment page with a QR-Code. Users need to scan the QR-Code in any Authenticator App and enter OTP. At the end, it returns a validation response.                                                                                                   |
| `org.gluu.agama.otp`       | This flow is used to validate OTP. If the user is already enrolled in TOTP 2FA then it provides an OTP page and asks the user to enter an OTP and return a validation response.                                                                                                                                            |


## org.gluu.agama.totp.main

When the main flow of this project is launched 
(namely, `org.gluu.agama.totp.main`) it shows the login page. The user enters a 
username and password. After the user authn, the OTP enrollmen page opens for 
new user and if a user is already enrolled then it will directly ask for OTP.

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

## Demo

Check out this [video](https://www.loom.com/share/56cb3b2328dd48a9b8a8ffb0b69646d1) 
of the `org.gluu.agama.totp.main` flow.

<!-- This are stats url reference for this repository -->

[contributors-shield]: https://img.shields.io/github/contributors/GluuFederation/agama-OATH-TOTP.svg?style=for-the-badge
[contributors-url]: https://github.com/GluuFederation/agama-OATH-TOTP/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/GluuFederation/agama-OATH-TOTP.svg?style=for-the-badge
[forks-url]: https://github.com/GluuFederation/agama-OATH-TOTP/network/members
[stars-shield]: https://img.shields.io/github/stars/GluuFederation/agama-OATH-TOTP?style=for-the-badge
[stars-url]: https://github.com/GluuFederation/agama-OATH-TOTP/stargazers
[issues-shield]: https://img.shields.io/github/issues/GluuFederation/agama-OATH-TOTP.svg?style=for-the-badge
[issues-url]: https://github.com/GluuFederation/agama-OATH-TOTP/issues
[license-shield]: https://img.shields.io/github/license/GluuFederation/agama-OATH-TOTP.svg?style=for-the-badge
[license-url]: https://github.com/GluuFederation/agama-OATH-TOTP/blob/main/LICENSE
