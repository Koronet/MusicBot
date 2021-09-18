/*
 * Copyright 2017 John Grosh <john.a.grosh@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.jmusicbot.commands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.commands.OwnerCommand;
import net.dv8tion.jda.api.entities.Guild;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class ShutdownCmd extends OwnerCommand
{
    private final Bot bot;
    
    public ShutdownCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "shutdown";
        this.help = "safely shuts down";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = false;
    }
    
    @Override
    protected void execute(CommandEvent event)
    {
        for (Guild g : event.getJDA().getGuilds()){
            AudioHandler ah = (AudioHandler)g.getAudioManager().getSendingHandler();
            if(ah!=null)
            {
                if (ah.isMusicPlaying(event.getJDA())){
                    bot.comandChannels.get(g).sendMessage("アップデート反映のため3秒後に再起動を行います。\n再生中に申し訳ありませんが、ご協力よろしくおねがいします。\nアップデートの詳細についてはサポートサーバーをご覧ください。\nhttps://discord.gg/Vka9uXU35H").complete();
                }
            }
        }

        bot.getThreadpool().schedule(() -> {
            event.replyWarning("Shutting down...");
            bot.shutdown();
        },3, TimeUnit.SECONDS);
    }
}
