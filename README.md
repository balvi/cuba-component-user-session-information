[![license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)
[![Build Status](https://travis-ci.org/balvi/cuba-component-user-session-information.svg?branch=master)](https://travis-ci.org/balvi/cuba-component-user-session-information)

#  CUBA Platform Component - User session information
CUBA component that shows detailed information about user sessions. The main purpose for this application component is to make the work of customer support of your software a little bit easier. Especially when you work heavily with deep levels of security groups the existing access groups screen is not capable of showing what session attributes with what values are applied for a particular user. The same goes for effective constraints in the security group.  It contains the following four parts:

* User information
* Applied session attributes
* Constraints
* Permissions

### Information on all current session for administration
The app components extends the `Administration > User Sessions` screen so that for all currently active session, the detailed session information can be retrieved.

### Own session information for every user
The user has the ability to display information about its current session via `Help > Current User Session`.

![Screenshot user session information](https://github.com/balvi/cuba-component-user-session-information/blob/master/img/user-session-information.png)
