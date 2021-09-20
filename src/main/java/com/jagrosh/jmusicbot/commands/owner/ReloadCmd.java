package com.jagrosh.jmusicbot.commands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.OwnerCommand;
import net.dv8tion.jda.api.entities.Activity;

import java.util.concurrent.TimeUnit;

public class ReloadCmd extends OwnerCommand {

    private final Bot bot;

    public ReloadCmd(Bot bot)
    {
        this.name = "reload";
        this.help = "reload config";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = false;
        this.bot = bot;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply(event.getClient().getWarning()+" Loading...");
        event.getJDA().getPresence().setActivity(Activity.playing("Reloading config..."));
        bot.getConfig().reload();

        bot.getThreadpool().schedule(() -> {
            event.getJDA().getPresence().setActivity(bot.genActivity());
            event.reply(event.getClient().getSuccess()+" 設定を反映しました。");
        },3, TimeUnit.SECONDS);
    }
}
