

import java.util.Arrays;
import java.util.List;




import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class FacebookShare {
	private Activity act;
	private String ShareUrl;
	private String ShareText;
	private UiLifecycleHelper uiHelper;
	public Session session;
	public FacebookShare(Activity a,String u,String t){
		act=a;
		ShareUrl=u;
		ShareText=t;
		uiHelper = new UiLifecycleHelper(act, callback);
		openSession();
	}	
	private void openSession(){
		session = Session.getActiveSession();
		if (session == null||session.isClosed()){
        	 session = new Session(act);
       }
       	Session.setActiveSession(session);
        if (!session.isOpened() && !session.isClosed()) {
            session.openForPublish(new Session.OpenRequest(act).setCallback(statusCallback)
            		.setPermissions(Arrays.asList("publish_actions")));
        }
        else {
            Session.openActiveSession(act, true, statusCallback);
        }
        
	}
	private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            //onSessionStateChange(session, state, exception);
        	Log.e("facebook","Session.StatusCallback callback");
        }
    };
	private SessionStatusCallback statusCallback=new SessionStatusCallback();
    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
     //       updateView();
//        	Share();
        }
    }
    public void Share(){
    	Log.i("facebook","isClosed="+session.isClosed()+",isOpen="+session.isOpened());
    	if (session.isClosed()){
    		openSession();
    	//	Share();
        //	session.openForRead(new Session.OpenRequest(act).setCallback(statusCallback));
        }
    	if(session.isOpened()&& session.getPermissions().contains("publish_actions")){
    		postStatusUpdate(session);
    		
    	}else if(session.isOpened()){
    		/** this should not be needed and executed the permission should be request during login**/
    		Log.i("facebook","request to permission");
    		final List<String> PERMISSIONS = Arrays.asList("publish_actions");
    		Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(act, PERMISSIONS);
            newPermissionsRequest.setCallback(new Session.StatusCallback(){
				
            	/**This is the bug of Facebook SDK;that call is never called; this is hack in SharedDialog**/
            	@Override
				public void call(Session session, SessionState state,Exception exception) {
					Share();
				}});
            session.requestNewPublishPermissions(newPermissionsRequest);
//       		 session.requestNewPublishPermissions(new Session.NewPermissionsRequest((Activity)c, "publish_actions"));
    	}
    
    }

    public boolean canPresentShareDialog(){
    	return FacebookDialog.canPresentShareDialog(act, FacebookDialog.ShareDialogFeature.SHARE_DIALOG);
    }
    private void postStatusUpdate(Session session) {
        if (canPresentShareDialog()) {
            final FacebookDialog shareDialog = createShareDialogBuilder().build();
//        	 publishFeedDialog();
            uiHelper.trackPendingDialogCall(shareDialog.present());
            
        } else if (session != null &&  session.getPermissions().contains("publish_actions")) {
        	
        	act.runOnUiThread(new Runnable(){
                @Override
                public void run() {
                	publishFeedDialog();  
                }
           });
        }
  /*      } else {
//            pendingAction = PendingAction.POST_STATUS_UPDATE;
        }*/
    }
    private void publishFeedDialog() {
    	 Bundle params = new Bundle();
    	  //      params.putString("name", "Facebook SDK for Android");
    	  //      params.putString("caption", "Build great social apps and get more installs.");
    	        params.putString("description", ShareText);
    	        params.putString("link", ShareUrl);
    	      //  params.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");
    	        
    	        final WebDialog feedDialog = (
    	            new WebDialog.FeedDialogBuilder( act, Session.getActiveSession(),params))
    	            .setOnCompleteListener(new OnCompleteListener() {

    	                @Override
    	                public void onComplete(Bundle values,FacebookException error) {
    	                    if (error == null) {
    	                        // When the story is posted, echo the success
    	                        // and the post Id.
    	                        final String postId = values.getString("post_id");
    	                        if (postId != null) {
    	                        	Log.i("FacebookShare","postId="+postId);
    	                        } else {
    	                            // User clicked the Cancel button
    	                            Toast.makeText(act, 
    	                                "Publish cancelled", 
    	                                Toast.LENGTH_SHORT).show();
    	                        }
    	                    } else if (error instanceof FacebookOperationCanceledException) {
    	                        // User clicked the "x" button
    	                        Toast.makeText((Context) act.getApplicationContext(), 
    	                            "Publish cancelled", 
    	                            Toast.LENGTH_SHORT).show();
    	                    } else {
    	                        // Generic, ex: network error
    	                        Toast.makeText((Context) act.getApplicationContext(),"Error posting story",Toast.LENGTH_SHORT).show();
    	                       
    	                    }
    	                }


    	            })
    	            .build();feedDialog.show();
       
//        feedDialog.show();
    }
    private FacebookDialog.ShareDialogBuilder createShareDialogBuilder() {
        return new FacebookDialog.ShareDialogBuilder(act)
//                .setName("Hello Facebook")
                .setDescription(ShareText)
                .setLink(ShareUrl);
    }
}
