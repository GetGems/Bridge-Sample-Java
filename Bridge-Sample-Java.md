# Bridge Sample

This project in a naive implementation of a Java-to-JavaScript communication.
There are two parts to the bridge, the Java part and the JavaScript part.


## The JavaScript Part

### index.html
This is the entry point of the JavaScript, this HTML file will load the JavaScript which will handle the bridging and the operations.

### android-bridge-helper.js (Bridge logic)
This is a helper file which actually handles the bridging to and from the Java code.

### client-api.js (Core logic)
This is a sample code of a client API. The most important part is after making the operation is returning a callback via `androidBridge`. In this example the method `doSomething` will be invoked via the bridge from the Java part.


## The Java Part

### JsBridgeHelper.java (Bridge logic)
This is a helper class which actually handles the bridging to and from the JavaScript.
The main componenet here is a WebView which loads the `index.html`.
This WebView stays in memory and isn't added to the View hirarchy.

The main entry point is the `executeJsFunction` which recieves the JS call function name, params and Java callback which will return with a result from the JavaScript part.  


### JSApiManager.java (Core logic)
This file is an example of how to use the `JsBridgeHelper`.
The entry point is the method `doSomething` which represents a method in the same name in JavaScript.
The `doSomething` method in Java transfroms the java params into string and uses the `JsBridgeHelper` to pass it to the JavaScript part.

## Summary
After loading the `index.html` file to the WebView, the native Java part should invoke method call via the `JsBridgeHelper`. The calls will be directed to the `android-bridge-helper` for the `nativeToJs` call.
From the `nativeToJs` call it should be redireted to the `client-api` for performing the operation. The result will be reported back to the `android-bridge-helper` via the `resultToNative`.
The `resultToNative` will return it back to the `JsBridgeHelper` and back to the invoking Java code.
