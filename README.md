# Client Authentication Attempt Counter Demo Plugin

This is a simple example event listener SDK Plugin for the Curity Identity Server.

It demonstrates how to plug in to the event listener framework (introduced in version 2.2) by registering two event 
listeners; one for successful and one for failed client authentication attempts. On a failed authentication the plugin
uses an attribute repository (introduced in version 3.0) to create a counter for the client, and will increment the 
counter on each authentication failure. The event listener handling successful authentication resets the same counter,
so that only consecutive failed attempts are counted. After three consecutive failed attempts, the plugin logs a message
on INFO level. At 10 consecutive failed attempts, the plugin logs a message on WARN level.

Please visit [curity.io](https://curity.io/) for more information about the Curity Identity Server.