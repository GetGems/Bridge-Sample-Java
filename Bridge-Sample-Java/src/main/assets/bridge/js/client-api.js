function doSomething(callId, v1, v2, v3, quantity) { //OK
	// cpWrapper is some other code that does the actual code
    cpWrapper.doSomething(v1, v2, v3, v4)
        .then(androidBridge.resultToNative(callId));
}
