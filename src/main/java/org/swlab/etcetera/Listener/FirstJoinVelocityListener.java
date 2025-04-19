package org.swlab.etcetera.Listener;

import com.binggre.velocitysocketclient.listener.VelocitySocketListener;
import com.binggre.velocitysocketclient.socket.SocketResponse;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class FirstJoinVelocityListener extends VelocitySocketListener {

    @Override
    public void onReceive(String[] messages) {
        Bukkit.broadcastMessage(messages[0]);
    }

    @Override
    public @NotNull SocketResponse onRequest(String... strings) {
        return SocketResponse.empty();
    }

    @Override
    public void onResponse(SocketResponse socketResponse) {

    }
}