# MultiBox Network Protocol

This document describes current protocol in use of this application, and
should always stay public and accessible.

## Basic concepts

*	Communication is message oriented, only one direction (Host -> Client / Client -> Host).
*	Transmission of messages can be done over any stream-like medium, for example classic TCP sockets.
*	Each message is independent of any other message.
*	Communication is stateless.
*	Messages from one client may arrive from completely different connections.
*	There is no guarantee, in which order will the server respond to the arriving messages, or in what time.

Currently, communication is done through **TCP** socket on port **13110**.

## Message encapsulation

### Layer 1 message:

This layer is here just to determine the message type, the content of which is totally undetermined
and may contain JSON encoded string or even a whole music files. Responsible code on each side should
be prepared, what data will appear in the message body depending on the message type.

 Message Type | Message Body Length | Message Body
:---:|:---:|:---:
 4 byte integer | 4 byte integer | N bytes (N = Message Body Length)
 
### Layer 2 message:
 
Purpose of this layer is to clearly state, how `java.lang.String` is transmitted across streams,
since classic Java documentation doesn't state that clearly enough with `DataOutputStream.writeUTF()` method.
 
**This data is the body of Layer 1 message**
 
String Length | String Content
:---: | :---:
4 byte integer | `java.lang.String` encoded to bytes with encoding `UTF-8`

### Layer 3 message:

JSON string encoded into *Layer 2 message content*.

### Example of layer 3 -> 2 -> 1 encapsulation

Message Type | Message Body length | String length | JSON Object
:---: | :---: | :---: | :---:
4b integer | 4b integer | 4b integer | JSON written as `java.lang.String`
`1` | `6` | `2` | `{}`

## JSON Data structures

Across communication and messages, there are some JSON structures, that may appear more than once in 
message communication, so they are described in this section and used later in message type descriptions.

### LibraryItem

```
{
	id: (long)<id of library item>,
	type: (LibraryItemType)<type of library item, uppercase as enum>,
	name: (string)<human readable name of item>
}
```

### Multimedia extends LibraryItem

```
{
	...
	length: (integer)<length of multimedia in seconds>
}
```

### Directory extends LibraryItem

```
{
	...,
	items:
	[
		(LibraryItem)<first directory item>,
		(LibraryItem)<second directory item>,
		...
	]
}
```

### LibraryItemType

`DIRECTORY` | `MULTIMEDIA`

## Message types

There will be message types

### Get player state (Message type: 1, Layer 3)

Clients sends an empty message *(0 bytes)* to server, and the server responds with it's current player state:

```
{
	multimedia: (Multimedia)<actual playing multimedia>,
	playbackPosition: (integer)<actual playback position>,
	playing: (boolean)<is player playing or is paused>
}
```

*TODO This protocol is not finished yet*