FacebookShare
=============

Android facebook share implemented with Facebook SDK 3.5.2

when Facebook app is installed in user's phone,this call activity of Facebook.
if the user didn't install Facebook app,this will call Feed Dialog

Usage
=====

    FacebookShare F=new FacebookShare(Activity,url,text);
    F.Share();

in the activity OnActivityResult add

    if( resultCode == Activity.RESULT_OK) F.session.onActivityResult(this, requestCode, resultCode, data);
    //this is activity

Change Log
==========

140310 
1.if user cancel login when calling openForPublish first time,the login
   window didn't show when calling openForPublish afterwards.
  =>call session.closeAndClearTokenInformation(); to close session,so the
    activity would create new session next time
2. use .setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO) to prevent auth error