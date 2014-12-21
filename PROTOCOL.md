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

Currently, communication is done through **TCP** socket communication on port **13110**.

## Message encapsulation

### Layer 1 message:

 Message Type | Message Body Length | Message Body
:------------:|:-------------------:|:------------:
 4 bytes | 4 bytes | N bytes (N = Message Body Length)