# TOTP Demo App

TOTP implementation using TOTPService which can be found in

[TOTPService](https://github.com/victorlee0505/TOTPService.git)


## Installation

This is a Springboot Project on JDK11.

Please `mvn install` TOTPService to resolve dependency.


## Getting Started

Login portal: http://localhost:8080

A default account is preloaded: `admin : 12345`

- Click "Show Users" anytime will display DB contents
- Click "Login" will start either QRCode or Passcode process depends on account status.
- Click "New Account" to register a new account

DB is not presistent as it is in-memory, restart this app will revert DB to initial state.


## License
[MIT](https://choosealicense.com/licenses/mit/)