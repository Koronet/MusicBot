package com.jagrosh.jmusicbot.commands.general;

import com.jagrosh.jdautilities.commons.JDAUtilitiesInfo;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.examples.doc.Author;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.JMusicBot;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.Permission;
import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Collections;

@CommandInfo(
        name = "About",
        description = "Gets information about the bot."
)
@Author("John Grosh (jagrosh)")
public class AboutCommand extends Command {
    private boolean IS_AUTHOR = true;
    private String REPLACEMENT_ICON = "\uD83C\uDFB6";
    private final Color color;
    private final Permission[] perms;
    private String oauthLink;
    private final String[] features;
    private final Bot bot;

    public AboutCommand(Bot bot)
    {
        this.color = Color.BLUE.brighter();
        this.features = new String[]{""};
        this.name = "about";
        this.help = "shows info about the bot";
        this.guildOnly = false;
        this.perms = JMusicBot.RECOMMENDED_PERMS;
        this.bot = bot;

        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    protected void execute(CommandEvent event) {
        if (oauthLink == null) {
            try {
                ApplicationInfo info = event.getJDA().retrieveApplicationInfo().complete();
                oauthLink = info.isBotPublic() ? info.getInviteUrl(0L, perms) : "";
            } catch (Exception e) {
                Logger log = LoggerFactory.getLogger("OAuth2");
                log.error("Could not generate invite link ", e);
                oauthLink = "";
            }
        }
        EmbedBuilder builder = new EmbedBuilder();

        builder.setColor(event.isFromType(ChannelType.TEXT) ? event.getGuild().getSelfMember().getColor() : color);

        builder.setAuthor("About " + event.getSelfUser().getName() + " !", null, event.getSelfUser().getAvatarUrl());

        StringBuilder description = new StringBuilder()
                .append("cronにより作成された音楽BOT").append("\n")
                .append("JMusicBOTをベースにカスタマイズをしています。").append("\n")
                .append("[サポートサーバー](").append(event.getClient().getServerInvite()).append(")").append("\n")
                .append("[招待リンク](").append(oauthLink).append(")").append("\n");

        builder.setDescription(description);

        StringBuilder field;

        // Memory
        long totalMB = Runtime.getRuntime().totalMemory() / 1024 / 1024;
        long freeMB = Runtime.getRuntime().freeMemory() / 1024 / 1024;
        long usedMB = totalMB-freeMB;
        long maxMB = Runtime.getRuntime().maxMemory() / 1024 / 1024;

        // used / 40
        double magnif40 = 40.0 / totalMB;
        int used40 = Math.toIntExact(Math.round(usedMB * magnif40));
        int free40 = 40 - used40;

        // used / 100
        double magnif1000 = 1000.0 / totalMB;
        double used1000 = Math.round(usedMB * magnif1000);

        field = new StringBuilder()
                .append("```fix").append("\n")
                .append("[").append(usedMB).append(" MB / ").append(totalMB).append(" MB] ( MAX ").append(maxMB/1024).append(" GB )").append("\n")
                .append("[")
                .append(
                    String.join("", Collections.nCopies(used40, "#"))
                ).append(
                    String.join("", Collections.nCopies(free40, " "))
                ).append("] [").append(used1000/10).append("%]");

        field.append("```");

        builder.addField("Memory",field.toString(),false);

        field = new StringBuilder()
                .append(
                        event.getJDA().getGuilds().size()
                )
                .append("サーバーで作動中 | ")
                .append(
                        event.getJDA().getGuilds().stream().filter(g -> g.getSelfMember().getVoiceState().inVoiceChannel()).count()
                )
                .append("サーバーに接続中");

        builder.addField("Status",field.toString(),false);

        builder.setFooter("最後の再起動", null);
        builder.setTimestamp(event.getClient().getStartTime());
        event.reply(builder.build());
    }

}
