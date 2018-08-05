package nl.stoux.slap.discord.models.helpers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DiscordId {

    private Long id;

    @Setter
    private String name;

}
