package me.gaminnlinux;

import com.sun.net.httpserver.HttpServer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class Bot extends ListenerAdapter {

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(3000), 0);
        server.createContext("/", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();

        final String token = System.getenv("token");
        JDA jda = JDABuilder.createDefault(token)
                .setActivity(Activity.playing("Join anchymc.tk!"))
                .setStatus(OnlineStatus.ONLINE)
                .build();

    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        final Message message = event.getMessage();
        final String msg = message.getContentRaw();
        if(event.getAuthor().getId() == "389604896606781440" && event.getAuthor().isBot() && event.getChannel().getId() == "840158134840459294") {
            if(msg.contains("Server Successfully Bumped")) {
                message.getChannel().sendMessage("Bot bump queued!").queue();
                message.getChannel().sendMessage("It's time to bump Liam! <@754229372981870663> <@460339405425737728>").queueAfter(2, TimeUnit.HOURS);
            }
        } else
            return;
    }

}
