## WebSSH

### Introduction

A simple web application to be used as an ssh client to connect to your ssh servers. It is written in Java, base on jsch and xterm.js.

### How it works
```
+---------+     http     +--------+    ssh    +-----------+
| browser | <==========> | webssh | <=======> | ssh server|
+---------+   websocket  +--------+    ssh    +-----------+
```

### Requirements

* JDK 1.8
