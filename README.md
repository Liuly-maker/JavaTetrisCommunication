# JavaTetrisCommunication
 It's school Java GUI homework

 In GameClient,

    Class GameFrame is the basic Frame.

    Left : Communication Area.
    Center : Game Area.
    Right : Button Area.
    top : Set connect Ip and Port.
    bottom : Send message.

    Class Game is MainGame embeded in GameFrame.

In GameServer,

    Class Server is communication server contain every socket.

    Class ServerThread is running every Socket in it. It's define what every socket to do.