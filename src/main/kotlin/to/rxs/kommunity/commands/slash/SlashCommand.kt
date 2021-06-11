package to.rxs.kommunity.commands.slash

import dev.kord.common.annotation.KordPreview
import dev.kord.core.Kord
import dev.kord.core.entity.interaction.Interaction

@KordPreview
interface SlashCommand {

    suspend fun handle(kord: Kord, interaction: Interaction)

}
