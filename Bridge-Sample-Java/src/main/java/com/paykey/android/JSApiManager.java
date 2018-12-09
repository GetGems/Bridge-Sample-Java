package com.paykey.android;

import java.util.ArrayList;

public class JSApiManager{
    private static final String TAG = JSApiManager.class.getSimpleName();

    private static JSApiManager instance = null;
    private RequestQueue volleyQueue;
    private boolean debugMode = false;

    // singleton pattern
    public synchronized static JSApiManager getInstance() {
        if (JSApiManager.instance == null) {
            JSApiManager.instance = new JSApiManager();
        }
        return JSApiManager.instance;
    }

    private JSApiManager() {
    }


    public void doSomething(String v1, String v2, String v3, String v4, final Success successBlock, final Failure failBlock)
    {
        JSONArray params = new JSONArray();
        params.put(v1);
        params.put(v2);
        params.put(v3);
        params.put(v4);
        jsApiCall("doSomething", params, successBlock, failBlock);
    }

    // utility function for communicating with the bridge
    void jsApiCall(String call, JSONArray params, final Success successBlock, final Failure failBlock)
    {
        try
        {
            // encode params
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i = 0; i < params.length(); i++)
            {
                sb.append("\"");
                sb.append(params.getString(i));
                sb.append("\"");
                if (i != params.length() - 1) sb.append(",");
            }
            sb.append("]");
            String stringifiedArgsArray = sb.toString();

            // use the helper
            JsBridgeHelper.getInstance().executeJsFunction(call, stringifiedArgsArray, new JsBridgeHelper.JsCallback()
            {
                @Override
                public void onResult(String res)
                {
                    try
                    {
                        JSONObject response = new JSONObject(res);

                        // get the server success result
                        Boolean success = response.getBoolean("success");
                        if (!success.booleanValue())
                        {
                            String error = response.optString("error");
                            if ((error == null) || error.isEmpty()) error = "Bridge failed with an unknown error.";
                            if (failBlock != null) failBlock.fail(error, response);
                            return;
                        }

                        //TODO: add message handling

                        if (successBlock != null) successBlock.success(response);
                        return;
                    }
                    catch (JSONException e)
                    {
                        String error = "Invalid response from bridge, try again later.";
                        if (failBlock != null) failBlock.fail(error, null);
                        return;
                    }
                }
            });
        }
        catch (JSONException e)
        {
            String error = "Error communicating with bridge, try again later.";
            if (failBlock != null) failBlock.fail(error, null);
            return;
        }
    }
    

    public interface Success
    {
        public void success(JSONObject response);
    }

    public interface Failure
    {
        public void fail(String error, JSONObject response);
    }
}
